package contracts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    name("shouldSendProductCreatedEventWithVariant")
    label("product_created_event_variant")
    description("Sends a ProductCreated event to Kafka with matchers for producer and specific values for consumer")

    input {
        triggeredBy("triggerProductWithVariantsCreated()")
    }

    outputMessage {
        sentTo("product")
        headers {
            header("contentType", applicationJson())
        }
        body(
                id: $(consumer(12345), producer(anyNumber())),
                sellerId: $(consumer("seller-001"), producer(anyNonBlankString())),
                name: $(consumer("Premium Wireless Headphones"), producer(anyNonBlankString())),
                description: $(consumer("High-quality wireless headphones with noise cancellation"), producer(anyNonBlankString())),
                status: $(consumer("AVAILABLE"), producer(anyOf("AVAILABLE", "HIDDEN", "OUT_OF_STOCK"))),
                categoryId: $(consumer(678), producer(anyNumber())),
                images: [
                        [
                                id          : $(consumer(1), producer(anyNumber())),
                                name        : $(consumer("headphones-main"), producer(anyNonBlankString())),
                                url         : $(consumer("https://example.com/images/headphones-main.jpg"), producer(anyUrl())),
                                isPrimary   : $(consumer(true), producer(anyBoolean())),
                                displayOrder: $(consumer(1), producer(anyNumber()))
                        ]
                ],
                variants: [
                        [
                                name         : $(consumer("Black Edition"), producer(anyNonBlankString())),
                                description  : $(consumer("Premium headphones in black color"), producer(anyNonBlankString())),
                                stock        : [amount: $(consumer(100), producer(anyPositiveInt()))],
                                basePrice    : [amount: $(consumer(199.99), producer(anyNumber())),
                                                currency: $(consumer("USD"), producer(anyAlphaUnicode()))],
                                isPrimary    : $(consumer(true), producer(anyBoolean())),
                                sku          : [sku: $(consumer("HP-BLK-001"), producer(anyNonBlankString()))],
                                variantImages: [
                                        [
                                                id          : $(consumer(2), producer(anyNumber())),
                                                name        : $(consumer("headphones-black"), producer(anyNonBlankString())),
                                                url         : $(consumer("https://example.com/images/headphones-black.jpg"), producer(anyUrl())),
                                                isPrimary   : $(consumer(true), producer(anyBoolean())),
                                                displayOrder: $(consumer(1), producer(anyNumber()))
                                        ]
                                ]
                        ]
                ],
                tagIds: $(consumer([101, 205, 307]), producer([$(anyNumber())]))
        )
    }
}