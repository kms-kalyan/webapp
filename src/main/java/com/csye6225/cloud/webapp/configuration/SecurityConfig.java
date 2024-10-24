package com.csye6225.cloud.webapp.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.csye6225.cloud.webapp.service.CustomUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in stateless APIs
                .authorizeRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/v2/user", "/healthz").permitAll()
                        .requestMatchers(HttpMethod.GET, "/healthz").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/v2/user/self").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/healthz").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/healthz").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/healthz").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/healthz").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/v2/user/self").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v2/user/self").authenticated()
                        .anyRequest().permitAll())
                .httpBasic(withDefaults());

        // http.csrf().disable()
        //     .authorizeHttpRequests(auth -> auth
        //     .requestMatchers(HttpMethod.POST).permitAll()
        //     .requestMatchers("/healthz","/v1/user/self").permitAll()
        //     .anyRequest().authenticated())
        //     .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
}
