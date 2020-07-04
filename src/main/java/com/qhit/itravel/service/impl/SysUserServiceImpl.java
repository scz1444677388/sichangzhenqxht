package com.qhit.itravel.service.impl;

import com.qhit.itravel.dto.UserDto;
import com.qhit.itravel.entity.Role;
import com.qhit.itravel.entity.SysPermission;
import com.qhit.itravel.entity.SysUser;
import com.qhit.itravel.dao.SysUserDao;
import com.qhit.itravel.service.SysUserService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * (SysUser)表服务实现类
 *
 * @author makejava
 * @since 2020-03-11 10:12:57
 */
@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserDao sysUserDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SysUser queryById(Integer id) {
        return this.sysUserDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<SysUser> queryAllByLimit(int offset, int limit) {
        return this.sysUserDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param sysUser 实例对象
     * @return 实例对象
     */
    @Override
    public SysUser insert(SysUser sysUser) {
        this.sysUserDao.insert(sysUser);
        return sysUser;
    }

    /**
     * 修改数据
     *
     * @param sysUser 实例对象
     * @return 实例对象
     */
    @Override
    public SysUser update(SysUser sysUser) {
        this.sysUserDao.update(sysUser);
        return this.queryById(sysUser.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.sysUserDao.deleteById(id) > 0;
    }

    @Override
    public SysUser findUserByName(String fusername) {
        return sysUserDao.findUserByName(fusername);
    }

    @Override
    public String passwordEncoder(String credentials, String salt) {
        Object object = new SimpleHash("MD5", credentials, salt, 3);
        return object.toString();
    }

    @Override
    public List<Role> findUserRoleByUId(Integer id) {
        return sysUserDao.findUserRoleByUId(id);
    }

    @Override
    public int count(Map<String, Object> params) {
        return sysUserDao.count(params);
    }

    @Override
    public List<SysUser> getpageData(Map<String, Object> params, Integer offset, Integer limit) {
        return sysUserDao.getpageData(params,offset,limit);
    }

    @Override
    @Transactional
    public SysUser addUser(UserDto userDto) {
        //1密码
        SysUser user=userDto;
        //加盐值,加密
        String salt= DigestUtils.md5DigestAsHex((UUID.randomUUID().toString()
                +System.currentTimeMillis()+toString()).getBytes());
        user.setSalt(salt);
        user.setPassword(passwordEncoder(user.getPassword(),user.getSalt()));
        //状态不能为空 先为1
        user.setStatus(1L);
        //2处理用户表
        sysUserDao.insert(user);
        //3用户角色表
        savaUserRole(user.getId(),userDto.getRoleIds());

        return user;
    }

    //修改的
    @Override
    @Transactional
    public SysUser editUser(UserDto userDto) {
        //1修改用户信
        SysUser user=userDto;
        sysUserDao.update(user);
        //2更新用户的角色
        savaUserRole(user.getId(),userDto.getRoleIds());
        return userDto;
    }


    private void savaUserRole(Integer id, List<Long> roleIds) {
        if (roleIds!=null){
            //先删除该用户已有的角色
            sysUserDao.deleteUserRole(id);
            if (roleIds.size()>0){
                sysUserDao.saveUserRoles(id,roleIds);
            }
        }

    }


}