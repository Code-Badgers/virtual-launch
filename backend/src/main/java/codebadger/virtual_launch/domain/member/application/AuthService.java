package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.common.security.CustomUserDetails;
import codebadger.virtual_launch.common.security.JwtTokenProvider;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
        // 1. 회원 조회
        Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 잘못되었습니다.");
        }

        // 3. CustomUserDetails 및 Authentication 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,           // principal (신분증)
                null,                  // credentials (인증 후라 비움)
                userDetails.getAuthorities() // 권한 목록
        );

        // 4. 토큰 발급 및 반환
        return jwtTokenProvider.createAccessToken(authentication);
    }


}
