package codebadger.virtual_launch.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Member(String email, String password, MemberRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    @Builder(builderMethodName = "stubBuilder")
    private Member(Long id, String email, MemberRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public static Member create(String email, String password, MemberRole role) {
        return Member.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}
