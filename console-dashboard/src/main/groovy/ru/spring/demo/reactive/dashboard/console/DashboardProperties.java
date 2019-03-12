package ru.spring.demo.reactive.dashboard.console;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dashboard")
public class DashboardProperties {
    private String letterSignatureUrl;
    private String letterGrabberUrl;
    private String guardUrl;
}
