package com.ruoyi.system.service.finance.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinCategoryMapper;
import com.ruoyi.system.domain.finance.FinCategory;
import com.ruoyi.system.service.finance.IFinCategoryService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 财务分类 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinCategoryServiceImpl implements IFinCategoryService
{
    @Autowired
    private FinCategoryMapper finCategoryMapper;

    /**
     * 查询财务分类信息
     * 
     * @param id 财务分类ID
     * @return 财务分类信息
     */
    @Override
    public FinCategory selectFinCategoryById(Long id)
    {
        return finCategoryMapper.selectFinCategoryById(id);
    }

    /**
     * 查询财务分类列表
     * 
     * @param finCategory 财务分类信息
     * @return 财务分类集合
     */
    @Override
    public List<FinCategory> selectFinCategoryList(FinCategory finCategory)
    {
        return finCategoryMapper.selectFinCategoryList(finCategory);
    }
    
    /**
     * 根据类型查询分类
     * 
     * @param type 类型
     * @return 财务分类集合
     */
    @Override
    public List<FinCategory> selectFinCategoryByType(Integer type)
    {
        return finCategoryMapper.selectFinCategoryByType(type);
    }
    
    /**
     * 根据用户ID查询分类
     * 
     * @param userId: 用户ID
     * @return 财务分类集合
     */
    @Override
    public List<FinCategory> selectFinCategoryByUserId(Long userId)
    {
        return finCategoryMapper.selectFinCategoryByUserId(userId);
    }

    /**
     * 新增财务分类
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    @Override
    public int insertFinCategory(FinCategory finCategory)
    {
        return finCategoryMapper.insertFinCategory(finCategory);
    }

    /**
     * 修改财务分类
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    @Override
    public int updateFinCategory(FinCategory finCategory)
    {
        return finCategoryMapper.updateFinCategory(finCategory);
    }

    /**
     * 删除财务分类对象
     * 
     * @param id 财务分类ID
     * @return 结果
     */
    @Override
    public int deleteFinCategoryById(Long id)
    {
        return finCategoryMapper.deleteFinCategoryById(id);
    }

    /**
     * 批量删除财务分类对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFinCategoryByIds(Long[] ids)
    {
        return finCategoryMapper.deleteFinCategoryByIds(ids);
    }
    
    /**
     * 校验财务分类名称是否唯一
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    @Override
    public boolean checkCategoryNameUnique(FinCategory finCategory)
    {
        Long categoryId = StringUtils.isNull(finCategory.getId()) ? -1L : finCategory.getId();
        FinCategory info = finCategoryMapper.checkCategoryNameUnique(finCategory);
        if (StringUtils.isNotNull(info) && info.getId().longValue() != categoryId.longValue())
        {
            return false;
        }
        return true;
    }
} 