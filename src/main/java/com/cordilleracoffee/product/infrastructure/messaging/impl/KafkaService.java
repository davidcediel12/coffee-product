package com.cordilleracoffee.product.infrastructure.messaging.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.infrastructure.exception.ProductSerializationException;
import com.cordilleracoffee.product.infrastructure.messaging.MessageService;
import com.cordilleracoffee.product.infrastructure.messaging.dto.ProductMessage;
import com.cordilleracoffee.product.infrastructure.messaging.mappers.ProductMessageMapper;
import com.cordilleracoffee.product.infrastructure.persistence.CategoryJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TagJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Category;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class KafkaService implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class.getName());

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final ProductMessageMapper productMessageMapper;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                        CategoryJpaRepository categoryJpaRepository, TagJpaRepository tagJpaRepository, ProductMessageMapper productMessageMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.categoryJpaRepository = categoryJpaRepository;
        this.tagJpaRepository = tagJpaRepository;
        this.productMessageMapper = productMessageMapper;
    }


    @Override
    public void sendNewProduct(Product product) {

        Category category = categoryJpaRepository.findById(product.getCategoryId())
                .orElseThrow();

        List<Tag> tags = tagJpaRepository.findAllById(product.getTagIds());

        ProductMessage productMessage = productMessageMapper.toProductMessage(product);
        productMessage.setTags(productMessageMapper.toTagMessages(tags));
        productMessage.setCategory(productMessageMapper.toCategoryMessage(category));

        try {
            String productString = objectMapper.writeValueAsString(productMessage);

            String topic = "product";
            MessageHeaders headers = new MessageHeaders(Map.of(
                    KafkaHeaders.TOPIC, topic,
                    MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            GenericMessage<String> message = new GenericMessage<>(productString, headers);
            kafkaTemplate.send(message);
            log.info("New product sent to Kafka topic {}: {}", topic, productString);
        } catch (JsonProcessingException e) {
            throw new ProductSerializationException("Error serializing product to send to kafka", e);
        }
    }
}
