package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.domain.member.domain.entity.CreditOwnerType;
import codebadger.virtual_launch.domain.member.domain.entity.CreditWallet;
import codebadger.virtual_launch.domain.member.domain.repository.CreditWalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers // 1. JUnit 5가 컨테이너 생명주기를 관리하게 합니다.
class CreditServiceConcurrencyTest {

    @Container // 2. 컨테이너를 자동으로 시작/종료합니다.
    @ServiceConnection // 3. 핵심! 위치를 여기 필드 위로 옮겨야 합니다.
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditWalletRepository creditWalletRepository;

    @Test
    @DisplayName("동시에 10개의 차감 요청이 들어와도 낙관적 락과 재시도 로직 덕분에 정합성이 유지된다.")
    void deductCreditConcurrencyTest() throws InterruptedException {
        // ... (이하 로직은 동일) ...
        Long ownerId = 1L;
        CreditWallet walletEntity = CreditWallet.builder()
                .memberId(ownerId)
                .balance(500L)
                .build();

        creditWalletRepository.save(walletEntity);

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    creditService.deductCredit(ownerId, CreditOwnerType.MEMBER, 100L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        CreditWallet wallet = creditWalletRepository.findByMemberId(ownerId).orElseThrow();
        assertThat(wallet.getBalance()).isEqualTo(0L);
    }
}