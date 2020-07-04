package com.qhit.itravel.dto;

import com.qhit.itravel.entity.SysUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public class UserDto extends SysUser {

    private static final long serialVersionUID = 143205018190849142L;
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }


}
