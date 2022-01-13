package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Permission;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    int deleteRoleModuleByRoleId(Integer roleId);

    int countRoleModulesByRoleId(Integer roleId);
}