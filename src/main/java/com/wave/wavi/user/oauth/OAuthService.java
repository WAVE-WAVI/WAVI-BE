package com.wave.wavi.user.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wave.wavi.config.jwt.JwtUtil;
import com.wave.wavi.user.model.GenderType;
import com.wave.wavi.user.model.JobType;
import com.wave.wavi.user.model.LoginType;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    //구글 로그인
    public String googleLogin(String code) throws JsonProcessingException {
        String accessToken = getGoogleAccessToken(code);
        JsonNode googleUserInfo = getGoogleUserInfo(accessToken);
        return processGoogleUser(googleUserInfo);
    }

    private String getGoogleAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        if (jsonNode.has("access_token")) {
            return jsonNode.get("access_token").asText();
        }
        else {
            String error = jsonNode.has("error") ? jsonNode.get("error").asText() : "Unknown error";
            String errorDescription = jsonNode.has("error_description") ? jsonNode.get("error_description").asText() : "No description";
            log.error("Google Access Token Error: {}, Description: {}", error, errorDescription);
            throw new RuntimeException("구글 액세스 토큰을 받아오는 데 실패했습니다.");
        }
    }

    private JsonNode getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();

        log.info("Google User Info: {}", responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.readTree(responseBody);
    }

    private String processGoogleUser(JsonNode googleUserInfo) {
        if (!googleUserInfo.has("email") || googleUserInfo.get("email").isNull()) {
            log.error("Google user info does not certain an email address.");
            throw new RuntimeException("구글 사용자 정보에 이메일이 포함되어 있지 않습니다.");
        }
        String email = googleUserInfo.get("email").asText();
        String nickname = googleUserInfo.has("name") ? googleUserInfo.get("name").asText() : email;

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            user.setLoginType(LoginType.GOOGLE);
            userRepository.save(user);
        }
        else {
            String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            user = User.builder()
                    .email(email)
                    .password(randomPassword)
                    .nickname(nickname)
                    .loginType(LoginType.GOOGLE)
                    //추가 입력 페이지 이동
                    .birthYear(1900L)
                    .gender(GenderType.UNKNOWN)
                    .job(JobType.JOBLESS)
                    .profileImage(1L)
                    .build();
            userRepository.save(user);
        }

        return jwtUtil.createToken(user.getEmail());
    }
}
