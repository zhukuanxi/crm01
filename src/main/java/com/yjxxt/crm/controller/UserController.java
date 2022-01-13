package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @RequestMapping("addOrUpdatePage") //用户页面的添加修改
    public String addOrUpdatePage(Integer id, Model model) {
        if(id!=null){
            User user = userService.selectByPrimaryKey(id);
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }

    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req){
        //获取用户的ID
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user",user);
        //转发
        return "user/setting";
    }

    @RequestMapping("login")
    @ResponseBody
    public ResultInfo say(User user){
        ResultInfo resultInfo = new ResultInfo();
            // 调用Service层的登录方法，得到返回的用户对象
            UserModel userModel = userService.userLogin(user.getUserName(), user.getUserPwd());
            resultInfo.setResult(userModel);
            return resultInfo;
/**
 * 登录成功后，有两种处理：
 * 1. 将用户的登录信息存入 Session （ 问题：重启服务器，Session 失效，客户端
 需要重复登录 ）
 * 2. 将用户信息返回给客户端，由客户端（Cookie）保存
 */




    }

    @PostMapping ("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest req,String oldPassword,String newPassword,String confirmPwd){
        ResultInfo resultInfo=new ResultInfo();
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req); //获取Cookie中得userId
      //  try{
            userService.changeUserPwd(userId,oldPassword,newPassword,confirmPwd);
      /*  }catch (ParamsException pe){
            pe.printStackTrace();
            resultInfo.setCode(pe.getCode());
            resultInfo.setMsg(pe.getMsg());
        }catch (Exception ex){
            ex.printStackTrace();
            resultInfo.setCode(300);
            resultInfo.setMsg(ex.getMessage());
        }*/
        return resultInfo;
    }

    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user){
        ResultInfo resultInfo=new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        return resultInfo;
    }

    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales() {
        List<Map<String, Object>> list = userService.querySales();
        return list;
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(UserQuery userQuery) {
        return userService.findUserByParams(userQuery);
    }

    @RequestMapping("save") //用户列表的用户添加
    @ResponseBody
    public ResultInfo addUser(User user){
        ResultInfo resultInfo=new ResultInfo();
        //修改信息
        userService.addUser(user);
        return success("添加成功");
    }

    @RequestMapping("update") //用户列表的用户修改
    @ResponseBody
    public ResultInfo update(User user){
        ResultInfo resultInfo=new ResultInfo();
        //修改信息
        userService.changeUser(user);
        return success("修改成功");
    }

    @RequestMapping("delete") //用户列表的用户批量删除
    @ResponseBody
    public ResultInfo delete(Integer[] ids){
        //修改信息
        userService.removeUserIds(ids);
        return success("删除成功");
    }
}
