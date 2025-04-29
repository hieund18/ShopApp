package com.project.shopapp.repository.httpclient;

import com.project.shopapp.dto.response.OutboundGithubUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "outbound-github-user", url = "https://api.github.com")
public interface OutboundGithubUserClient {
    @GetMapping("/user")
    OutboundGithubUserResponse getUserInfo(@RequestHeader("Authorization") String authorization);
}
