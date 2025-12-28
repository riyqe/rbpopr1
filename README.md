OWASP-2021-A01 Broken Access Control
REST-endpoint’ы /api/** были доступны без авторизации, что позволяло любому пользователю выполнять CRUD-операции над курсами, студентами и т.д.
 Создан файл SecurityConfig.java
package ru.mtuci.coursemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/h2-console/**")
                );
        return http.build();
    }
}
Добавление зависимости в pom.xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency> 

OWASP-2021-A02 Cryptographic Failures
Библиотека dom4j 1.6.1 содержит уязвимость XXE (CVE-2020-10683). Изменена версия до 2.1.4, где по умолчанию отключён DOCTYPE и внешние сущности.
2.1.
<dependency>
           <groupId>dom4j</groupId> <groupId>org.dom4j</groupId>
            <artifactId>dom4j</artifactId>
           <version>1.6.1</version> <version>2.1.4</version>
</dependency>
 
2.2.  BCryptPasswordEncoder, хэшируем пароль перед сохранением
log.info("User {} logged in with password {}", username, password);
...
u.setPassword(password);

log.info("User {} logged in", username);
...
u.setPassword(encoder.encode(password));

OWASP-2021-A03 Injection
SAXReader разрешал DOCTYPE и внешние сущности -> XXE-инъекция.
Отключили DOCTYPE и внешние сущности на уровне парсера.
3.1.
  @PostMapping(value = "/api/xml/parse", consumes = {MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String parse(@RequestBody String xml) throws Exception {
        SAXReader reader = new SAXReader();
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        Document doc = reader.read(new StringReader(xml));
        return doc.getRootElement().getText();
    }
    
OWASP-2021-A05 Security Misconfiguration
H2-консоль открыта для всех (web-allow-others: true)
Actuator показывал внутренности (/actuator/health)
не хватало security-заголовков (CSP, X-Frame-Options и т.д.)


package ru.mtuci.coursemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/h2-console/**")
                );
        return http.build();
    }
}

    name: course-management
  h2:
    console:
      enabled: true -> false
      path: /h2-console
      settings:
        web-allow-others: true

package ru.mtuci.coursemanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.coursemanagement.model.User;
import ru.mtuci.coursemanagement.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    public Optional<User> findByUsername(String u) {
        return repo.findByUsername(u);
    }

   private final BCryptPasswordEncoder encoder;

    public User save(User u) {
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }
}

OWASP-2021-A06 Vulnerable and Outdated Components
tomcat-embed-core 10.1.43 содержит 4 CVE (например, CVE-2025-55754).
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.4</version> <version>3.5.7</version>
        <relativePath/>
    </parent>
    
OWASP-2021-A09 Security Logging and Monitoring Failures
Убрали логирование открытого текста.
log.info("User {} logged in with password {}", username, password);
...
u.setPassword(password);

log.info("User {} logged in", username);
...
u.setPassword(encoder.encode(password));
