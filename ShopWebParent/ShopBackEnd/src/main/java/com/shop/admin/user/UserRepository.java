package com.shop.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shop.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>, CrudRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);

    @Query("""
    	SELECT u
    	FROM User u
    	WHERE CONCAT(u.id, ' ', u.email, ' ', u.name) LIKE %:keyword%
    	""")
    public Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    public void updateEnabledStatus(Integer id, boolean enabled);
}
