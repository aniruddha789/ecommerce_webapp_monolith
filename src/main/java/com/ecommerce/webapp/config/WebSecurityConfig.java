package com.ecommerce.webapp.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    @Autowired
    private UnauthorizedUserAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private SecurityFilter securityFilter;

//    @Override
//    @Bean
//    protected AuthenticationManager authenticationManager() throws Exception {
//        return authenticationManager();
//    }

//    @Bean
//    public UserDetailsService getUserDetailsService(){
//        return userDetailsService;
//    }

//    @Bean
//    public PasswordEncoder getPasswordEncoder(){
//        return bCryptEncoder;
//    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }

//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(_csrf ->
//                _csrf.disable()).authorizeHttpRequests(
//                authorizeRequests -> authorizeRequests
//                        .anyRequest().permitAll()
//        );
//
//        return http.build();
//
//    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(_csrf ->
               _csrf.disable())    //Disabling CSRF as not using form based login
                .authorizeRequests(authz ->

                {
                    try {
                        authz.requestMatchers("/user/register", "/user/login", "/user/delete", "/user/getUser").permitAll()
                                .anyRequest().authenticated()
                    .and()
                .exceptionHandling( excep ->
                        excep.authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //To Verify user from second request onwards............
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });



        return http.build();

    }

    @Bean
    public AuthenticationProvider  authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptEncoder);
        return authenticationProvider;
    }

}
