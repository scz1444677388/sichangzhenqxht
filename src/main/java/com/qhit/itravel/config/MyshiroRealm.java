package com.qhit.itravel.config;

import com.qhit.itravel.entity.Role;
import com.qhit.itravel.entity.SysPermission;
import com.qhit.itravel.entity.SysUser;
import com.qhit.itravel.service.SysPermissionService;
import com.qhit.itravel.service.SysUserService;
import com.qhit.itravel.utils.UserUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyshiroRealm extends AuthorizingRealm{
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysPermissionService sysPermissionService;
    /*
认证
* */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //获取当前用户的角色信息
        SysUser user = UserUtil.getCurrentUser();
        List<Role> roles=sysUserService.findUserRoleByUId(user.getId());
        Set<String> roleSet=new HashSet<>();
        for (Role role : roles) {
            roleSet.add(role.getName());
        }
        simpleAuthorizationInfo.setRoles(roleSet);
        //获取当前用户的权限信息;
        List<SysPermission> permissions=sysPermissionService.findPermissionByUserId(user.getId());
        Set<String> perminsionSet=new HashSet<>();
        for (SysPermission permission : permissions) {
            if (permission.getPermission()!=null && permission.getPermission()!=""){
                perminsionSet.add(permission.getPermission());
            }

        }
        simpleAuthorizationInfo.setStringPermissions(perminsionSet);

        //把当前用户权限放到sission
        UserUtil.setPermissionSession(permissions);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken)authenticationToken;
        String fusername = usernamePasswordToken.getUsername();

        SysUser user=sysUserService.findUserByName(fusername);
        if (user==null){
            throw new UnknownAccountException("用户名不存在");

        }
        //判断用户名是否正确
        //将前台传过来的密码进行加密
        String password = user.getPassword();
        String s = new String(usernamePasswordToken.getPassword());
        String s1 = sysUserService.passwordEncoder(s, user.getSalt());

        if (!password.equals(s1)){
            throw new IncorrectCredentialsException("密码不正确");
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user,
                password, ByteSource.Util.bytes(user.getSalt()), getName());

        //到这里表示用户名和密码都正确,把当前登录用户储存起来
        UserUtil.setUserSession(user);

        return authenticationInfo;
    }


}
