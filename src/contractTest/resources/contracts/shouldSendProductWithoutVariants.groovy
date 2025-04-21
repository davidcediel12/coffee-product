package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("shouldSendProductCreatedEventWithoutVariant")
    label("product_created_event_no_variant")
    description("Sends a ProductCreated event to Kafka with matchers for producer and specific values for consumer")

    input {
        triggeredBy("triggerProductWithoutVariantsCreated()")
    }

    outputMessage {
        sentTo("product")
        headers {
            header("contentType", applicationJson())
        }
        body(
                id: $(consumer(12346), producer(anyNumber())),
                sellerId: $(consumer("seller-002"), producer(anyNonBlankString())),
                name: $(consumer("Classic Wired Headphones"), producer(anyNonBlankString())),
                description: $(consumer("High-quality wired headphones with crisp sound"), producer(anyNonBlankString())),
                sku: [
                        sku: $(consumer("HP-WRD-001"), producer(anyNonBlankString()))
                ],
                stock: [
                        amount: $(consumer(50), producer(anyPositiveInt()))
                ],
                status: $(consumer("AVAILABLE"), producer(anyOf("AVAILABLE", "HIDDEN", "OUT_OF_STOCK"))),
                categoryId: $(consumer(679), producer(anyNumber())),
                basePrice: [
                        amount : $(consumer(99.99), producer(anyNumber())),
                        currency: $(consumer("USD"), producer(anyAlphaUnicode()))
                ],
                images: [
                        [
                                id          : $(consumer(3), producer(anyNumber())),
                                name        : $(consumer("wired-headphones-main"), producer(anyNonBlankString())),
                                url         : $(consumer("https://example.com/images/wired-headphones.jpg"), producer(anyUrl())),
                                isPrimary   : $(consumer(true), producer(anyBoolean())),
                                displayOrder: $(consumer(1), producer(anyNumber()))
                        ]
                ],
                variants: [],
                tagIds: $(consumer([102, 206]), producer([anyNumber()]))
        )
    }
}