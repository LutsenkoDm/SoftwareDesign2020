package main;

import main.security.Role;
import main.security.User;
import main.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class KursWorkApplication {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(KursWorkApplication.class, args);
    }

    @Bean
    public void createAdmin() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("$2y$10$2Eymfny/vXNvAFNIC.pzku98.KfOv02trT.Pn8Z9.tR.tWw0YFluC");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        userRepository.save(user);
    }
}

