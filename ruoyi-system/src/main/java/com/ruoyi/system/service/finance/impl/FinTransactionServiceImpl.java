package com.ruoyi.system.service.finance.impl;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinTransactionMapper;
import com.ruoyi.system.domain.finance.FinTransaction;
import com.ruoyi.system.service.finance.IFinBudgetService;
import com.ruoyi.system.service.finance.IFinTransactionService;

/**
 * 财务交易记录 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinTransactionServiceImpl implements IFinTransactionService
{
    @Autowired
    private FinTransactionMapper finTransactionMapper;

    @Autowired
    private IFinBudgetService finBudgetService;

    /**
     * 查询财务交易记录信息
     * 
     * @param id 交易记录ID
     * @return 交易记录信息
     */
    @Override
    public FinTransaction selectFinTransactionById(Long id)
    {
        return finTransactionMapper.selectFinTransactionById(id);
    }

    /**
     * 查询财务交易记录列表
     * 
     * @param finTransaction 交易记录信息
     * @return 交易记录集合
     */
    @Override
    public List<FinTransaction> selectFinTransactionList(FinTransaction finTransaction)
    {
        return finTransactionMapper.selectFinTransactionList(finTransaction);
    }
    
    /**
     * 根据用户ID查询交易记录
     * 
     * @param userId 用户ID
     * @return 交易记录集合
     */
    @Override
    public List<FinTransaction> selectFinTransactionByUserId(Long userId)
    {
        return finTransactionMapper.selectFinTransactionByUserId(userId);
    }
    
    /**
     * 根据分类ID查询交易记录
     * 
     * @param categoryId 分类ID
     * @return 交易记录集合
     */
    @Override
    public List<FinTransaction> selectFinTransactionByCategoryId(Long categoryId)
    {
        return finTransactionMapper.selectFinTransactionByCategoryId(categoryId);
    }
    
    /**
     * 查询用户某月的交易记录
     * 
     * @param params 查询参数
     * @return 交易记录集合
     */
    @Override
    public List<FinTransaction> selectFinTransactionByMonth(Map<String, Object> params)
    {
        return finTransactionMapper.selectFinTransactionByMonth(params);
    }
    
    /**
     * 查询用户某年的消费统计
     * 
     * @param params 查询参数
     * @return 交易统计数据
     */
    @Override
    public List<Map<String, Object>> selectFinTransactionStatByYear(Map<String, Object> params)
    {
        return finTransactionMapper.selectFinTransactionStatByYear(params);
    }
    
    /**
     * 查询用户某月的消费统计
     * 
     * @param params 查询参数
     * @return 交易统计数据
     */
    @Override
    public List<Map<String, Object>> selectFinTransactionStatByMonth(Map<String, Object> params)
    {
        return finTransactionMapper.selectFinTransactionStatByMonth(params);
    }
    
    /**
     * 查询用户某月的支出总额
     * 
     * @param params 查询参数
     * @return 支出总额
     */
    @Override
    public BigDecimal selectExpenseAmountByMonth(Map<String, Object> params)
    {
        return finTransactionMapper.selectExpenseAmountByMonth(params);
    }
    
    /**
     * 查询用户某月的收入总额
     * 
     * @param params 查询参数
     * @return 收入总额
     */
    @Override
    public BigDecimal selectIncomeAmountByMonth(Map<String, Object> params)
    {
        return finTransactionMapper.selectIncomeAmountByMonth(params);
    }

    /**
     * 新增财务交易记录
     * 
     * @param finTransaction 交易记录信息
     * @return 结果
     */
    @Override
    public int insertFinTransaction(FinTransaction finTransaction)
    {
        int rows = finTransactionMapper.insertFinTransaction(finTransaction);
        
        // 如果为支出类型且添加成功，更新预算使用情况
        if (rows > 0 && finTransaction.getType() != null && finTransaction.getType() == 1) {
            updateBudgetUsageForTransaction(finTransaction);
        }
        
        return rows;
    }

    /**
     * 修改财务交易记录
     * 
     * @param finTransaction 交易记录信息
     * @return 结果
     */
    @Override
    public int updateFinTransaction(FinTransaction finTransaction)
    {
        int rows = finTransactionMapper.updateFinTransaction(finTransaction);
        
        // 如果为支出类型且修改成功，更新预算使用情况
        if (rows > 0 && finTransaction.getType() != null && finTransaction.getType() == 1) {
            updateBudgetUsageForTransaction(finTransaction);
        }
        
        return rows;
    }

    /**
     * 删除财务交易记录对象
     * 
     * @param id 交易记录ID
     * @return 结果
     */
    @Override
    public int deleteFinTransactionById(Long id)
    {
        // 先获取交易记录信息
        FinTransaction transaction = selectFinTransactionById(id);
        
        int rows = finTransactionMapper.deleteFinTransactionById(id);
        
        // 如果为支出类型且删除成功，更新预算使用情况
        if (rows > 0 && transaction != null && transaction.getType() != null && transaction.getType() == 1) {
            updateBudgetUsageForTransaction(transaction);
        }
        
        return rows;
    }

    /**
     * 批量删除财务交易记录对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFinTransactionByIds(Long[] ids)
    {
        // 先获取所有需要删除的交易记录
        for (Long id : ids) {
            deleteFinTransactionById(id);
        }
        
        return ids.length;
    }
    
    /**
     * 更新与交易相关的预算使用情况
     * 
     * @param transaction 交易记录
     */
    private void updateBudgetUsageForTransaction(FinTransaction transaction) {
        // 从交易时间中获取年月
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.getTransactionTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar月份从0开始
        
        // 更新该分类的预算使用情况
        finBudgetService.updateBudgetUsage(
            transaction.getUserId(), 
            transaction.getCategoryId(), 
            year, 
            month
        );
    }
    
    /**
     * 按分类统计交易情况
     * 
     * @param params 查询参数
     * @return 分类统计数据
     */
    @Override
    public List<Map<String, Object>> selectFinTransactionStatByCategory(Map<String, Object> params)
    {
        return finTransactionMapper.selectFinTransactionStatByCategory(params);
    }
    
    /**
     * 获取收支趋势
     * 
     * @param params 查询参数
     * @return 趋势数据
     */
    @Override
    public List<Map<String, Object>> selectFinTransactionTrend(Map<String, Object> params)
    {
        return finTransactionMapper.selectFinTransactionTrend(params);
    }
    
    /**
     * 获取金额最高的交易记录
     * 
     * @param params 查询参数
     * @return 交易记录列表
     */
    @Override
    public List<Map<String, Object>> selectTopTransactions(Map<String, Object> params)
    {
        return finTransactionMapper.selectTopTransactions(params);
    }
    
    /**
     * 获取特定月份每日交易记录
     * 
     * @param params 查询参数，包含userId、year、month
     * @return 每日交易记录列表
     */
    @Override
    public List<Map<String, Object>> selectDailyTransactionsByMonth(Map<String, Object> params)
    {
        return finTransactionMapper.selectDailyTransactionsByMonth(params);
    }

    /**
     * 按时间范围获取特定月份每日交易记录
     * 
     * @param params 查询参数，包含userId、startTime、endTime
     * @return 每日交易记录列表
     */
    @Override
    public List<Map<String, Object>> selectDailyTransactionsByTimeRange(Map<String, Object> params)
    {
        return finTransactionMapper.selectDailyTransactionsByTimeRange(params);
    }
} 