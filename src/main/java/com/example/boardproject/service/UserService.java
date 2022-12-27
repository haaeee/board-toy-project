package com.example.boardproject.service;

import com.example.boardproject.domain.User;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserDto> searchUser(String username) {
        return userRepository.findByEmail(username)
                .map(UserDto::from);
    }

    @Transactional
    public UserDto saveUser(String email, String password, String nickname, String memo) {
        return UserDto.from(
                userRepository.save(User.of(email, password, nickname, memo, nickname))
        );
    }

}
