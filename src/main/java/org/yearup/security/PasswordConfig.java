package org.yearup.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Capstone DB passwords are plain text (training project),
        // so we must use NoOp to allow login to work.
        return NoOpPasswordEncoder.getInstance();
    }
}
