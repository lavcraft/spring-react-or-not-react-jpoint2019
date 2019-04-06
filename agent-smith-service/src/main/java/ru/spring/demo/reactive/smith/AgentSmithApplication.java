package ru.spring.demo.reactive.smith;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.RejectedExecutionHandler;

@Slf4j
@EnableAsync
@SpringBootApplication
public class AgentSmithApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentSmithApplication.class, args);
    }

    @Bean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        return (r, executor) -> log.info("Miss. Not enough soldiers!");
    }
}

