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
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import java.time.Duration


@SpringBootTest
@TestInstance(PER_CLASS)
class StockServiceApplicationTests {
    var requester: RSocketRequester? = null

    @BeforeAll
    fun setupOnce(@Autowired builder: RSocketRequester.Builder,
                  @Value("\${spring.rsocket.server.port}") port: Int) {
        requester = builder
                .connectTcp("localhost", port)
                .block()
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun testRequestGetsResponse() {
        val result: Flux<StockPrice> = requester!!
                .route("stockPrices")
                .data("a")
                .retrieveFlux(StockPrice::class.java)

        StepVerifier
                .withVirtualTime { result.take(1) }
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNextMatches {
                    it.symbol == "a"
                }
                .verifyComplete()
    }
}
