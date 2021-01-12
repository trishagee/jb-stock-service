package com.mechanitis.demo.stockservice

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration.ofSeconds
import org.xmlunit.builder.Transform.source
import java.util.function.Predicate


@SpringBootTest
@TestInstance(PER_CLASS)
class StockServiceApplicationTests {
    var requester: RSocketRequester? = null

    @BeforeAll
    fun setupOnce(
        @Autowired builder: RSocketRequester.Builder,
        @Value("\${spring.rsocket.server.port}") port: Int
    ) {
        requester = builder.tcp("localhost", port)
    }

    @Test
    fun shouldBeAbleToConnectToTheTopicAndReceivePricesForASymbol() {
        StepVerifier
            .create(requester!!
                .route("stockPrices")
                .data("SYMB")
                .retrieveFlux(StockPrice::class.java).take(1))
            .expectNextMatches {
                it.symbol == "SYMB" && it.price != 0.0
            }
            .expectComplete()
            .verify()
    }
}
