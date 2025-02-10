package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.security.UserDetailsServiceImpl;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class MemberManager {

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${kakao.admin.key}")
    private String KAKAO_ADMIN_KEY;

    private final RestTemplate rt;
    private final ObjectMapper objectMapper;

    private final MemberRepository memberRepository;

    /**
     * "인가 코드"로 "액세스 토큰" 요청
     *
     * @param requestDto 카카오 서버 제공 인가코드, 프론트 서버 제공 리다이렉트 URL
     * @return accessToken 인가코드로 얻은 엑세스 토큰
     * @throws JsonProcessingException 카카오 API 응답 본문 파싱 에러
     */
    public String getKakaoAccessToken(KakaoAuthorizationRequestDto requestDto)
        throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("client_secret", KAKAO_CLIENT_SECRET);
        body.add("redirect_uri", requestDto.getRedirectUrl());
        body.add("code", requestDto.getCode());

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body,
            headers);

        ResponseEntity<String> response = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();
    }

    /**
     *
     * @param accessToken 인가코드로 얻은 액세스 토큰
     * @return 카카오에 등록된 유저 이메일
     * @throws JsonProcessingException
     */
    public String getKakaoUserEmail(String accessToken)
        throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(
            headers);
        ResponseEntity<String> response = rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoMemberInfoRequest,
            String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("kakao_account").get("email").asText();
    }

    /**
     *
     * @param accessToken 인가코드로 얻은 액세스 토큰
     * @return 고유 카카오 회원번호 -> 회원 탈퇴 시 연결 해제하기 위해 필요
     * @throws JsonProcessingException
     */
    public Long getKakaoUserId(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
            "https://kapi.kakao.com/v1/user/access_token_info",
            HttpMethod.GET,
            kakaoTokenInfoRequest,
            String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("id").asLong();
    }

    public KakaoAuthResponseDto getOrCreateMember(HttpSession session,
        String kakaoEmail, String accessToken) throws JsonProcessingException {
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail).orElse(null);

        // TODO 회원가입, 로그인 API 분리
        boolean hasRegistered = true;
        if (kakaoMember == null) {
            hasRegistered = false;

            Long kakaoUserId = getKakaoUserId(accessToken);

            String localPart = kakaoEmail.split("@")[0];
            String tempNickname = localPart + "-" + kakaoUserId;

            kakaoMember = Member.builder()
                .email(kakaoEmail)
                .nickname(tempNickname)
                .kakaoUserId(kakaoUserId)
                .isAdmin(false)
                .build();

            memberRepository.save(kakaoMember);
        }

        UserDetailsServiceImpl.setSecurityContextAndSession(kakaoMember, session);

        return KakaoAuthResponseDto.builder()
            .hasRegistered(hasRegistered)
            .nickname(kakaoMember.getNickname())
            .build();
    }

    public void updateNickname(Member member, String nickname) {
        member.updateNickname(nickname);
        memberRepository.save(member);
    }

    public void deleteMember(Member member) {
        member.deregister();
        memberRepository.save(member);
    }

    public void unlinkKakao(Member member) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "KakaoAK " + KAKAO_ADMIN_KEY);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", member.getKakaoUserId());

        HttpEntity<MultiValueMap<String, Object>> kakaoUnlinkRequest = new HttpEntity<>(body,
            headers);

        rt.exchange(
            "https://kapi.kakao.com/v1/user/unlink",
            HttpMethod.POST,
            kakaoUnlinkRequest,
            String.class
        );
    }
}
