package uni.djem.spring.jokesworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class JokesWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(JokesWorldApplication.class, args);
	}

}
