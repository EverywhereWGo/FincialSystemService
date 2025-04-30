package com.ruoyi.system.service.finance.impl;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(FinNotificationServiceImpl.class);
    
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
        try {
            log.info("开始创建通知: userId={}, title={}, type={}", 
                    finNotification.getUserId(), finNotification.getTitle(), finNotification.getType());
            
            // 确保必要字段不为空
            if (finNotification.getCreateTime() == null) {
                finNotification.setCreateTime(new Date());
            }
            
            if (finNotification.getRead() == null) {
                finNotification.setRead(0); // 默认未读
            }
            
            if (finNotification.getCreateBy() == null || finNotification.getCreateBy().isEmpty()) {
                finNotification.setCreateBy("system");
            }
            
            if (finNotification.getDelFlag() == null || finNotification.getDelFlag().isEmpty()) {
                finNotification.setDelFlag("0");
            }
            
            // 打印完整的通知对象信息
            log.info("准备插入通知记录: userId={}, title={}, content长度={}, type={}, read={}, createBy={}, delFlag={}",
                    finNotification.getUserId(), finNotification.getTitle(), 
                    (finNotification.getContent() != null ? finNotification.getContent().length() : 0),
                    finNotification.getType(), finNotification.getRead(), 
                    finNotification.getCreateBy(), finNotification.getDelFlag());
            
            // 执行插入操作
            int result = finNotificationMapper.insertFinNotification(finNotification);
            
            if (result > 0) {
                log.info("通知创建成功: id={}, userId={}", finNotification.getId(), finNotification.getUserId());
            } else {
                log.error("通知创建失败: userId={}, title={}, result={}", 
                         finNotification.getUserId(), finNotification.getTitle(), result);
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建通知异常: userId={}, title={}, error={}",
                    finNotification.getUserId(), finNotification.getTitle(), e.getMessage());
            log.error("异常详情", e);
            
            // 重新尝试简化的插入
            try {
                log.info("尝试使用简化方式重新插入");
                // 确保只包含必要字段
                FinNotification simpleNotification = new FinNotification();
                simpleNotification.setUserId(finNotification.getUserId());
                simpleNotification.setTitle(finNotification.getTitle());
                
                // 如果内容太长，可能是问题所在，尝试截取
                String content = finNotification.getContent();
                if (content != null && content.length() > 2000) {
                    content = content.substring(0, 2000) + "...";
                }
                simpleNotification.setContent(content);
                
                simpleNotification.setType(finNotification.getType());
                simpleNotification.setRead(0);
                simpleNotification.setCreateBy("system");
                simpleNotification.setCreateTime(new Date());
                simpleNotification.setDelFlag("0");
                
                log.info("简化通知记录: userId={}, title={}, type={}", 
                        simpleNotification.getUserId(), simpleNotification.getTitle(), simpleNotification.getType());
                
                int retryResult = finNotificationMapper.insertFinNotification(simpleNotification);
                if (retryResult > 0) {
                    log.info("使用简化方式重新插入成功: id={}", simpleNotification.getId());
                    // 如果重试成功，设置原通知的ID
                    finNotification.setId(simpleNotification.getId());
                    return retryResult;
                } else {
                    log.error("使用简化方式重新插入也失败: result={}", retryResult);
                }
                return retryResult;
            } catch (Exception ex) {
                log.error("使用简化方式重新插入异常: {}", ex.getMessage(), ex);
                return 0;
            }
        }
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
        notification.setTitle("预算超出提醒");

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
        notification.setCreateBy("system");
        notification.setCreateTime(new Date());
        notification.setDelFlag("0");

        try {
            return insertFinNotification(notification);
        } catch (Exception e) {
            log.error("创建预算预警通知异常: userId={}, categoryName={}, error={}", 
                    userId, categoryName, e.getMessage(), e);
            return 0;
        }
    }
}
