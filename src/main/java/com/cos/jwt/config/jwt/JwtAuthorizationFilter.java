package com.cos.jwt.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 시큐리티가 filter를 가지고있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있음.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.
// 만약 권한이나 인증이 필요한 주소가 아니라면 BasicAuthenticationFilter 필터를 타지 않음
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을 때 아래 필터를 타게 될 것이다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + jwtHeader);


        //header가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) { //토큰의 시작점이 Bearer 인지 확인
            chain.doFilter(request, response); //다시 필터를 타게 넘겨버림
            return; //그리고 리턴을하면 진행이 멈출것
        }
        //JWT 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace(JwtProperties.TOKEN_PREFIX, "");//첫인자를 공백으로 치환

        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build() //값이 잘들어왔으면 서명이 되었다는것
                .verify(jwtToken)//서명 인증
                .getClaim("username")
                .asString(); //스트링으로 캐스팅

        //서명이 정상적으로 됨
        if (username != null) {
            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            //강제로 시큐리티의 세션에 접근하여 Authentication 객체에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication); //시큐리티의 세션공간

            chain.doFilter(request, response);
        }
    }


}