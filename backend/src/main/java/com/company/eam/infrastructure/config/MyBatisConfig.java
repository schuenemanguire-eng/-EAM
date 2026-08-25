package com.company.eam.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.company.eam.infrastructure.persistence.mapper")
public class MyBatisConfig {
}
