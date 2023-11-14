package com.jo.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jo.api.entity.SysMenu;
import com.jo.biz.mapper.SysMenuMapper;
import com.jo.biz.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xtc
 * @date 2023/11/3
 */
@Service
public class SysMenuServiceImpl  extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        return baseMapper.listMenusByRoleId(roleId);
    }

}
