package contracts

import org.springframework.cloud.contract.spec.Contract
import static org.springframework.cloud.contract.spec.internal.BodyMatchers.*



Contract.make {
    name("shouldSendProductCreatedEventWithVariant")
    label("product_created_event_variant")
    description("Sends a ProductCreated event to Kafka")

    input {
        triggeredBy("triggerProductWithVariantsCreated()")
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
                status: anyOf("AVAILABLE", "HIDDEN", "OUT_OF_STOCK"),
                categoryId: anyNumber(),
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
                                basePrice    : [amount: anyNumber(), currency: anyAlphaUnicode()],
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
                tagIds: [$(anyNumber())]
        )
    }
}