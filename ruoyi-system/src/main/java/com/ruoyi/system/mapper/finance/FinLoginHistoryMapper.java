package com.ruoyi.system.mapper.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinLoginHistory;

/**
 * 登录历史 数据层
 * 
 * @author ruoyi
 */
public interface FinLoginHistoryMapper
{
    /**
     * 查询登录历史信息
     * 
     * @param id 登录历史ID
     * @return 登录历史信息
     */
    public FinLoginHistory selectFinLoginHistoryById(Long id);

    /**
     * 查询登录历史列表
     * 
     * @param finLoginHistory 登录历史信息
     * @return 登录历史集合
     */
    public List<FinLoginHistory> selectFinLoginHistoryList(FinLoginHistory finLoginHistory);
    
    /**
     * 查询用户最近一次登录记录
     * 
     * @param userId 用户ID
     * @return 登录历史信息
     */
    public FinLoginHistory selectLatestLoginByUserId(Long userId);

    /**
     * 新增登录历史
     * 
     * @param finLoginHistory 登录历史信息
     * @return 结果
     */
    public int insertFinLoginHistory(FinLoginHistory finLoginHistory);

    /**
     * 删除登录历史
     * 
     * @param id 登录历史ID
     * @return 结果
     */
    public int deleteFinLoginHistoryById(Long id);

    /**
     * 批量删除登录历史
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFinLoginHistoryByIds(Long[] ids);
}