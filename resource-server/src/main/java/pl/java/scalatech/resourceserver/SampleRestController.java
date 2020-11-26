package pl.java.scalatech.resourceserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.security.core.authority.AuthorityUtils.authorityListToSet;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
class SampleRestController {
    public static final String HELLO = "hello";
    private final DefaultTokenServices tokenServices;

    @GetMapping(value = {"/test"})
    String greeting() {
        return HELLO;
    }


    @PreAuthorize("hasAuthority('client')")
    @GetMapping(value = {"/client"})
    String client() {
        return HELLO;
    }

    //@PreAuthorize("hasAuthority('read')")
    @PreAuthorize("#oauth2.hasScope('read')")
    @GetMapping(value = {"/read"})
    String read() {
        return HELLO;
    }

    @PreAuthorize("hasAuthority('write')")
    @GetMapping(value = {"/write"})
    String write() {
        return HELLO;
    }

    @GetMapping("/me")
    public ResponseEntity<Principal> get(final Principal principal) {
        log.info("principal : {}", principal);
        return ok(principal);
    }

    @GetMapping("/simple")
    String simple(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details =
                (OAuth2AuthenticationDetails) authentication.getDetails();
        return "simple : " + details.getDecodedDetails();
    }

    @GetMapping("/simple2")
    String simple(Authentication authentication) {
        authentication.getCredentials();
        System.out.println(authentication.toString());
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        String token = details.getTokenValue();
        return "simple : " + token;
    }

    @GetMapping(value = {"/user"}, produces = "application/json")
    Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication()
                                 .getPrincipal());
        userInfo.put("authorities", authorityListToSet(user.getUserAuthentication()
                                                           .getAuthorities()));
        return userInfo;
    }

    @GetMapping("/whoami")
    String whoami(@AuthenticationPrincipal(expression = "name") String name) {
        return name;
    }

    @GetMapping("/endpointuser")
    ResponseEntity<String> endPointUser(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String, Object> additionalInfo = tokenServices.readAccessToken(oAuth2AuthenticationDetails.getTokenValue())
                                                          .getAdditionalInformation();
        return new ResponseEntity<String>("Your UUID: " + additionalInfo.get("uuid")
                                                                        .toString()
                + " , your username: " + authentication.getPrincipal() + " and your role USER",
                OK);
    }

    @GetMapping("/user/info")
    Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt principal) {
        log.info("jwt {}", principal);
        return Map.of("m1", "v1");
    }


}

