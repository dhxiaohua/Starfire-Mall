package com.starfire;

import com.starfire.entity.User;
import com.starfire.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@MapperScan("com.starfire.mapper")
public class StarfireMallApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StarfireMallApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner initAdmin(UserMapper userMapper, PasswordEncoder passwordEncoder,
            @Value("${admin.username:}") String adminUsername,
            @Value("${admin.password:}") String adminPassword) {
        return args -> {
            if (adminUsername == null || adminUsername.isEmpty() || adminPassword == null || adminPassword.isEmpty()) {
                System.out.println("未配置管理员账号，跳过创建");
                return;
            }
            
            // 检查默认管理员是否存在
            User existingAdmin = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", adminUsername)
            );
            
            if (existingAdmin == null) {
                // 创建默认管理员
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole("admin");
                admin.setAdminStatus("approved");
                admin.setStatus(1);
                userMapper.insert(admin);
                System.out.println("默认管理员账号已创建: " + adminUsername);
            } else {
                // 始终更新管理员密码确保正确
                existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
                existingAdmin.setRole("admin");
                existingAdmin.setAdminStatus("approved");
                userMapper.updateById(existingAdmin);
                System.out.println("默认管理员账号已更新: " + adminUsername);
            }
        };
    }
}
