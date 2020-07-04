package com.qhit.itravel.service.impl;

import com.qhit.itravel.entity.SysPermission;
import com.qhit.itravel.dao.SysPermissionDao;
import com.qhit.itravel.service.SysPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (SysPermission)表服务实现类
 *
 * @author makejava
 * @since 2020-03-12 16:06:37
 */
@Service("sysPermissionService")
public class SysPermissionServiceImpl implements SysPermissionService {
    @Resource
    private SysPermissionDao sysPermissionDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SysPermission queryById(Integer id) {
        return this.sysPermissionDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<SysPermission> queryAllByLimit(int offset, int limit) {
        return this.sysPermissionDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param sysPermission 实例对象
     * @return 实例对象
     */
    @Override
    public SysPermission insert(SysPermission sysPermission) {
        this.sysPermissionDao.insert(sysPermission);
        return sysPermission;
    }

    /**
     * 修改数据
     *
     * @param sysPermission 实例对象
     * @return 实例对象
     */
    @Override
    public SysPermission update(SysPermission sysPermission) {
        this.sysPermissionDao.update(sysPermission);
        return this.queryById(sysPermission.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.sysPermissionDao.deleteById(id) > 0;
    }

    @Override
    public List<SysPermission> findPermissionByUserId(Integer id) {
        return sysPermissionDao.findPermissionsByUserId(id);
    }

    @Override
    public List<SysPermission> getAll() {
        return sysPermissionDao.getAll();
    }

    @Override
    public List<SysPermission> getqAll() {
        return sysPermissionDao.getqAll();
    }

    @Override
    public List<SysPermission> getParentsMenus() {
        return sysPermissionDao.getParentsMenus();
    }
}