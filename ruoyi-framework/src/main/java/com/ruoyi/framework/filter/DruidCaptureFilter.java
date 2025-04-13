package com.ruoyi.framework.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DruidCaptureFilter - 用于解决与Druid WebStatFilter的响应包装器冲突
 * 在Druid过滤器之前拦截，以确保能正确读取响应内容
 */
public class DruidCaptureFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(DruidCaptureFilter.class);
    
    // 需要特殊处理的接口路径
    private static final List<String> SPECIAL_PATHS = Arrays.asList("/login", "/logout", "/captchaImage");
    private static final AtomicBoolean LOG_ONCE = new AtomicBoolean(false);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        boolean isSpecialPath = isSpecialPath(path);
        
        if (isSpecialPath) {
            if (!LOG_ONCE.getAndSet(true)) {
                log.info("DruidCaptureFilter初始化成功，将处理特殊路径: {}", SPECIAL_PATHS);
            }
            
            CaptureResponseWrapper responseWrapper = new CaptureResponseWrapper(httpResponse);
            
            try {
                chain.doFilter(request, responseWrapper);
            } finally {
                try {
                    // 获取响应内容
                    byte[] content = responseWrapper.getContent();
                    if (content.length > 0) {
                        // 将内容保存到请求属性，供后续拦截器读取
                        request.setAttribute("capturedResponse", new String(content, responseWrapper.getCharacterEncoding()));
                        
                        // 写入原始响应
                        response.getOutputStream().write(content);
                    }
                } catch (Exception e) {
                    log.error("DruidCaptureFilter处理响应失败", e);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
    
    private boolean isSpecialPath(String path) {
        return SPECIAL_PATHS.stream().anyMatch(p -> path.equals(p) || path.startsWith(p + "/"));
    }
    
    /**
     * 自定义响应包装器，捕获全部响应内容
     */
    private static class CaptureResponseWrapper extends HttpServletResponseWrapper {
        private CaptureServletOutputStream outputStream;
        private PrintWriter writer;
        private byte[] content;

        public CaptureResponseWrapper(HttpServletResponse response) {
            super(response);
            outputStream = new CaptureServletOutputStream(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PrintWriter(outputStream.getWriter());
            }
            return writer;
        }

        public byte[] getContent() {
            if (writer != null) {
                writer.flush();
            }
            
            if (content == null) {
                content = outputStream.getContent();
            }
            return content;
        }
    }
    
    /**
     * 捕获所有写入的数据的输出流
     */
    private static class CaptureServletOutputStream extends ServletOutputStream {
        private final HttpServletResponse response;
        private final CapturingByteArrayOutputStream capturing;
        private ServletOutputStream original;
        private PrintWriter writer;

        public CaptureServletOutputStream(HttpServletResponse response) {
            this.response = response;
            this.capturing = new CapturingByteArrayOutputStream();
        }

        private ServletOutputStream getOriginal() throws IOException {
            if (original == null) {
                original = response.getOutputStream();
            }
            return original;
        }
        
        public PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PrintWriter(new java.io.OutputStreamWriter(capturing, response.getCharacterEncoding()));
            }
            return writer;
        }

        @Override
        public boolean isReady() {
            try {
                return getOriginal().isReady();
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            try {
                getOriginal().setWriteListener(listener);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void write(int b) throws IOException {
            capturing.write(b);
            getOriginal().write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            capturing.write(b);
            getOriginal().write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            capturing.write(b, off, len);
            getOriginal().write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            capturing.flush();
            getOriginal().flush();
        }

        @Override
        public void close() throws IOException {
            capturing.close();
            getOriginal().close();
        }

        public byte[] getContent() {
            return capturing.toByteArray();
        }
    }
    
    /**
     * 扩展的ByteArrayOutputStream，用于捕获所有写入的数据
     */
    private static class CapturingByteArrayOutputStream extends java.io.ByteArrayOutputStream {
        public CapturingByteArrayOutputStream() {
            super(1024);
        }
    }
}