package com.infinum.springsecurityoauthclient.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Profile("github")
@Controller
public class GithubHomeController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate = new RestTemplate();

    public GithubHomeController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OAuth2User oauth2User,
                       OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        // Fetch GitHub user repositories
        List<Map<String, Object>> repos = getGitHubRepositories(accessToken);

        model.addAttribute("username", oauth2User.getAttributes().get("name"));
        model.addAttribute("repos", repos);

        return "home-github";
    }

    private List<Map<String, Object>> getGitHubRepositories(OAuth2AccessToken accessToken) {
        // GitHub API URL to fetch repositories
        String url = "https://api.github.com/user/repos";

        // Set Authorization header with the access token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken.getTokenValue());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Call GitHub API to fetch repositories
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        return response.getBody();
    }
}
