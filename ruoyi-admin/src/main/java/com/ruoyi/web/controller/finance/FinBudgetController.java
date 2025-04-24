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
import org.springframework.security.access.prepost.PreAuthorize;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.ArrayList;

import com.ruoyi.common.constant.HttpStatus;

/**
 * 预算管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/budget")
public class FinBudgetController extends BaseController {
    @Autowired
    private IFinBudgetService finBudgetService;

    /**
     * 查询财务预算列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinBudget finBudget) {
        // 确保查询时带上userId，保证数据安全
        if (finBudget.getUserId() == null) {
            Long userId = SecurityUtils.getUserId();
            if (userId != null && !SecurityUtils.isAdmin(userId)) {
                finBudget.setUserId(userId);
            }
        }

        // 校验月份参数
        Integer month = finBudget.getMonth();
        Integer year = finBudget.getYear();

        // 如果只有月份没有年份，返回空数据
        if (month != null && year == null) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("请同时提供年份和月份参数");
            return rspData;
        }

        // 如果提供了月份，进行有效性检查
        if (month != null && (month < 1 || month > 12)) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("月份必须在1-12之间");
            return rspData;
        }

        startPage();
        List<FinBudget> list = finBudgetService.selectFinBudgetList(finBudget);
        return getDataTable(list);
    }

    /**
     * 导出财务预算列表
     */
    @Log(title = "财务预算", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinBudget finBudget) {
        List<FinBudget> list = finBudgetService.selectFinBudgetList(finBudget);
        ExcelUtil<FinBudget> util = new ExcelUtil<FinBudget>(FinBudget.class);
        return util.exportExcel(list, "财务预算数据");
    }

    /**
     * 获取财务预算详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(finBudgetService.selectFinBudgetById(id));
    }

    /**
     * 新增财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinBudget finBudget) {
        // 如果请求中没有提供userId，则尝试从SecurityContext获取
        // 这样可以同时兼容移动端直接传userId和后台管理界面登录后自动获取userId的方式
        if (finBudget.getUserId() == null) {
            try {
                finBudget.setUserId(getUserId());
            } catch (Exception e) {
                return AjaxResult.error("获取用户信息异常，请确保已登录或在请求中提供userId");
            }
        }

        // 设置默认值，确保非空字段有值
        if (finBudget.getWarned() == null) {
            finBudget.setWarned(false);
        }

        if (!finBudgetService.checkBudgetUnique(finBudget)) {
            return AjaxResult.error("新增预算失败，该用户指定月份的该分类预算已存在");
        }

        try {
            finBudget.setCreateBy(getUsername());
        } catch (Exception e) {
            finBudget.setCreateBy("mobile_user");
        }

        return toAjax(finBudgetService.insertFinBudget(finBudget));
    }

    /**
     * 修改财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinBudget finBudget) {
        // 设置默认值，确保非空字段有值
        if (finBudget.getWarned() == null) {
            finBudget.setWarned(false);
        }

        if (!finBudgetService.checkBudgetUnique(finBudget)) {
            return AjaxResult.error("修改预算失败，该用户指定月份的该分类预算已存在");
        }

        try {
            finBudget.setUpdateBy(getUsername());
        } catch (Exception e) {
            finBudget.setUpdateBy("mobile_user");
        }

        return toAjax(finBudgetService.updateFinBudget(finBudget));
    }

    /**
     * 删除财务预算
     */
    @Log(title = "财务预算", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(finBudgetService.deleteFinBudgetByIds(ids));
    }

    /**
     * 查询用户指定月份的预算
     */
    @GetMapping("/month")
    public AjaxResult getBudgetsByMonth(Long userId, Integer year, Integer month) {
        // 参数校验
        if (userId == null) {
            try {
                userId = getUserId();
            } catch (Exception e) {
                return AjaxResult.error("用户ID不能为空");
            }
        }

        if (year == null) {
            return AjaxResult.error("年份参数不能为空");
        }

        if (month == null) {
            return AjaxResult.error("月份参数不能为空");
        }

        // 月份范围检查
        if (month < 1 || month > 12) {
            return AjaxResult.error("月份必须在1-12之间");
        }

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
    public AjaxResult getWarningBudgets(Long userId, Integer year, Integer month) {
        // 参数校验
        if (userId == null) {
            try {
                userId = getUserId();
            } catch (Exception e) {
                return AjaxResult.error("用户ID不能为空");
            }
        }

        if (year == null) {
            return AjaxResult.error("年份参数不能为空");
        }

        if (month == null) {
            return AjaxResult.error("月份参数不能为空");
        }

        // 月份范围检查
        if (month < 1 || month > 12) {
            return AjaxResult.error("月份必须在1-12之间");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", month);

        List<FinBudget> list = finBudgetService.selectWarningBudgets(params);
        return AjaxResult.success(list);
    }

    /**
     * 获取预算执行汇总信息
     */

    @GetMapping("/summary")
    public AjaxResult getBudgetSummary(Long userId, Integer year, Integer month) {
        // 参数校验
        if (userId == null) {
            try {
                userId = getUserId();
            } catch (Exception e) {
                return AjaxResult.error("用户ID不能为空");
            }
        }

        if (year == null) {
            return AjaxResult.error("年份参数不能为空");
        }

        if (month == null) {
            return AjaxResult.error("月份参数不能为空");
        }

        // 月份范围检查
        if (month < 1 || month > 12) {
            return AjaxResult.error("月份必须在1-12之间");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("year", year);
        params.put("month", month);

        Map<String, Object> summary = finBudgetService.selectBudgetExecutionSummary(params);
        return AjaxResult.success(summary);
    }
}
