package com.qhit.itravel.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qhit.itravel.dao.SysUserDao;
import com.qhit.itravel.dto.UserDto;
import com.qhit.itravel.entity.Role;
import com.qhit.itravel.entity.SysPermission;
import com.qhit.itravel.entity.SysUser;
import com.qhit.itravel.service.SysPermissionService;
import com.qhit.itravel.service.SysUserService;
import com.qhit.itravel.utils.UserUtil;
import com.qhit.itravel.utils.page.PageTableHandler;
import com.qhit.itravel.utils.page.PageTableRequest;
import com.qhit.itravel.utils.page.PageTableResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (SysUser)表控制层
 *
 * @author makejava
 * @since 2020-03-11 10:12:58
 */
@RestController
@RequestMapping("sys")
//@RequiresPermissions("sys:user:add")
public class SysUserController {
    /**
     * 服务对象
     */
    @Resource
    private SysPermissionService sysPermissionService;

    @Resource
    private SysUserService sysUserService;
    @Autowired
    private ServerProperties serverProperties;


    @GetMapping("/users")
    //需要权限才能访问 查询全部 分页
    @RequiresPermissions("sys:user:query ")
    public PageTableResponse getAllUsers(PageTableRequest request){
        PageTableHandler pageTableHandler=new PageTableHandler(new pageCout(),new PageList());

        PageTableResponse handle = pageTableHandler.handle(request);
        return handle;
    }

    @RequestMapping("/login")
    public void login(String username,String password){
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

        Subject subject = SecurityUtils.getSubject();
        subject.login(usernamePasswordToken);

        //给shiro的session过期时间
        subject.getSession().setTimeout(1000000);
        // 设置shiro的session过期时间
        SecurityUtils.getSubject().getSession().setTimeout(serverProperties.getServlet().getSession().getTimeout().toMillis());

    }



    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    @RequiresPermissions("sys:user:query ")
    public SysUser selectOne(Integer id) {
        return this.sysUserService.queryById(id);
    }


    //内部类 分页条件
    class pageCout implements PageTableHandler.CountHandler{

        @Override
        public int count(PageTableRequest request) {
           int rows =sysUserService.count(request.getParams());
            return rows;
        }
    }
    //内部类 查询全部
    class PageList implements PageTableHandler.ListHandler{

        @Override
        public List<?> list(PageTableRequest request) {
            Integer offset = request.getOffset();
            Integer limit = request.getLimit();
            List<SysUser> list=sysUserService.getpageData(request.getParams(),offset,limit);
            return list;
        }
    }

    @RequestMapping("/add")
    @RequiresPermissions("sys:user:add")
    public SysUser saveUser(@RequestBody UserDto userDto){
        //保存之前先查找，根据名字差查找，判断是否已经注册过
        SysUser user=sysUserService.findUserByName(userDto.getUsername());

        if(user!=null){
            throw new IllegalArgumentException(userDto.getUsername()+"该用户名已经存在");
        }

        return sysUserService.addUser(userDto);
    }

    //修改操作
    @RequestMapping("/edit")
    @RequiresPermissions("sys:user:add")
    public SysUser editUser(@RequestBody UserDto userDto){

        return sysUserService.editUser(userDto);
    }

    //////下面四个应该写SysPermissionController里的
    //添加，查找全部角色
    @GetMapping("/all")
    @RequiresPermissions("sys:menu:query")
    public JSONArray listAll(){
        List<SysPermission> allPerissions=sysPermissionService.getAll();

        JSONArray array=new JSONArray();
        buildTreePermissions(0,allPerissions,array);

        return array;

    }

    //递归方法
    private void buildTreePermissions(int pId, List<SysPermission> allPerissions, JSONArray array) {
        for (SysPermission perission : allPerissions) {
            if (perission.getParentid()==pId){
                String json= JSONObject.toJSONString(perission);
                JSONObject parent=(JSONObject)JSONObject.parse(json);
                array.add(parent);
                for (SysPermission p : allPerissions) {
                    if (perission.getId()==p.getParentid()){
                        JSONArray children = new JSONArray();
                        //和前台ztree-menu.js里对应
                        parent.put("child",children);
                        buildTreePermissions(perission.getId(),allPerissions,children);
                    }
                }
            }
        }
    }

    //查询全部菜单的递归方法
    private void setPermissionsList(int pId, List<SysPermission> allPerissions, List<SysPermission> list) {

        for (SysPermission perission : allPerissions) {
            if (perission.getParentid()==pId){
                list.add(perission);
                if (allPerissions.stream().filter(p -> p.getParentid().equals(perission.getId())).findAny() != null) {
                    setPermissionsList(perission.getId(), allPerissions, list);
                }
            }
        }
    }
    //查询全部的菜单
   @RequestMapping("allType1Menus")
   @RequiresPermissions("sys:menu:query")
    public List<SysPermission> getAllType1Menus(){
        List<SysPermission> permissionsAll=sysPermissionService.getqAll();
        List<SysPermission> list= Lists.newArrayList();

        setPermissionsList(0,permissionsAll,list);

        return list;

    }


//上面四个应该写SysPermissionController里的




}