package com.ruoyi.system.mapper.finance;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.finance.FinBudget;

/**
 * 财务系统预算 数据层
 * 
 * @author ruoyi
 */
public interface FinBudgetMapper
{
    /**
     * 查询财务系统预算信息
     * 
     * @param id 预算ID
     * @return 预算信息
     */
    public FinBudget selectFinBudgetById(Long id);

    /**
     * 查询财务系统预算列表
     * 
     * @param finBudget 预算信息
     * @return 预算集合
     */
    public List<FinBudget> selectFinBudgetList(FinBudget finBudget);

    /**
     * 根据用户ID和年月查询预算
     * 
     * @param params 查询参数
     * @return 预算信息
     */
    public FinBudget selectFinBudgetByUserAndMonth(Map<String, Object> params);

    /**
     * 根据用户ID和分类ID查询预算
     * 
     * @param params 查询参数
     * @return 预算信息
     */
    public FinBudget selectFinBudgetByUserAndCategory(Map<String, Object> params);
    
    /**
     * 查询用户指定月份所有预算
     * 
     * @param params 查询参数
     * @return 预算集合
     */
    public List<FinBudget> selectFinBudgetByMonth(Map<String, Object> params);

    /**
     * 查询超出预警阈值的预算
     * 
     * @param params 查询参数
     * @return 预算集合
     */
    public List<FinBudget> selectWarningBudgets(Map<String, Object> params);

    /**
     * 新增预算
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    public int insertFinBudget(FinBudget finBudget);

    /**
     * 修改预算
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    public int updateFinBudget(FinBudget finBudget);

    /**
     * 更新预算预警状态
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    public int updateBudgetWarningStatus(FinBudget finBudget);

    /**
     * 删除预算
     * 
     * @param id 预算ID
     * @return 结果
     */
    public int deleteFinBudgetById(Long id);

    /**
     * 批量删除预算
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFinBudgetByIds(Long[] ids);
    
    /**
     * 检查预算是否唯一
     * 
     * @param finBudget 预算信息
     * @return 结果
     */
    public FinBudget checkBudgetUnique(FinBudget finBudget);
}