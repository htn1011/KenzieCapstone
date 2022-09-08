package com.kenzie.appserver.config;

import com.kenzie.capstone.service.client.UserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LambdaServiceClientConfiguration {

    @Bean
    public UserServiceClient referralServiceClient() {
        return new UserServiceClient();
    }
}
