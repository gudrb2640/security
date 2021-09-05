package com.travel.club.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testEncoder() {

        String password = "1111";

        String enPw = passwordEncoder.encode(password);

        System.out.println("enPw: " + enPw);

        boolean matchResult = passwordEncoder.matches(password, enPw);
        System.out.println("matchResult: " + matchResult);
    }
}