package com.ruoyi.framework.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 编码配置
 */
@Configuration
public class CharacterEncodingConfig {

    /**
     * 配置字符编码过滤器
     */
    @Bean(name = "myCharacterEncodingFilter")
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("characterEncodingFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}