package com.infinum.springsecurityoauthclient.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
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

import java.time.Instant;
import java.util.Collections;
import java.util.logging.Logger;

@Profile("custom")
@Controller
public class HomeController {

    private final Logger log = Logger.getLogger("HomeController");

    @Value("${resource-server.url}")
    private String resourceServerUrl;

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final RestTemplate restTemplate = new RestTemplate();

    public HomeController(OAuth2AuthorizedClientService authorizedClientService, OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientService = authorizedClientService;
        this.authorizedClientManager = authorizedClientManager;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @AuthenticationPrincipal OidcUser oidcUser,
                       OAuth2AuthenticationToken authentication) {

//         Get the authorized client (OAuth2AuthorizedClient contains the access token)
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String accessTokenValue = accessToken.getTokenValue();

        OidcIdToken idToken = oidcUser.getIdToken();
        String idTokenValue = idToken.getTokenValue();

        if (accessToken.getExpiresAt().isBefore(Instant.now())) {
            log.info("Access token expired. Refreshing...");

            // Refresh the access token using the authorized client manager
            OAuth2AuthorizedClient refreshedClient = refreshAccessToken(authentication);

            // Get the refreshed access token
            accessToken = refreshedClient.getAccessToken();
            accessTokenValue = accessToken.getTokenValue();
        }

        String authServerResponse = callAuthorizationServer(accessTokenValue);

        model.addAttribute("user", oidcUser.getSubject());
        model.addAttribute("idToken", idTokenValue);
        model.addAttribute("accessToken", accessTokenValue);
        model.addAttribute("authServerResponse", authServerResponse);

        return "home";
    }

    private OAuth2AuthorizedClient refreshAccessToken(OAuth2AuthenticationToken authentication) {
        // Create a refresh request and use the manager to refresh the client
        return authorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId(authentication.getAuthorizedClientRegistrationId())
                        .principal(authentication)
                        .build()
        );
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
