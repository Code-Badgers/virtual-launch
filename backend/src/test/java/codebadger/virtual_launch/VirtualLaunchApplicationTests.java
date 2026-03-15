package codebadger.virtual_launch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class VirtualLaunchApplicationTests {

	@Test
	void contextLoads() {
	}

}
