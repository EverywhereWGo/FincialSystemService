package com.ruoyi.system.domain.finance;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 财务分类对象 fin_categories
 * 
 * @author ruoyi
 */
public class FinCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @Excel(name = "分类序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String name;

    /** 分类类型（1：支出，2：收入） */
    @Excel(name = "分类类型", readConverterExp = "1=支出,2=收入")
    private Integer type;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 颜色 */
    @Excel(name = "颜色")
    private String color;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer displayOrder;

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
    
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    
    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }
    
    public void setColor(String color) 
    {
        this.color = color;
    }

    public String getColor() 
    {
        return color;
    }
    
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    
    public void setDisplayOrder(Integer displayOrder) 
    {
        this.displayOrder = displayOrder;
    }

    public Integer getDisplayOrder() 
    {
        return displayOrder;
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
            .append("name", getName())
            .append("type", getType())
            .append("icon", getIcon())
            .append("color", getColor())
            .append("userId", getUserId())
            .append("displayOrder", getDisplayOrder())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 