package com.example.demo.security;

import com.example.demo.security.user.JwtUserDetailsService;
import com.example.demo.security.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class JwtTotalSecurity {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final AuthSuccessHandler authSuccessHandler;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final String secret;

    public JwtTotalSecurity(AuthSuccessHandler authSuccessHandler, JwtUserDetailsService jwtUserDetailsService, @Value("${jwt.secret}") String secret) throws Exception {
        this.authSuccessHandler = authSuccessHandler;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.secret = secret;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .formLogin(withDefaults())
                .authorizeHttpRequests(auth -> {
                    try {
                        auth
                                // company
                                .antMatchers("/company/validate-registration-voucher").permitAll()
                                .antMatchers("/company/register-user-by-voucher").permitAll()
                                .antMatchers("/company/**").hasRole("SUPER_ADMIN")
                                // lessons
                                .antMatchers("/lessons/get/by-student-id").hasRole("ADMIN")
                                .antMatchers("/lessons/get/by-teacher-id").hasRole("ADMIN")
                                .antMatchers("/lessons/get/for-interval").hasRole("ADMIN")
                                .antMatchers("/lessons/get/planned-lessons-for-teacher").hasRole("ADMIN")
                                .antMatchers("/lessons/get/upcoming-lessons").hasRole("ADMIN")
                                .antMatchers("/lessons/add/**").hasRole("ADMIN")
                                .antMatchers("/lessons/cancel/**").hasRole("ADMIN")
                                .antMatchers("/lessons/update/**").hasRole("ADMIN")
                                .antMatchers("/lessons/**").authenticated()
                                // activities
                                // _---_---_---_---
                                // coachings
                                .antMatchers("/coachings/get/**").hasRole("ADMIN")
                                .antMatchers("/coachings/add/**").hasRole("SUPER_ADMIN")
                                .antMatchers("/coachings/update/**").hasRole("SUPER_ADMIN")
                                .antMatchers("/coachings/**").authenticated()
                                // competitions
                                .antMatchers("/competitions/add").hasRole("SUPER_ADMIN")
                                .antMatchers("/competitions/get/**").hasRole("ADMIN")
                                .antMatchers("/competitions/update/**").hasRole("SUPER_ADMIN")
                                .antMatchers("/competitions/**").authenticated()
                                // events
                                .antMatchers("/events/get/by-student/**").hasRole("ADMIN")
                                .antMatchers("/events/add/member/**").hasRole("ADMIN")
                                .antMatchers("/events/add").hasRole("SUPER_ADMIN")
                                .antMatchers("/events/update").hasRole("SUPER_ADMIN")
                                .antMatchers("/events/**").authenticated()
                                // -___-___-___-___
                                // users
                                // students
                                .antMatchers("/students/**").hasRole("ADMIN")
                                .antMatchers("/teachers/**").hasRole("ADMIN")
                                // jwt User
                                .antMatchers("/user/get-all").hasRole("OWNER")
                                .antMatchers("/user/update-as-owner").hasRole("OWNER")
                                .antMatchers("/user/create/student").hasRole("ADMIN")
                                .antMatchers("/user/create/teacher").hasRole("SUPER_ADMIN")
                                .antMatchers("/user/create/user").hasRole("OWNER")
                                .antMatchers("/user/remove/user/**").hasRole("SUPER_ADMIN")
                                .antMatchers("/user/**").authenticated()
                                .anyRequest().permitAll()
                                .and()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and()
                                .formLogin(withDefaults())
                                .addFilter(authenticationFilter())
                                .addFilter(new JsonAuthorizationFilter(authenticationManager, jwtUserDetailsService, secret))
                                .exceptionHandling()
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public JsonObjectAuthenticationFilter authenticationFilter() {
        JsonObjectAuthenticationFilter filter = new JsonObjectAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(authSuccessHandler);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}