package com.jo.joauth.service.impl;

import com.jo.joauth.service.JoUserDetailService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author xtc
 * @date 2023/11/1
 */
public class JoUserDetailServiceImpl implements JoUserDetailService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return JoUserDetailService.super.loadUserByUsername(username);
    }
}
