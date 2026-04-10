package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.member.domain.entity.CreditOwnerType;
import codebadger.virtual_launch.domain.member.domain.entity.CreditWallet;
import codebadger.virtual_launch.domain.member.domain.repository.CreditWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditWalletRepository creditWalletRepository;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class}, // 낙관적 락 예외 시 재시도
            maxAttempts = 3, // 최대 3번 시도 (처음 1번 + 재시도 2번)
            backoff = @Backoff(delay = 100) // 0.1초 간격으로 재시도
    )
    @Transactional
    public Long deductCredit(Long ownerId, CreditOwnerType ownerType, Long amount) {
        CreditWallet wallet = findWalletOrThrow(ownerId, ownerType);
        wallet.deduct(amount);
        return wallet.getBalance();
    }

    @Recover // 3번 모두 실패했을 때 실행될 복구 로직
    public Long recover(ObjectOptimisticLockingFailureException e, Long ownerId, CreditOwnerType ownerType, Long amount) {
        log.error("크레딧 차감 최종 실패 - OwnerID: {}, Error: {}", ownerId, e.getMessage());
        throw new BusinessException(ErrorCode.CONCURRENCY_ERROR); // 사용자에게 알림
    }

    private CreditWallet findWalletOrThrow(Long id, CreditOwnerType type) {
        return switch (type) {
            case MEMBER -> creditWalletRepository.findByMemberId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Member wallet not found: " + id));
            case COMPANY -> creditWalletRepository.findByCompanyId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Company wallet not found: " + id));
        };
    }


}
