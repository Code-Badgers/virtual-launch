package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductSpecService {

    private final ProductSpecRepository productSpecRepository;
    private final CategoryRepository categoryRepository;

}
