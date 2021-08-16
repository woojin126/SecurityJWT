package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

/**
 * 1.username,password 받아서
 * 2.정상인지 로그인 시도를 해보는것. authenticationManager로 로그인 시도를 하면 !! PrincipalDetailsService가 호출이됨
 * 그러면 loadUserByUsername이 자동으로 실행됨.
 * 3.PrincipalDetails를 세션에 담고 => 세션에 담는이유는 SecurityConfig의 권한 관리를 위해서 , 권한 관리를 않아면 세션에 담을 필요가 없다!
 * 4.JWT토큰을 만들어서 응답해주면 됨
 *
 */
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


        try {
        /*    BufferedReader br = request.getReader();
            String input = null;
            while((input = br.readLine()) != null) {
                System.out.println(input);
            }*/
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class); //유저엔티티에 담아줌 JSON 요청을 파싱한것을
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()); //토큰 만들걸로 로그인시도 ㄱㄱ

            //PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨 (로그인이 완료되었다는거)
            //DB의 USERNAME ,PASSWORD가 내 가입력한 값 과 같다는 것
            Authentication authentication = // authentication 내로그인 정보가 담기고
                    authenticationManager.authenticate(authenticationToken);//이게실행 될떄 PrincipalDetailsService에 loadUserByUsername 이실행된다.


            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인이 완료됨 + principalDetails = " + principalDetails.getUser().getUsername());

            //리턴을 하는 이유는 권한 관리를 SECURITY가 대신 해주기 때문에 편하게 하려고 하는거임
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없지만, 단지 권한 처리떄문에 SESSION에 넣어 줍니다.
            return authentication; //리턴 되는 시점에 세션에 저장이됨, authentication 객체가 session영역에 저장이 되었다는 것은 => 로그인이 되었다는 뜻.(아이디 패스워드가 정확하다는것)
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //attemptAuthentication실행 후 인증이 정상적으로 되었으면 => successfulAuthentication 함수가 실행됨
    //여기서 JWT 토큰을 만들어서 request요청한 사용자에게 jwt 토큰을 응답해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는것");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        //RSA(공개키,개인키) 방식은 아니고 Hash암호방식 => 서버만 알고있는 개인키
        String jwtToken = JWT.create()
                .withSubject("cos토큰") //의미없음 
                .withExpiresAt(new Date(System.currentTimeMillis()+(JwtProperties.EXPIRATION_TIME))) // 토큰 유효시간 만료시간 1분 * 10
                .withClaim("id", principalDetails.getUser().getId()) //비공개 클레임 키벨류
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));//내서버만 아는 고유한 시크릿키


        response.addHeader(JwtProperties.HEADER_STRING,JwtProperties.TOKEN_PREFIX+jwtToken); //사용자에게 응답할 response 헤더안에

        /**
         * 기존 시큐리티 Ouath2 방식은
         * 유저네임,패스워드 로그인 정상
         * 서보쪽 세션ID생성
         * 클라이언트 쿠키 세션ID를 응답
         *
         * 요청할 때마다 쿠키값 세션ID를 항상 들고 서버쪽으로 요청하기 때문에
         * session.getAttribute("세션값 확인");
         * 서버는 세션ID가 유요한지 판단해서 유효하면 인증이 필요한 페이지로 접근하게 하면된다.
         * ------------------------------------------------------------------
         * 현재우리가하는 JWT 방식은
         * 유저네임, 패스워드 로그인 정상
         * JWT 토큰을 생성
         * 클라이언트 쪽으로 JWT토큰을 응답
         *
         * 요청할 때마다 JWT토큰을 가지고 요청
         * 서버는 JWT토큰이 유요한지를 판단( 필터를 만들어줘야한다 )
         */
    }

}
