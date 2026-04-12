package codebadger.virtual_launch.domain.crawling.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorCrawlingService {
    @Async
    @Transactional
    public void crawlReviews(String keyword) {
    }
}
