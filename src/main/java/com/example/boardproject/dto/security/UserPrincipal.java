package com.example.boardproject.dto.security;

import com.example.boardproject.domain.constant.RoleType;
import com.example.boardproject.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public record UserPrincipal(
        Long id,
        String username, // email
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String nickname,
        String memo
) implements UserDetails {

    public static UserPrincipal of(Long userId, String username, String password, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new UserPrincipal(
                userId,
                username,
                password,
                roleTypes.stream().map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                nickname,
                memo
        );
    }

    public static UserPrincipal from(UserDto dto) {
        return UserPrincipal.of(
                dto.id(),
                dto.email(),
                dto.userPassword(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserDto toDto() {
        return UserDto.of(
                id,
                username,
                password,
                nickname,
                memo
        );
    }

    // 권한에 관련된 부분: 인증과 다른 점은 인증(로그인의 여부) 권한(로그인한 사용자의 권한)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
