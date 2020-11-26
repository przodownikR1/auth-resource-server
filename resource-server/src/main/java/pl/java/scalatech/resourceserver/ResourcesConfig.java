package pl.java.scalatech.resourceserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableResourceServer
@RequiredArgsConstructor
@Slf4j
class ResourcesConfig extends ResourceServerConfigurerAdapter {


    private static final String[] AUTH_WHITELIST = {"/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
            "/configuration/security", "/open-api", "/swagger-ui*", "/swagger-ui/**", "/api-docs/**"};
    private final DefaultTokenServices tokenServices;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .exceptionHandling()
            .accessDeniedHandler(new OAuth2AccessDeniedHandler())
            .and()
            .authorizeRequests()
            .antMatchers("/actuator/**")
            .permitAll()
            .antMatchers("/fake/check_token")
            .permitAll()
            .antMatchers(AUTH_WHITELIST)
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .cors()
            .and()
            .csrf()
            .disable();
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.stateless(true);
        resources.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        resources.tokenServices(tokenServices);
    }


    /*@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("api");
           //resources.resourceId("other-clients");
    }*/

    @Bean
    DomainPermissionEvaluator domainPermissionEvaluator(){
        return new DomainPermissionEvaluator();
    }
    }
