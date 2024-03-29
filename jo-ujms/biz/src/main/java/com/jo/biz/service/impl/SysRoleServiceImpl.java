package com.jo.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jo.api.entity.SysRole;
import com.jo.biz.mapper.SysRoleMapper;
import com.jo.biz.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * @author xtc
 * @date 2023/11/3
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Override
    public SysRole findRolesByUserId(Long userId) {
        return baseMapper.listRolesByUserId(userId);
    }
}
