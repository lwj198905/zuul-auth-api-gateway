package com.zhph.security.gateway;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.zhph.security.common.JwtAuthenticationConfig;
import com.zhph.security.common.JwtTokenAuthenticationFilter;

import java.util.Collection;

/**
 * web 安全配置 基于角色的身份验证
 *
 * @author zb
 */
@EnableWebSecurity
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationConfig config;

    @Bean
    public JwtAuthenticationConfig jwtConfig() {
        return new JwtAuthenticationConfig();
    }


    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new CustomFilterInvocationSecurityMetadataSource();
    }

//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        //httpSercurity 使用参考
//        //https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
//        httpSecurity
//                .csrf().disable()
//                .logout().disable()
//                .formLogin().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                    .anonymous()
//                .and()
//                    .exceptionHandling().authenticationEntryPoint(
//                            (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
//                .and()
//                //允许在一个已知的filter之后添加filter UsernamePasswordAuthenticationFilter是已知filter
//                    .addFilterAfter(new JwtTokenAuthenticationFilter(config),
//                            UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                    .antMatchers(config.getUrl()).permitAll()
//                    .antMatchers("/backend/admin").hasRole("ADMIN")
//                    .antMatchers("/backend/user").hasRole("USER")
//                    .antMatchers("/backend/guest").permitAll();
//    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //httpSercurity 使用参考
        //https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
        httpSecurity
                .csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous()
                .and()
                .exceptionHandling().authenticationEntryPoint(
                (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                //允许在一个已知的filter之后添加filter UsernamePasswordAuthenticationFilter是已知filter
                .addFilterAfter(new JwtTokenAuthenticationFilter(config),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(config.getUrl()).permitAll()
                .antMatchers("/backend/guest").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(securityMetadataSource());
                        return object;
                    }
                });
    }
}

