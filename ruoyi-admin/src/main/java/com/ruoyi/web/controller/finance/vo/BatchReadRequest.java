package com.ruoyi.web.controller.finance.vo;

import java.util.List;

/**
 * 批量标记已读请求对象
 * 
 * @author ruoyi
 */
public class BatchReadRequest {
    
    /** 用户ID */
    private Long userId;
    
    /** 通知ID列表 */
    private List<Long> ids;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}