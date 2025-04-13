package com.ruoyi.framework.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * API响应缓存过滤器
 * 专门用于处理/login接口，确保响应内容被正确缓存和包装
 */
public class ApiResponseCacheFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiResponseCacheFilter.class);
    
    // 需要特殊处理的接口路径
    private static final List<String> SPECIAL_PATHS = Arrays.asList("/login", "/logout", "/captchaImage");
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        boolean isSpecialPath = isSpecialPath(path);
        
        // 所有请求都包装，但特殊路径使用自定义包装器
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        
        if (isSpecialPath) {
            log.info("特殊路径拦截: {}", path);
            
            // 特殊接口使用自定义包装器
            TeeResponseWrapper responseWrapper = new TeeResponseWrapper(response);
            
            try {
                // 执行过滤器链
                filterChain.doFilter(requestWrapper, responseWrapper);
            } finally {
                try {
                    // 获取响应内容并输出到请求属性中，供拦截器读取
                    byte[] content = responseWrapper.toByteArray();
                    request.setAttribute("responseContent", new String(content, "UTF-8"));
                    
                    // 将内容写回响应
                    response.getOutputStream().write(content);
                } catch (Exception e) {
                    log.error("拷贝响应内容失败", e);
                }
            }
        } else {
            // 其他请求使用Spring标准包装器
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            try {
                filterChain.doFilter(requestWrapper, responseWrapper);
            } finally {
                // 确保响应被复制回去
                responseWrapper.copyBodyToResponse();
            }
        }
    }
    
    private boolean isSpecialPath(String path) {
        return SPECIAL_PATHS.stream().anyMatch(p -> path.equals(p) || path.startsWith(p + "/"));
    }
    
    /**
     * 自定义响应包装器，将响应内容复制一份到内存中
     */
    public static class TeeResponseWrapper extends HttpServletResponseWrapper {
        private FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
        private PrintWriter writer;
        private TeeServletOutputStream teeStream;
        
        public TeeResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called");
            }
            
            if (teeStream == null) {
                teeStream = new TeeServletOutputStream(getResponse().getOutputStream(), bos);
            }
            return teeStream;
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            if (teeStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called");
            }
            
            if (writer == null) {
                writer = new PrintWriter(new java.io.OutputStreamWriter(bos, getCharacterEncoding()));
            }
            return writer;
        }
        
        public byte[] toByteArray() throws IOException {
            if (writer != null) {
                writer.flush();
            }
            if (teeStream != null) {
                teeStream.flush();
            }
            return bos.toByteArray();
        }
    }
    
    /**
     * 同时写入到两个输出流的ServletOutputStream
     */
    private static class TeeServletOutputStream extends ServletOutputStream {
        private final ServletOutputStream original;
        private final FastByteArrayOutputStream copy;
        
        public TeeServletOutputStream(ServletOutputStream original, FastByteArrayOutputStream copy) {
            this.original = original;
            this.copy = copy;
        }
        
        @Override
        public void write(int b) throws IOException {
            original.write(b);
            copy.write(b);
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            original.write(b);
            copy.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            original.write(b, off, len);
            copy.write(b, off, len);
        }
        
        @Override
        public boolean isReady() {
            return original.isReady();
        }
        
        @Override
        public void setWriteListener(WriteListener listener) {
            original.setWriteListener(listener);
        }
    }
    
    /**
     * 内部使用的快速字节数组输出流
     */
    private static class FastByteArrayOutputStream extends java.io.OutputStream {
        private byte[] buffer = new byte[1024];
        private int size = 0;
        
        @Override
        public void write(int b) {
            ensureCapacity(size + 1);
            buffer[size++] = (byte) b;
        }
        
        @Override
        public void write(byte[] b) {
            write(b, 0, b.length);
        }
        
        @Override
        public void write(byte[] b, int off, int len) {
            ensureCapacity(size + len);
            System.arraycopy(b, off, buffer, size, len);
            size += len;
        }
        
        public byte[] toByteArray() {
            byte[] result = new byte[size];
            System.arraycopy(buffer, 0, result, 0, size);
            return result;
        }
        
        private void ensureCapacity(int minCapacity) {
            if (minCapacity > buffer.length) {
                int newCapacity = Math.max(buffer.length << 1, minCapacity);
                byte[] newBuffer = new byte[newCapacity];
                System.arraycopy(buffer, 0, newBuffer, 0, size);
                buffer = newBuffer;
            }
        }
    }
}