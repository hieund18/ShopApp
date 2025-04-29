package com.project.shopapp.repository.httpclient;

import com.project.shopapp.dto.request.GithubExchangeTokenRequest;
import com.project.shopapp.dto.response.GithubExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-github-identity", url = "https://github.com")
public interface OutboundGithubIdentityClient {
    @PostMapping(value = "/login/oauth/access_token",
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    GithubExchangeTokenResponse exchangeToken(@QueryMap GithubExchangeTokenRequest request);
}
