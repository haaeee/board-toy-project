package com.example.boardproject.controller;

import com.example.boardproject.dto.security.UserPrincipal;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    public SecurityContext createSecurityContext(final WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal = UserPrincipal.of(1L, "test@email.com", "password", "nickname", "memo");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, userPrincipal.getPassword(),
                grantedAuthorities);
        context.setAuthentication(authenticationToken);
        return context;
    }
}
