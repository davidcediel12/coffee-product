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
                id: 12345,
                sellerId: "seller-001",
                name: "Premium Wireless Headphones",
                description: "High-quality wireless headphones with noise cancellation",
                status: "AVAILABLE",
                categoryId: 678,
                images: [
                        [
                                id          : 1,
                                name        : "headphones-main",
                                url         : "https://example.com/images/headphones-main.jpg",
                                isPrimary   : true,
                                displayOrder: 1
                        ]
                ],
                variants: [
                        [
                                name         : "Black Edition",
                                description  : "Premium headphones in black color",
                                stock        : [amount: 100],
                                basePrice    : [amount: 199.99, currency: "USD"],
                                isPrimary    : true,
                                sku          : [sku: "HP-BLK-001"],
                                variantImages: [
                                        [
                                                id          : 2,
                                                name        : "headphones-black",
                                                url         : "https://example.com/images/headphones-black.jpg",
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