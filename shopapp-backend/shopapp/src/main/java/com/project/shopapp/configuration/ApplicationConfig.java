package com.project.shopapp.configuration;

import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            if(userRepository.findByPhoneNumber("admin").isEmpty()){
                Role role = roleRepository.findByName("ADMIN").orElseThrow(
                        () -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
                HashSet<Role> roles = new HashSet<>();
                roles.add(role);

                User user = User.builder()
                        .phoneNumber("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it ");
            }
        };
    }
}
