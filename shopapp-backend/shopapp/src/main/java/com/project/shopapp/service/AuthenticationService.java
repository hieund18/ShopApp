package com.project.shopapp.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.shopapp.constant.PredefinedRole;
import com.project.shopapp.dto.request.*;
import com.project.shopapp.dto.response.AuthenticationResponse;
import com.project.shopapp.dto.response.IntrospectResponse;
import com.project.shopapp.entity.InvalidatedToken;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.repository.InvalidatedTokenRepository;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.repository.httpclient.OutboundGithubIdentityClient;
import com.project.shopapp.repository.httpclient.OutboundGithubUserClient;
import com.project.shopapp.repository.httpclient.OutboundIdentityClient;
import com.project.shopapp.repository.httpclient.OutboundUserClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundGithubIdentityClient outboundGithubIdentityClient;
    OutboundUserClient outboundUserClient;
    OutboundGithubUserClient outboundGithubUserClient;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected int VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected int REFRESHABLE_DURATION;

    @NonFinal
    @Value("${outbound.identity.google.client-id}")
    protected String CLIENT_ID_GOOGLE;

    @NonFinal
    @Value("${outbound.identity.google.client-secret}")
    protected String CLIENT_SECRET_GOOGLE;

    @NonFinal
    @Value("${outbound.identity.google.redirect-uri}")
    protected String REDIRECT_URI_GOOGLE;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    @NonFinal
    @Value("${outbound.identity.github.client-id}")
    protected String CLIENT_ID_GITHUB;

    @NonFinal
    @Value("${outbound.identity.github.client-secret}")
    protected String CLIENT_SECRET_GITHUB;

    @NonFinal
    @Value("${outbound.identity.github.redirect-uri}")
    protected String REDIRECT_URI_GITHUB;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("SIGNER KEY: {}", SIGNER_KEY);

        User user = userRepository
                .findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getIsActive()) throw new AppException(ErrorCode.DEACTIVATED_USER);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse outboundAuthenticate(String code) {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID_GOOGLE)
                .clientSecret(CLIENT_SECRET_GOOGLE)
                .redirectUri(REDIRECT_URI_GOOGLE)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE: {}", response);

        //get user info

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("user info: {}", userInfo);

        //onboard user

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.USER.name()).ifPresent(roles::add);

        var user = userRepository.findByPhoneNumber(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(User.builder()
                        .googleAccountId(userInfo.getId())
                        .phoneNumber(userInfo.getEmail())
                        .fullName(userInfo.getName())
                        .isActive(true)
                        .roles(roles)
                        .build())
        );

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse outboundAuthenticateGithub(String code) {
        var response = outboundGithubIdentityClient
                .exchangeToken(GithubExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID_GITHUB)
                        .clientSecret(CLIENT_SECRET_GITHUB)
                        .redirectUri(REDIRECT_URI_GITHUB)
                        .build());

        log.info("Token response: {}", response);

        var userInfo = outboundGithubUserClient.getUserInfo("Bearer " + response.getAccessToken());

        log.info("User info: {}", userInfo);

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.USER.name()).ifPresent(roles::add);

        var user = userRepository.findByGithubAccountId(userInfo.getId()).orElseGet(
                () -> userRepository.save(User.builder()
                        .githubAccountId(userInfo.getId())
                        .fullName(userInfo.getName())
                        .phoneNumber(userInfo.getLogin())
                        .roles(roles)
                        .isActive(true)
                        .build())
        );

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public void linkGoogleAccount(String code) {
        var context = SecurityContextHolder.getContext();
        var phoneNumber = context.getAuthentication().getName();

        var user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (StringUtils.hasText(user.getGoogleAccountId()))
            throw new AppException(ErrorCode.ACCOUNT_LINKED_GOOGLE);

        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID_GOOGLE)
                .clientSecret(CLIENT_SECRET_GOOGLE)
                .redirectUri(REDIRECT_URI_GOOGLE)
                .grantType(GRANT_TYPE)
                .build());

        log.info("token response: {}", response);

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("user info: {}", userInfo);

        user.setGoogleAccountId(userInfo.getId());

        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.GOOGLE_ACCOUNT_EXISTED);
        }

    }

    public void linkGithubAccount(String code) {
        var context = SecurityContextHolder.getContext();
        var phoneNumber = context.getAuthentication().getName();

        var user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (StringUtils.hasText(user.getGithubAccountId()))
            throw new AppException(ErrorCode.ACCOUNT_LINKED_GITHUB);

        var response = outboundGithubIdentityClient.exchangeToken(GithubExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID_GITHUB)
                .clientSecret(CLIENT_SECRET_GITHUB)
                .redirectUri(REDIRECT_URI_GITHUB)
                .build());

        log.info("token response: {}", response);

        var userInfo = outboundGithubUserClient.getUserInfo("Bearer " + response.getAccessToken());

        log.info("user info: {}", userInfo);

        user.setGithubAccountId(userInfo.getId());

        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.GITHUB_ACCOUNT_EXISTED);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
            log.info(e.getMessage());
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {

        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var user = userRepository
                .findByPhoneNumber(signedJWT.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jti = signToken.getJWTClaimsSet().getJWTID();
            var expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getPhoneNumber())
                .issuer("hieu.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });

        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(verifier);

        var expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var phoneNumber = signedJWT.getJWTClaimsSet().getSubject();
        if (userRepository.existsByPhoneNumberAndIsActive(phoneNumber, false))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
