package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.common.security.JwtTokenProvider;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.entity.MemberRole;
import codebadger.virtual_launch.domain.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito를 사용하기 위한 확장
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldCreateNewMemberSuccessfully() {
        //given
        SignUpCommand command = new SignUpCommand("test@email.com", "password123", MemberRole.ROLE_USER);
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");
        Member member = Member.stubBuilder().id(1L).email("test@email.com").role(MemberRole.ROLE_USER).build();
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        //when
        Long memberId = authService.signUp(command);

        //then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        // 1. Given: 로그인 시도 정보와 기대하는 토큰값 설정
        LoginCommand command = new LoginCommand("test@email.com", "password123");
        String expectedToken = "mocked-jwt-token";

        // AuthenticationManager가 인증에 성공했을 때 반환할 Mock Authentication 객체 생성
        Authentication authentication = mock(Authentication.class);

        // BDDMockito 스타일로 Mock 동작 정의
        // authenticationManager.authenticate()가 호출되면 가짜 authentication 객체를 반환
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        // jwtTokenProvider.createToken()이 호출되면 가짜 토큰을 반환
        given(jwtTokenProvider.createAccessToken(authentication))
                .willReturn(expectedToken);

        // 2. When: 실제 로그인 로직 실행
        // 로그인 결과로 DTO(예: TokenResponse)를 반환한다고 가정합니다.
        String token = authService.login(command);

        // 3. Then: 결과 검증 (AssertJ 사용)
        assertThat(token).isNotNull();
        assertThat(token).isEqualTo(expectedToken); // 발행된 토큰이 가짜 토큰(mocked-jwt-token)과 일치하는지 확인

        // 핵심 로직이 실제로 호출되었는지 검증
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).createAccessToken(authentication);
    }
}