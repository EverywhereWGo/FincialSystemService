package com.ruoyi.web.controller.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.finance.IFinBudgetService;
import com.ruoyi.system.service.finance.IFinTransactionService;

/**
 * 财务统计分析 Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/statistic")
public class FinStatisticController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(FinStatisticController.class);

    @Autowired
    private IFinTransactionService finTransactionService;
    
    @Autowired
    private IFinBudgetService finBudgetService;

    /**
     * 获取月度分类支出统计
     */
    @GetMapping("/category")
    public AjaxResult getCategoryStats(
            @RequestParam("userId") Long userId,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam("type") Integer type) 
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("type", type);
        
        List<Map<String, Object>> stats = finTransactionService.selectFinTransactionStatByCategory(params);
        
        // 添加日志，记录结果集大小
        logger.info("分类统计查询结果条数: {}, userId: {}, type: {}", 
                stats != null ? stats.size() : 0, userId, type);
        
        // 计算分类百分比
        if (stats != null && !stats.isEmpty()) {
            // 计算总金额
            double total = 0;
            for (Map<String, Object> stat : stats) {
                Object amountObj = stat.get("amount");
                if (amountObj != null) {
                    double amount = 0;
                    if (amountObj instanceof Number) {
                        amount = ((Number) amountObj).doubleValue();
                    } else if (amountObj instanceof String) {
                        try {
                            amount = Double.parseDouble((String) amountObj);
                        } catch (NumberFormatException e) {
                            logger.error("解析金额失败: {}", amountObj);
                        }
                    }
                    total += amount;
                }
            }
            
            // 计算各分类占比
            if (total > 0) {
                for (Map<String, Object> stat : stats) {
                    Object amountObj = stat.get("amount");
                    if (amountObj != null) {
                        double amount = 0;
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).doubleValue();
                        } else if (amountObj instanceof String) {
                            try {
                                amount = Double.parseDouble((String) amountObj);
                            } catch (NumberFormatException e) {
                                logger.error("解析金额失败: {}", amountObj);
                            }
                        }
                        // 计算百分比并四舍五入保留两位小数
                        double percentage = Math.round((amount / total) * 10000) / 100.0;
                        stat.put("percentage", percentage);
                    }
                }
            }
        }
        
        return AjaxResult.success(stats);
    }
    
    /**
     * 获取年度收支统计
     */
    @GetMapping("/year")
    public AjaxResult getYearlyStats(
            @RequestParam("userId") Long userId,
            @RequestParam("year") Integer year)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        
        List<Map<String, Object>> stats = finTransactionService.selectFinTransactionStatByYear(params);
        return AjaxResult.success(stats);
    }
    
    /**
     * 获取收支趋势
     */
    @GetMapping("/trend")
    public AjaxResult getTrend(
            @RequestParam("userId") Long userId,
            @RequestParam("months") Integer months)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("months", months);
        
        List<Map<String, Object>> trends = finTransactionService.selectFinTransactionTrend(params);
        return AjaxResult.success(trends);
    }
    
    /**
     * 获取预算执行情况
     */
    @GetMapping("/budget")
    public AjaxResult getBudgetExecution(
            @RequestParam("userId") Long userId,
            @RequestParam("month") String month)
    {
        // 解析month (yyyy-MM)
        String[] parts = month.split("-");
        Integer year = Integer.parseInt(parts[0]);
        Integer monthNum = Integer.parseInt(parts[1]);
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", monthNum);
        
        Map<String, Object> budgetExecution = finBudgetService.selectBudgetExecutionSummary(params);
        return AjaxResult.success(budgetExecution);
    }
    
    /**
     * 获取顺序记录
     */
    @GetMapping("/topTransactions")
    public AjaxResult getTopTransactions(
            @RequestParam("userId") Long userId,
            @RequestParam("type") Integer type,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("type", type);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("limit", limit);
        
        List<Map<String, Object>> topTransactions = finTransactionService.selectTopTransactions(params);
        return AjaxResult.success(topTransactions);
    }
}