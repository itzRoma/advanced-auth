package com.itzroma.advancedauth;

import com.itzroma.advancedauth.config.AppConfig;
import com.itzroma.advancedauth.model.Role;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.service.RoleService;
import com.itzroma.advancedauth.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class})
public class AdvancedAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdvancedAuthApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RoleService roleService, UserService userService) {
        return args -> {
            roleService.save(new Role(Role.RoleName.USER));
            Role adminRole = roleService.save(new Role(Role.RoleName.ADMIN));

            User admin = new User("admin", "admin", "admin@mail.com", "123");
            admin.getRoles().add(adminRole);
            admin.setEnabled(true);
            userService.save(admin);
        };
    }
}
