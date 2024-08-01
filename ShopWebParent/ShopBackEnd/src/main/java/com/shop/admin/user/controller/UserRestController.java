package com.shop.admin.user.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.admin.user.UserService;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/users")
public class UserRestController {

    @Resource
    private UserService service;

    @PostMapping("/check_email")
    public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
	return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
