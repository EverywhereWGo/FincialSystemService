package com.ruoyi.system.service.finance.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinNotificationMapper;
import com.ruoyi.system.domain.finance.FinNotification;
import com.ruoyi.system.service.finance.IFinNotificationService;

/**
 * 财务通知 服务层实现
 *
 * @author ruoyi
 */
@Service
public class FinNotificationServiceImpl implements IFinNotificationService
{
    @Autowired
    private FinNotificationMapper finNotificationMapper;

    /**
     * 查询财务通知信息
     *
     * @param id 通知ID
     * @return 通知信息
     */
    @Override
    public FinNotification selectFinNotificationById(Long id)
    {
        return finNotificationMapper.selectFinNotificationById(id);
    }

    /**
     * 查询财务通知列表
     *
     * @param finNotification 通知信息
     * @return 通知集合
     */
    @Override
    public List<FinNotification> selectFinNotificationList(FinNotification finNotification)
    {
        return finNotificationMapper.selectFinNotificationList(finNotification);
    }

    /**
     * 查询用户未读通知
     *
     * @param userId 用户ID
     * @return 通知集合
     */
    @Override
    public List<FinNotification> selectUnreadNotificationsByUserId(Long userId)
    {
        return finNotificationMapper.selectUnreadNotificationsByUserId(userId);
    }

    /**
     * 新增财务通知
     *
     * @param finNotification 通知信息
     * @return 结果
     */
    @Override
    public int insertFinNotification(FinNotification finNotification)
    {
        return finNotificationMapper.insertFinNotification(finNotification);
    }

    /**
     * 修改财务通知
     *
     * @param finNotification 通知信息
     * @return 结果
     */
    @Override
    public int updateFinNotification(FinNotification finNotification)
    {
        return finNotificationMapper.updateFinNotification(finNotification);
    }

    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     * @return 结果
     */
    @Override
    public int markAsRead(Long id)
    {
        return finNotificationMapper.markAsRead(id);
    }

    /**
     * 标记用户所有通知为已读
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int markAllAsRead(Long userId)
    {
        return finNotificationMapper.markAllAsRead(userId);
    }

    /**
     * 删除财务通知对象
     *
     * @param id 通知ID
     * @return 结果
     */
    @Override
    public int deleteFinNotificationById(Long id)
    {
        return finNotificationMapper.deleteFinNotificationById(id);
    }

    /**
     * 批量删除财务通知对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFinNotificationByIds(Long[] ids)
    {
        return finNotificationMapper.deleteFinNotificationByIds(ids);
    }

    /**
     * 批量标记通知为已读
     *
     * @param userId 用户ID
     * @param ids 通知ID列表
     * @return 结果
     */
    @Override
    public int batchMarkAsRead(Long userId, List<Long> ids)
    {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return finNotificationMapper.batchMarkAsRead(userId, ids);
    }

    /**
     * 新增预算预警通知
     *
     * @param userId 用户ID
     * @param budgetId 预算ID
     * @param categoryName 分类名称
     * @param percentage 使用百分比
     * @return 结果
     */
    @Override
    public int insertBudgetWarningNotification(Long userId, Long budgetId, String categoryName, double percentage)
    {
        FinNotification notification = new FinNotification();
        notification.setUserId(userId);
        notification.setTitle("预算使用预警");

        StringBuilder content = new StringBuilder();
        content.append("您的");
        if (categoryName != null) {
            content.append(categoryName);
        } else {
            content.append("总预算");
        }
        content.append("已使用 ").append(String.format("%.2f", percentage)).append("%，");

        if (percentage >= 100) {
            content.append("已超出预算限制。请及时调整支出计划。");
        } else {
            content.append("接近预算限制。请注意控制支出。");
        }

        notification.setContent(content.toString());
        notification.setType("budget_warning");
        notification.setRead(0);

        return finNotificationMapper.insertFinNotification(notification);
    }
}
