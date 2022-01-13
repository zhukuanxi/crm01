package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping("index")  //登录页面
    public String index(){
        return "index";
    }

    @RequestMapping("main")  //后台资源页面

    public String main(HttpServletRequest req){
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        User user = userService.selectByPrimaryKey(userId);  // 根据id查询用户信息
        req.setAttribute("user",user); //存储
        return "main"; //转发
    }

    @RequestMapping("welcome")   //欢迎页面
    public String welcome(){
        return "welcome";
    }
}
