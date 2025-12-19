package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.User;
import org.yearup.models.authentication.LoginDto;
import org.yearup.models.authentication.LoginResponseDto;
import org.yearup.security.jwt.TokenProvider;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication, false);

        User user = (User) authentication.getPrincipal();

        LoginResponseDto response = new LoginResponseDto(token, user);

        return ResponseEntity.ok(response);
    }
}
