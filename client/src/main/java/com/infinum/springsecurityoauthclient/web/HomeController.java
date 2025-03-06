package com.infinum.springsecurityoauthclient.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.logging.Logger;

@Profile("custom")
@Controller
public class HomeController {

    private final Logger log = Logger.getLogger("HomeController");

    @Value("${resource-server.url}")
    private String resourceServerUrl;

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final RestTemplate restTemplate = new RestTemplate();

    public HomeController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @AuthenticationPrincipal OidcUser oidcUser,
                       OAuth2AuthenticationToken authentication) {

        // Get the authorized client (OAuth2AuthorizedClient contains the access token)
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String accessTokenValue = accessToken.getTokenValue();

        OidcIdToken idToken = oidcUser.getIdToken();
        String idTokenValue = idToken.getTokenValue();

        String authServerResponse = callAuthorizationServer(accessTokenValue);

        model.addAttribute("user", oidcUser.getSubject());
        model.addAttribute("idToken", idTokenValue);
        model.addAttribute("accessToken", accessTokenValue);
        model.addAttribute("authServerResponse", authServerResponse);

        return "home";
    }

    private String callAuthorizationServer(String accessToken) {
        String url = resourceServerUrl + "/greetings";

        log.info("➡ Calling Resource Server at: %s".formatted(url));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            return "Error: " + e.getStatusCode();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
