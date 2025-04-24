package com.ruoyi.framework.filter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;

/**
 * API日志记录过滤器
 * 记录请求和响应的详细信息
 */
public class ApiLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger("api-access");
    private static final boolean DEBUG = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("API日志记录过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 检查Content-Type，如果是multipart/form-data，则直接传递，不进行包装和日志记录
        String contentType = httpRequest.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
            // 对于文件上传请求，直接放行，不进行请求包装
            log.info("检测到文件上传请求，跳过请求体日志记录: {}", httpRequest.getRequestURI());
            chain.doFilter(request, response);
            return;
        }
        
        // 生成唯一请求ID用于关联请求和响应日志
        String requestId = UUID.randomUUID().toString();
        
        // 包装请求以便多次读取Body
        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpRequest);
        // 包装响应以便捕获返回内容
        BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();
        
        // 记录请求信息
        logRequest(bufferedRequest, requestId);
        
        try {
            // 继续处理请求
            chain.doFilter(bufferedRequest, bufferedResponse);
        } finally {
            // 记录响应信息
            logResponse(bufferedRequest, bufferedResponse, requestId, System.currentTimeMillis() - startTime);
            
            // 确保响应内容写回客户端
            byte[] content = bufferedResponse.getContent();
            if (content.length > 0) {
                OutputStream out = response.getOutputStream();
                out.write(content);
                out.flush();
            }
        }
    }

    @Override
    public void destroy() {
        log.info("API日志记录过滤器销毁");
    }
    
    /**
     * 记录请求信息
     */
    private void logRequest(BufferedRequestWrapper request, String requestId) {
        try {
            // 获取请求相关信息
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String requestUrl = queryString == null ? uri : uri + "?" + queryString;
            String clientIp = IpUtils.getIpAddr(request);
            Map<String, String> headers = getRequestHeaders(request);
            
            // 获取请求体，注意可能是空的或者不是JSON格式
            String requestBody = "";
            if (request.getRequestBody().length > 0) {
                try {
                    requestBody = new String(request.getRequestBody(), getCharset(request.getCharacterEncoding()));
                } catch (Exception e) {
                    requestBody = "[无法解析的请求内容]";
                    log.warn("解析请求体失败", e);
                }
            }
            
            // 构造日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n接口请求 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            logMessage.append("\n请求ID: ").append(requestId);
            logMessage.append("\n请求路径: ").append(requestUrl);
            logMessage.append("\n请求方法: ").append(method);
            logMessage.append("\n客户端IP: ").append(clientIp);
            logMessage.append("\n请求头信息: ").append(JSON.toJSONString(headers));
            
            if (StringUtils.isNotEmpty(requestBody)) {
                logMessage.append("\n请求内容: ").append(requestBody);
            }
            
            logMessage.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            
            // 输出日志
            String logContent = logMessage.toString();
            log.info(logContent);
            if (DEBUG) System.out.println(logContent);
        } catch (Exception e) {
            log.error("记录请求日志发生异常", e);
        }
    }
    
    /**
     * 记录响应信息
     */
    private void logResponse(HttpServletRequest request, BufferedResponseWrapper response, String requestId, long duration) {
        try {
            // 获取请求相关信息
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String requestUrl = queryString == null ? uri : uri + "?" + queryString;
            
            // 获取响应内容
            byte[] content = response.getContent();
            String responseBody = "";
            
            if (content.length > 0) {
                try {
                    // 从响应头或请求头获取字符集
                    String charset = getCharsetFromContentType(response.getContentType());
                    if (charset == null) {
                        charset = request.getCharacterEncoding();
                    }
                    
                    responseBody = new String(content, getCharset(charset));
                } catch (Exception e) {
                    responseBody = "[无法解析的响应内容]";
                    log.warn("解析响应体失败", e);
                }
            }
            
            // 构造日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n接口响应 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            logMessage.append("\n请求ID: ").append(requestId);
            logMessage.append("\n请求路径: ").append(requestUrl);
            
            if (StringUtils.isNotEmpty(responseBody)) {
                // 限制响应体日志长度，避免日志过大
                if (responseBody.length() > 1000) {
                    logMessage.append("\n响应内容: ").append(responseBody.substring(0, 1000)).append("... (已截断)");
                } else {
                    logMessage.append("\n响应内容: ").append(responseBody);
                }
            } else {
                logMessage.append("\n响应内容: [empty]");
            }
            
            logMessage.append("\n响应状态: ").append(response.getStatus());
            logMessage.append("\n响应类型: ").append(response.getContentType());
            logMessage.append("\n响应时间: ").append(duration).append("ms");
            logMessage.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            
            // 输出日志
            String logContent = logMessage.toString();
            log.info(logContent);
            if (DEBUG) System.out.println(logContent);
            
            // 打印分隔线，便于查看
            if (DEBUG) System.out.println("================================================================");
        } catch (Exception e) {
            log.error("记录响应日志发生异常", e);
        }
    }
    
    /**
     * 从Content-Type中提取字符集
     */
    private String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        
        // 例如 "text/html; charset=UTF-8" 或 "application/json;charset=UTF-8"
        String[] parts = contentType.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.toLowerCase().startsWith("charset=")) {
                return part.substring("charset=".length());
            }
        }
        
        return null;
    }
    
    /**
     * 获取Charset对象，处理字符集异常
     */
    private Charset getCharset(String charsetName) {
        if (StringUtils.isEmpty(charsetName)) {
            return StandardCharsets.UTF_8;
        }
        
        try {
            return Charset.forName(charsetName);
        } catch (Exception e) {
            log.warn("不支持的字符集: " + charsetName + ", 使用UTF-8替代");
            return StandardCharsets.UTF_8;
        }
    }
    
    /**
     * 获取请求头信息
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // 过滤掉敏感的请求头信息
                if (!"cookie".equalsIgnoreCase(headerName) && 
                    !"authorization".equalsIgnoreCase(headerName)) {
                    headerMap.put(headerName, request.getHeader(headerName));
                }
            }
        }
        return headerMap;
    }
    
    /**
     * 可以多次读取请求体的HttpServletRequest包装器
     */
    public static class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private final String encoding;
        private byte[] rawData;
        
        public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            
            // 获取字符编码
            encoding = request.getCharacterEncoding();
            if (encoding == null) {
                request.setCharacterEncoding("UTF-8");
            }
            
            // 读取原始请求体
            rawData = readInputStream(request.getInputStream());
        }
        
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new BufferedServletInputStream(rawData);
        }
        
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new BufferedServletInputStream(rawData), getCharacterEncoding()));
        }
        
        public byte[] getRequestBody() {
            return rawData;
        }
        
        private byte[] readInputStream(ServletInputStream is) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[1024];
            
            while ((read = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            
            return buffer.toByteArray();
        }
    }
    
    /**
     * 用于捕获HTTP响应内容的包装器
     */
    public static class BufferedResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream buffer;
        private final ServletOutputStream outputStream;
        private final PrintWriter writer;
        private int httpStatus;
        
        public BufferedResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
            buffer = new ByteArrayOutputStream();
            outputStream = new BufferedServletOutputStream(buffer);
            writer = new PrintWriter(new java.io.OutputStreamWriter(buffer, getCharacterEncoding() != null ? getCharacterEncoding() : "UTF-8"), true);
            httpStatus = 200; // 默认状态码
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }
        
        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            }
            if (outputStream != null) {
                outputStream.flush();
            }
        }
        
        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            httpStatus = sc;
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }
        
        public int getStatus() {
            return httpStatus;
        }
        
        public byte[] getContent() throws IOException {
            flushBuffer();
            return buffer.toByteArray();
        }
    }
    
    /**
     * 字节数组输入流的ServletInputStream实现
     */
    private static class BufferedServletInputStream extends ServletInputStream {
        private final java.io.ByteArrayInputStream inputStream;
        
        public BufferedServletInputStream(byte[] rawData) {
            inputStream = new java.io.ByteArrayInputStream(rawData);
        }
        
        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }
        
        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }
        
        @Override
        public boolean isReady() {
            return true;
        }
        
        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("不支持ReadListener");
        }
    }
    
    /**
     * 基于ByteArrayOutputStream的ServletOutputStream实现
     */
    private static class BufferedServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream buffer;
        
        public BufferedServletOutputStream(ByteArrayOutputStream buffer) {
            this.buffer = buffer;
        }
        
        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }
        
        @Override
        public boolean isReady() {
            return true;
        }
        
        @Override
        public void setWriteListener(WriteListener listener) {
            throw new UnsupportedOperationException("不支持WriteListener");
        }
    }
}