package pl.java.scalatech.resourceserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;
@Configuration
@Slf4j
class JWTConfig {
    @Value("${jwt.key}")
    private String jwtKey;

    @Bean
    TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        var converter = new MyJwtAutheenticationConverter();
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(new MyJwtAuthenticationConverter());
        converter.setAccessTokenConverter(defaultAccessTokenConverter);
        converter.setSigningKey(jwtKey);
        return converter;
    }

    @Bean
    @Primary
    DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    static class MyJwtAuthenticationConverter extends DefaultUserAuthenticationConverter {

        public static final String USER_NAME = "user_name";

        @Override
        public Authentication extractAuthentication(Map<String, ?> map) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            Object userName = map.get(USER_NAME);
            UserDetails user = new User((String) userName, "N/A", authorities);
            return new UsernamePasswordAuthenticationToken(user, "N/A", authorities);
        }

        private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
            if (!map.containsKey(AUTHORITIES)) {
                return emptyList();
            }
            Object authorities = map.get(AUTHORITIES);
            if (authorities instanceof String) {
                return commaSeparatedStringToAuthorityList((String) authorities);
            }
            if (authorities instanceof Collection) {
                return commaSeparatedStringToAuthorityList(StringUtils
                        .collectionToCommaDelimitedString((Collection<?>) authorities));
            }
            throw new IllegalArgumentException("Authorities must be either a String or a Collection");
        }
    }

    static class MyJwtAutheenticationConverter extends JwtAccessTokenConverter {
        @Override
        public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
            OAuth2Authentication result = super.extractAuthentication(map);
            result.setDetails(map);
            log.info("Oauth2Authentication : {}", result.getDetails());
            return result;
        }
    }
}
