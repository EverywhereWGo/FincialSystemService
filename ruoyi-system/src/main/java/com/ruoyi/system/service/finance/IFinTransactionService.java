package com.ruoyi.system.service.finance;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import com.ruoyi.system.domain.finance.FinTransaction;

/**
 * 财务交易记录 服务层
 * 
 * @author ruoyi
 */
public interface IFinTransactionService
{
    /**
     * 查询财务交易记录信息
     * 
     * @param id 交易记录ID
     * @return 交易记录信息
     */
    public FinTransaction selectFinTransactionById(Long id);

    /**
     * 查询财务交易记录列表
     * 
     * @param finTransaction 交易记录信息
     * @return 交易记录集合
     */
    public List<FinTransaction> selectFinTransactionList(FinTransaction finTransaction);
    
    /**
     * 根据用户ID查询交易记录
     * 
     * @param userId 用户ID
     * @return 交易记录集合
     */
    public List<FinTransaction> selectFinTransactionByUserId(Long userId);
    
    /**
     * 根据分类ID查询交易记录
     * 
     * @param categoryId 分类ID
     * @return 交易记录集合
     */
    public List<FinTransaction> selectFinTransactionByCategoryId(Long categoryId);
    
    /**
     * 查询用户某月的交易记录
     * 
     * @param params 查询参数
     * @return 交易记录集合
     */
    public List<FinTransaction> selectFinTransactionByMonth(Map<String, Object> params);
    
    /**
     * 查询用户某年的消费统计
     * 
     * @param params 查询参数
     * @return 交易统计数据
     */
    public List<Map<String, Object>> selectFinTransactionStatByYear(Map<String, Object> params);
    
    /**
     * 查询用户某月的消费统计
     * 
     * @param params 查询参数
     * @return 交易统计数据
     */
    public List<Map<String, Object>> selectFinTransactionStatByMonth(Map<String, Object> params);
    
    /**
     * 查询用户某月的支出总额
     * 
     * @param params 查询参数
     * @return 支出总额
     */
    public BigDecimal selectExpenseAmountByMonth(Map<String, Object> params);
    
    /**
     * 查询用户某月的收入总额
     * 
     * @param params 查询参数
     * @return 收入总额
     */
    public BigDecimal selectIncomeAmountByMonth(Map<String, Object> params);
    
    /**
     * 按分类统计交易情况
     * 
     * @param params 查询参数
     * @return 分类统计数据
     */
    public List<Map<String, Object>> selectFinTransactionStatByCategory(Map<String, Object> params);
    
    /**
     * 获取收支趋势
     * 
     * @param params 查询参数
     * @return 趋势数据
     */
    public List<Map<String, Object>> selectFinTransactionTrend(Map<String, Object> params);
    
    /**
     * 获取金额最高的交易记录
     * 
     * @param params 查询参数
     * @return 交易记录列表
     */
    public List<Map<String, Object>> selectTopTransactions(Map<String, Object> params);
    
    /**
     * 获取特定月份每日交易记录
     * 
     * @param params 查询参数，包含userId、year、month
     * @return 每日交易记录列表
     */
    public List<Map<String, Object>> selectDailyTransactionsByMonth(Map<String, Object> params);

    /**
     * 新增财务交易记录
     * 
     * @param finTransaction 交易记录信息
     * @return 结果
     */
    public int insertFinTransaction(FinTransaction finTransaction);

    /**
     * 修改财务交易记录
     * 
     * @param finTransaction 交易记录信息
     * @return 结果
     */
    public int updateFinTransaction(FinTransaction finTransaction);

    /**
     * 删除财务交易记录信息
     * 
     * @param id 交易记录ID
     * @return 结果
     */
    public int deleteFinTransactionById(Long id);

    /**
     * 批量删除财务交易记录信息
     * 
     * @param ids 需要删除的交易记录ID
     * @return 结果
     */
    public int deleteFinTransactionByIds(Long[] ids);

    /**
     * 按时间范围获取特定月份每日交易记录
     * 
     * @param params 查询参数，包含userId、startTime、endTime
     * @return 每日交易记录列表
     */
    public List<Map<String, Object>> selectDailyTransactionsByTimeRange(Map<String, Object> params);
} 