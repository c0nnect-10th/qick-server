package connect.qick.global.security.jwt.filter;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.global.security.jwt.JwtExtract;
import connect.qick.global.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtExtract jwtExtract;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtExtract.extractTokenFromRequest(request);
        if (token != null) {
            SecurityContextHolder.getContext().setAuthentication(jwtExtract.getAuthentication(token));
        }

        filterChain.doFilter(request, response);
    }
}
