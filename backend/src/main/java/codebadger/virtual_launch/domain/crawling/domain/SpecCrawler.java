package codebadger.virtual_launch.domain.crawling.domain;

public interface SpecCrawler {
    SpecCrawlingResultDto crawlSpecs(String keyword);
}
