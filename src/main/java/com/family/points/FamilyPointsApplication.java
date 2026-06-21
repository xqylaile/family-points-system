package com.family.points;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 家庭积分系统启动类
 */
@SpringBootApplication
@MapperScan("com.family.points.mapper")
public class FamilyPointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyPointsApplication.class, args);
        System.out.println("家庭积分系统启动成功！访问地址：http://localhost:8080");
    }
}
