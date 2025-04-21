package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("shouldSendProductCreatedEventWithoutVariant")
    label("product_created_event_no_variant")
    description("Sends a ProductCreated event to Kafka")

    input {
        triggeredBy("triggerProductWithoutVariantsCreated()")
    }

    outputMessage {
        sentTo("product")
        headers {
            header("contentType", applicationJson())
        }
        body(
                id: anyNumber(),
                sellerId: anyNonBlankString(),
                name: anyNonBlankString(),
                description: anyNonBlankString(),
                sku: [
                        sku: anyNonBlankString()
                ],
                stock: [
                        amount: anyPositiveInt()
                ],
                status: anyOf("AVAILABLE", "HIDDEN", "OUT_OF_STOCK"),
                categoryId: anyNumber(),
                basePrice: [
                        amount : anyNumber(),
                        currency: anyAlphaUnicode()
                ],
                images: [
                        [
                                id          : anyNumber(),
                                name        : anyNonBlankString(),
                                url         : anyUrl(),
                                isPrimary   : anyBoolean(),
                                displayOrder: anyNumber()
                        ]
                ],
                variants: [],
                tagIds: [$(anyNumber())]
        )
    }
}