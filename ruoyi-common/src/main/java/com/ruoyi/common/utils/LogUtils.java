package com.ruoyi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理并记录日志文件
 * 
 * @author ruoyi
 */
public class LogUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);
    
    /**
     * 获取日志记录器
     */
    public static Logger getLogger()
    {
        return LOGGER;
    }
    
    public static String getBlock(Object msg)
    {
        if (msg == null)
        {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
