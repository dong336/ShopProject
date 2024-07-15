package com.shop.admin.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;

@Service
public class UserService {

	@Resource
	private UserRepository userRepo;
	@Resource
	private RoleRepository roleRepo;

	public List<User> listAll() {
		return (List<User>) userRepo.findAll();
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public void save(User user) {
		userRepo.save(user);
	}
}
