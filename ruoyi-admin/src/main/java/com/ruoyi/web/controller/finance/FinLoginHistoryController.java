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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.finance.FinLoginHistory;
import com.ruoyi.system.service.finance.IFinLoginHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 登录历史Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/loginHistory")
public class FinLoginHistoryController extends BaseController
{
    @Autowired
    private IFinLoginHistoryService finLoginHistoryService;

    /**
     * 查询登录历史列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinLoginHistory finLoginHistory)
    {
        startPage();
        List<FinLoginHistory> list = finLoginHistoryService.selectFinLoginHistoryList(finLoginHistory);
        return getDataTable(list);
    }

    /**
     * 导出登录历史列表
     */
    @Log(title = "登录历史", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinLoginHistory finLoginHistory)
    {
        List<FinLoginHistory> list = finLoginHistoryService.selectFinLoginHistoryList(finLoginHistory);
        ExcelUtil<FinLoginHistory> util = new ExcelUtil<FinLoginHistory>(FinLoginHistory.class);
        return util.exportExcel(list, "登录历史数据");
    }

    /**
     * 获取登录历史详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finLoginHistoryService.selectFinLoginHistoryById(id));
    }

    /**
     * 删除登录历史
     */
    @Log(title = "登录历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 即使删除了 0 条记录也返回成功
        finLoginHistoryService.deleteFinLoginHistoryByIds(ids);
        return AjaxResult.success();
    }
    
    /**
     * 获取用户登录历史
     */
    @GetMapping("/user")
    public AjaxResult getUserLoginHistory(@RequestParam("userId") Long userId)
    {
        FinLoginHistory finLoginHistory = new FinLoginHistory();
        finLoginHistory.setUserId(userId);
        
        startPage();
        List<FinLoginHistory> list = finLoginHistoryService.selectFinLoginHistoryList(finLoginHistory);
        return AjaxResult.success(getDataTable(list));
    }
    
    /**
     * 获取用户最近一次登录
     */
    @GetMapping("/latest")
    public AjaxResult getLatestLogin(@RequestParam("userId") Long userId)
    {
        FinLoginHistory loginHistory = finLoginHistoryService.selectLatestLoginByUserId(userId);
        return AjaxResult.success(loginHistory);
    }
}