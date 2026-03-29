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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}