package com.jo.common.security.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jo.api.dto.UserInfo;
import com.jo.api.entity.SysUser;
import com.jo.common.core.constant.CommonConstants;
import com.jo.common.core.constant.SecurityConstants;
import com.jo.common.core.util.R;
import com.jo.common.core.util.RetOps;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xtc
 * @date 2023/11/1
 */
@Configuration
public interface JoUserDetailService extends UserDetailsService, Ordered {


    /**
     * 是否支持此客户端校验
     * @param clientId 目标客户端
     * @return true/false
     */
    default boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 排序值 默认取最大的
     * @return 排序值
     */
    default int getOrder() {
        return 0;
    }

    default UserDetails getUserDetails(R<UserInfo> result) throws UsernameNotFoundException {

        UserInfo info = RetOps.of(result).getData().orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        Set<String> dbAuthSet = new HashSet<>();
        if (ArrayUtil.isNotEmpty(info.getRoles())){
            // 获取角色
//            Arrays.stream(info.getRoles()).forEach(role -> dbAuthSet.add(SecurityConstants.ROLE + role));

            dbAuthSet.add(SecurityConstants.ROLE + info.getRoles().getRole());
            dbAuthSet.addAll(Arrays.asList(info.getRoles().getPermissions()));
            // 获取资源
//            dbAuthSet.addAll(Arrays.asList(info.getPermissions()));
        }

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(dbAuthSet.toArray(new String[0]));
        SysUser sysUser = info.getSysUser();

        // 构造security用户

        return new JoUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser.getUsername(),
                SecurityConstants.BCRYPT+sysUser.getPassword(),sysUser.getPhone(),true,true,true,
                StrUtil.equals(sysUser.getLockFlag(), CommonConstants.STATUS_NORMAL),authorityList);
    }

    default UserDetails loadUserByUser(JoUser joUser) {
        return this.loadUserByUsername(joUser.getUsername());
    }
}
