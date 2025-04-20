package com.cordilleracoffee.product.infrastructure.messaging.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.infrastructure.exception.ProductSerializationException;
import com.cordilleracoffee.product.infrastructure.messaging.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaService implements MessageService {

    private static Logger log = LoggerFactory.getLogger(KafkaService.class.getName());

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public void sendNewProduct(Product product) {

        try {
            String message = objectMapper.writeValueAsString(product);
            kafkaTemplate.send("product", message);
            log.info("New product sent to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            throw new ProductSerializationException("Error serializing product to send to kafka", e);
        }
    }
}
