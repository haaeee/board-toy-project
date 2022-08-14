package com.example.boardproject.config;

import com.example.boardproject.domain.User;
import com.example.boardproject.repository.UserRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserRepository userRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(User.of(
                "test@email.com",
                "password",
                "nickname(test)",
                "memo(test)"
        )));
    }

}
