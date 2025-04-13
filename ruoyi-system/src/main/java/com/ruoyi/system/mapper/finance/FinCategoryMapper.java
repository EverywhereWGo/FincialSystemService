package com.ruoyi.system.mapper.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinCategory;

/**
 * 财务系统分类 数据层
 * 
 * @author ruoyi
 */
public interface FinCategoryMapper
{
    /**
     * 查询财务系统分类信息
     * 
     * @param id 财务系统分类ID
     * @return 财务系统分类信息
     */
    public FinCategory selectFinCategoryById(Long id);

    /**
     * 查询财务系统分类列表
     * 
     * @param finCategory 财务系统分类信息
     * @return 财务系统分类集合
     */
    public List<FinCategory> selectFinCategoryList(FinCategory finCategory);

    /**
     * 根据类型查询分类列表
     * 
     * @param type 类型（1:支出,2:收入）
     * @return 分类列表
     */
    public List<FinCategory> selectFinCategoryByType(Integer type);

    /**
     * 根据用户ID查询分类列表
     * 
     * @param userId 用户ID
     * @return 分类列表
     */
    public List<FinCategory> selectFinCategoryByUserId(Long userId);

    /**
     * 新增财务系统分类
     * 
     * @param finCategory 财务系统分类信息
     * @return 结果
     */
    public int insertFinCategory(FinCategory finCategory);

    /**
     * 修改财务系统分类
     * 
     * @param finCategory 财务系统分类信息
     * @return 结果
     */
    public int updateFinCategory(FinCategory finCategory);

    /**
     * 删除财务系统分类
     * 
     * @param id 财务系统分类ID
     * @return 结果
     */
    public int deleteFinCategoryById(Long id);

    /**
     * 批量删除财务系统分类
     * 
     * @param ids 需要删除的财务系统分类ID数组
     * @return 结果
     */
    public int deleteFinCategoryByIds(Long[] ids);

    /**
     * 查询分类名称是否唯一（同一用户下同一类型）
     *
     * @param finCategory 分类信息
     * @return 结果
     */
    public FinCategory checkCategoryNameUnique(FinCategory finCategory);
} 