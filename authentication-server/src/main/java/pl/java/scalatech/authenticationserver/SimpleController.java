package pl.java.scalatech.authenticationserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class SimpleController {
    @Autowired
    private OAuth2ClientContext clientContext;
    @GetMapping("/token")
    String showToken() {
        return clientContext.getAccessToken().getValue();
    }
}
