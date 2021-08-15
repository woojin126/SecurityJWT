package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//config설정으로 시큐리티 동작 전에 실행
public class MyFilter3 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;


        req.setCharacterEncoding("UTF-8");
        /**
         * 토큰만들었따고 가정하고 토큰이름:코스
         * 코스라는 토큰이 들어오면 인증이되게하고
         * 토큰이안들어오면 컨트롤러조차 진입이 안되게할 것
         */

        //토큰: cos 이걸 만들어줘야함, id,pw정상적으로 들어와서 로그인이 완료 되면 토큰을 만들어주고 그걸 응답을 해준다
        //요청할 때 마다 header에 Authorization value 값으로 토큰을 가지고 오겠죠?
        //그때 토큰이 넘어오면 이토큰이 내가 만든 토큰이 맞는지만 검증만 하면 됨. (RSA, HS256) 토큰검증방식
       if(req.getMethod().equals("POST")) {
            System.out.println("POST 요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터3");
            if(headerAuth.equals("cos")){
                chain.doFilter(req, res);
            }else{
                PrintWriter out = res.getWriter();
                out.print("인증 안됨");
            }
        }
    }
}
