package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import spring.database.entity.CustomUserDetails;
import spring.database.entity.Role;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static spring.database.entity.Role.ADMIN;
import static spring.database.entity.Role.OPERATOR;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/users/registration", "/v3/api-docs/", "/swagger-ui/").permitAll()
                        .requestMatchers("/admin/**").hasRole(ADMIN.getAuthority())
                        .requestMatchers(antMatcher("/users")).hasAnyAuthority(ADMIN.getAuthority())
                        .requestMatchers(antMatcher("/users")).hasAnyAuthority(OPERATOR.getAuthority())
                        .requestMatchers(antMatcher("/users/{\\d}/delete")).hasAnyAuthority(ADMIN.getAuthority())
                        .requestMatchers(antMatcher("/users/{\\d}/update")).hasAnyAuthority(ADMIN.getAuthority())
                        .requestMatchers(antMatcher("/users/{\\d}/update")).hasAnyAuthority(OPERATOR.getAuthority())
                        .anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                            if (userDetails.getRole().equals(Role.ADMIN) || userDetails.getRole().equals(Role.OPERATOR)) {
                                response.sendRedirect("/users");
                            } else {
                                response.sendRedirect("/users/" + userDetails.getId());
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
}
