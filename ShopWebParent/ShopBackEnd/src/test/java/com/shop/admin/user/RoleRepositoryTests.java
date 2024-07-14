package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;

import jakarta.annotation.Resource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	@Resource
	private RoleRepository repo;

	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "모든 권한");
		Role savedRole = repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateRestRoles() {
		Role roleSalesperson = new Role("Salesperson", """
				상품,고객,배송,주문,판매,후기 관리
				""");
		Role roleEditor = new Role("Editor", """
				카테고리,브랜드,상품,기사,메뉴 관리
				""");
		Role roleShipper = new Role("Shipper", """
				상품,주문 조회하고 주문 상태를 변경
				""");
		Role roleAssistant = new Role("Assistant", """
				QnA, 리뷰 관리
				""");

		repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
	}
}
