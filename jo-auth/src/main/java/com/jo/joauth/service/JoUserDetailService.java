package com.jo.joauth.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author xtc
 * @date 2023/11/1
 */
@Configuration
public interface JoUserDetailService extends UserDetailsService, Ordered {

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
