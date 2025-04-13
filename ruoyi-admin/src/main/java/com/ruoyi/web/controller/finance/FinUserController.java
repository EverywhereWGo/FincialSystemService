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
import com.ruoyi.system.domain.finance.FinUser;
import com.ruoyi.system.service.finance.IFinUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 用户管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/user")
public class FinUserController extends BaseController
{
    @Autowired
    private IFinUserService finUserService;

    /**
     * 查询财务系统用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(FinUser finUser)
    {
        startPage();
        List<FinUser> list = finUserService.selectFinUserList(finUser);
        return getDataTable(list);
    }

    /**
     * 导出财务系统用户列表
     */
    @Log(title = "财务系统用户", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FinUser finUser)
    {
        List<FinUser> list = finUserService.selectFinUserList(finUser);
        ExcelUtil<FinUser> util = new ExcelUtil<FinUser>(FinUser.class);
        return util.exportExcel(list, "财务系统用户数据");
    }

    /**
     * 获取财务系统用户详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(finUserService.selectFinUserById(id));
    }

    /**
     * 新增财务系统用户
     */
    @Log(title = "财务系统用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinUser finUser)
    {
        // 新增用户信息
        finUser.setCreateBy(getUsername());
        // 生成随机盐值
        String salt = StringUtils.randomStr(6);
        finUser.setSalt(salt);
        // 加密处理密码
        if (StringUtils.isNotEmpty(finUser.getPassword()))
        {
            finUser.setPassword(SecurityUtils.encryptPassword(finUser.getPassword()));
        }
        return toAjax(finUserService.insertFinUser(finUser));
    }

    /**
     * 修改财务系统用户
     */
    @Log(title = "财务系统用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinUser finUser)
    {
        finUser.setUpdateBy(getUsername());
        return toAjax(finUserService.updateFinUser(finUser));
    }

    /**
     * 删除财务系统用户
     */
    @Log(title = "财务系统用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(finUserService.deleteFinUserByIds(ids));
    }
    
    /**
     * 重置用户密码
     */
    @Log(title = "财务系统用户", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody FinUser finUser)
    {
        finUser.setUpdateBy(getUsername());
        return toAjax(finUserService.resetFinUserPwd(finUser));
    }
}