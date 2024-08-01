package com.shop.admin.test;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    @PostMapping("/test/authenticate")
    public Authentication authenticate(Authentication authentication) {
	return authentication;
    }
}
