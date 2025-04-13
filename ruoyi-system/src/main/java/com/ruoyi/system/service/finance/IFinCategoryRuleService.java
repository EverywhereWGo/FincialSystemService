package com.ruoyi.system.service.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinCategoryRule;

/**
 * 分类规则 服务层
 * 
 * @author ruoyi
 */
public interface IFinCategoryRuleService
{
    /**
     * 查询分类规则信息
     * 
     * @param id 分类规则ID
     * @return 分类规则信息
     */
    public FinCategoryRule selectFinCategoryRuleById(Long id);

    /**
     * 查询分类规则列表
     * 
     * @param finCategoryRule 分类规则信息
     * @return 分类规则集合
     */
    public List<FinCategoryRule> selectFinCategoryRuleList(FinCategoryRule finCategoryRule);
    
    /**
     * 查询所有分类规则
     * 
     * @return 分类规则集合
     */
    public List<FinCategoryRule> selectAllCategoryRules();
    
    /**
     * 通过描述匹配分类规则
     * 
     * @param description 描述文本
     * @return 分类规则信息
     */
    public FinCategoryRule matchCategoryByDescription(String description);

    /**
     * 新增分类规则
     * 
     * @param finCategoryRule 分类规则信息
     * @return 结果
     */
    public int insertFinCategoryRule(FinCategoryRule finCategoryRule);

    /**
     * 修改分类规则
     * 
     * @param finCategoryRule 分类规则信息
     * @return 结果
     */
    public int updateFinCategoryRule(FinCategoryRule finCategoryRule);

    /**
     * 批量删除分类规则
     * 
     * @param ids 需要删除的分类规则ID
     * @return 结果
     */
    public int deleteFinCategoryRuleByIds(Long[] ids);

    /**
     * 删除分类规则信息
     * 
     * @param id 分类规则ID
     * @return 结果
     */
    public int deleteFinCategoryRuleById(Long id);
}