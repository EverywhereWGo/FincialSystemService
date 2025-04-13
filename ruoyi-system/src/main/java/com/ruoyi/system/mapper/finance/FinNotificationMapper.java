package com.ruoyi.system.mapper.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinNotification;

/**
 * 财务通知 数据层
 * 
 * @author ruoyi
 */
public interface FinNotificationMapper
{
    /**
     * 查询财务通知信息
     * 
     * @param id 通知ID
     * @return 通知信息
     */
    public FinNotification selectFinNotificationById(Long id);

    /**
     * 查询财务通知列表
     * 
     * @param finNotification 通知信息
     * @return 通知集合
     */
    public List<FinNotification> selectFinNotificationList(FinNotification finNotification);
    
    /**
     * 查询用户未读通知
     * 
     * @param userId 用户ID
     * @return 通知集合
     */
    public List<FinNotification> selectUnreadNotificationsByUserId(Long userId);

    /**
     * 新增财务通知
     * 
     * @param finNotification 通知信息
     * @return 结果
     */
    public int insertFinNotification(FinNotification finNotification);

    /**
     * 修改财务通知
     * 
     * @param finNotification 通知信息
     * @return 结果
     */
    public int updateFinNotification(FinNotification finNotification);
    
    /**
     * 标记通知为已读
     * 
     * @param id 通知ID
     * @return 结果
     */
    public int markAsRead(Long id);
    
    /**
     * 标记用户所有通知为已读
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int markAllAsRead(Long userId);

    /**
     * 删除财务通知
     * 
     * @param id 通知ID
     * @return 结果
     */
    public int deleteFinNotificationById(Long id);

    /**
     * 批量删除财务通知
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFinNotificationByIds(Long[] ids);
    
    /**
     * 批量标记通知为已读
     * 
     * @param userId 用户ID
     * @param ids 通知ID列表
     * @return 结果
     */
    public int batchMarkAsRead(Long userId, List<Long> ids);
}