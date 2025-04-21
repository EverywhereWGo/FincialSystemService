package com.ruoyi.web.controller.finance;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Calendar;

/**
 * 财务交易记录Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/transaction")
public class FinTransactionController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(FinTransactionController.class);
    
    @Autowired
    private IFinTransactionService finTransactionService;

    /**
     * 判断用户是否为管理员
     * 
     * @param userId 用户ID
     * @return 是否为管理员
     */
    private boolean isAdmin(Long userId) {
        // 在实际项目中，这里应该检查用户角色
        // 简化处理：假设ID为1的用户是管理员
        return userId != null && userId == 1L;
    }

    /**
     * 查询财务交易记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinTransaction finTransaction)
    {
        // 确保查询时包含了用户ID
        if (finTransaction.getUserId() == null) {
            try {
                // 尝试获取当前登录用户ID
                finTransaction.setUserId(getUserId());
            } catch (Exception e) {
                // 如果获取失败且没有提供用户ID，直接返回空列表
                log.info("查询交易记录失败: 无法获取用户ID且请求中未提供用户ID");
                startPage();
                return getDataTable(new java.util.ArrayList<>());
            }
        } else {
            // 安全性检查：如果提供的用户ID与当前登录用户不匹配，则使用当前登录用户ID
            try {
                Long currentUserId = getUserId();
                // 如果当前用户不是管理员，则强制使用当前用户ID
                if (!isAdmin(currentUserId) && !currentUserId.equals(finTransaction.getUserId())) {
                    log.warn("尝试越权查询其他用户的交易记录，已重置为当前用户ID");
                    finTransaction.setUserId(currentUserId);
                }
            } catch (Exception e) {
                // 如果无法获取当前用户身份（如移动端应用），则继续使用提供的用户ID
                log.info("无Token环境，使用提供的用户ID: {}", finTransaction.getUserId());
            }
        }
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
        try {
            // 尝试从上下文获取用户信息
            finTransaction.setCreateBy(getUsername());
            finTransaction.setUserId(getUserId());
        } catch (Exception e) {
            // 获取用户信息失败，使用请求体中的userId
            log.info("无Token添加交易记录，使用请求中的用户ID: {}", finTransaction.getUserId());
            if (finTransaction.getUserId() == null) {
                log.error("添加交易记录失败: 用户ID为空");
                return AjaxResult.error("用户ID不能为空");
            }
            finTransaction.setCreateBy("mobile_user");
        }
        return toAjax(finTransactionService.insertFinTransaction(finTransaction));
    }

    /**
     * 修改财务交易记录
     */
    @Log(title = "财务交易记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinTransaction finTransaction)
    {
        try {
            // 尝试从上下文获取用户信息
            finTransaction.setUpdateBy(getUsername());
        } catch (Exception e) {
            // 获取用户信息失败，使用fixed值
            log.info("无Token修改交易记录，交易ID: {}, 用户ID: {}", finTransaction.getId(), finTransaction.getUserId());
            finTransaction.setUpdateBy("mobile_user");
        }
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
    
    /**
     * 获取特定月份每日交易记录
     */
    @GetMapping("/daily")
    public AjaxResult getDailyTransactions(Long userId, Integer year, Integer month)
    {
        if (userId == null) {
            try {
                // 尝试获取当前登录用户ID
                userId = getUserId();
            } catch (Exception e) {
                // 如果获取失败且没有提供用户ID，直接返回错误信息
                log.error("查询每日交易记录失败: 无法获取用户ID且请求中未提供用户ID");
                return AjaxResult.error("用户ID不能为空");
            }
        }
        
        if (year == null || month == null) {
            return AjaxResult.error("年份和月份不能为空");
        }
        
        // 使用新的按时间范围查询方法
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        
        // 计算月份的起始和结束时间戳
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar月份从0开始
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        long endTime = cal.getTimeInMillis();
        
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        
        List<Map<String, Object>> list = finTransactionService.selectDailyTransactionsByTimeRange(params);
        return AjaxResult.success(list);
    }
} 