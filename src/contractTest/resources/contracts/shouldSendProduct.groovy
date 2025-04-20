

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    name("shouldSendProductCreatedEvent")
    label("product_created_event")
    description("Sends a ProductCreated event to Kafka")

    input {
        triggeredBy("triggerProductCreated()")
    }

    outputMessage {
        sentTo("product")
        headers {
            header("contentType", applicationJsonUtf8())
        }
        body(
                productId: anyNumber(),
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
                        amount : anyDouble(),
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
                variants: [
                        [
                                name         : anyNonBlankString(),
                                description  : anyNonBlankString(),
                                stock        : [amount: anyPositiveInt()],
                                basePrice    : [amount: anyDouble(), currency: anyAlphaUnicode()],
                                isPrimary    : anyBoolean(),
                                sku          : [sku: anyNonBlankString()],
                                variantImages: [
                                        [
                                                id          : anyNumber(),
                                                name        : anyNonBlankString(),
                                                url         : anyUrl(),
                                                isPrimary   : anyBoolean(),
                                                displayOrder: anyNumber()
                                        ]
                                ]
                        ]
                ],
                tagIds: [
                        [
                                id : anyNumber()
                        ]
                ]
        )
    }
}