package com.rafalcendrowski.AccountApplication.security;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                .mvcMatchers("/api/payments/*").hasRole("ACCOUNTANT")
                .mvcMatchers("/api/payments/user/*").hasRole("ACCOUNTANT")
                .mvcMatchers(HttpMethod.GET, "/api/empl/**").hasAnyRole("USER", "ACCOUNTANT")
                .mvcMatchers(HttpMethod.GET, "/api/security/events").hasRole("AUDITOR")
                .anyRequest().hasRole("ADMINISTRATOR")
                .and()
                .httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}

@Configuration
class BCryptEncoderConfig {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(13);
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}

class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    Logger secLogger;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String subject = request.getUserPrincipal() == null ? "Anonymous" : request.getUserPrincipal().getName();
        String path = request.getRequestURI();
        if(!path.equals("/error")) {
            secLogger.info(LoggerConfig.getEventLogMap(subject, path,
                    "LOGIN_FAILED", path));
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}

