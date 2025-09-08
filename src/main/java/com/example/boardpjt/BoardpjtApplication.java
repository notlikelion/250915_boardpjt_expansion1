package com.example.boardpjt;

import com.example.boardpjt.model.entity.UserAccount;
import com.example.boardpjt.model.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BoardpjtApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardpjtApplication.class, args);
	}

    @Bean
    public CommandLineRunner initAdmin(UserAccountRepository userAccountRepository) {
        return args -> {
                String adminUsername = "admin";
                String adminPassword = "pass";
                boolean adminExists = userAccountRepository.findByUsername(adminUsername).isPresent();

                if (!adminExists) { // 기존에 추가된 어드민이 없으면...
                    UserAccount admin = new UserAccount();
                    admin.setUsername(adminUsername);
                    admin.setPassword(adminPassword);
                    admin.setRole("ROLE_ADMIN");
                    userAccountRepository.save(admin);
                }
        };
    }
}
