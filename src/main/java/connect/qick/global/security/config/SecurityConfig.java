package connect.qick.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

  @Bean //TODO: 컨포넌트로 변경
    public JwtExceptionFilter jwtExceptionFilter() {
    // Spring에 등록된 ObjectMapper는 LocalDateTime 직렬화를 할 수 있는 JavaTimeModule이 자동등록되어있음
    return new JwtExceptionFilter(apiResponseWriter);
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
        .addFilterBefore(jwtExceptionFilter(), JwtFilter.class)

        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/auth/**", "/api/v1/user/join").permitAll()

            .anyRequest().authenticated()
        )

        .exceptionHandling(ex ->
              ex.accessDeniedHandler(customAccessDeniedHandler())
              .authenticationEntryPoint(customAuthenticationEntryPoint())
        );


    return http.build();
  }

}
