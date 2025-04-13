package com.ruoyi.web.controller.finance;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.system.domain.finance.FinNotification;
import com.ruoyi.system.service.finance.IFinNotificationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.controller.finance.vo.BatchReadRequest;

/**
 * 系统通知Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/notification")
public class FinNotificationController extends BaseController
{
    @Autowired
    private IFinNotificationService finNotificationService;

    /**
     * 查询系统通知列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinNotification finNotification)
    {
        startPage();
        List<FinNotification> list = finNotificationService.selectFinNotificationList(finNotification);
        return getDataTable(list);
    }

    /**
     * 导出系统通知列表
     */
    @Log(title = "系统通知", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinNotification finNotification)
    {
        List<FinNotification> list = finNotificationService.selectFinNotificationList(finNotification);
        ExcelUtil<FinNotification> util = new ExcelUtil<FinNotification>(FinNotification.class);
        return util.exportExcel(list, "系统通知数据");
    }

    /**
     * 获取系统通知详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finNotificationService.selectFinNotificationById(id));
    }

    /**
     * 新增系统通知
     */
    @Log(title = "系统通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinNotification finNotification)
    {
        finNotification.setCreateBy(getUsername());
        return toAjax(finNotificationService.insertFinNotification(finNotification));
    }

    /**
     * 修改系统通知
     */
    @Log(title = "系统通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinNotification finNotification)
    {
        finNotification.setUpdateBy(getUsername());
        return toAjax(finNotificationService.updateFinNotification(finNotification));
    }

    /**
     * 删除系统通知
     */
    @Log(title = "系统通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finNotificationService.deleteFinNotificationByIds(ids));
    }
    
    /**
     * 将通知标记为已读
     */
    @Log(title = "系统通知", businessType = BusinessType.UPDATE)
    @PutMapping("/read/{id}")
    public AjaxResult read(@PathVariable Long id)
    {
        return toAjax(finNotificationService.markAsRead(id));
    }
    
    /**
     * 批量标记通知为已读
     */
    @Log(title = "系统通知", businessType = BusinessType.UPDATE)
    @PutMapping("/batchRead")
    public AjaxResult batchRead(@RequestBody Map<String, Object> params)
    {
        Long userId = Long.valueOf(params.get("userId").toString());
        List<Long> ids = (List<Long>) params.get("ids");
        
        return toAjax(finNotificationService.batchMarkAsRead(userId, ids));
    }
    
    /**
     * 获取用户未读通知
     */
    @GetMapping("/unread")
    public AjaxResult getUnreadNotifications(Long userId)
    {
        return AjaxResult.success(finNotificationService.selectUnreadNotificationsByUserId(userId));
    }
}