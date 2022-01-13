package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Autowired(required = false)
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    //查询所有角色信息
    public List<Map<String,Object>> findRoles(Integer userId){
        return roleMapper.selectRoles(userId);
    }
    //角色的条件查询和分页
    public Map<String,Object> findRoleByParam(RoleQuery roleQuery){
        //实例化Map
        Map<String,Object> map=new HashMap<>();
        //开启分页单位
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> rlist=new PageInfo<>(selectByParams(roleQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",rlist.getTotal());
        map.put("data",rlist.getList());
        //返回目标map
        return map;
    }

    //添加角色
    @Transactional
    public void addRole(Role role){
        //验证角色名非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        Role temp=roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色已经存在");
        //默认参数：is_valid=1  createDate  updateDate
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //添加是否成功
        AssertUtil.isTrue(insertHasKey(role)<1,"添加失败了");
    }
    //给角色进行授权
    @Transactional
    public void addGrant(Integer roleId,Integer[] mids) {
        AssertUtil.isTrue(roleId==null || moduleMapper.selectByPrimaryKey(roleId)==null,"请选择角色");
        //t_permission roleId,,,mid
        //统计当前角色的资源数量
        int count=permissionMapper.countRoleModulesByRoleId(roleId);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deleteRoleModuleByRoleId(roleId)!=count,"角色资源分配失败");
        }
        //删除角色的资源信息
        List<Permission> plist=new ArrayList<>();
        //遍历mids
        if(mids!=null && mids.length>0){
            for(Integer mid:mids){
                //实例化对象
                Permission permission =new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(mid);
                //权限码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                plist.add(permission);
            }
        }

        AssertUtil.isTrue(permissionMapper.insertBatch(plist)!=plist.size(),"授权失败");
    }
    //修改角色信息
    @Transactional
    public void changeRole(Role role){
        //验证当前对象是否存在
        Role temp=roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(temp==null,"待修改的记录不存在");
        //角色名唯一
        Role temp2=roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp2!=null && !(temp2.getId().equals(role.getId())),"角色已存在");
        //设定默认值
        role.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"修改失败了");
    }
    //删除角色信息
    @Transactional
    public void deleteRole(Integer roleId){
        Role temp =selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId||null==temp,"待删除的记录不存在!");
        temp.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"角色记录删除失败!");
    }
}
