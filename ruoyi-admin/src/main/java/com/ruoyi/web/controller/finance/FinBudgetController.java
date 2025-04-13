package com.ruoyi.web.controller.finance;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
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
import com.ruoyi.system.domain.finance.FinBudget;
import com.ruoyi.system.service.finance.IFinBudgetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预算管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/budget")
public class FinBudgetController extends BaseController
{
    @Autowired
    private IFinBudgetService finBudgetService;

    /**
     * 查询财务预算列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinBudget finBudget)
    {
        startPage();
        List<FinBudget> list = finBudgetService.selectFinBudgetList(finBudget);
        return getDataTable(list);
    }

    /**
     * 导出财务预算列表
     */
    @Log(title = "财务预算", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinBudget finBudget)
    {
        List<FinBudget> list = finBudgetService.selectFinBudgetList(finBudget);
        ExcelUtil<FinBudget> util = new ExcelUtil<FinBudget>(FinBudget.class);
        return util.exportExcel(list, "财务预算数据");
    }

    /**
     * 获取财务预算详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finBudgetService.selectFinBudgetById(id));
    }

    /**
     * 新增财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinBudget finBudget)
    {
        finBudget.setUserId(getUserId());
        if (!finBudgetService.checkBudgetUnique(finBudget))
        {
            return AjaxResult.error("新增预算失败，该用户指定月份的该分类预算已存在");
        }
        finBudget.setCreateBy(getUsername());
        
        return toAjax(finBudgetService.insertFinBudget(finBudget));
    }

    /**
     * 修改财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinBudget finBudget)
    {
        if (!finBudgetService.checkBudgetUnique(finBudget))
        {
            return AjaxResult.error("修改预算失败，该用户指定月份的该分类预算已存在");
        }
        finBudget.setUpdateBy(getUsername());
        
        return toAjax(finBudgetService.updateFinBudget(finBudget));
    }

    /**
     * 删除财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finBudgetService.deleteFinBudgetByIds(ids));
    }
    
    /**
     * 查询用户指定月份的预算
     */
    @GetMapping("/month")
    public AjaxResult getBudgetsByMonth(Long userId, Integer year, Integer month)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", month);
        
        List<FinBudget> list = finBudgetService.selectFinBudgetByMonth(params);
        return AjaxResult.success(list);
    }
    
    /**
     * 查询超出预警阈值的预算
     */
    @GetMapping("/warning")
    public AjaxResult getWarningBudgets(Long userId, Integer year, Integer month)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", month);
        
        List<FinBudget> list = finBudgetService.selectWarningBudgets(params);
        return AjaxResult.success(list);
    }
}