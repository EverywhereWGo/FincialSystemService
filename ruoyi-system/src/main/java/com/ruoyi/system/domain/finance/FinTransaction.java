package com.ruoyi.system.domain.finance;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 财务交易记录对象 fin_transactions
 *
 * @author ruoyi
 */
public class FinTransaction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交易记录ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 交易类型（1：支出，2：收入） */
    @Excel(name = "交易类型", readConverterExp = "1=支出,0=收入")
    private Integer type;

    /** 交易时间 */
    @Excel(name = "交易时间")
    private Long transactionTime;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String note;

    /** 图片路径 */
    private String imagePath;

    /** 位置信息 */
    private String location;

    /** 同步状态（0未同步 1已同步） */
    private Integer syncState;

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

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }

    public void setTransactionTime(Long transactionTime)
    {
        this.transactionTime = transactionTime;
    }

    public Long getTransactionTime()
    {
        return transactionTime;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public String getNote()
    {
        return note;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }

    public void setSyncState(Integer syncState)
    {
        this.syncState = syncState;
    }

    public Integer getSyncState()
    {
        return syncState;
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
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("amount", getAmount())
            .append("type", getType())
            .append("transactionTime", getTransactionTime())
            .append("note", getNote())
            .append("imagePath", getImagePath())
            .append("location", getLocation())
            .append("syncState", getSyncState())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
