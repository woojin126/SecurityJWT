package com.cos.jwt.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter 이필터가 잇음
// /login 요청해서 username,password 전송하면 (post로)
// UsernamePasswordAuthenticationFilter 동작을 함
//현재는 config  .formLogin().disable() 설정을해서 동작을 안함 그래서 따로 config에 설정해줘야함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override //AuthenticationManager를 통해서 로그인 시도
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter :로그인 시도중");


        /**
         * 1.username,password 받아서
         * 2.정상인지 로그인 시도를 해보는것. authenticationManager로 로그인 시도를 하면 !! PrincipalDetailsService가 호출이됨
         * 그러면 loadUserByUsername이 자동으로 실행됨.
         * 3.PrincipalDetails를 세션에 담고 => 세션에 담는이유는 SecurityConfig의 권한 관리를 위해서 , 권한 관리를 않아면 세션에 담을 필요가 없다!
         * 4.JWT토큰을 만들어서 응답해주면 됨
         *
         */
        return super.attemptAuthentication(request, response);
    }
}
