package com.shop.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	@Resource
	private UserRepository userRepo;

	@Resource
	private RoleRepository roleRepo;

	@Resource
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepo.findAll();
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();

			if (user.getPassword().isEmpty()) {
				// 비번 미기입
				user.setPassword(existingUser.getPassword());
			} else {
				// 비번 변경
				encodePassword(user);
			}
		} else {
			encodePassword(user);

		}
		
		return userRepo.save(user);
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);

		if (userByEmail == null)
			return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			// 이미 해당 메일주소가 존재하는 새로운 회원 등록임
			if (userByEmail != null)
				return false;
		} else {
			// 이미 해당 메일주소와 일치하는 기존 회원이 있음
			if (userByEmail.getId() != id) {
				return false;
			}
		}

		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("존재하지 않는 사용자 (ID: " + id + ")");
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("존재하지 않는 사용자 (ID: " + id + ")");
		}
		
		userRepo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
