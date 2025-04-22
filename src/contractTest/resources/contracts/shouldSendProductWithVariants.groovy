package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("shouldSendProductCreatedEventWithVariant")
    label("product_created_event_variant")
    description("Sends a ProductCreated event to Kafka with consumer values")

    input {
        triggeredBy("triggerProductWithVariantsCreated()")
    }

    outputMessage {
        sentTo("product")
        headers {
            header("contentType", applicationJson())
        }
        body(
                id: anyPositiveInt(),
                sellerId: "seller-001",
                name: "Coffee Maker",
                description: "High-quality coffee maker",
                status: "AVAILABLE",
                categoryId: 678,
                images: [
                        [
                                id          : 1,
                                name        : "coffee-maker-main",
                                url         : "https://example.com/images/coffee-maker-main.jpg",
                                isPrimary   : true,
                                displayOrder: 1
                        ]
                ],
                variants: [
                        [
                                name         : "Black Edition",
                                description  : "Premium coffee maker in black color",
                                stock        : [amount: 100],
                                basePrice    : [amount: 199.99, currency: "USD"],
                                isPrimary    : true,
                                sku          : [sku: "CM-BLK-001"],
                                variantImages: [
                                        [
                                                id          : 2,
                                                name        : "coffee-maker-black",
                                                url         : "https://example.com/images/coffee-maker-black.jpg",
                                                isPrimary   : true,
                                                displayOrder: 1
                                        ]
                                ]
                        ]
                ],
                tagIds: [101, 205, 307]
        )
    }
}