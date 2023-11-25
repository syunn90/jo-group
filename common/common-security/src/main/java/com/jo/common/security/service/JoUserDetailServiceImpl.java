package com.jo.common.security.service;

import com.jo.api.dto.UserDTO;
import com.jo.api.dto.UserInfo;
import com.jo.api.feign.RemoteUserService;
import com.jo.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.jo.common.core.constant.SecurityConstants.FROM_IN;

/**
 * @author xtc
 * @date 2023/11/1
 */
@Primary
@RequiredArgsConstructor
@Service
public class JoUserDetailServiceImpl implements JoUserDetailService {

    private final RemoteUserService remoteUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        R<UserInfo> result = remoteUserService.info(userDTO, FROM_IN);
        return getUserDetails(result);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
