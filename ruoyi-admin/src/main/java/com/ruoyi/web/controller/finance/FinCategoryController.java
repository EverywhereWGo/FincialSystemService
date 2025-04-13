package com.ruoyi.web.controller.finance;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.finance.FinCategory;
import com.ruoyi.system.service.finance.IFinCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 财务分类Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/category")
public class FinCategoryController extends BaseController
{
    @Autowired
    private IFinCategoryService finCategoryService;

    /**
     * 查询财务分类列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinCategory finCategory)
    {
        startPage();
        List<FinCategory> list = finCategoryService.selectFinCategoryList(finCategory);
        return getDataTable(list);
    }

    /**
     * 导出财务分类列表
     */
    @Log(title = "财务分类", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinCategory finCategory)
    {
        List<FinCategory> list = finCategoryService.selectFinCategoryList(finCategory);
        ExcelUtil<FinCategory> util = new ExcelUtil<FinCategory>(FinCategory.class);
        return util.exportExcel(list, "财务分类数据");
    }

    /**
     * 获取财务分类详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finCategoryService.selectFinCategoryById(id));
    }

    /**
     * 新增财务分类
     */
    @Log(title = "财务分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinCategory finCategory)
    {
        if (!finCategoryService.checkCategoryNameUnique(finCategory))
        {
            return AjaxResult.error("新增财务分类'" + finCategory.getName() + "'失败，分类名称已存在");
        }
        // 设置用户ID和创建者
        finCategory.setUserId(getUserId());
        finCategory.setCreateBy(getUsername());
        
        return toAjax(finCategoryService.insertFinCategory(finCategory));
    }

    /**
     * 修改财务分类
     */
    @Log(title = "财务分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinCategory finCategory)
    {
        if (!finCategoryService.checkCategoryNameUnique(finCategory))
        {
            return AjaxResult.error("修改财务分类'" + finCategory.getName() + "'失败，分类名称已存在");
        }
        // 设置更新者
        finCategory.setUpdateBy(getUsername());
        
        return toAjax(finCategoryService.updateFinCategory(finCategory));
    }

    /**
     * 删除财务分类
     */
    @Log(title = "财务分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finCategoryService.deleteFinCategoryByIds(ids));
    }
    
    /**
     * 根据类型获取分类
     */
    @GetMapping("/type/{type}")
    public AjaxResult getByType(@PathVariable("type") Integer type)
    {
        List<FinCategory> list = finCategoryService.selectFinCategoryByType(type);
        return AjaxResult.success(list);
    }
} 