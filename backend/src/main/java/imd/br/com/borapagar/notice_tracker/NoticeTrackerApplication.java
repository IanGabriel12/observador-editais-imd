package imd.br.com.borapagar.notice_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class NoticeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoticeTrackerApplication.class, args);
	}

}
