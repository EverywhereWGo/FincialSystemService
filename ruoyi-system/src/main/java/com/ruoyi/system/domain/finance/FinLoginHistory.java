package com.ruoyi.system.domain.finance;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 登录历史对象 fin_login_history
 * 
 * @author ruoyi
 */
public class FinLoginHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 历史ID */
    @Excel(name = "历史序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 登录时间 */
    @Excel(name = "登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String loginTime;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 设备信息 */
    @Excel(name = "设备信息")
    private String deviceInfo;

    /** 是否成功 */
    @Excel(name = "是否成功", readConverterExp = "0=失败,1=成功")
    private Boolean success;

    
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    
    public void setLoginTime(String loginTime) 
    {
        this.loginTime = loginTime;
    }

    public String getLoginTime() 
    {
        return loginTime;
    }
    
    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }
    
    public void setDeviceInfo(String deviceInfo) 
    {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() 
    {
        return deviceInfo;
    }
    
    public void setSuccess(Boolean success) 
    {
        this.success = success;
    }

    public Boolean getSuccess() 
    {
        return success;
    }
    
    

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("loginTime", getLoginTime())
            .append("ipAddress", getIpAddress())
            .append("deviceInfo", getDeviceInfo())
            .append("success", getSuccess())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}