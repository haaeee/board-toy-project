package com.example.boardproject.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.boardproject.domain.User;
import com.example.boardproject.repository.UserRepository;
import java.util.Optional;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserRepository userRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(User.of("test@email.com",
                        "password",
                        "nickname(test)",
                        "memo(test)")));
    }

}
