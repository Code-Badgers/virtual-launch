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

    // 개인용 지갑 생성
    public static CreditWallet createMemberWallet(Long memberId) {
        // 1. memberId가 null인지 체크
        if (memberId == null) {
            throw new IllegalArgumentException("개인 지갑 생성 시 회원 ID는 필수입니다.");
        }

        // 2. 내부 생성자를 호출 (개인이므로 companyId는 null, 초기 잔액은 0)
        return new CreditWallet(memberId, null, 0L);
    }

    // 기업용 지갑 생성
    public static CreditWallet createCompanyWallet(Long companyId) {
        // 1. companyId가 null인지 체크
        if (companyId == null) {
            throw new IllegalArgumentException("기업 지갑 생성 시 기업 ID는 필수입니다.");
        }
        // 2. 내부 생성자를 호출하여 객체 생성 (이때 memberId는 null로 고정)
        return new CreditWallet(null, companyId, 0L);
    }
}
