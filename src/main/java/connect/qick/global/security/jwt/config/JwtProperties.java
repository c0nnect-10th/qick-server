package connect.qick.global.security.jwt.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix="spring.jwt")
public class JwtProperties {

    private String secretKey;

    private long accessExpiration;

    private long refreshExpiration;
}
