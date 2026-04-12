package codebadger.virtual_launch.domain.crawling.domain;

public interface SpecCrawler {
    SpecCrawlingResultDto crawlSpecs(String keyword);

    // 해당 크롤러가 특정 플랫폼을 지원하는지 여부를 반환
    boolean supports(String platform);
}
