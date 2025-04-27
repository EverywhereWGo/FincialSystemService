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
import com.ruoyi.system.domain.finance.FinCategoryRule;
import com.ruoyi.system.service.finance.IFinCategoryRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 分类规则Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/categoryRule")
public class FinCategoryRuleController extends BaseController
{
    @Autowired
    private IFinCategoryRuleService finCategoryRuleService;

    /**
     * 查询分类规则列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinCategoryRule finCategoryRule)
    {
        startPage();
        List<FinCategoryRule> list = finCategoryRuleService.selectFinCategoryRuleList(finCategoryRule);
        return getDataTable(list);
    }

    /**
     * 导出分类规则列表
     */
    @Log(title = "分类规则", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinCategoryRule finCategoryRule)
    {
        List<FinCategoryRule> list = finCategoryRuleService.selectFinCategoryRuleList(finCategoryRule);
        ExcelUtil<FinCategoryRule> util = new ExcelUtil<FinCategoryRule>(FinCategoryRule.class);
        return util.exportExcel(list, "分类规则数据");
    }

    /**
     * 获取分类规则详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finCategoryRuleService.selectFinCategoryRuleById(id));
    }

    /**
     * 新增分类规则
     */
    @Log(title = "分类规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinCategoryRule finCategoryRule)
    {
        finCategoryRule.setCreateBy(getUsername());
        return toAjax(finCategoryRuleService.insertFinCategoryRule(finCategoryRule));
    }

    /**
     * 修改分类规则
     */
    @Log(title = "分类规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinCategoryRule finCategoryRule)
    {
        finCategoryRule.setUpdateBy("mobile_user");
        return toAjax(finCategoryRuleService.updateFinCategoryRule(finCategoryRule));
    }

    /**
     * 删除分类规则
     */
    @Log(title = "分类规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finCategoryRuleService.deleteFinCategoryRuleByIds(ids));
    }
}
