package com.ruoyi.system.service.finance.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinBudgetMapper;
import com.ruoyi.system.mapper.finance.FinTransactionMapper;
import com.ruoyi.system.domain.finance.FinBudget;
import com.ruoyi.system.service.finance.IFinBudgetService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 财务预算 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinBudgetServiceImpl implements IFinBudgetService
{
    @Autowired
    private FinBudgetMapper finBudgetMapper;
    
    @Autowired
    private FinTransactionMapper finTransactionMapper;

    /**
     * 查询财务预算信息
     * 
     * @param id 预算ID
     * @return 预算信息
     */
    @Override
    public FinBudget selectFinBudgetById(Long id)
    {
        return finBudgetMapper.selectFinBudgetById(id);
    }

    /**
     * 查询财务预算列表
     * 
     * @param finBudget 预算信息
     * @return 预算集合
     */
    @Override
    public List<FinBudget> selectFinBudgetList(FinBudget finBudget)
    {
        return finBudgetMapper.selectFinBudgetList(finBudget);
    }

    /**
     * 根据用户ID和年月查询预算
     * 
     * @param params 查询参数
     * @return 预算信息
     */
    @Override
    public FinBudget selectFinBudgetByUserAndMonth(Map<String, Object> params)
    {
        return finBudgetMapper.selectFinBudgetByUserAndMonth(params);
    }

    /**
     * 根据用户ID和分类ID查询预算
     * 
     * @param params 查询参数
     * @return 预算信息
     */
    @Override
    public FinBudget selectFinBudgetByUserAndCategory(Map<String, Object> params)
    {
        return finBudgetMapper.selectFinBudgetByUserAndCategory(params);
    }
    
    /**
     * 查询用户指定月份所有预算
     * 
     * @param params 查询参数
     * @return 预算集合
     */
    @Override
    public List<FinBudget> selectFinBudgetByMonth(Map<String, Object> params)
    {
        return finBudgetMapper.selectFinBudgetByMonth(params);
    }

    /**
     * 查询超出预警阈值的预算
     * 
     * @param params 查询参数
     * @return 预算集合
     */
    @Override
    public List<FinBudget> selectWarningBudgets(Map<String, Object> params)
    {
        return finBudgetMapper.selectWarningBudgets(params);
    }
    
    /**
     * 查询预算执行汇总
     * 
     * @param params 查询参数
     * @return 预算执行汇总信息
     */
    @Override
    public Map<String, Object> selectBudgetExecutionSummary(Map<String, Object> params)
    {
        Long userId = (Long) params.get("userId");
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("month");
        
        // 查询用户指定月份所有预算
        List<FinBudget> budgets = finBudgetMapper.selectFinBudgetByMonth(params);
        
        Map<String, Object> timeParams = new HashMap<>();
        timeParams.put("userId", userId);
        
        // 计算月的开始和结束时间戳
        Long startTime = calculateMonthStartTime(year, month);
        Long endTime = calculateMonthEndTime(year, month);
        timeParams.put("startTime", startTime);
        timeParams.put("endTime", endTime);
        
        // 查询月度总支出
        timeParams.put("type", 1); // 支出类型
        BigDecimal totalSpent = finTransactionMapper.selectExpenseAmountByMonth(timeParams);
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }
        
        // 查询月度总预算
        BigDecimal totalBudget = BigDecimal.ZERO;
        List<Map<String, Object>> categories = new ArrayList<>();
        
        for (FinBudget budget : budgets) {
            Map<String, Object> categoryBudget = new HashMap<>();
            categoryBudget.put("categoryId", budget.getCategoryId());
            categoryBudget.put("categoryName", budget.getCategoryName());
            categoryBudget.put("amount", budget.getAmount());
            
            // 使用budget中的usedAmount，而不是重新计算
            BigDecimal spentAmount = budget.getUsedAmount();
            if (spentAmount == null) {
                spentAmount = BigDecimal.ZERO;
            }
            
            categoryBudget.put("spentAmount", spentAmount);
            BigDecimal remainAmount = budget.getAmount().subtract(spentAmount);
            if (remainAmount.compareTo(BigDecimal.ZERO) < 0) {
                remainAmount = BigDecimal.ZERO;
            }
            categoryBudget.put("remainAmount", remainAmount);
            
            // 使用budget中的usedPercentage，而不是重新计算
            BigDecimal percentage = budget.getUsedPercentage();
            if (percentage == null) {
                percentage = BigDecimal.ZERO;
            }
            categoryBudget.put("percentage", percentage);
            
            // 添加notifyEnable字段
            categoryBudget.put("notifyEnable", budget.getNotifyEnable());
            
            categories.add(categoryBudget);
            
            // 总预算只计入有分类ID的预算
            if (budget.getCategoryId() != null) {
                totalBudget = totalBudget.add(budget.getAmount());
            }
        }
        
        // 计算总的剩余金额和使用百分比
        BigDecimal totalRemain = totalBudget.subtract(totalSpent);
        if (totalRemain.compareTo(BigDecimal.ZERO) < 0) {
            totalRemain = BigDecimal.ZERO;
        }
        
        BigDecimal totalPercentage = BigDecimal.ZERO;
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            totalPercentage = totalSpent.multiply(new BigDecimal("100"))
                    .divide(totalBudget, 2, RoundingMode.HALF_UP);
        }
        
        // 返回汇总信息
        Map<String, Object> result = new HashMap<>();
        result.put("totalBudget", totalBudget);
        result.put("totalSpent", totalSpent);
        result.put("totalRemain", totalRemain);
        result.put("percentage", totalPercentage);
        result.put("categories", categories);
        
        return result;
    }

    /**
     * 新增预算
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    @Override
    public int insertFinBudget(FinBudget finBudget)
    {
        return finBudgetMapper.insertFinBudget(finBudget);
    }

    /**
     * 修改预算
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    @Override
    public int updateFinBudget(FinBudget finBudget)
    {
        return finBudgetMapper.updateFinBudget(finBudget);
    }

    /**
     * 更新预算预警状态
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    @Override
    public int updateBudgetWarningStatus(FinBudget finBudget)
    {
        return finBudgetMapper.updateBudgetWarningStatus(finBudget);
    }

    /**
     * 删除预算对象
     * 
     * @param id 预算ID
     * @return 结果
     */
    @Override
    public int deleteFinBudgetById(Long id)
    {
        return finBudgetMapper.deleteFinBudgetById(id);
    }

    /**
     * 批量删除预算对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFinBudgetByIds(Long[] ids)
    {
        return finBudgetMapper.deleteFinBudgetByIds(ids);
    }
    
    /**
     * 检查预算是否唯一
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    @Override
    public boolean checkBudgetUnique(FinBudget finBudget)
    {
        Long budgetId = StringUtils.isNull(finBudget.getId()) ? -1L : finBudget.getId();
        FinBudget info = finBudgetMapper.checkBudgetUnique(finBudget);
        if (StringUtils.isNotNull(info) && info.getId().longValue() != budgetId.longValue())
        {
            return false;
        }
        return true;
    }
    
    /**
     * 计算月份开始时间的时间戳
     */
    private Long calculateMonthStartTime(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Calendar月份从0开始
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    /**
     * 计算月份结束时间的时间戳
     */
    private Long calculateMonthEndTime(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month); // 下个月
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 下个月1号
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() - 1; // 减1毫秒，表示当前月最后一天的最后一毫秒
    }

    /**
     * 更新预算使用情况
     * 
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    @Override
    public int updateBudgetUsage(Long userId, Long categoryId, Integer year, Integer month)
    {
        // 查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", month);
        params.put("categoryId", categoryId);
        
        // 查询预算
        FinBudget budget = finBudgetMapper.selectFinBudgetByUserAndCategory(params);
        if (budget == null)
        {
            return 0;
        }
        
        // 查询交易数据计算已用金额
        Map<String, Object> timeParams = new HashMap<>();
        timeParams.put("userId", userId);
        timeParams.put("categoryId", categoryId);
        
        // 计算月的开始和结束时间戳
        Long startTime = calculateMonthStartTime(year, month);
        Long endTime = calculateMonthEndTime(year, month);
        timeParams.put("startTime", startTime);
        timeParams.put("endTime", endTime);
        timeParams.put("type", 1); // 支出类型
        
        // 日志记录
        System.out.println("更新预算使用情况: userId=" + userId + ", categoryId=" + categoryId + 
                          ", year=" + year + ", month=" + month + 
                          ", startTime=" + startTime + ", endTime=" + endTime);
        
        // 查询指定分类的支出金额
        BigDecimal spentAmount = finTransactionMapper.selectExpenseAmountByCategoryAndMonth(timeParams);
        if (spentAmount == null)
        {
            spentAmount = BigDecimal.ZERO;
        }
        
        System.out.println("预算金额: " + budget.getAmount() + ", 已用金额: " + spentAmount);
        
        // 更新预算使用情况
        budget.setUsedAmount(spentAmount);
        
        // 计算已使用百分比
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal percentage = spentAmount.multiply(new BigDecimal("100"))
                    .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
            budget.setUsedPercentage(percentage);
            
            System.out.println("使用百分比: " + percentage + ", 预警阈值: " + budget.getWarningThreshold());
        }
        else
        {
            budget.setUsedPercentage(BigDecimal.ZERO);
        }
        
        // 更新预算
        return finBudgetMapper.updateBudgetUsage(budget);
    }
}