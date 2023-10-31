package com.jo.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jo.common.entity.SysOauthClientDetails;
import com.jo.common.mapper.SysOauthClientDetailsMapper;
import com.jo.common.service.SysOauthClientDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lengleng
 * @since 2018-05-15
 */
@Service
@Primary
@RequiredArgsConstructor
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails>
		implements SysOauthClientDetailsService {

}
