package com.ruoyi.web.controller.finance;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.finance.FinBudget;
import com.ruoyi.system.domain.finance.FinCategory;
import com.ruoyi.system.domain.finance.FinNotification;
import com.ruoyi.system.domain.finance.FinTransaction;
import com.ruoyi.system.mapper.finance.FinBudgetMapper;
import com.ruoyi.system.mapper.finance.FinNotificationMapper;
import com.ruoyi.system.service.finance.IFinCategoryService;
import com.ruoyi.system.service.finance.IFinNotificationService;
import com.ruoyi.system.service.finance.task.BudgetWarningTask;
import com.ruoyi.system.service.finance.test.BudgetWarningTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 财务系统测试接口
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/test")
public class FinTestController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(FinTestController.class);
    
    @Autowired
    private BudgetWarningTest budgetWarningTest;
    
    @Autowired(required = false)
    private IFinNotificationService finNotificationService;
    
    @Autowired(required = false)
    private IFinCategoryService finCategoryService;
    
    @Autowired(required = false)
    private BudgetWarningTask budgetWarningTask;
    
    @Autowired(required = false)
    private FinBudgetMapper finBudgetMapper;
    
    @Autowired(required = false)
    private FinNotificationMapper finNotificationMapper;
    
    /**
     * 测试预算预警功能
     */
    @GetMapping("/budget/warning")
    public AjaxResult testBudgetWarning()
    {
        budgetWarningTest.testBudgetWarning();
        return AjaxResult.success("测试完成");
    }
    
    /**
     * 测试创建预算预警通知
     */
    @GetMapping("/notification/create")
    public AjaxResult testCreateNotification()
    {
        if (finNotificationService == null) {
            return AjaxResult.error("通知服务未注入，无法执行测试");
        }
        
        try {
            // 创建测试通知
            FinNotification notification = new FinNotification();
            notification.setUserId(1L); // 管理员用户
            notification.setTitle("预算超出提醒");
            notification.setContent("这是一条测试通知：在2023年08月15日10时30分，你有一笔住房类型的交易记录，超出了2023年08月的住房类型的预算，请注意！");
            notification.setType("budget_warning");
            notification.setRead(0); // 未读
            notification.setCreateBy("system_test");
            notification.setCreateTime(new Date()); // 设置创建时间
            
            // 保存通知
            int result = finNotificationService.insertFinNotification(notification);
            
            if (result > 0) {
                return AjaxResult.success("测试通知创建成功", notification);
            } else {
                return AjaxResult.error("测试通知创建失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 测试完整的预算预警流程
     */
    @GetMapping("/budget/warning/full")
    public AjaxResult testFullBudgetWarning()
    {
        try {
            // 1. 手动创建交易记录
            FinTransaction transaction = new FinTransaction();
            transaction.setUserId(1L); // 管理员
            transaction.setCategoryId(4L); // 住房类型
            transaction.setAmount(new BigDecimal("800"));
            transaction.setType(1); // 支出类型
            transaction.setTransactionTime(System.currentTimeMillis());
            transaction.setNote("测试预算预警流程");
            
            // 2. 查询分类信息，用于通知展示
            FinCategory category = null;
            String categoryName = "住房";
            if (finCategoryService != null) {
                category = finCategoryService.selectFinCategoryById(transaction.getCategoryId());
                if (category != null) {
                    categoryName = category.getName();
                }
            }
            
            // 3. 手动创建通知
            FinNotification notification = new FinNotification();
            notification.setUserId(transaction.getUserId());
            notification.setTitle("预算超出提醒");
            
            // 格式化时间
            String dateStr = new java.text.SimpleDateFormat("yyyy年MM月dd日HH时mm分").format(new Date(transaction.getTransactionTime()));
            
            // 构建内容
            StringBuilder content = new StringBuilder();
            content.append("在").append(dateStr);
            content.append("，你有一笔").append(categoryName).append("类型的交易记录");
            content.append("，超出了").append(new java.text.SimpleDateFormat("yyyy").format(new Date()))
                  .append("年").append(new java.text.SimpleDateFormat("MM").format(new Date())).append("月的");
            content.append(categoryName).append("类型的预算，请注意！");
            
            notification.setContent(content.toString());
            notification.setType("budget_warning");
            notification.setRead(0); // 未读
            notification.setCreateBy("system_test");
            notification.setCreateTime(new Date()); // 设置创建时间
            
            // 保存通知
            int result = 0;
            String message;
            if (finNotificationService != null) {
                result = finNotificationService.insertFinNotification(notification);
                message = "手动创建通知结果: " + (result > 0 ? "成功" : "失败");
            } else {
                message = "通知服务未注入，无法创建通知";
            }
            
            // 4. 调用原有测试流程
            budgetWarningTest.testBudgetWarning();
            
            return AjaxResult.success("测试完成。" + message);
        } catch (Exception e) {
            return AjaxResult.error("测试异常: " + e.getMessage());
        }
    }
    
    /**
     * 直接测试预算预警通知创建（最简化版本）
     */
    @GetMapping("/notification/direct")
    public AjaxResult testDirectNotification()
    {
        try {
            if (budgetWarningTask == null) {
                return AjaxResult.error("预算预警任务未注入");
            }
            
            log.info("开始直接测试预算预警通知创建");
            
            // 创建测试交易记录
            FinTransaction transaction = new FinTransaction();
            transaction.setUserId(1L); // 管理员
            transaction.setCategoryId(4L); // 住房类型
            transaction.setAmount(new BigDecimal("900"));
            transaction.setType(1); // 支出类型
            transaction.setTransactionTime(System.currentTimeMillis());
            transaction.setNote("直接测试预算预警通知");
            
            // 确保有对应的预算记录
            if (finBudgetMapper != null) {
                // 获取当前年月
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Calendar月份从0开始
                
                // 查询参数
                Map<String, Object> params = new HashMap<>();
                params.put("userId", 1L);
                params.put("categoryId", 4L); // 住房类别
                params.put("year", year);
                params.put("month", month);
                
                // 查询预算
                FinBudget budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
                if (budget == null) {
                    log.info("没有找到预算记录，测试可能会失败");
                } else {
                    // 重置预警状态，确保可以再次触发预警
                    budget.setWarned(false);
                    budget.setNotifyEnable(true);
                    finBudgetMapper.updateBudgetWarningStatus(budget);
                    finBudgetMapper.updateFinBudget(budget);
                    
                    log.info("已重置预算预警状态: budgetId={}, warned={}, notifyEnable={}",
                            budget.getId(), budget.getWarned(), budget.getNotifyEnable());
                }
            }
            
            // 直接调用预算预警任务
            budgetWarningTask.setTransaction(transaction);
            budgetWarningTask.run();
            
            // 查询通知结果
            String resultMessage = "预算预警通知测试已执行，请查看日志了解详细信息";
            if (finNotificationMapper != null) {
                FinNotification queryParam = new FinNotification();
                queryParam.setUserId(1L);
                queryParam.setType("budget_warning");
                
                java.util.List<FinNotification> notifications = finNotificationMapper.selectFinNotificationList(queryParam);
                if (notifications != null && !notifications.isEmpty()) {
                    resultMessage = String.format("预算预警测试成功！找到%d条通知记录", notifications.size());
                    for (FinNotification notif : notifications) {
                        log.info("找到通知: ID={}, 标题={}, 创建时间={}", 
                                notif.getId(), notif.getTitle(), notif.getCreateTime());
                    }
                } else {
                    resultMessage = "预算预警测试执行完毕，但未找到通知记录";
                    log.warn(resultMessage);
                }
            }
            
            return AjaxResult.success(resultMessage);
        } catch (Exception e) {
            log.error("直接测试预算预警通知失败: {}", e.getMessage(), e);
            return AjaxResult.error("测试异常: " + e.getMessage());
        }
    }
} 