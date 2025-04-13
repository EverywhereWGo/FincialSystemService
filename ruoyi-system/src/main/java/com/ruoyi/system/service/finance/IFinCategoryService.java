package com.ruoyi.system.service.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinCategory;

/**
 * 财务分类 服务层
 * 
 * @author ruoyi
 */
public interface IFinCategoryService
{
    /**
     * 查询财务分类信息
     * 
     * @param id 财务分类ID
     * @return 财务分类信息
     */
    public FinCategory selectFinCategoryById(Long id);

    /**
     * 查询财务分类列表
     * 
     * @param finCategory 财务分类信息
     * @return 财务分类集合
     */
    public List<FinCategory> selectFinCategoryList(FinCategory finCategory);
    
    /**
     * 根据类型查询分类
     * 
     * @param type 类型
     * @return 财务分类集合
     */
    public List<FinCategory> selectFinCategoryByType(Integer type);
    
    /**
     * 根据用户ID查询分类
     * 
     * @param userId: 用户ID
     * @return 财务分类集合
     */
    public List<FinCategory> selectFinCategoryByUserId(Long userId);

    /**
     * 新增财务分类
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    public int insertFinCategory(FinCategory finCategory);

    /**
     * 修改财务分类
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    public int updateFinCategory(FinCategory finCategory);

    /**
     * 删除财务分类信息
     * 
     * @param id 财务分类ID
     * @return 结果
     */
    public int deleteFinCategoryById(Long id);

    /**
     * 批量删除财务分类信息
     * 
     * @param ids 需要删除的财务分类ID
     * @return 结果
     */
    public int deleteFinCategoryByIds(Long[] ids);
    
    /**
     * 校验财务分类名称是否唯一
     * 
     * @param finCategory 财务分类信息
     * @return 结果
     */
    public boolean checkCategoryNameUnique(FinCategory finCategory);
} 