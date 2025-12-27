package connect.qick.infra.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GoogleAuthConfig {

    @Bean
    public NetHttpTransport netHttpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public GsonFactory gsonFactory() {
        return new GsonFactory();
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(
            HttpTransport httpTransport,
            GsonFactory gsonFactory,
            GoogleProperties googleProperties
    ) {
        return new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
                .setAudience(List.of(googleProperties.getClientId()))
                .setIssuer("https://accounts.google.com")
                .build();
    }

}
