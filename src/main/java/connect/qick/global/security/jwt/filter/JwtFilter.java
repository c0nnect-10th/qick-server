package connect.qick.global.security.jwt.filter;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.global.security.jwt.JwtExtract;
import connect.qick.global.security.jwt.JwtProvider;
import connect.qick.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtExtract jwtExtract;
    private final JwtProvider jwtProvider;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtExtract.extractTokenFromRequest(request);
        if (token != null) {
            Claims claims = jwtProvider.getClaims(token).getPayload();
            checkEndpointAuthorization(request, claims);

            SecurityContextHolder.getContext().setAuthentication(jwtExtract.getAuthentication(token));
        }
        filterChain.doFilter(request, response);
    }


    public void checkEndpointAuthorization(HttpServletRequest request, Claims claims) {
        String uri = request.getRequestURI();
        String tokenType = claims.get("token_type", String.class);

        if (!TokenType.SIGNUP.name().equals(tokenType)) {
            return;
        }

        List<String> allowedPaths = jwtExtract.extractAllowedPaths(claims);
        boolean allowed = allowedPaths.stream()
            .anyMatch(path -> match(path, uri));

        if (!allowed) {
            throw new AuthException(AuthStatusCode.REQUIRE_SIGNUP_COMPLETION);
        }
    }

    private boolean match(String path, String uri) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return PATH_MATCHER.match(path, uri);
    }
}
