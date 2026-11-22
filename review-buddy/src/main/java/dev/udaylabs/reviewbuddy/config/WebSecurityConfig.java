package dev.udaylabs.reviewbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    private static final String[] PUBLIC_RESOURCES = {
        "/", "/login", "/registration", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/actuator/health"
    };

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_RESOURCES)
                        .permitAll()
                        // "/products/new" must be matched before the public "/products/{slug}" pattern
                        .requestMatchers(HttpMethod.GET, "/products/new")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/{slug}")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form.loginPage("/login")
                        .defaultSuccessUrl("/products")
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/products").permitAll());
        return http.build();
    }
}
