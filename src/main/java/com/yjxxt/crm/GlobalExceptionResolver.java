package com.yjxxt.crm;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        if(ex instanceof NoLoginException){
            ModelAndView mav=new ModelAndView("redirect:/index");
            return mav;
        }


        ModelAndView mav = new ModelAndView();
        //设置默认异常处理
        mav.setViewName("error");
        mav.addObject("code",400);
        mav.addObject("msg","系统异常，请稍后再试...");

        //判断HandlerMethod
        if(handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            //获取方法上得Responsebody注解
            ResponseBody responseBody = handlerMethod.getMethod().getAnnotation(ResponseBody.class);
            //判断ResponseBody注解是否存在（如果不存在返回得是视图，如果存在，表示返回得是JSON）
            if (null==responseBody){
                //方法返回视图
                if (ex instanceof ParamsException){
                    ParamsException pe =(ParamsException) ex;
                    mav.addObject("code",pe.getCode());
                    mav.addObject("msg",pe.getMsg());
                }
                return mav;
            }else{
                //方法上返回JSON
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                //如果捕获得是自定义异常
                if (ex instanceof ParamsException) {
                   ParamsException pe = (ParamsException) ex;
                   resultInfo.setCode(pe.getCode());
                   resultInfo.setMsg(pe.getMsg());
                }
                //设置响应类型和编码格式（响应JSON格式）
                resp.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out =null;
                try{
                    out=resp.getWriter();
                    out.write(JSON.toJSONString(resultInfo)); // 讲对象转换成JSON格式，通过输出流输出，响应给请求的前台
                    out.flush();
                }catch (Exception e){
                    e.printStackTrace();

                }finally {
                    if(out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }
        return mav;
    }
}
