package com.jigumulmi.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.KakaoMemberInfoDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    private final MemberRepository memberRepository;

    public void authorize(String authorizationCode, HttpSession session) throws JsonProcessingException {
        String accessToken = getAccessToken(authorizationCode);

        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);

        Member member = registerKakaoUserIfNeeded(kakaoMemberInfo);

        forceLogin(member, session);
    }

    /**
     * "인가 코드"로 "액세스 토큰" 요청
     *
     * @param authorizationCode: 카카오 서버 제공 인가코드
     * @return accessToken: 인가코드로 얻은 엑세스 토큰
     * @throws JsonProcessingException
     */
    private String getAccessToken(String authorizationCode) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("client_secret", KAKAO_CLIENT_SECRET);
        body.add("redirect_uri", KAKAO_REDIRECT_URL);
        body.add("code", authorizationCode);

        // Http Header와 Http Body를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        // HTTP 요청 보내기 그리고 response의 응답 받기
        // RestTemplate : 간편하게 rest API 호출할 수 있는 스프링 내장 클래스
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        System.out.println("response = " + response);

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        // ObjectMapper : json을 자바 객체로.
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();

        return accessToken;
    }

    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        // 토큰으로 카카오 API 호출
        // HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoMemberInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        //String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return KakaoMemberInfoDto.builder().email(email).build();
    }

    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        String kakaoEmail = kakaoMemberInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoMember == null) {
            //String nickname = kakaoMemberInfo.getNickname();
            kakaoMember = Member.builder().email(kakaoEmail).build();
            memberRepository.save(kakaoMember);
        }

        return kakaoMember;
    }

    private void forceLogin(Member kakaoMember, HttpSession session) {
        UserDetails userDetails = new UserDetailsImpl(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }
}
