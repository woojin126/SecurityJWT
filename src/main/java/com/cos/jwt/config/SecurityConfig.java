package com.cos.jwt.config;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity //활성화가 되기위한 어노테이션, 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter; // CorsConfig에 Bean설정해놓은것 떙겨옴
    private final UserRepository userRepository;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class);//이필터는 시큐리티 필터(BasicAuthenticationFilter)가끝난전후 실행됨 (이건 시큐리티의 중간단계이기때문에  내필터를 전,후에 실행해도 시큐리티 다음에 실행됨
        //http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);//이것은 시큐리티의 맨처음필터 전에 실행되기때문에 시큐리티 전에 실행되는 필터
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다. stateLess 서버로 만들겠다.
            .and()
                .addFilter(corsFilter) //@CrossOrigin(인증이없을 때 사용가능), //모든요청에 이제 설정한 filter를 다 지나간다. 내서버는 cors 정책에서 벗어날수있다.(다허용함) 시큐리티 필터에 등록인증할때는 이방식
                .formLogin().disable() //jwt사용으로 폼 로그인은 안한다.
                .httpBasic().disable() //기본 http 방식은 안쓰겠다. 이렇게 막아놓으면 난 Bearer 방식을 쓸꺼다!
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //AuthenticationManager 필수
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),userRepository)) //AuthenticationManager 필수
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }
}
