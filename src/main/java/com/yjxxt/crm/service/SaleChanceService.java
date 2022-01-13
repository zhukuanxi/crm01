package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    public Map<String,Object>  querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        //实例
        Map<String,Object>  map=new HashMap<>();
        //实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //开始分页
        PageInfo<SaleChance> plist=new PageInfo<SaleChance>(selectByParams(saleChanceQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    @Transactional
    public void addSaleChance(SaleChance saleChance){
        //验证
        checkSaleChanceParam(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //检查开发状态
        //未分配
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //已分配
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        saleChance.setIsValid(1);
        //添加是否成功
        AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败了！");

    }

    private void checkSaleChanceParam(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名称");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系电话");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"请输入正确号码");
    }

    @Transactional
    public void changeSaleChance(SaleChance saleChance){
        //验证
        SaleChance temp=selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"待修改的数据不存在");
        checkSaleChanceParam(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //原来未分配state，devResult
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
            //已经分配
        if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设定默认值
        saleChance.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"修改失败了");
        }


    public void removeSaleChanceIds(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择要删除的数据");
        //删除是否成功
        AssertUtil.isTrue(deleteBatch(ids)!= ids.length,"批量删除失败了");
    }
}



