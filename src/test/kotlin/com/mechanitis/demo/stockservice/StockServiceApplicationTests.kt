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
    fun contextLoads() {
    }

    @Test
    fun testRequestGetsResponse() {
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

    //        StepVerifier
//            .withVirtualTime {
//                requester!!
//                    .route("stockPrices")
//                    .data("a")
//                    .retrieveFlux(StockPrice::class.java).take(1)
//            }
//            .expectSubscription()
//            .expectNextMatches {
//                it.symbol == "a"
//            }
//            .verifyComplete()

    }

    @Test
    internal fun testStepVerifierWithVirtualTime() {
        StepVerifier
            .withVirtualTime {
                Flux.interval(
                    ofSeconds(1)
                ).take(2)
            }
            .expectSubscription()
            .expectNoEvent(ofSeconds(1))
            .expectNext(0L)
            .thenAwait(ofSeconds(1))
            .expectNext(1L)
            .verifyComplete()
    }
}
