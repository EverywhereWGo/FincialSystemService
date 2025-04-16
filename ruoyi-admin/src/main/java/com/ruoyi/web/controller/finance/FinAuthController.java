package com.ruoyi.web.controller.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.finance.FinUser;
import com.ruoyi.system.domain.finance.FinLoginHistory;
import com.ruoyi.system.service.finance.IFinUserService;
import com.ruoyi.system.service.finance.IFinLoginHistoryService;

/**
 * 认证管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/auth")
public class FinAuthController extends BaseController
{
    @Autowired
    private IFinUserService finUserService;

    @Autowired
    private IFinLoginHistoryService finLoginHistoryService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, Object> loginParams)
    {
        String username = (String) loginParams.get("username");
        String password = (String) loginParams.get("password");
        String deviceInfo = (String) loginParams.get("deviceInfo");
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            return AjaxResult.error("用户名或密码不能为空");
        }
        
        // 根据用户名查询用户
        FinUser user = finUserService.selectFinUserByUsername(username);
        
        // 登录历史记录
        FinLoginHistory loginHistory = new FinLoginHistory();
        loginHistory.setLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()));
        loginHistory.setIpAddress(IpUtils.getIpAddr());
        loginHistory.setDeviceInfo(deviceInfo);
        
        // 用户不存在
        if (user == null)
        {
            loginHistory.setSuccess(false);
            loginHistory.setUserId(0L); // 临时设置无效用户ID
            finLoginHistoryService.insertFinLoginHistory(loginHistory);
            return AjaxResult.error("用户不存在");
        }
        
        loginHistory.setUserId(user.getId());
        
        // 验证密码
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            // 密码错误，增加失败次数
            user.setFailedAttempts(user.getFailedAttempts() == null ? 1 : user.getFailedAttempts() + 1);
            
            // 超过5次锁定账户
            if (user.getFailedAttempts() >= 5)
            {
                // 锁定1小时
                Date lockedUntil = DateUtils.addHours(new Date(), 1);
                user.setLockedUntil(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", lockedUntil));
            }
            
            finUserService.updateFinUserLoginInfo(user);
            
            loginHistory.setSuccess(false);
            finLoginHistoryService.insertFinLoginHistory(loginHistory);
            
            if (user.getFailedAttempts() >= 5)
            {
                return AjaxResult.error("账户已被锁定，请1小时后再试");
            }
            
            return AjaxResult.error("密码错误");
        }
        
        // 检查账户是否被锁定
        if (StringUtils.isNotEmpty(user.getLockedUntil()))
        {
            Date lockedUntil = DateUtils.parseDate(user.getLockedUntil());
            if (lockedUntil != null && lockedUntil.after(new Date()))
            {
                loginHistory.setSuccess(false);
                finLoginHistoryService.insertFinLoginHistory(loginHistory);
                return AjaxResult.error("账户已被锁定，请稍后再试");
            }
        }
        
        // 重置失败次数和锁定时间
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()));
        finUserService.updateFinUserLoginInfo(user);
        
        // 记录登录成功
        loginHistory.setSuccess(true);
        finLoginHistoryService.insertFinLoginHistory(loginHistory);
        
        // 生成登录令牌 - 使用TokenService生成JWT格式的Token
        com.ruoyi.common.core.domain.entity.SysUser sysUser = new com.ruoyi.common.core.domain.entity.SysUser();
        sysUser.setUserId(user.getId());
        sysUser.setUserName(user.getUsername());
        sysUser.setNickName(user.getNickname());
        
        com.ruoyi.common.core.domain.model.LoginUser loginUser = new com.ruoyi.common.core.domain.model.LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUser(sysUser);
        
        com.ruoyi.framework.web.service.TokenService tokenService = com.ruoyi.common.utils.spring.SpringUtils.getBean(com.ruoyi.framework.web.service.TokenService.class);
        String token = tokenService.createToken(loginUser);
        long expiryTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000; // 7天有效期
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("expiryTime", expiryTime);
        
        // 移除敏感信息
        user.setPassword(null);
        user.setSalt(null);
        user.setFailedAttempts(null);
        user.setLockedUntil(null);
        result.put("user", user);
        
        return AjaxResult.success("登录成功", result);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody FinUser finUser)
    {
        if (StringUtils.isEmpty(finUser.getUsername()) || StringUtils.isEmpty(finUser.getPassword()))
        {
            return AjaxResult.error("用户名或密码不能为空");
        }
        
        // 校验用户名是否唯一
        if ("1".equals(finUserService.checkUsernameUnique(finUser.getUsername())))
        {
            return AjaxResult.error("注册失败，该用户名已被使用");
        }
        
        // 校验手机号是否唯一
        if (StringUtils.isNotEmpty(finUser.getPhone()) 
                && "1".equals(finUserService.checkPhoneUnique(finUser)))
        {
            return AjaxResult.error("注册失败，该手机号已被使用");
        }
        
        // 校验邮箱是否唯一
        if (StringUtils.isNotEmpty(finUser.getEmail()) 
                && "1".equals(finUserService.checkEmailUnique(finUser)))
        {
            return AjaxResult.error("注册失败，该邮箱已被使用");
        }
        
        // 设置默认角色
        finUser.setRole("user");
        
        // 生成随机盐值
        String salt = StringUtils.randomStr(6);
        finUser.setSalt(salt);
        
        // 加密密码
        finUser.setPassword(SecurityUtils.encryptPassword(finUser.getPassword()));
        
        // 设置默认值
        finUser.setFailedAttempts(0);
        finUser.setCreateBy("system");
        
        // 保存用户
        int rows = finUserService.insertFinUser(finUser);
        if (rows > 0)
        {
            return AjaxResult.success("注册成功");
        }
        
        return AjaxResult.error("注册失败，请联系管理员");
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public AjaxResult logout(@RequestBody Map<String, Object> logoutParams)
    {
        String token = (String) logoutParams.get("token");
        Long userId = null;
        
        // 处理userId可能是Integer或Long类型的情况
        Object userIdObj = logoutParams.get("userId");
        if (userIdObj != null)
        {
            if (userIdObj instanceof Integer)
            {
                userId = ((Integer) userIdObj).longValue();
            }
            else if (userIdObj instanceof Long)
            {
                userId = (Long) userIdObj;
            }
        }
        
        if (userId != null)
        {
            // 记录登出日志
            FinLoginHistory logoutHistory = new FinLoginHistory();
            logoutHistory.setUserId(userId);
            logoutHistory.setLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()));
            logoutHistory.setIpAddress(IpUtils.getIpAddr());
            logoutHistory.setSuccess(true);
            logoutHistory.setRemark("用户主动登出");
            finLoginHistoryService.insertFinLoginHistory(logoutHistory);
            
            // 这里可以实现token失效处理
            // 实际项目中应该将token加入黑名单或从缓存中删除
        }
        
        return AjaxResult.success("退出成功");
    }
    
    /**
     * 用户注销账号
     */
    @PostMapping("/unregister")
    public AjaxResult unregister(@RequestBody Map<String, Object> unregisterParams)
    {
        Long userId = null;
        
        // 处理userId可能是Integer或Long类型的情况
        Object userIdObj = unregisterParams.get("userId");
        if (userIdObj != null)
        {
            if (userIdObj instanceof Integer)
            {
                userId = ((Integer) userIdObj).longValue();
            }
            else if (userIdObj instanceof Long)
            {
                userId = (Long) userIdObj;
            }
        }
        
        if (userId == null)
        {
            return AjaxResult.error("用户ID不能为空");
        }
        
        // 查询用户信息
        FinUser user = finUserService.selectFinUserById(userId);
        if (user == null)
        {
            return AjaxResult.error("用户不存在");
        }
        
        // 记录注销日志
        FinLoginHistory unregisterHistory = new FinLoginHistory();
        unregisterHistory.setUserId(userId);
        unregisterHistory.setLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()));
        unregisterHistory.setIpAddress(IpUtils.getIpAddr());
        unregisterHistory.setSuccess(true);
        unregisterHistory.setRemark("用户注销账号");
        finLoginHistoryService.insertFinLoginHistory(unregisterHistory);
        
        // 删除用户账号
        int result = finUserService.deleteFinUserById(userId);
        if (result > 0) 
        {
            return AjaxResult.success("账号注销成功");
        }
        else
        {
            return AjaxResult.error("账号注销失败，请联系管理员");
        }
    }
}