package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("shouldSendProductCreatedEventWithoutVariant")
    label("product_created_event_no_variant")
    description("Sends a ProductCreated event to Kafka with consumer values")

    input {
        triggeredBy("triggerProductWithoutVariantsCreated()")
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
                sku: [
                        sku: "CM-BLK-001"
                ],
                stock: [
                        amount: 50
                ],
                status: "AVAILABLE",
                categoryId: 678,
                basePrice: [
                        amount : 99.99,
                        currency: "USD"
                ],
                images: [
                        [
                                id          : 1,
                                name        : "coffee-maker-main",
                                url         : "https://example.com/images/coffee-maker-main.jpg",
                                isPrimary   : true,
                                displayOrder: 1
                        ]
                ],
                variants: [],
                tagIds: [101, 205, 307]
        )
    }
}