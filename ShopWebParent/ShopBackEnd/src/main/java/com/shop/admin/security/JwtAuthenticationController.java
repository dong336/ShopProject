package com.shop.admin.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
public class JwtAuthenticationController {
    @Resource
    private JwtTokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtTokenResponse> generateToken(
            @RequestBody JwtTokenRequest jwtTokenRequest) {
        var authenticationToken = 
                new UsernamePasswordAuthenticationToken(
                        jwtTokenRequest.username(), 
                        jwtTokenRequest.password());
        
        var authentication = authenticationManager.authenticate(authenticationToken);
        
        var token = tokenService.generateToken(authentication);
        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        
        return ResponseEntity.ok(new JwtTokenResponse(token, userDetails.getRealname()));
    }
}

record JwtTokenRequest(String username, String password) {}
record JwtTokenResponse(String token, String realname) {}