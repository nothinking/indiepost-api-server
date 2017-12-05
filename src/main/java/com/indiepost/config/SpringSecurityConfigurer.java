package com.indiepost.config;

import com.indiepost.filter.JWTAuthenticationFilter;
import com.indiepost.repository.UserRepository;
import com.indiepost.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.inject.Inject;

/**
 * Created by jake on 7/26/16.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private static final String SPRING_SECURITY_EXPRESSION =
            "hasAuthority('Editor')";

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Inject
    public SpringSecurityConfigurer(PasswordEncoder passwordEncoder, UserRepository userRepository, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsServiceBean())
                .passwordEncoder(passwordEncoder);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new CustomUserDetailService(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .antMatchers("/api/admin/**").access(SPRING_SECURITY_EXPRESSION)
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Inject
    public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceBean());
    }
}