package com.ruoyi.system.domain.finance;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 财务通知对象 fin_notification
 * 
 * @author ruoyi
 */
public class FinNotification extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 通知ID */
    @Excel(name = "通知序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 类型（budget_warning/system_notice） */
    @Excel(name = "类型", readConverterExp = "budget_warning=预算预警,system_notice=系统通知")
    private String type;

    /** 是否已读 */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private Boolean read;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

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
    
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    
    public void setRead(Boolean read) 
    {
        this.read = read;
    }

    public Boolean getRead() 
    {
        return read;
    }
    
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("type", getType())
            .append("read", getRead())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}