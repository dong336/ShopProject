package com.shop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
	return new ShopUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	
	authProvider.setUserDetailsService(userDetailsService());
	authProvider.setPasswordEncoder(passwordEncoder());
	
	return authProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http.authenticationProvider(authenticationProvider());
	http.authorizeHttpRequests(authz -> authz
		.anyRequest().authenticated()
	    )
	    .formLogin(form -> form
		.loginPage("/login")
		.usernameParameter("email")
		.permitAll()
	    )
	    .logout(logout -> logout.permitAll());
	
	return http.build();
    }

    @Bean
    WebSecurityCustomizer configureWebSecurity() throws Exception {
	return (web) -> web.ignoring().requestMatchers("/js/**", "/images/**", "/webjars/**");
    }
}
