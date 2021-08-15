package com.cos.jwt.config;

import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter2;
import com.cos.jwt.filter.MyFilter3;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 직접 만든필터는 request요청이 올때 바로 실행 (시큐리티 필터실행 전,후에 실행된다) //설정으로가는 securityConfig에서
 */
@Configuration//메모리에뜨는작업
public class FilterConfig { //스프링시큐리티 필터에 체인을 거는게아닌 내가따로 만든 필터

    @Bean
    public FilterRegistrationBean<MyFilter1> filter1(){
        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);//낮은번호가 필터중에서 가장 먼저 실행됨
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2(){
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*");
        bean.setOrder(0);//낮은번호가 필터중에서 가장 먼저 실행됨
        return bean;
    }

}
