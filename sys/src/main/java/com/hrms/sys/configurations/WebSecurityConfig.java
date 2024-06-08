package com.hrms.sys.configurations;

import com.hrms.sys.filter.JwtTokenFilter;
import com.hrms.sys.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
@Configuration
@RequiredArgsConstructor
//@EnableMethodSecurity
@EnableWebMvc
@EnableWebSecurity
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)

                            )
                            .permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/users/details",apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/roles**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/employees**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/employees**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/employees**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/employees**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/news**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/news/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/news**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/departments**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/departments/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/departments**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/departments**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/departments**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.MANAGER)
                            .requestMatchers(GET,
                                    String.format("%s/leaves**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/leaves**",apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/leaves**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/leaves**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/overtimes**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/overtimes/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/overtimes**",apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/overtime**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/overtime**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/remotes**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/remotes/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/remotes**",apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/remotes**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/remotes**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/payroll**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/payroll**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/payroll**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/payroll**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/courses**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/courses**", apiPrefix)).permitAll()
                            .requestMatchers(DELETE,
                                    String.format("%s/courses**", apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/courses**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/videos**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/videos**", apiPrefix)).permitAll()
                            .requestMatchers(DELETE,
                                    String.format("%s/videos**", apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/videos**", apiPrefix)).permitAll()




                            .anyRequest().authenticated();
                    //.anyRequest().permitAll();

                })
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}
