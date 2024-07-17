package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import jakarta.annotation.Resource;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @Resource
    private UserRepository repo;

    @Resource
    private TestEntityManager entityManager;
    
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Test
    public void testListFirstPage() {
	int pageNumber = 0;
	int pageSize = 4;

	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	Page<User> page = repo.findAll(pageable);
	List<User> listUsers = page.getContent();

	listUsers.forEach(user -> System.out.println(user));

	assertThat(listUsers.size()).isEqualTo(pageSize);
    }
    
    @Test
    public void testSearchUsers() {
	String keyword = "bruce";
	
	int pageNumber = 0;
	int pageSize = 4;
	
	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	Page<User> page = repo.findAll(keyword, pageable);
	
	List<User> listUsers = page.getContent();
	
	listUsers.forEach(user -> System.out.println(user));
	
	assertThat(listUsers.size()).isGreaterThan(0);
    }
    
    @Test
    public void testPasswordEncoder() {
	String encoded = passwordEncoder.encode("12345678");
	
	System.out.println(encoded);
    }
}
