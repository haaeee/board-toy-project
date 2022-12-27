package com.example.boardproject.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.boardproject.domain.User;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService sut;

    @Mock
    private UserRepository userRepository;

    @DisplayName("존재하는 회원 ID를 검색하면, 회원 데이터를 Optional로 반환한다.")
    @Test
    void givenExistentUserId_whenSearching_thenReturnsOptionalUserData() {
        // Given
        String username = "jm@email.com";
        given(userRepository.findByEmail(username)).willReturn(Optional.of(createUser(username)));

        // When
        Optional<UserDto> result = sut.searchUser(username);

        // Then
        assertThat(result).isPresent();
        then(userRepository).should().findByEmail(username);
    }

    @DisplayName("존재하지 않는 회원 ID를 검색하면, 비어있는 Optional을 반환한다.")
    @Test
    void givenNonexistentUserId_whenSearching_thenReturnsOptionalUserData() {
        // Given
        String username = "wrong-user@email.com";
        given(userRepository.findByEmail(username)).willReturn(Optional.empty());

        // When
        Optional<UserDto> result = sut.searchUser(username);

        // Then
        assertThat(result).isEmpty();
        then(userRepository).should().findByEmail(username);
    }

    @DisplayName("회원 정보를 입력하면, 새로운 회원 정보를 저장하여 가입시키고 해당 회원 데이터를 리턴한다.")
    @Disabled
    @Test
    void givenUserParams_whenSaving_thenSavesUser() {
        // Given
        User user = createUser("jm@email.com");
        User savedUser = createSigningUpUser("jm@email.com");
        given(userRepository.save(user)).willReturn(savedUser);

        // When
        UserDto result = sut.saveUser(
                user.getEmail(),
                user.getUserPassword(),
                user.getNickname(),
                user.getMemo()
        );

        // Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("userPassword", user.getUserPassword())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                .hasFieldOrPropertyWithValue("memo", user.getMemo())
                .hasFieldOrPropertyWithValue("createdBy", user.getNickname())
                .hasFieldOrPropertyWithValue("modifiedBy", user.getNickname());
        then(userRepository).should().save(user);
    }

    private User createSigningUpUser(String username) {
        User user = createUser(username);
        return user;
    }

    private User createUser(String username) {
        User user = User.of(
                username,
                "password",
                "nickname",
                "memo",
                "nickname"
        );
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}