package com.cordilleracoffee.product.messaging;


import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.infrastructure.messaging.impl.KafkaService;
import com.cordilleracoffee.product.infrastructure.messaging.mappers.ProductMessageMapper;
import com.cordilleracoffee.product.infrastructure.messaging.mappers.ProductMessageMapperImpl;
import com.cordilleracoffee.product.infrastructure.persistence.CategoryJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TagJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Tag;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TagType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {
                KafkaService.class, BaseTestClass.TestConfig.class,
                KafkaAutoConfiguration.class, ObjectMapper.class, ProductMessageMapper.class
        })
@Testcontainers
@AutoConfigureMessageVerifier
public abstract class BaseTestClass {


    @MockitoBean
    CategoryJpaRepository categoryJpaRepository;

    @MockitoBean
    TagJpaRepository tagJpaRepository;

    @Autowired
    private KafkaService kafkaService;

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:4.0.0"));


    Product.Builder productBuilder = new Product.Builder("Coffee Maker",
            "High-quality coffee maker",
            "seller-001",
            new Sku("CM-BLK-001"), 678L,
            Set.of(
                    new ProductImage(1L, "coffee-maker-main", 1, true, "https://example.com/images/coffee-maker-main.jpg")
            )
    );

    @BeforeEach
    void setup() throws InterruptedException {
        com.cordilleracoffee.product.infrastructure.persistence.entity.Category category = new com.cordilleracoffee.product.infrastructure.persistence.entity.Category();
        category.setId(678L);
        category.setName("Machines");

        when(categoryJpaRepository.findById(anyLong()))
                .thenReturn(Optional.of(category));

        Tag tag = new Tag();
        tag.setId(101L);
        tag.setName("electric");

        TagType tagType = new TagType();
        tagType.setId(11L);
        tagType.setName("machine type");

        tag.setTagType(tagType);

        when(tagJpaRepository.findAllById(anyIterable())).thenReturn(List.of(tag));
        Thread.sleep(2000); // give time to kafka to start properly
    }

    /**
     * Used by shouldSendProductWithoutVariants.groovy contract
     */
    public void triggerProductWithoutVariantsCreated() {

        var product = productBuilder
                .id(10L)
                .basePrice(new Money(BigDecimal.valueOf(99.99), "USD"))
                .stock(new Stock(50L))
                .status(ProductStatus.AVAILABLE)
                .variants(Collections.emptySet())
                .tagIds(Set.of(101L, 205L, 307L))
                .build();


        kafkaService.sendNewProduct(product);
    }

    /**
     * Used by shouldSendProductWithVariants.groovy
     */
    public void triggerProductWithVariantsCreated() {
        var variant = new Variant("Black Edition", "Premium coffee maker in black color",
                new Stock(100L), new Money(BigDecimal.valueOf(199.99), "USD"), true,
                new Sku("CM-BLK-001"), Set.of(new VariantImage(2L, "coffee-maker-black", 1, true,
                "https://example.com/images/coffee-maker-black.jpg")));


        Product product = productBuilder
                .id(10L)
                .status(ProductStatus.AVAILABLE)
                .variants(Set.of(variant))
                .tagIds(Set.of(101L, 205L, 307L))
                .build();

        kafkaService.sendNewProduct(product);
    }


    @Configuration
    @EnableKafka
    static class TestConfig {

        @Bean
        public ProductMessageMapper productMessageMapper() {
            return new ProductMessageMapperImpl(); // auto-generated by MapStruct
        }

        @Bean
        KafkaMessageVerifier kafkaTemplateMessageVerifier() {
            return new KafkaMessageVerifier();
        }

        @Bean
        @Primary
        JsonMessageConverter noopMessageConverter() {
            return new NoopJsonMessageConverter();
        }

    }

    static class KafkaMessageVerifier implements MessageVerifierReceiver<Message<?>> {

        private static final Log LOG = LogFactory.getLog(KafkaMessageVerifier.class);

        Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();


        @Override
        public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
            broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(destination);
            Message<?> message;
            try {
                message = messageQueue.poll(timeout, timeUnit);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (message != null) {
                LOG.info("Removed a message from a topic [" + destination + "]");
                LOG.info(message.getPayload().toString());
            }
            return message;
        }


        @KafkaListener(id = "productContractTestListener", topics = {"product"})
        public void listen(ConsumerRecord payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            LOG.info("Got a message from a topic [" + topic + "]");
            Map<String, Object> headers = new HashMap<>();
            new DefaultKafkaHeaderMapper().toHeaders(payload.headers(), headers);
            broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(topic);
            messageQueue.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(headers)));
        }

        @Override
        public Message receive(String destination, YamlContract contract) {
            return receive(destination, 30, TimeUnit.SECONDS, contract);
        }

    }
}

class NoopJsonMessageConverter extends JsonMessageConverter {

    NoopJsonMessageConverter() {
    }

    @Override
    protected Object convertPayload(Message<?> message) {
        return message.getPayload();
    }
}
