package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.PasswordChangeRequest;
import com.vietnguyen.ums.dto.PasswordResetRequest;
import com.vietnguyen.ums.entity.StatusCodeEntity;
import com.vietnguyen.ums.entity.UserEntity;
import com.vietnguyen.ums.entity.UserRoleEntity;
import com.vietnguyen.ums.repo.StatusCodeRepository;
import com.vietnguyen.ums.repo.UserRepository;
import com.vietnguyen.ums.repo.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusCodeRepository statusCodeRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UserEntity admin;
    private UserEntity user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        statusCodeRepository.deleteAll();
        userRoleRepository.deleteAll();

        StatusCodeEntity active = new StatusCodeEntity();
        active.setDomain("ums");
        active.setCode("active");
        active.setName("Active");
        active.setIsActive(true);
        active = statusCodeRepository.save(active);

        UserRoleEntity adminRole = new UserRoleEntity();
        adminRole.setCode("admin");
        adminRole.setName("Admin");
        adminRole = userRoleRepository.save(adminRole);

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setCode("user");
        userRole.setName("User");
        userRole = userRoleRepository.save(userRole);

        admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("adminpass"));
        admin.setStatusId(active.getId());
        admin.setRoleId(adminRole.getId());
        admin = userRepository.save(admin);

        user = new UserEntity();
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("oldpass"));
        user.setStatusId(active.getId());
        user.setRoleId(userRole.getId());
        user = userRepository.save(user);
    }

    @Test
    void userCanChangeOwnPassword() {
        userService.changePassword(new PasswordChangeRequest("oldpass", "newpass"), user.getId());
        UserEntity updated = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newpass", updated.getPasswordHash()));
    }

    @Test
    void adminCanResetOtherPassword() {
        userService.resetPassword(user.getId(), new PasswordResetRequest("resetpass"), admin.getId());
        UserEntity updated = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("resetpass", updated.getPasswordHash()));
    }
}
