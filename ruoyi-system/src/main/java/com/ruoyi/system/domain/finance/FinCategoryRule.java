package com.ruoyi.system.domain.finance;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分类规则对象 fin_category_rules
 * 
 * @author ruoyi
 */
public class FinCategoryRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    @Excel(name = "规则序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;
    
    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 匹配模式 */
    @Excel(name = "匹配模式")
    private String pattern;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

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
    
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    
    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }
    
    public void setPattern(String pattern) 
    {
        this.pattern = pattern;
    }

    public String getPattern() 
    {
        return pattern;
    }
    
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
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
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("pattern", getPattern())
            .append("userId", getUserId())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}