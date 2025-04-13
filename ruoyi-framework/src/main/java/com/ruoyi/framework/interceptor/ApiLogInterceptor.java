package com.ruoyi.framework.interceptor;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口日志拦截器
 * 记录每个接口的调用信息，包括请求参数和响应结果
 */
@Component
public class ApiLogInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger("api-access");
    private static final boolean DEBUG = true;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求处理前记录时间戳，后续用于计算接口响应时间
        request.setAttribute("startTime", System.currentTimeMillis());
        
        try {
            // 获取请求相关信息
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String requestUrl = queryString == null ? uri : uri + "?" + queryString;
            String clientIp = IpUtils.getIpAddr(request);
            Map<String, String> headers = getRequestHeaders(request);
            
            // 获取请求参数
            String requestBody = "";
            if (request instanceof ContentCachingRequestWrapper) {
                requestBody = getRequestBody((ContentCachingRequestWrapper)request);
            } else if (DEBUG) {
                log.debug("请求未被包装为ContentCachingRequestWrapper: {}", requestUrl);
            }
            
            // 构造日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n接口请求 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            logMessage.append("\n请求路径: ").append(requestUrl);
            logMessage.append("\n请求方法: ").append(method);
            logMessage.append("\n客户端IP: ").append(clientIp);
            logMessage.append("\n请求头信息: ").append(JSON.toJSONString(headers));
            
            if (StringUtils.isNotEmpty(requestBody)) {
                logMessage.append("\n请求参数: ").append(requestBody);
            }
            
            logMessage.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            
            // 同时输出到控制台和日志文件
            String logContent = logMessage.toString();
            log.info(logContent);
            if (DEBUG) System.out.println(logContent);
        } catch (Exception e) {
            log.error("记录请求日志发生异常", e);
            if (DEBUG) System.err.println("记录请求日志发生异常: " + e.getMessage());
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // 获取请求开始时间
            Long startTime = (Long) request.getAttribute("startTime");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 获取请求相关信息
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String requestUrl = queryString == null ? uri : uri + "?" + queryString;
            
            // 获取响应信息 - 从不同来源尝试
            String responseBody = "";
            
            // 1. 检查DruidCaptureFilter是否捕获了响应
            Object capturedResponse = request.getAttribute("capturedResponse");
            if (capturedResponse instanceof String) {
                responseBody = (String) capturedResponse;
                if (DEBUG) log.debug("从DruidCaptureFilter获取到响应内容, 长度: {}", responseBody.length());
            }
            // 2. 检查特殊接口的请求属性中是否有响应内容(ApiResponseCacheFilter放置)
            else {
                Object cachedResponse = request.getAttribute("responseContent");
                if (cachedResponse instanceof String) {
                    responseBody = (String) cachedResponse;
                    if (DEBUG) log.debug("从ApiResponseCacheFilter获取到响应内容, 长度: {}", responseBody.length());
                } 
                // 3. 尝试从ContentCachingResponseWrapper获取
                else if (response instanceof ContentCachingResponseWrapper) {
                    responseBody = getResponseBody((ContentCachingResponseWrapper)response);
                    if (responseBody.length() > 0 && DEBUG) {
                        log.debug("从ContentCachingResponseWrapper获取到响应内容, 长度: {}", responseBody.length());
                    }
                }
            }
            
            // 构造日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n接口响应 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            logMessage.append("\n请求路径: ").append(requestUrl);
            
            if (StringUtils.isNotEmpty(responseBody)) {
                // 限制响应体日志长度，避免日志过大
                if (responseBody.length() > 1000) {
                    logMessage.append("\n响应内容: ").append(responseBody.substring(0, 1000)).append("... (已截断)");
                } else {
                    logMessage.append("\n响应内容: ").append(responseBody);
                }
            } else {
                logMessage.append("\n响应内容: [empty] - 可能是直接写入响应流或响应未被正确包装");
                if (DEBUG) {
                    logMessage.append("\n响应类型: ").append(response.getClass().getName());
                    logMessage.append("\n响应ContentType: ").append(response.getContentType());
                }
            }
            
            logMessage.append("\n响应时间: ").append(duration).append("ms");
            logMessage.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            
            // 同时输出到控制台和日志文件
            String logContent = logMessage.toString();
            log.info(logContent);
            if (DEBUG) System.out.println(logContent);
            
            // 打印分隔线，便于查看
            if (DEBUG) System.out.println("================================================================");
        } catch (Exception e) {
            log.error("记录接口日志发生异常", e);
            if (DEBUG) System.err.println("记录接口日志发生异常: " + e.getMessage());
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
     * 获取请求内容体
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("读取请求内容失败", e);
                return "[无法读取请求内容]";
            }
        }
        return "";
    }
    
    /**
     * 获取响应内容体
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("读取响应内容失败", e);
            return "[无法读取响应内容: " + e.getMessage() + "]";
        }
        return "";
    }
}