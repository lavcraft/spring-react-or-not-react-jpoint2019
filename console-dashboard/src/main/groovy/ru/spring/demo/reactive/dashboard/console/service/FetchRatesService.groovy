package ru.spring.demo.reactive.dashboard.console.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.spring.demo.reactive.dashboard.console.model.RateStatus

import java.util.concurrent.CompletableFuture

import static java.util.concurrent.CompletableFuture.completedFuture

@Service
class FetchRatesService {
    @Autowired RestTemplate restTemplate

    @Async
    CompletableFuture<RateStatus> getRateStatus(String letterSignatureUrl) {
        try {
            completedFuture restTemplate.getForObject(letterSignatureUrl, RateStatus.class)
        } catch (ignored) {
            completedFuture new RateStatus().setLetterRps(-1d)
        }
    }
}
