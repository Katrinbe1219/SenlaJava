package com.example.application.security;

import com.example.application.errors.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private com.example.application.security.UserDetailsService userDetailsService;

    @Autowired
    private JwtCheckerFilter jwtCheckerFilter;

    @Autowired
    private JwtService jwtService;

    public SecurityConfiguration(com.example.application.security.UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authenticationManager(http), jwtService);
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/books/**","/orders/**","/requests/**").hasAnyRole("USER_WATCHER", "ADMIN", "USER_FULL")
                        .requestMatchers("/receive/**","/settings/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtCheckerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customLoginFilter, BasicAuthenticationFilter.class)


//                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        
        AuthenticationManagerBuilder builder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }
}
