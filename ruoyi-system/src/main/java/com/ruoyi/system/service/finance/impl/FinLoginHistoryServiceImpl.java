package com.ruoyi.system.service.finance.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.finance.FinLoginHistoryMapper;
import com.ruoyi.system.domain.finance.FinLoginHistory;
import com.ruoyi.system.service.finance.IFinLoginHistoryService;

/**
 * 登录历史 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinLoginHistoryServiceImpl implements IFinLoginHistoryService
{
    @Autowired
    private FinLoginHistoryMapper finLoginHistoryMapper;

    /**
     * 查询登录历史信息
     * 
     * @param id 登录历史ID
     * @return 登录历史信息
     */
    @Override
    public FinLoginHistory selectFinLoginHistoryById(Long id)
    {
        return finLoginHistoryMapper.selectFinLoginHistoryById(id);
    }

    /**
     * 查询登录历史列表
     * 
     * @param finLoginHistory 登录历史信息
     * @return 登录历史集合
     */
    @Override
    public List<FinLoginHistory> selectFinLoginHistoryList(FinLoginHistory finLoginHistory)
    {
        return finLoginHistoryMapper.selectFinLoginHistoryList(finLoginHistory);
    }
    
    /**
     * 查询用户最近一次登录记录
     * 
     * @param userId 用户ID
     * @return 登录历史信息
     */
    @Override
    public FinLoginHistory selectLatestLoginByUserId(Long userId)
    {
        return finLoginHistoryMapper.selectLatestLoginByUserId(userId);
    }

    /**
     * 新增登录历史
     * 
     * @param finLoginHistory 登录历史信息
     * @return 结果
     */
    @Override
    public int insertFinLoginHistory(FinLoginHistory finLoginHistory)
    {
        return finLoginHistoryMapper.insertFinLoginHistory(finLoginHistory);
    }

    /**
     * 批量删除登录历史
     * 
     * @param ids 需要删除的登录历史ID
     * @return 结果
     */
    @Override
    public int deleteFinLoginHistoryByIds(Long[] ids)
    {
        return finLoginHistoryMapper.deleteFinLoginHistoryByIds(ids);
    }

    /**
     * 删除登录历史信息
     * 
     * @param id 登录历史ID
     * @return 结果
     */
    @Override
    public int deleteFinLoginHistoryById(Long id)
    {
        return finLoginHistoryMapper.deleteFinLoginHistoryById(id);
    }
}