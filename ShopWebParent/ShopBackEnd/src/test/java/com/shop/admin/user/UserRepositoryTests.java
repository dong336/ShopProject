package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Resource
	private UserRepository repo;
	
	@Resource
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userMe = new User("dong094724@gmail.com", "1234", "김동운");
		userMe.addRole(roleAdmin);
		
		User savedUser = repo.save(userMe);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userOther = new User("test@test.com", "1234", "홍길동");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		
		userOther.addRole(roleEditor);
		userOther.addRole(roleAssistant);
		
		User savedUser = repo.save(userOther);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userOne = repo.findById(1).get();
		System.out.println(userOne);
		
		assertThat(userOne).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userOne = repo.findById(1).get();
		userOne.setEnabled(true);
		userOne.setEmail("sdfgh724@naver.com");
		
		repo.save(userOne);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userOne = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		
		userOne.getRoles().remove(roleEditor);
		userOne.addRole(roleSalesperson);
		
		repo.save(userOne);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 3;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "test1@test.com";
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, true);
	}
}
