package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;

    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }
//  /views/saleChance/add_update.ftl
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdate(Integer id, Model model){
        //判断
        if (id!=null){
            //查询用户信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(SaleChanceQuery saleChanceQuery){
        Map<String,Object> map =saleChanceService.querySaleChanceByParams(saleChanceQuery);
        return map;

    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest req , SaleChance saleChance){
        int userId= LoginUserUtil.releaseUserIdFromCookie(req); //获取登录用户的ID
        String trueName=userService.selectByPrimaryKey(userId).getTrueName();
        saleChance.setCreateMan(trueName); //创建人
        saleChanceService.addSaleChance(saleChance); //添加操作
        return success("添加成功了");
    }


    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update( SaleChance saleChance){

        saleChanceService.changeSaleChance(saleChance); //添加操作
        return success("修改成功了");
    }


    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo deletes(Integer[] ids){
        //添加操作
        saleChanceService.removeSaleChanceIds(ids);
        //返回目标对象
        return success("批量删除成功");
    }



}
