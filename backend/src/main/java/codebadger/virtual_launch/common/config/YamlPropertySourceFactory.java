package codebadger.virtual_launch.common.config;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
    // .yml 파일을 읽어서 Spring의 PropertySource로 변환하는 로직을 구현

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) { // 리소스가 null인 경우 기본 동작으로 처리
            return super.createPropertySource(name, resource);
        }

        // .yml 파일을 읽어서 PropertySource 리스트로 변환
        List<PropertySource<?>> propertySourcesList = new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource());
        return propertySourcesList.get(0);
    }
}
