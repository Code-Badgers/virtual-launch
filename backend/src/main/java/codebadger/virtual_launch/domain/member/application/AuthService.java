package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.common.security.CustomUserDetails;
import codebadger.virtual_launch.common.security.JwtTokenProvider;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long signUp(SignUpCommand command) {
        // 1. 중복 이메일 확인 로직
        if (memberRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(command.password());

        // 3. Member 엔티티 생성
        Member member = Member.create(command.email(), encodePassword, command.role());

        // 4. 저장 및 ID 반환
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Transactional
    public String login(LoginCommand command) {
        // 1. 인증 전용 토큰 생성 (ID/PW만 담음)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(command.email(), command.password());

        // 2. 지휘자(AuthenticationManager)에게 인증 위임
        // 여기서 내부적으로 CustomUserDetailsService.loadUserByUsername()이 호출
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증이 완료된 authentication 객체로 JWT 생성
        return jwtTokenProvider.createAccessToken(authentication);
    }


}
