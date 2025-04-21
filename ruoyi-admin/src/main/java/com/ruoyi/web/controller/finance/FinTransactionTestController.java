package com.ruoyi.web.controller.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.mapper.finance.FinTransactionMapper;
import com.ruoyi.system.domain.finance.FinTransaction;

/**
 * 交易记录测试Controller
 */
@RestController
@RequestMapping("/finance/test")
public class FinTransactionTestController extends BaseController
{
    @Autowired
    private FinTransactionMapper finTransactionMapper;

    /**
     * 查询原始交易记录
     */
    @GetMapping("/raw")
    public AjaxResult getTransactions(Long userId, Integer year, Integer month)
    {
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (year != null && month != null) {
            String startDateStr = String.format("%d-%02d-01 00:00:00", year, month);
            String endDateStr = String.format("%d-%02d-31 23:59:59", year, month);
            
            // 这里为了简单演示，仅查询所有记录
            FinTransaction query = new FinTransaction();
            query.setUserId(userId);
            List<FinTransaction> list = finTransactionMapper.selectFinTransactionList(query);
            
            return AjaxResult.success(list);
        } else {
            return AjaxResult.error("请提供年月参数");
        }
    }
    
    /**
     * 测试每日记录查询
     */
    @GetMapping("/daily")
    public AjaxResult testDailyQuery(Long userId, Integer year, Integer month)
    {
        // 检查参数
        if (userId == null || year == null || month == null) {
            return AjaxResult.error("参数不完整");
        }
        
        // 构建参数
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year.toString());
        params.put("month", String.format("%02d", month));
        
        // 执行查询
        List<Map<String, Object>> result = finTransactionMapper.selectDailyTransactionsByMonth(params);
        
        return AjaxResult.success(result);
    }
} 