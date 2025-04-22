package com.ruoyi.system.domain.finance;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户对象 fin_users
 * 
 * @author ruoyi
 */
public class FinUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    private String password;

    /** 旧密码 */
    private String oldPassword;

    /** 新密码 */
    private String newPassword;

    /** 密码盐值 */
    private String salt;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String phone;

    /** 角色（admin/user） */
    @Excel(name = "角色", readConverterExp = "admin=管理员,user=普通用户")
    private String role;

    /** 登录失败次数 */
    private Integer failedAttempts;

    /** 锁定截止时间 */
    private String lockedUntil;

    /** 微信号 */
    @Excel(name = "微信号")
    private String wechat;

    /** QQ号 */
    @Excel(name = "QQ号")
    private String qq;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 最后登录时间 */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String lastLoginTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    
    public void setUsername(String username) 
    {
        this.username = username;
    }

    @NotBlank(message = "用户名不能为空")
    @Size(min = 0, max = 30, message = "用户名长度不能超过30个字符")
    public String getUsername() 
    {
        return username;
    }
    
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    
    public void setOldPassword(String oldPassword) 
    {
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() 
    {
        return oldPassword;
    }
    
    public void setNewPassword(String newPassword) 
    {
        this.newPassword = newPassword;
    }

    public String getNewPassword() 
    {
        return newPassword;
    }
    
    public void setSalt(String salt) 
    {
        this.salt = salt;
    }

    public String getSalt() 
    {
        return salt;
    }
    
    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
    }
    
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    
    public void setRole(String role) 
    {
        this.role = role;
    }

    public String getRole() 
    {
        return role;
    }
    
    public void setFailedAttempts(Integer failedAttempts) 
    {
        this.failedAttempts = failedAttempts;
    }

    public Integer getFailedAttempts() 
    {
        return failedAttempts;
    }
    
    public void setLockedUntil(String lockedUntil) 
    {
        this.lockedUntil = lockedUntil;
    }

    public String getLockedUntil() 
    {
        return lockedUntil;
    }
    
    public void setWechat(String wechat) 
    {
        this.wechat = wechat;
    }

    public String getWechat() 
    {
        return wechat;
    }
    
    public void setQq(String qq) 
    {
        this.qq = qq;
    }

    public String getQq() 
    {
        return qq;
    }
    
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    
    public void setLastLoginTime(String lastLoginTime) 
    {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginTime() 
    {
        return lastLoginTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("oldPassword", getOldPassword())
            .append("newPassword", getNewPassword())
            .append("salt", getSalt())
            .append("nickname", getNickname())
            .append("name", getName())
            .append("email", getEmail())
            .append("phone", getPhone())
            .append("role", getRole())
            .append("failedAttempts", getFailedAttempts())
            .append("lockedUntil", getLockedUntil())
            .append("wechat", getWechat())
            .append("qq", getQq())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("lastLoginTime", getLastLoginTime())
            .append("remark", getRemark())
            .toString();
    }
}