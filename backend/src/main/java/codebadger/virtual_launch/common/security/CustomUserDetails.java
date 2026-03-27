package codebadger.virtual_launch.common.security;

import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.entity.MemberRole;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 토큰 정보로만 재조립할 때 사용하는 정적 메서드
    public static CustomUserDetails fromValues(Long id, String email, String role) {
        Member member = Member.stubBuilder()
                .id(id)
                .email(email)
                .role(MemberRole.valueOf(role))
                .build();
        return new CustomUserDetails(member);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole()));
    }

    @Override
    public @Nullable String getPassword() {
        return member.getPassword();
    }

    public Long getId() {
        return member.getId();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
