package com.ruoyi.system.service.finance.test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.system.domain.finance.FinBudget;
import com.ruoyi.system.domain.finance.FinCategory;
import com.ruoyi.system.domain.finance.FinNotification;
import com.ruoyi.system.domain.finance.FinTransaction;
import com.ruoyi.system.mapper.finance.FinBudgetMapper;
import com.ruoyi.system.mapper.finance.FinCategoryMapper;
import com.ruoyi.system.mapper.finance.FinNotificationMapper;
import com.ruoyi.system.mapper.finance.FinTransactionMapper;
import com.ruoyi.system.service.finance.IFinNotificationService;
import com.ruoyi.system.service.finance.task.BudgetWarningTask;

/**
 * 预算预警测试类
 * 
 * @author ruoyi
 */
@Component
public class BudgetWarningTest {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetWarningTest.class);
    
    @Autowired
    private FinBudgetMapper finBudgetMapper;
    
    @Autowired
    private FinTransactionMapper finTransactionMapper;
    
    @Autowired
    private FinNotificationMapper finNotificationMapper;
    
    @Autowired
    private FinCategoryMapper finCategoryMapper;
    
    @Autowired
    private BudgetWarningTask budgetWarningTask;
    
    @Autowired(required = false)
    private IFinNotificationService finNotificationService;
    
    /**
     * 测试预算预警功能
     */
    public void testBudgetWarning() {
        log.info("开始测试预算预警功能");
        
        try {
            // 设置当前年月
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar月份从0开始
            
            log.info("当前测试的年月: {}-{}", year, month);
            
            // 1. 首先检查测试用户(ID=1)是否存在相应的预算
            Map<String, Object> params = new HashMap<>();
            params.put("userId", 1L);
            params.put("categoryId", 4L); // 住房类别
            params.put("year", year);
            params.put("month", month);
            
            FinBudget budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
            if (budget == null) {
                log.info("当前用户在{}-{}月没有住房类别的预算，将创建一个测试预算", year, month);
                
                // 创建测试预算
                budget = new FinBudget();
                budget.setUserId(1L);
                budget.setCategoryId(4L);
                budget.setYear(year);
                budget.setMonth(month);
                budget.setAmount(new BigDecimal("1000.00"));  // 预算金额1000元
                budget.setUsedAmount(new BigDecimal("0.00")); // 已使用金额初始为0
                budget.setWarningThreshold(new BigDecimal("80.00")); // 预警阈值80%
                budget.setUsedPercentage(new BigDecimal("0.00")); // 已使用百分比初始为0
                budget.setWarned(false); // 未预警
                budget.setNotifyEnable(true); // 启用通知
                budget.setCreateBy("system_test");
                
                // 保存预算
                finBudgetMapper.insertFinBudget(budget);
                log.info("创建测试预算成功: {}", budget);
            } else {
                log.info("找到现有预算: {}", budget);
                
                // 如果预算已被预警过，重置预警状态以便能再次测试
                if (budget.getWarned()) {
                    log.info("重置预算预警状态以便再次测试");
                    budget.setWarned(false);
                    finBudgetMapper.updateBudgetWarningStatus(budget);
                }
                
                // 确保预算启用了通知
                if (!budget.getNotifyEnable()) {
                    log.info("启用预算通知");
                    budget.setNotifyEnable(true);
                    finBudgetMapper.updateFinBudget(budget);
                }
            }
            
            // 2. 查询分类信息
            FinCategory category = finCategoryMapper.selectFinCategoryById(4L);
            String categoryName = category != null ? category.getName() : "住房";
            log.info("测试使用的分类: {}", categoryName);
            
            // 3. 计算应该添加的交易金额，使其恰好触发预警
            // 假设预算是1000元，预警阈值是80%，那么需要交易金额为800.01元才会触发
            BigDecimal threshold = budget.getWarningThreshold().divide(new BigDecimal("100"));
            BigDecimal warningAmount = budget.getAmount().multiply(threshold);
            
            // 如果已使用金额接近或超过预警金额，尝试将其重置
            if (budget.getUsedAmount().compareTo(warningAmount) >= 0) {
                log.info("当前已使用金额({})已接近或超过预警金额({}), 尝试重置", budget.getUsedAmount(), warningAmount);
                
                // 在实际场景中不会这样做，这里只是为了测试
                budget.setUsedAmount(new BigDecimal("0.00"));
                budget.setUsedPercentage(new BigDecimal("0.00"));
                finBudgetMapper.updateBudgetUsage(budget);
                log.info("重置已使用金额成功");
            }
            
            // 计算需要添加的交易金额，以触发预警
            BigDecimal transactionAmount;
            if (budget.getUsedAmount().compareTo(BigDecimal.ZERO) > 0) {
                // 如果已有使用金额，计算还需多少触发预警
                transactionAmount = warningAmount.subtract(budget.getUsedAmount()).add(new BigDecimal("0.01"));
                if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    // 确保至少有一点金额
                    transactionAmount = new BigDecimal("1.00");
                }
            } else {
                // 如果没有使用金额，直接设置为略高于预警金额
                transactionAmount = warningAmount.add(new BigDecimal("0.01"));
            }
            
            log.info("创建测试交易记录: 金额={}, 预警阈值金额={}", transactionAmount, warningAmount);
            
            // 创建一个测试交易记录
            FinTransaction transaction = new FinTransaction();
            transaction.setUserId(1L);
            transaction.setCategoryId(4L); // 住房类别
            transaction.setAmount(transactionAmount);
            transaction.setType(1); // 支出类型
            transaction.setTransactionTime(calendar.getTimeInMillis());
            transaction.setNote("预算预警测试交易");
            transaction.setCreateBy("system_test");
            
            log.info("测试交易记录: userId={}, categoryId={}, amount={}, time={}", 
                     transaction.getUserId(), transaction.getCategoryId(), 
                     transaction.getAmount(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(transaction.getTransactionTime())));
            
            // 4. 清理旧的测试通知
            try {
                // 查询并删除之前可能存在的测试通知
                FinNotification queryParam = new FinNotification();
                queryParam.setUserId(1L);
                queryParam.setType("budget_warning");
                // 如果有finNotificationMapper且实现了方法，可以尝试清理
                // 这里不做实际清理，以免影响功能测试
            } catch (Exception e) {
                log.warn("清理旧通知时出现异常，忽略: {}", e.getMessage());
            }
            
            // 5. 执行预算预警任务
            budgetWarningTask.setTransaction(transaction);
            budgetWarningTask.run();
            
            // 6. 验证结果
            // 重新查询预算
            budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
            if (budget != null) {
                log.info("预警执行后的预算状态: warned={}, usedAmount={}, usedPercentage={}%", 
                        budget.getWarned(), budget.getUsedAmount(), budget.getUsedPercentage());
                
                // 查询是否生成了通知
                try {
                    FinNotification queryParam = new FinNotification();
                    queryParam.setUserId(1L);
                    queryParam.setType("budget_warning");
                    
                    log.info("尝试查询生成的预算预警通知...");
                    if (finNotificationService != null) {
                        List<FinNotification> notifications = finNotificationService.selectFinNotificationList(queryParam);
                        if (notifications != null && !notifications.isEmpty()) {
                            log.info("找到{}条预算预警通知", notifications.size());
                            for (FinNotification notification : notifications) {
                                log.info("通知ID: {}, 标题: {}, 创建时间: {}", 
                                        notification.getId(), notification.getTitle(), 
                                        notification.getCreateTime());
                            }
                        } else {
                            log.warn("未找到预算预警通知!");
                        }
                    } else {
                        log.warn("finNotificationService未注入，无法查询通知");
                    }
                } catch (Exception e) {
                    log.error("查询预算预警通知时出现异常", e);
                }
            } else {
                log.error("预警执行后未找到预算!");
            }
            
            log.info("预算预警任务执行完成");
            
        } catch (Exception e) {
            log.error("测试预算预警功能失败", e);
        }
    }
    
    /**
     * 导入java.util.List
     */
    private static java.util.List<FinNotification> __placeholder__;
} 