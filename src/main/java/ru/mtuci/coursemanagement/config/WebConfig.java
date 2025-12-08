package ru.mtuci.coursemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // отключаем CSRF для API (для ZAP, чтобы мог тестировать POST),
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        // защита от Clickjacking (X-Frame-Options)
                        .frameOptions(frame -> frame.sameOrigin())
                        // защита от сниффинга (X-Content-Type-Options)
                        .contentTypeOptions(withDefaults())
                );

        return http.build();
    }
}
