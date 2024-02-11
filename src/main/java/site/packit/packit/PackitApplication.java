package site.packit.packit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PackitApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackitApplication.class, args);
	}

}
