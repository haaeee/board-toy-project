package com.example.boardproject.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.boardproject.dto.security.KakaoOAuth2Response;
import com.example.boardproject.dto.security.UserPrincipal;
import com.example.boardproject.service.UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

//@EnableWebSecurity  // 넣지 않아도 된다. -> Spring Boot AutoConfig 에 들어감.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()  // StaticResourceLocation {css, js}
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> userService
                .searchUser(username)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - userName: " + username));
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            final UserService userService,
            final PasswordEncoder passwordEncoder) {

        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoOAuth2Response = KakaoOAuth2Response.from(oAuth2User.getAttributes());
//            String registrationId = userRequest.getClientRegistration().getRegistrationId();  // "kakao"
//            String providerId = String.valueOf(kakaoOAuth2Response.id());
            String username = kakaoOAuth2Response.email();
            String dummyPassword = passwordEncoder.encode("{bcrypt}dummy");

            return userService.searchUser(username)
                    .map(UserPrincipal::from)
                    .orElseGet(() -> UserPrincipal.from(
                            userService.saveUser(username, dummyPassword, kakaoOAuth2Response.nickname(),
                                    null)));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
