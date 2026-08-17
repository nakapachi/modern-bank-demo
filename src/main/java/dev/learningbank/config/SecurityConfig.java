package dev.learningbank.config;

import dev.learningbank.repository.AppUserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import dev.learningbank.domain.CustomerStatus;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/images/**", "/login", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/accounts/**").hasRole("CUSTOMER")
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").successHandler((request, response, authentication) -> {
                boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                response.sendRedirect(admin ? "/admin" : "/accounts");
            }).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
            .build();
    }

    @Bean
    UserDetailsService userDetailsService(AppUserRepository users) {
        return username -> users.findByUsername(username)
            .map(user -> User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(user.getStatus() == CustomerStatus.SUSPENDED)
                .build())
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
