package com.ruoyi.web.controller.finance;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
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
import com.ruoyi.system.domain.finance.FinTransaction;
import com.ruoyi.system.service.finance.IFinTransactionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 财务交易记录Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/transaction")
public class FinTransactionController extends BaseController
{
    @Autowired
    private IFinTransactionService finTransactionService;

    /**
     * 查询财务交易记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinTransaction finTransaction)
    {
        startPage();
        List<FinTransaction> list = finTransactionService.selectFinTransactionList(finTransaction);
        return getDataTable(list);
    }

    /**
     * 导出财务交易记录列表
     */
    @Log(title = "财务交易记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinTransaction finTransaction)
    {
        List<FinTransaction> list = finTransactionService.selectFinTransactionList(finTransaction);
        ExcelUtil<FinTransaction> util = new ExcelUtil<FinTransaction>(FinTransaction.class);
        return util.exportExcel(list, "财务交易记录数据");
    }

    /**
     * 获取财务交易记录详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finTransactionService.selectFinTransactionById(id));
    }

    /**
     * 新增财务交易记录
     */
    @Log(title = "财务交易记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinTransaction finTransaction)
    {
        finTransaction.setCreateBy(getUsername());
        finTransaction.setUserId(getUserId());
        return toAjax(finTransactionService.insertFinTransaction(finTransaction));
    }

    /**
     * 修改财务交易记录
     */
    @Log(title = "财务交易记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinTransaction finTransaction)
    {
        finTransaction.setUpdateBy(getUsername());
        return toAjax(finTransactionService.updateFinTransaction(finTransaction));
    }

    /**
     * 删除财务交易记录
     */
    @Log(title = "财务交易记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finTransactionService.deleteFinTransactionByIds(ids));
    }
    
    /**
     * 根据月份获取交易记录
     */
    @GetMapping("/month")
    public AjaxResult getTransactionsByMonth(Long userId, Long startTime, Long endTime, Integer type)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        if (type != null) {
            params.put("type", type);
        }
        
        List<FinTransaction> list = finTransactionService.selectFinTransactionByMonth(params);
        return AjaxResult.success(list);
    }
    
    /**
     * 获取年度消费统计
     */
    @GetMapping("/stat/year")
    public AjaxResult getYearStat(Long userId, Integer year)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        
        List<Map<String, Object>> stat = finTransactionService.selectFinTransactionStatByYear(params);
        return AjaxResult.success(stat);
    }
    
    /**
     * 获取月度消费统计
     */
    @GetMapping("/stat/month")
    public AjaxResult getMonthStat(Long userId, Long startTime, Long endTime, Integer type)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("type", type);
        
        List<Map<String, Object>> stat = finTransactionService.selectFinTransactionStatByMonth(params);
        return AjaxResult.success(stat);
    }
    
    /**
     * 获取月度收支总额
     */
    @GetMapping("/stat/amount")
    public AjaxResult getMonthAmount(Long userId, Long startTime, Long endTime)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        
        // 查询支出参数
        Map<String, Object> expenseParams = new HashMap<>(params);
        expenseParams.put("type", 1); // 支出类型为1
        BigDecimal expense = finTransactionService.selectExpenseAmountByMonth(expenseParams);
        
        BigDecimal income = finTransactionService.selectIncomeAmountByMonth(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("expense", expense);
        result.put("income", income);
        result.put("balance", income.subtract(expense));
        
        return AjaxResult.success(result);
    }
} 