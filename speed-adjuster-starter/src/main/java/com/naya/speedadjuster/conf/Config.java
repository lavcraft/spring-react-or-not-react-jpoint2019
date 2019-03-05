package com.naya.speedadjuster.conf;

import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.aspects.SpeedAdjusterAspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author Evgeny Borisov
 */
@Configuration
@EnableConfigurationProperties(AdjustmentProperties.class)
@EnableAspectJAutoProxy
@EnableScheduling
@ComponentScan(basePackages = "com.naya.speedadjuster.services")
public class Config {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public SpeedAdjusterAspect speedAdjusterAspect(){
        return new SpeedAdjusterAspect();
    }
}
