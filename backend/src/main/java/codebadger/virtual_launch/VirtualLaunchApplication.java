package codebadger.virtual_launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VirtualLaunchApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualLaunchApplication.class, args);
	}

}
