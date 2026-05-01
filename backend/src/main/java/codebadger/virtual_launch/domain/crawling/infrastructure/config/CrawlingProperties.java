package codebadger.virtual_launch.domain.crawling.infrastructure.config;

import codebadger.virtual_launch.common.config.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-crawling.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "crawling")
@Getter @Setter
public class CrawlingProperties {


}
