package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String,Object>> sayRoles(Integer userId){
        System.out.println(userId);
        return roleService.findRoles(userId);
    }

    @RequestMapping("index")
    public String index() {
        return "role/role";
    }
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(RoleQuery roleQuery){
        return roleService.findRoleByParam(roleQuery);
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.addRole(role);
        return success("角色记录添加成功");
    }
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        System.out.println("role = " + role);
        roleService.changeRole(role);
        System.out.println(role);
        return success("角色记录更新成功");
    }
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer roleId, Model model){
        System.out.println(roleId);
        if(null !=roleId){
            model.addAttribute("role",roleService.selectByPrimaryKey(roleId));
        }
        return "role/add_update";
    }
    //删除
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色记录删除成功");
    }

    //授权跳转
    @RequestMapping("AddGrantPage")
    public String toRoleGrantPage(Integer roleId,Model model) {
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    //开始授权
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo grant(Integer roleId,Integer[] mids){
        roleService.addGrant(roleId,mids);
        return success("授权成功了");
    }
}
