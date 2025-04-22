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
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.config.RuoYiConfig;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ruoyi.common.utils.file.MimeTypeUtils;

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
        // 将userId参数映射到id字段
        if (finUser.getId() == null && finUser.getParams().get("userId") != null) {
            finUser.setId(Long.valueOf(finUser.getParams().get("userId").toString()));
        }
        
        int result = finUserService.resetFinUserPwd(finUser);
        if (result == -1) {
            return AjaxResult.error("旧密码不正确");
        } else if (result == 0) {
            return AjaxResult.error("用户不存在");
        }
        return toAjax(result);
    }
    
    /**
     * 编辑个人资料
     */
    @Log(title = "个人资料", businessType = BusinessType.UPDATE)
    @PutMapping("/profile")
    public AjaxResult editProfile(@RequestBody FinUser finUser)
    {
        // 参数验证
        if (finUser.getId() == null && finUser.getParams().get("userId") != null) {
            finUser.setId(Long.valueOf(finUser.getParams().get("userId").toString()));
        }
        
        if (finUser.getId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        
        if (StringUtils.isEmpty(finUser.getNickname())) {
            return AjaxResult.error("昵称不能为空");
        }
        
        if (StringUtils.isEmpty(finUser.getEmail())) {
            return AjaxResult.error("邮箱不能为空");
        }
        
        if (StringUtils.isEmpty(finUser.getPhone())) {
            return AjaxResult.error("手机号码不能为空");
        }
        
        try {
            finUser.setUpdateBy(getUsername());
        } catch (Exception e) {
            finUser.setUpdateBy("mobile_user");
        }
        
        // 检查邮箱和手机号是否被其他用户使用
        String emailUnique = finUserService.checkEmailUnique(finUser);
        if (!"0".equals(emailUnique)) {
            return AjaxResult.error("修改个人资料失败，邮箱地址已被使用");
        }
        
        String phoneUnique = finUserService.checkPhoneUnique(finUser);
        if (!"0".equals(phoneUnique)) {
            return AjaxResult.error("修改个人资料失败，手机号码已被使用");
        }
        
        int result = finUserService.updateFinUser(finUser);
        if (result > 0) {
            return AjaxResult.success("个人资料修改成功");
        }
        return AjaxResult.error("修改个人资料失败，请联系管理员");
    }
    
    /**
     * 上传用户头像
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult uploadAvatar(MultipartFile avatarfile, Long userId) throws Exception
    {
        if (!avatarfile.isEmpty())
        {
            Long currentUserId = null;
            try {
                // 如果未提供用户ID，则使用当前登录用户ID
                currentUserId = (userId != null) ? userId : getUserId();
            } catch (Exception e) {
                return AjaxResult.error("获取用户信息异常，请确保已登录或在请求中提供userId");
            }
            
            // 上传文件路径
            String avatarPath = RuoYiConfig.getAvatarPath();
            // 上传并返回新文件名称
            String avatar = FileUploadUtils.upload(avatarPath, avatarfile, MimeTypeUtils.IMAGE_EXTENSION);
            
            // 直接返回成功和图片URL，不再保存到用户对象中，因为avatar字段已移除
            AjaxResult ajax = AjaxResult.success("头像上传成功");
            ajax.put("imgUrl", avatar);
            return ajax;
        }
        return AjaxResult.error("上传头像异常，请重新上传");
    }
}