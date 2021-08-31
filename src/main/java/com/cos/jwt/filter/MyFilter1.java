package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        System.out.println("필터1");
        chain.doFilter(request, response); //이렇게 체인에 담아줘야 프로세스가 끝나지않고 필터가 끝나지않고 계속 프로세스를 진행 한다.

    }
}
