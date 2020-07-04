package com.qhit.itravel.controller;

import com.qhit.itravel.entity.SysPermission;
import com.qhit.itravel.entity.SysUser;
import com.qhit.itravel.service.SysPermissionService;
import com.qhit.itravel.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * (SysPermission)表控制层
 *
 * @author makejava
 * @since 2020-03-12 16:06:37
 */
@RestController
@RequestMapping("/permissions")
public class SysPermissionController {
    /**
     * 服务对象
     */
    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public SysPermission selectOne(Integer id) {
        return this.sysPermissionService.queryById(id);
    }

    @PostMapping("/current")
    public List<SysPermission> getpermissionsCueernt(){
        List<SysPermission> l = UserUtil.getCurrentPermissions();
        if (l==null){
            SysUser currentUser = UserUtil.getCurrentUser();
           l= sysPermissionService.findPermissionByUserId(currentUser.getId());
            UserUtil.setPermissionSession(l);
        }

        //获取type等于1的菜单
        List<SysPermission> permissionsType1=new ArrayList<>();
        List<SysPermission> permissionsType0=new ArrayList<>();

        for (SysPermission sysPermission : l) {
            if (sysPermission.getType()==1){
                permissionsType1.add(sysPermission);
            }

            if (sysPermission.getParentid()==0){
                permissionsType0.add(sysPermission);
            }

        }
        //为1级菜单绑定子菜单
        for (SysPermission sysPermission : permissionsType0) {
            sczChind(sysPermission,permissionsType1);
        }

        return permissionsType0;

    }

    //遍历绑
    private void sczChind(SysPermission p, List<SysPermission> permissions) {
        List<SysPermission> chidren=new ArrayList<>();
        for (SysPermission sysPermission : permissions) {
            if (p.getId()==sysPermission.getParentid()){
                chidren.add(sysPermission);
            }

        }
        p.setChild(chidren);

        ///递归处理
        if (chidren!=null && chidren.size()>0){
            for (SysPermission child : chidren) {
                //递归
                sczChind(child,permissions);
            }

        }


    }
    //效验权限
    @GetMapping("/owns")

    public Set<String> ownsPermission() {
        List<SysPermission> permissions = UserUtil.getCurrentPermissions();
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptySet();
        }

        return permissions.parallelStream().filter(p -> !StringUtils.isEmpty(p.getPermission()))
                .map(SysPermission::getPermission).collect(Collectors.toSet());
    }
    //回显菜单添加里的 上级菜单
    @GetMapping("/parents")
    @RequiresPermissions("sys:menu:query")
    public List<SysPermission> getParentsMenus(){
        return sysPermissionService.getParentsMenus();
    }
    //添加菜单
    @PostMapping
    @RequiresPermissions("sys:menu:add")
    public void addMenu(@RequestBody SysPermission sysPermission){
        sysPermissionService.insert(sysPermission);
    }
    //删除菜单
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:menu:delete")
    public void deleteMenus(@PathVariable Integer id){
        sysPermissionService.deleteById(id);
    }
    //回显修改菜单数据
    @GetMapping("/{id}")
    @RequiresPermissions("sys:menu:query")
    public SysPermission getMenusById(@PathVariable Integer id){
      return sysPermissionService.queryById(id);
    }
    //修改菜单数据
    @PutMapping
    @RequiresPermissions("sys:menu:add")
    public void updataMenus(@RequestBody SysPermission sysPermission){
        sysPermissionService.update(sysPermission);
    }



}