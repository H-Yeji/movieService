package com.movie.common.config;

import com.movie.common.auth.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * 검증 코드
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf().disable()
                // cors 활성화 : 다른 도메인에서 서버로 호출하는 것을 금지 (같은 도메인끼리만 통신 가능)
                .cors().and()
                .httpBasic().disable()
                .authorizeRequests()
                    .antMatchers("/member/create", "/", "/member/doLogin")
                    .permitAll()
                .anyRequest().authenticated()
                .and()
                // 세션 로그인이 아닌 stateless한 token을 사용하겠다는 의미
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
//        return null;
    }
}
