package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;

import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.plaf.IconUIResource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName,String userPwd){
        checkLoginParams(userName,userPwd);
        User user=userMapper.queryByUserName(userName);
        AssertUtil.isTrue(user==null,"用户不存在或已注销");
        checkLoginPwd(userPwd,user.getUserPwd());
        return builduserInfo(user);
    }
    private UserModel builduserInfo(User user){
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));  //id加密
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }
    private void checkLoginPwd(String userPwd,String upwd){
        userPwd= Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(upwd),"用户密码不正确！");
    }
    private void checkLoginParams(String userName,String userPwd){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");
    }

    public void changeUserPwd(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        User user = userMapper.selectByPrimaryKey(userId); //通过userId获取用户对象，先登录
        checkPasswordParams(user,oldPassword,newPassword,confirmPwd); //密码验证
        user.setUserPwd(Md5Util.encode(newPassword)); //给修改后得新密码进行加密
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败了");
    }
    private void checkPasswordParams(User user,String oldPassword,String newPassword,String confirmPwd){
        AssertUtil.isTrue(user==null,"用户未登录或者不存在");
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");  //原始密码非空
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确"); //判断原始密码是否输入正确
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码不能与原始密码相同");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd),"确认密码不能为空");
        AssertUtil.isTrue(!(newPassword.equals(confirmPwd)),"确认密码与新密码要一致");
    }

    //查询所有销售人员
    public List<Map<String,Object>> querySales(){
        return userMapper.selectSales();
    }

    public Map<String,Object> findUserByParams(UserQuery userQuery){
        //实例化map
        Map<String, Object> map = new HashMap<>();
        //初始化分页单位
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        //开始分页
        PageInfo<User> plist=new PageInfo<User>(selectByParams(userQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }


    //添加用户
    @Transactional
    public void addUser(User user){
        //验证
        checkUser(user);
        //设定默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //判断是否添加成功
        AssertUtil.isTrue(insertHasKey(user)<1,"添加失败");
        relaionUserRole(user.getId(), user.getRoleIds());
    }
    //修改用户
    @Transactional
     public  void changeUser(User user){
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"该用户不存在");
        checkUser(user);
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"修改失败");
    }




        //验证添加修改用户列表
    private void checkUser(User user) {
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()),"用户名不能为空");
        //用户名唯一
        User temp = userMapper.queryByUserName(user.getUserName());
       if(user.getId()==null) //添加操作
       {
           AssertUtil.isTrue(temp!=null,"用户名已存在");
       }else{
           AssertUtil.isTrue(temp!=null && !temp.getId().equals(user.getId()),"用户名已被使用，请重新输入！");
       }
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()),"手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()),"手机号格式不正确");
    }

    //批量删除
    public void removeUserIds(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择删除数据");
        //判断删除成功与否
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"删除失败了");
    }

    private void relaionUserRole(int useId, String roleIds) {
/**
 * 用户角色分配
 * 原始角色不存在 添加新的角色记录
 * 原始角色存在 添加新的角色记录
 * 原始角色存在 清空所有角色
 * 原始角色存在 移除部分角色
 * 如何进行角色分配???
 * 如果用户原始角色存在 首先清空原始所有角色 添加新的角色记录到用户角色表
 */
            //统计当前用户有多少个角色
        int count = userRoleMapper.countUserRoleByUserId(useId);
        if (count > 0) {
            //删除当前用户的角色信息
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(useId) != count, "用户角色分配失败!");
        }
        if (StringUtils.isNotBlank(roleIds)) {
//重新添加新的角色
            List<UserRole> userRoles = new ArrayList<UserRole>();
            for (String s : roleIds.split(",")) {
                UserRole userRole = new UserRole();
                userRole.setUserId(useId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //存放到集合
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)<userRoles.size(), "用户角色分配失败!");
        }
    }
}
