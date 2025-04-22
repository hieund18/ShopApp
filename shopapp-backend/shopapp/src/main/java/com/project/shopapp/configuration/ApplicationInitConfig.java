package com.project.shopapp.configuration;

import java.util.Set;

import com.project.shopapp.constant.PredefinedRole;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USERNAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application...");
        return args -> {
            if (userRepository.findByPhoneNumber(ADMIN_USERNAME).isEmpty()) {
                roleRepository.save(
                        Role.builder().name(PredefinedRole.USER.name()).build());

                Role role = roleRepository.save(
                        Role.builder().name(PredefinedRole.ADMIN.name()).build());

                User user = User.builder()
                        .phoneNumber(ADMIN_USERNAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(Set.of(role))
                        .isActive(true)
                        .build();

                userRepository.save(user);

                log.warn("Admin user has been created with default password: admin, please change it ");
            }
            log.info("Application initialization completed...");
        };
    }
}
