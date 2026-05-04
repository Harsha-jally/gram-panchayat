package com.harsha;

import com.harsha.entity.User;
import com.harsha.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class GramPanchayatApplication {

    public static void main(String[] args) {
        SpringApplication.run(GramPanchayatApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            // Load pincodes from static JSON file in resources
            ObjectMapper mapper = new ObjectMapper();
            List<String> pincodes = mapper.readValue(
                    new ClassPathResource("karnataka-pincodes.json").getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            System.out.println("📍 Total pincodes loaded: " + pincodes.size());

            int created = 0;
            for (String pincode : pincodes) {
                String email = "admin@" + pincode + ".com";
                if (userRepo.findByEmail(email).isEmpty()) {
                    User admin = new User("Admin " + pincode, "0000000000",
                            email, encoder.encode(pincode));
                    admin.setRole("ADMIN");
                    userRepo.save(admin);
                    created++;
                }
            }
            System.out.println("✅ Total admins seeded: " + created);
        };
    }
}