package com.example.boardproject.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.boardproject.dto.UserDto;
import com.example.boardproject.service.UserService;
import java.util.Optional;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserService userService;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userService.searchUser(anyString())).willReturn(Optional.of(createUserDto()));
        given(userService.saveUser(anyString(), anyString(), anyString(), anyString()))
                .willReturn(createUserDto());
    }

    private UserDto createUserDto() {
        return UserDto.of(
                1L,
                "test@email.com",
                "password",
                "nickname(test)",
                "memo(test)");
    }
}
