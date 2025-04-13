package com.ruoyi.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API日志配置
 */
@Configuration
public class ApiLogConfig implements WebMvcConfigurer {
    // 所有过滤器注册已移至FilterConfig类，此类保持为空防止影响其他代码
}