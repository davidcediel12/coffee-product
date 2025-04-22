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
                sellerId: "seller-002",
                name: "Classic Wired Headphones",
                description: "High-quality wired headphones with crisp sound",
                sku: [
                        sku: "HP-WRD-001"
                ],
                stock: [
                        amount: 50
                ],
                status: "AVAILABLE",
                categoryId: 679,
                basePrice: [
                        amount : 99.99,
                        currency: "USD"
                ],
                images: [
                        [
                                id          : 3,
                                name        : "wired-headphones-main",
                                url         : "https://example.com/images/wired-headphones.jpg",
                                isPrimary   : true,
                                displayOrder: 1
                        ]
                ],
                variants: [],
                tagIds: [102, 206]
        )
    }
}