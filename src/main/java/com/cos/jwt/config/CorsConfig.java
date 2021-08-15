package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 교차 출처 리소스 공유(Cross-Origin Resource Sharing, CORS)는 추가 HTTP 헤더를 사용하여,
 * 한 출처에서 실행 중인 웹 애플리케이션이 다른 출처의 선택한 자원에 접근할 수 있는 권한을 부여하도록 브라우저에 알려주는 체제입니다.
 * 웹 애플리케이션은 리소스가 자신의 출처(도메인, 프로토콜, 포트)와 다를 때 교차 출처 HTTP 요청을 실행합니다.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);//1. 내서버가 응답을 할 때 JSON을 자바스크립트에서 처리할 수 있게 처리할지를 설정하는 것
                                         //2. 자격증명과 함께 요청을 할 수 있는지 여부.
                                         //3. 해당 서버에서 Authorization로 사용자 인증도 서비스할 것이라면 true로 응답해야 할 것이다.
        config.addAllowedOrigin("*");// 모든 IP에 응답을 허용하겠다. ->CORS 요청을 허용할 사이트
        config.addAllowedHeader("*");// 모든 HEADER의 응답을 허용하겠다. ->특정 헤더를 가진 경우에만 CORS 요청을 허용할 경우
        config.addAllowedMethod("*");// 모든 post,get,put,patch 요청을 허용하겠다. ->CORS 요청을 허용할 Http Method들
        source.registerCorsConfiguration("/api/**", config); //api/** 경로로 들어오는 것을

        return new CorsFilter(source);
    }
}
