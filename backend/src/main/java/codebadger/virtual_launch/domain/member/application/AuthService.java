package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.common.security.JwtTokenProvider;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

}
