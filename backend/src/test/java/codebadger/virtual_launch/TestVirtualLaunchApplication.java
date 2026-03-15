package codebadger.virtual_launch;

import org.springframework.boot.SpringApplication;

public class TestVirtualLaunchApplication {

	public static void main(String[] args) {
		SpringApplication.from(VirtualLaunchApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
