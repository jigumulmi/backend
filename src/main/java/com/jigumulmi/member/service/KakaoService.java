package com.jigumulmi.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.KakaoMemberInfoDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Value("${kakao.admin.key}")
    private String KAKAO_ADMIN_KEY;

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final MemberRepository memberRepository;


    @Transactional
    public KakaoAuthResponseDto authorize(String authorizationCode, HttpSession session) throws JsonProcessingException {
        String accessToken = getAccessToken(authorizationCode);

        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);

        Member member = registerKakaoUserIfNeeded(kakaoMemberInfo, accessToken);

        forceLogin(member, session);

        String nicknameFromDb = member.getNickname();
        if (nicknameFromDb == null) { // 신규 회원 -> 회원가입
            Long id = member.getId();
            String email = member.getEmail();
            String[] splitEmail = email.split("@");
            String tempNickname = splitEmail[0] + 739 + id;
            return KakaoAuthResponseDto.builder().hasRegistered(false).nickname(tempNickname).build();
        } else { // 기존 회원 -> 로그인
            return KakaoAuthResponseDto.builder().hasRegistered(true).nickname(nicknameFromDb).build();
        }

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

    private Long getKakaoUserId(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/access_token_info",
                HttpMethod.GET,
                kakaoTokenInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();

        return id;
    }

    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo, String accessToken) throws JsonProcessingException {
        String kakaoEmail = kakaoMemberInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoMember == null) {
            Long kakaoUserId = getKakaoUserId(accessToken);

            //String nickname = kakaoMemberInfo.getNickname();
            kakaoMember = Member.builder().email(kakaoEmail).kakaoUserId(kakaoUserId).build();
            memberRepository.save(kakaoMember);

            EntityManager entityManager = emf.createEntityManager();
            entityManager.flush();
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

    public void unlink(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Long kakaoUserId = member.getKakaoUserId();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", kakaoUserId);

        HttpEntity<MultiValueMap<String, Object>> kakaoUnlinkRequest = new HttpEntity<>(body, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                kakaoUnlinkRequest,
                String.class
        );
    }
}
