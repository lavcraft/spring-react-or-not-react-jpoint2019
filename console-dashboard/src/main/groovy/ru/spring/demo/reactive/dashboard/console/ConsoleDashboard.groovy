package ru.spring.demo.reactive.dashboard.console

import groovy.transform.CompileStatic
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.fusesource.jansi.Ansi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.spring.demo.reactive.dashboard.console.model.RateStatus
import ru.spring.demo.reactive.dashboard.console.service.FetchRatesService

import javax.annotation.PostConstruct
import java.util.concurrent.CompletableFuture

import static java.lang.String.format
import static java.util.concurrent.CompletableFuture.allOf
import static org.fusesource.jansi.Ansi.ansi

@Slf4j
@Component
@CompileStatic
@RequiredArgsConstructor
class ConsoleDashboard {
    public static final int PAD = 20

    @Autowired FetchRatesService   fetchRatesService
    @Autowired DashboardProperties properties

    @Scheduled(fixedDelay = 50L)
    void run() {
        def whenDone = sequence([
                fetchRatesService.getRateStatus(properties.getLetterGrabberUrl()).thenApply({ status -> status.setComponent("letter-grabber-producer") }),
                fetchRatesService.getRateStatus(properties.getLetterSignatureUrl()).thenApply({ status -> status.setComponent("letter-signature-consumer") }),
                fetchRatesService.getRateStatus(properties.getGuardUrl()).thenApply({ status -> status.setComponent("guard-consumer") })
        ])

        def builder = new StringBuilder()
        whenDone.thenAccept { statuses ->
            ansi().restoreCursorPosition()
            (statuses.size() + 2).times {
                builder.append ansi().cursorUpLine().eraseLine()
            }

            builder.append("┏${('━' * PAD * 3)}┓\n")

            statuses.each { status ->
                builder.append '┃'
                builder.append formatComponent(status)
                builder.append "${formatRate(status)} ${formatBuffer(status)}".padLeft(PAD * 2 - 8, '.')
                builder.append '┃\n'
            }

            builder.append("┗${('━' * PAD * 3)}┛\n")

            print builder
        }
    }

    private String formatBuffer(RateStatus status) {
        def size = status.getBufferSize()
        def capacity = status.getBufferCapacity()


        def result = ansi()

        if (size <= capacity * 0.75) {
            result.fgBrightRed()
        } else {
            result.fgBrightGreen()
        }

        result.format('%6d/%-6d', size, capacity).reset().toString()
    }

    private String formatRate(RateStatus status) {
        ansi().fgBrightCyan().format('%6.2f', status.getLetterRps()).reset().toString()
    }

    private String formatComponent(RateStatus status) {
        ansi().fgBrightGreen().a(status.getComponent().padRight(PAD * 2, '.')).reset().toString()
    }

    static CompletableFuture<List<RateStatus>> sequence(List<CompletableFuture<RateStatus>> futures) {
        allOf(futures as CompletableFuture[]).thenApply({ ignored ->
            return futures.collect { it.join() }
        })
    }
}
