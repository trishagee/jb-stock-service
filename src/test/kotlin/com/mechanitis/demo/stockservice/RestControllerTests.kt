package com.mechanitis.demo.stockservice

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Flux
import reactor.kotlin.test.test
import java.time.LocalDateTime.now

@WebFluxTest(controllers = [RestController::class])
class RestControllerTests(@Autowired val client: WebTestClient) {
    @MockBean
    private lateinit var priceService: PriceService

    @Test
    fun prices() {
        given(priceService.generatePrices("a")).willReturn(Flux.just(StockPrice("a",
                0.0, now())))

        val stockPrices = client.get().uri("/stocks/a").exchange()
                .expectStatus().isOk
                .returnResult<StockPrice>().responseBody

        stockPrices.test()
                .expectNextMatches { it.symbol == "a" &&
                        it.price == 0.0 }
                .verifyComplete()
    }
}
