package com.ruoyi.system.domain.finance;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 财务系统预算表 fin_budgets
 * 
 * @author ruoyi
 */
public class FinBudget extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预算ID */
    @Excel(name = "预算序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID", type = Excel.Type.IMPORT)
    private Long userId;

    /** 分类ID（NULL表示总预算） */
    @Excel(name = "分类ID", type = Excel.Type.IMPORT)
    private Long categoryId;

    /** 分类名称（非数据库字段） */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 年份 */
    @Excel(name = "年份")
    private Integer year;

    /** 月份 */
    @Excel(name = "月份")
    private Integer month;

    /** 预算金额 */
    @Excel(name = "预算金额", cellType = ColumnType.NUMERIC)
    private BigDecimal amount;

    /** 预警阈值（百分比） */
    @Excel(name = "预警阈值", cellType = ColumnType.NUMERIC, suffix = "%")
    private BigDecimal warningThreshold;

    /** 是否已预警 */
    @Excel(name = "是否已预警", readConverterExp = "0=否,1=是")
    private Boolean warned;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 已使用金额（非数据库字段） */
    @Excel(name = "已使用金额", cellType = ColumnType.NUMERIC)
    private BigDecimal usedAmount;

    /** 使用百分比（非数据库字段） */
    @Excel(name = "使用百分比", cellType = ColumnType.NUMERIC, suffix = "%")
    private BigDecimal usedPercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull(message = "用户ID不能为空")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @NotNull(message = "年份不能为空")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @NotNull(message = "月份不能为空")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.01", message = "预算金额必须大于0")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull(message = "预警阈值不能为空")
    @DecimalMin(value = "0.00", message = "预警阈值不能小于0")
    @DecimalMax(value = "100.00", message = "预警阈值不能大于100")
    public BigDecimal getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(BigDecimal warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public Boolean getWarned() {
        return warned;
    }

    public void setWarned(Boolean warned) {
        this.warned = warned;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getUsedPercentage() {
        return usedPercentage;
    }

    public void setUsedPercentage(BigDecimal usedPercentage) {
        this.usedPercentage = usedPercentage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("year", getYear())
            .append("month", getMonth())
            .append("amount", getAmount())
            .append("warningThreshold", getWarningThreshold())
            .append("warned", getWarned())
            .append("delFlag", getDelFlag())
            .append("usedAmount", getUsedAmount())
            .append("usedPercentage", getUsedPercentage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 