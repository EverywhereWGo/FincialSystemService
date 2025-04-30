package com.ruoyi.system.service.finance.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.finance.FinBudget;
import com.ruoyi.system.domain.finance.FinCategory;
import com.ruoyi.system.domain.finance.FinNotification;
import com.ruoyi.system.domain.finance.FinTransaction;
import com.ruoyi.system.mapper.finance.FinBudgetMapper;
import com.ruoyi.system.mapper.finance.FinCategoryMapper;
import com.ruoyi.system.service.finance.IFinBudgetService;
import com.ruoyi.system.service.finance.IFinNotificationService;
import com.ruoyi.system.mapper.finance.FinNotificationMapper;

/**
 * 预算预警处理任务
 * 
 * @author ruoyi
 */
@Component
public class BudgetWarningTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(BudgetWarningTask.class);

    private FinTransaction transaction;
    
    @Autowired
    private IFinBudgetService finBudgetService;
    
    @Autowired(required = false)
    private IFinNotificationService finNotificationService;
    
    @Autowired
    private FinBudgetMapper finBudgetMapper;
    
    @Autowired
    private FinCategoryMapper finCategoryMapper;
    
    @Autowired
    private FinNotificationMapper finNotificationMapper;

    public BudgetWarningTask() {
    }

    public BudgetWarningTask(FinTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void run() {
        try {
            checkBudgetWarning();
        } catch (Exception e) {
            log.error("预算预警处理异常", e);
        }
    }

    /**
     * 检查预算预警
     */
    private void checkBudgetWarning() {
        // 只处理支出类型的交易
        if (transaction == null || transaction.getType() == null || transaction.getType() != 1) {
            log.info("非支出类型交易，不处理预算预警");
            return;
        }

        try {
            // 获取交易的年月
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(transaction.getTransactionTime());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar月份从0开始
            
            log.info("开始检查预算预警: userId={}, categoryId={}, year={}, month={}", 
                     transaction.getUserId(), transaction.getCategoryId(), year, month);
            
            // 查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("userId", transaction.getUserId());
            params.put("categoryId", transaction.getCategoryId());
            params.put("year", year);
            params.put("month", month);
            
            // 查询预算
            FinBudget budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
            
            // 如果没有设置预算，则不处理
            if (budget == null) {
                log.info("未设置预算，不处理预警: userId={}, categoryId={}, year={}, month={}", 
                         transaction.getUserId(), transaction.getCategoryId(), year, month);
                return;
            }
            
            // 如果已经预警过且不需要提醒，则不处理
            if (budget.getWarned() || !budget.getNotifyEnable()) {
                log.info("预算已预警过或不需要提醒: userId={}, categoryId={}, year={}, month={}, warned={}, notifyEnable={}", 
                         transaction.getUserId(), transaction.getCategoryId(), year, month, 
                         budget.getWarned(), budget.getNotifyEnable());
                return;
            }
            
            // 更新预算使用情况
            log.info("更新预算使用情况: userId={}, categoryId={}, year={}, month={}", 
                    transaction.getUserId(), transaction.getCategoryId(), year, month);
            int updateResult = finBudgetService.updateBudgetUsage(transaction.getUserId(), transaction.getCategoryId(), year, month);
            log.info("更新预算使用情况结果: {}", updateResult);
            
            // 重新查询更新后的预算数据
            budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
            if (budget == null) {
                log.error("预算数据异常，无法获取更新后的预算: userId={}, categoryId={}, year={}, month={}", 
                          transaction.getUserId(), transaction.getCategoryId(), year, month);
                return;
            }
            
            log.info("预算使用情况: amount={}, usedAmount={}, usedPercentage={}, warningThreshold={}", 
                     budget.getAmount(), budget.getUsedAmount(), budget.getUsedPercentage(), budget.getWarningThreshold());
            
            // 检查是否超过预警阈值
            if (budget.getUsedPercentage().compareTo(budget.getWarningThreshold()) >= 0) {
                log.info("预算使用百分比({})已超过预警阈值({}), 开始创建通知", 
                        budget.getUsedPercentage(), budget.getWarningThreshold());
                
                // 获取分类名称
                FinCategory category = finCategoryMapper.selectFinCategoryById(transaction.getCategoryId());
                String categoryName = category != null ? category.getName() : "未知分类";
                
                // 生成通知内容
                String content = generateWarningContent(transaction, categoryName, budget, year, month);
                
                // 创建通知对象
                FinNotification notification = new FinNotification();
                notification.setUserId(transaction.getUserId());
                notification.setTitle("预算超出提醒");
                notification.setContent(content);
                notification.setType("budget_warning");
                notification.setRead(0); // 未读
                notification.setCreateBy("system");
                notification.setCreateTime(new Date()); // 设置当前时间
                notification.setDelFlag("0"); // 确保删除标志设置正确
                
                log.info("准备保存预算预警通知: userId={}, title={}, type={}, read={}",
                        notification.getUserId(), notification.getTitle(), 
                        notification.getType(), notification.getRead());
                
                // 保存通知 - 尝试多种方式确保能成功插入
                boolean saveSuccess = false;
                int result = 0;
                
                // 方式1: 使用Service保存
                if (!saveSuccess && finNotificationService != null) {
                    try {
                        log.info("使用NotificationService保存通知");
                        result = finNotificationService.insertFinNotification(notification);
                        if (result > 0) {
                            saveSuccess = true;
                            log.info("通过Service保存预算预警通知成功: id={}", notification.getId());
                        } else {
                            log.warn("通过Service保存预算预警通知失败: result={}", result);
                        }
                    } catch (Exception e) {
                        log.error("使用Service保存通知失败: {}", e.getMessage(), e);
                    }
                }
                
                // 方式2: 使用Mapper保存
                if (!saveSuccess && finNotificationMapper != null) {
                    try {
                        log.info("使用finNotificationMapper保存通知");
                        // 确保创建时间已设置
                        if (notification.getCreateTime() == null) {
                            notification.setCreateTime(new Date());
                        }
                        result = finNotificationMapper.insertFinNotification(notification);
                        if (result > 0) {
                            saveSuccess = true;
                            log.info("通过Mapper保存预算预警通知成功: id={}", notification.getId());
                        } else {
                            log.warn("通过Mapper保存预算预警通知失败: result={}", result);
                        }
                    } catch (Exception ex) {
                        log.error("使用Mapper保存通知失败: {}", ex.getMessage(), ex);
                    }
                }
                
                // 方式3: 创建简化通知对象再次尝试
                if (!saveSuccess && finNotificationMapper != null) {
                    try {
                        log.info("使用简化通知对象再次尝试");
                        FinNotification simpleNotification = new FinNotification();
                        simpleNotification.setUserId(transaction.getUserId());
                        simpleNotification.setTitle("预算超出提醒");
                        simpleNotification.setContent(content);
                        simpleNotification.setType("budget_warning");
                        simpleNotification.setRead(0);
                        simpleNotification.setCreateBy("system");
                        simpleNotification.setCreateTime(new Date());
                        simpleNotification.setDelFlag("0");
                        
                        result = finNotificationMapper.insertFinNotification(simpleNotification);
                        if (result > 0) {
                            saveSuccess = true;
                            notification.setId(simpleNotification.getId()); // 更新ID
                            log.info("通过简化对象保存预算预警通知成功: id={}", notification.getId());
                        } else {
                            log.warn("通过简化对象保存预算预警通知失败: result={}", result);
                        }
                    } catch (Exception ex) {
                        log.error("使用简化对象保存通知失败: {}", ex.getMessage(), ex);
                    }
                }
                
                if (saveSuccess) {
                    log.info("预算预警通知创建成功: ID={}, userId={}, title={}", 
                            notification.getId(), notification.getUserId(), notification.getTitle());
                    
                    // 更新预算预警状态
                    budget.setWarned(true);
                    int warningResult = finBudgetMapper.updateBudgetWarningStatus(budget);
                    log.info("更新预算预警状态结果: {}", warningResult);
                    
                    log.info("预算预警通知已生成: userId={}, categoryId={}, month={}-{}", 
                             transaction.getUserId(), transaction.getCategoryId(), year, month);
                } else {
                    log.error("所有尝试创建预算预警通知均失败");
                }
            } else {
                log.info("预算未超过预警阈值: usedPercentage={}%, warningThreshold={}%", 
                         budget.getUsedPercentage(), budget.getWarningThreshold());
            }
        } catch (Exception e) {
            log.error("预算预警处理异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 生成预警内容
     */
    private String generateWarningContent(FinTransaction transaction, String categoryName, 
                                          FinBudget budget, int year, int month) {
        // 格式化日期时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        String dateTimeStr = dateFormat.format(new Date(transaction.getTransactionTime()));
        
        // 构建通知内容（按照要求的格式）
        StringBuilder content = new StringBuilder();
        content.append("在").append(dateTimeStr);
        content.append("，你有一笔").append(categoryName).append("类型的交易记录");
        content.append("，超出了").append(year).append("年").append(month).append("月的");
        content.append(categoryName).append("类型的预算，请注意！");
        
        // 添加详细信息
        content.append("\n\n预算金额: ").append(budget.getAmount()).append("元");
        content.append("\n已使用: ").append(budget.getUsedAmount()).append("元");
        content.append("\n使用比例: ").append(budget.getUsedPercentage()).append("%");
        content.append("\n预警阈值: ").append(budget.getWarningThreshold()).append("%");
        
        log.debug("生成的预警内容: {}", content.toString());
        return content.toString();
    }
    
    public void setTransaction(FinTransaction transaction) {
        this.transaction = transaction;
    }
} 