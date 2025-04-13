package com.ruoyi.system.service.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FinUser;

/**
 * 用户 服务层
 * 
 * @author ruoyi
 */
public interface IFinUserService
{
    /**
     * 查询用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    public FinUser selectFinUserById(Long id);

    /**
     * 查询用户列表
     * 
     * @param finUser 用户信息
     * @return 用户集合
     */
    public List<FinUser> selectFinUserList(FinUser finUser);
    
    /**
     * 通过用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象信息
     */
    public FinUser selectFinUserByUsername(String username);
    
    /**
     * 通过手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户对象信息
     */
    public FinUser selectFinUserByPhone(String phone);
    
    /**
     * 通过邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户对象信息
     */
    public FinUser selectFinUserByEmail(String email);

    /**
     * 新增用户
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public int insertFinUser(FinUser finUser);

    /**
     * 修改用户
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public int updateFinUser(FinUser finUser);
    
    /**
     * 重置用户密码
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public int resetFinUserPwd(FinUser finUser);
    
    /**
     * 修改用户登录信息
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public int updateFinUserLoginInfo(FinUser finUser);

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的用户ID
     * @return 结果
     */
    public int deleteFinUserByIds(Long[] ids);

    /**
     * 删除用户信息
     * 
     * @param id 用户ID
     * @return 结果
     */
    public int deleteFinUserById(Long id);
    
    /**
     * 校验用户名是否唯一
     * 
     * @param username 用户名
     * @return 结果
     */
    public String checkUsernameUnique(String username);
    
    /**
     * 校验手机号是否唯一
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public String checkPhoneUnique(FinUser finUser);
    
    /**
     * 校验email是否唯一
     * 
     * @param finUser 用户信息
     * @return 结果
     */
    public String checkEmailUnique(FinUser finUser);
}