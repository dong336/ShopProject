package com.shop.admin.user;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 5;

    @Resource
    private UserRepository userRepo;

    @Resource
    private RoleRepository roleRepo;

    @Resource
    private PasswordEncoder passwordEncoder;

    public User getByEmail(String email) {
	return userRepo.getUserByEmail(email);
    }

    public List<User> listAll() {
	return (List<User>) userRepo.findAll(Sort.by("name").ascending());
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
	Sort sort = Sort.by(sortField);

	sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

	Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

	if (keyword != null) {
	    return userRepo.findAll(keyword, pageable);
	}

	return userRepo.findAll(pageable);
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

    public User updateAccount(User userInForm) {
	User userInDB = userRepo.findById(userInForm.getId()).get();

	if (!userInDB.getPassword().isEmpty()) {
	    userInDB.setPassword(userInForm.getPassword());
	    encodePassword(userInDB);
	}

	if (userInDB.getPhotos() != null) {
	    userInDB.setPhotos(userInForm.getPhotos());
	}

	userInDB.setName(userInForm.getName());

	return userRepo.save(userInDB);
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

    public User get(Integer id) throws UserException {
	try {
	    return userRepo.findById(id).get();
	} catch (NoSuchElementException e) {
	    throw new UserException("존재하지 않는 사용자 (ID: " + id + ")");
	}
    }

    public void delete(Integer id) throws UserException {
	Long countById = userRepo.countById(id);

	if (countById == null || countById == 0) {
	    throw new UserException("존재하지 않는 사용자 (ID: " + id + ")");
	}

	userRepo.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
	userRepo.updateEnabledStatus(id, enabled);
    }
}
