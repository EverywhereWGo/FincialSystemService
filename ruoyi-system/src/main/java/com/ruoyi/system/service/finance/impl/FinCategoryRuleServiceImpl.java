package com.ruoyi.system.service.finance.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinCategoryRuleMapper;
import com.ruoyi.system.domain.finance.FinCategoryRule;
import com.ruoyi.system.service.finance.IFinCategoryRuleService;

/**
 * 分类规则 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinCategoryRuleServiceImpl implements IFinCategoryRuleService
{
    @Autowired
    private FinCategoryRuleMapper finCategoryRuleMapper;

    /**
     * 查询分类规则信息
     * 
     * @param id 分类规则ID
     * @return 分类规则信息
     */
    @Override
    public FinCategoryRule selectFinCategoryRuleById(Long id)
    {
        return finCategoryRuleMapper.selectFinCategoryRuleById(id);
    }

    /**
     * 查询分类规则列表
     * 
     * @param finCategoryRule 分类规则信息
     * @return 分类规则集合
     */
    @Override
    public List<FinCategoryRule> selectFinCategoryRuleList(FinCategoryRule finCategoryRule)
    {
        return finCategoryRuleMapper.selectFinCategoryRuleList(finCategoryRule);
    }
    
    /**
     * 查询所有分类规则
     * 
     * @return 分类规则集合
     */
    @Override
    public List<FinCategoryRule> selectAllCategoryRules()
    {
        return finCategoryRuleMapper.selectAllCategoryRules();
    }
    
    /**
     * 通过描述匹配分类规则
     * 
     * @param description 描述文本
     * @return 分类规则信息
     */
    @Override
    public FinCategoryRule matchCategoryByDescription(String description)
    {
        return finCategoryRuleMapper.matchCategoryByDescription(description);
    }

    /**
     * 新增分类规则
     * 
     * @param finCategoryRule 分类规则信息
     * @return 结果
     */
    @Override
    public int insertFinCategoryRule(FinCategoryRule finCategoryRule)
    {
        return finCategoryRuleMapper.insertFinCategoryRule(finCategoryRule);
    }

    /**
     * 修改分类规则
     * 
     * @param finCategoryRule 分类规则信息
     * @return 结果
     */
    @Override
    public int updateFinCategoryRule(FinCategoryRule finCategoryRule)
    {
        return finCategoryRuleMapper.updateFinCategoryRule(finCategoryRule);
    }

    /**
     * 批量删除分类规则
     * 
     * @param ids 需要删除的分类规则ID
     * @return 结果
     */
    @Override
    public int deleteFinCategoryRuleByIds(Long[] ids)
    {
        return finCategoryRuleMapper.deleteFinCategoryRuleByIds(ids);
    }

    /**
     * 删除分类规则信息
     * 
     * @param id 分类规则ID
     * @return 结果
     */
    @Override
    public int deleteFinCategoryRuleById(Long id)
    {
        return finCategoryRuleMapper.deleteFinCategoryRuleById(id);
    }
}