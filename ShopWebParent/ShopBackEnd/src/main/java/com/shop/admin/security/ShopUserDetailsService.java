package com.shop.admin.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shop.admin.user.UserRepository;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;

public class ShopUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	User user = userRepo.getUserByEmail(email);
	if (user != null) {
	    return new ShopUserDetails(user);
	}
	throw new UsernameNotFoundException("해당하는 사용자를 찾을 수 없습니다: " + email);
    }

}
