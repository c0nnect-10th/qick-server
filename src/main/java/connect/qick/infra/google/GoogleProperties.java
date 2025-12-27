package connect.qick.infra.google;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix="spring.oauth2.google")
public class GoogleProperties {

    private String clientId;
}
