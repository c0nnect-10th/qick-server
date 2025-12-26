package connect.qick.global.security.config;

import connect.qick.global.security.jwt.filter.JwtExceptionFilter;
import connect.qick.global.security.jwt.filter.JwtFilter;
import connect.qick.global.security.jwt.handler.CustomAccessDeniedHandler;
import connect.qick.global.security.jwt.handler.CustomAuthenticationEntryPoint;
import connect.qick.global.util.ApiResponseWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtFilter jwtFilter;
  private final ApiResponseWriter apiResponseWriter;
  private final JwtExceptionFilter jwtExceptionFilter;

  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

  @Bean
  public CustomAccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler(apiResponseWriter);
  }

  @Bean
  public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint(apiResponseWriter);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtFilter.class)

        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/login/**").permitAll()
            .requestMatchers("/user/student").hasRole("STUDENT")
            .requestMatchers("/volunteer/create", "/volunteer/delete/*").hasRole("TEACHER")
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

            .anyRequest().authenticated()
        )

        .exceptionHandling(ex ->
              ex.accessDeniedHandler(customAccessDeniedHandler())
              .authenticationEntryPoint(customAuthenticationEntryPoint())
        );


    return http.build();
  }

}
