package codebadger.virtual_launch.domain.member.domain.entity;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
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

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CreditWallet.java 내부 수정

    @Builder
    private CreditWallet(Long memberId, Long companyId, Long balance) {
        this.memberId = memberId;
        this.companyId = companyId;
        this.balance = (balance != null) ? balance : 0L; // null 방어 로직 추가
        // this.updatedAt = LocalDateTime.now(); // 제거: @LastModifiedDate가 처리함
    }

    // 테스트 편의를 위해 잔액을 지정할 수 있는 정적 팩토리 메서드 추가
    public static CreditWallet createMemberWalletWithBalance(Long memberId, Long balance) {
        if (memberId == null) throw new IllegalArgumentException("회원 ID는 필수입니다.");
        return new CreditWallet(memberId, null, balance);
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

    // 잔액에서 금액 차감
    public void deduct(Long amount) {

        if (amount != null && amount <= balance && amount > 0 )  {
            balance -= amount;
        } else if (amount == null || amount < 0){
            throw new BusinessException(ErrorCode.INVALID_CREDIT_VALUE);
        } else if (amount > balance) {
            throw new BusinessException(ErrorCode.LACK_OF_BALANCE);
        }

    }
}
