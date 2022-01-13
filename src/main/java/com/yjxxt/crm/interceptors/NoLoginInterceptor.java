package com.yjxxt.crm.interceptors;

import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        //获取Cookie中的用户ID
       Integer userID= LoginUserUtil.releaseUserIdFromCookie(req);
       //判断用户ID是否不为空，且数据库中存在对应的用户记录
        if (userID == null || null== userService.selectByPrimaryKey(userID)) {
            throw new NoLoginException("用户未登录");
        }
        return true;
    }

}
