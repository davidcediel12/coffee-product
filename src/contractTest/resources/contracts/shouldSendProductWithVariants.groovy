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
                category: [
                        id : 678,
                        name : "Machines"
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
                variants: [
                        [
                                name         : "Black Edition",
                                description  : "Premium coffee maker in black color",
                                stock        :  100,
                                basePrice    : [amount: 199.99, currency: "USD"],
                                isPrimary    : true,
                                sku          : "CM-BLK-001",
                                images: [
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
                tags: [
                        [
                            id : 101,
                            name: "electric",
                            tagType: [
                                    id: 11,
                                    name: "machine type",

                            ]
                        ]
                ]
        )
    }
}