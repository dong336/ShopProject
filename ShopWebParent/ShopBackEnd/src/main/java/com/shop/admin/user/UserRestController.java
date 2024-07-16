package com.shop.admin.user;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
public class UserRestController {

    @Resource
    private UserService service;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
	return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
