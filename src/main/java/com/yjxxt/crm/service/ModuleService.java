package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Autowired(required = false)
    public ModuleMapper moduleMapper;

    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }
}
