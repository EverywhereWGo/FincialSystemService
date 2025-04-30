package com.ruoyi.system.service.finance.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.ruoyi.system.service.finance.util.FinanceThreadPoolUtil;

/**
 * 财务系统应用程序监听器
 * 
 * @author ruoyi
 */
@Component
public class FinanceApplicationListener implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LoggerFactory.getLogger(FinanceApplicationListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("财务系统应用程序关闭，正在关闭线程池资源...");
        
        // 关闭线程池
        FinanceThreadPoolUtil.shutdown();
        
        log.info("财务系统资源已关闭");
    }
} 