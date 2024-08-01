package com.shop.admin.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtSecurityConfig {

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
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService) {
        var authenticationProvider = new DaoAuthenticationProvider();
        
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        
        return new ProviderManager(authenticationProvider);
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http.authenticationProvider(authenticationProvider());
	http.authorizeHttpRequests(authz -> authz
		.requestMatchers("/authenticate").permitAll()
		.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll() // 운영에서 제거 할 것
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
		.requestMatchers("/users/**").hasAnyAuthority("Admin")
		.requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
		.anyRequest().authenticated()
		)
	
	    .csrf(AbstractHttpConfigurer::disable)
	    
	    .sessionManagement(session -> session
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    
	    .oauth2ResourceServer(oAuth2 -> oAuth2
		    .jwt(Customizer.withDefaults()))
	    
	    .httpBasic(Customizer.withDefaults()) // 운영에서 제거 할 것
	    
	    .headers(header -> header.frameOptions(FrameOptionsConfig::sameOrigin));
//	    .formLogin(form -> form
//		.loginPage("/login")
//		.usernameParameter("email")
//		.permitAll())
//	    .logout(logout -> logout.permitAll())
//	    .rememberMe(rem -> rem
//		    .key("abc")
//		    .tokenValiditySeconds(7 * 24 * 60 * 60)); // 7일 후 만료
	
	return http.build();
    }
    
    @Bean
    JWKSource<SecurityContext> jwkSource() {
        JWKSet jwkSet = new JWKSet(rsaKey());
        return (((jwkSelector, securityContext) 
                        -> jwkSelector.select(jwkSet)));
    }
    
    @Bean
    RSAKey rsaKey() {
	KeyPair keyPair = keyPair();
	return new RSAKey
	    .Builder((RSAPublicKey) keyPair.getPublic())
            .privateKey((RSAPrivateKey) keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString())
            .build();
    }
    
    @Bean
    KeyPair keyPair() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate an RSA Key Pair", e);
        }
    }
    
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
    
    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder
            .withPublicKey(rsaKey().toRSAPublicKey())
            .build();
    }
}
