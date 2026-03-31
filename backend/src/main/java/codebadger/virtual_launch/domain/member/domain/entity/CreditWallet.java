package codebadger.virtual_launch.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "credit_wallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    // Long 타입 잔액
    @Column(nullable = false)
    private Long balance;

    // 낙관적 락을 위한 버전 관리
    @Version
    private Long version;

    // 식별자(ID) 기반 연관관계
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    private CreditWallet(Long memberId, Long companyId, Long balance) {
        this.memberId = memberId;
        this.companyId = companyId;
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }
}
