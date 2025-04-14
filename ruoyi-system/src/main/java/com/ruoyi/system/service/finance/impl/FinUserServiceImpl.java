package com.ruoyi.system.service.finance.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.finance.FinUserMapper;
import com.ruoyi.system.domain.finance.FinUser;
import com.ruoyi.system.service.finance.IFinUserService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 用户 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class FinUserServiceImpl implements IFinUserService
{
    @Autowired
    private FinUserMapper finUserMapper;

    /**
     * 查询用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public FinUser selectFinUserById(Long id)
    {
        return finUserMapper.selectFinUserById(id);
    }

    /**
     * 查询用户列表
     * 
     * @param finUser 用户信息
     * @return 用户集合
     */
    @Override
    public List<FinUser> selectFinUserList(FinUser finUser)
    {
        return finUserMapper.selectFinUserList(finUser);
    }

    /**
     * 通过用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象信息
     */
    @Override
    public FinUser selectFinUserByUsername(String username)
    {
        return finUserMapper.selectFinUserByUsername(username);
    }
    
    /**
     * 通过手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户对象信息
     */
    @Override
    public FinUser selectFinUserByPhone(String phone)
    {
        return finUserMapper.selectFinUserByPhone(phone);
    }
    
    /**
     * 通过邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户对象信息
     */
    @Override
    public FinUser selectFinUserByEmail(String email)
    {
        return finUserMapper.selectFinUserByEmail(email);
    }
    
    /**
     * 新增用户
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    @Override
    public int insertFinUser(FinUser finUser)
    {
        return finUserMapper.insertFinUser(finUser);
    }

    /**
     * 修改用户
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    @Override
    public int updateFinUser(FinUser finUser)
    {
        return finUserMapper.updateFinUser(finUser);
    }

    /**
     * 重置用户密码
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    @Override
    public int resetFinUserPwd(FinUser finUser)
    {
        // 验证旧密码是否正确
        FinUser originalUser = finUserMapper.selectFinUserById(finUser.getId());
        if (originalUser == null) {
            return 0; // 用户不存在
        }
        
        // 验证旧密码是否正确
        if (!SecurityUtils.matchesPassword(finUser.getOldPassword(), originalUser.getPassword())) {
            return -1; // 旧密码不正确
        }
        
        // 生成新的盐值（可选）
        String salt = StringUtils.randomStr(6);
        finUser.setSalt(salt);
        
        // 加密新密码
        finUser.setPassword(SecurityUtils.encryptPassword(finUser.getNewPassword()));
        
        return finUserMapper.resetFinUserPwd(finUser);
    }
    
    /**
     * 修改用户登录信息
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    @Override
    public int updateFinUserLoginInfo(FinUser finUser)
    {
        return finUserMapper.updateFinUserLoginInfo(finUser);
    }

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的用户ID
     * @return 结果
     */
    @Override
    public int deleteFinUserByIds(Long[] ids)
    {
        return finUserMapper.deleteFinUserByIds(ids);
    }

    /**
     * 删除用户信息
     * 
     * @param id 用户ID
     * @return 结果
     */
    @Override
    public int deleteFinUserById(Long id)
    {
        return finUserMapper.deleteFinUserById(id);
    }
    
    /**
     * 校验用户名是否唯一
     * 
     * @param username 用户名
     * @return 结果
     */
    @Override
    public String checkUsernameUnique(String username)
    {
        int count = finUserMapper.checkUsernameUnique(username);
        if (count > 0)
        {
            return UserConstants.NOT_UNIQUE ? "1" : "0";
        }
        return UserConstants.UNIQUE ? "0" : "1";
    }
    
    /**
     * 校验手机号是否唯一
     *
     * @param finUser 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(FinUser finUser)
    {
        Long userId = StringUtils.isNull(finUser.getId()) ? -1L : finUser.getId();
        FinUser info = finUserMapper.selectFinUserByPhone(finUser.getPhone());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE ? "1" : "0";
        }
        return UserConstants.UNIQUE ? "0" : "1";
    }
    
    /**
     * 校验email是否唯一
     *
     * @param finUser 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(FinUser finUser)
    {
        Long userId = StringUtils.isNull(finUser.getId()) ? -1L : finUser.getId();
        FinUser info = finUserMapper.selectFinUserByEmail(finUser.getEmail());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE ? "1" : "0";
        }
        return UserConstants.UNIQUE ? "0" : "1";
    }
}