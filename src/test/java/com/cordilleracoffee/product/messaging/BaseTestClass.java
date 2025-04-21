package com.cordilleracoffee.product.messaging;


import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.infrastructure.messaging.impl.KafkaService;
import com.cordilleracoffee.product.infrastructure.persistence.*;
import com.cordilleracoffee.product.utils.TestDataFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SpringBootTest(properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@AutoConfigureMessageVerifier
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public abstract class BaseTestClass {


    @MockitoBean
    ProductJpaRepository productJpaRepository;

    @MockitoBean
    TagJpaRepository tagJpaRepository;

    @MockitoBean
    VariantImageJpaRepository variantImageJpaRepository;

    @MockitoBean
    VariantJpaRepository variantJpaRepository;

    @MockitoBean
    CategoryJpaRepository categoryJpaRepository;

    @MockitoBean
    ProductImageJpaRepository productImageJpaRepository;

    @Autowired
    private KafkaService kafkaService;

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:4.0.0"));

    public void triggerProductWithoutVariantsCreated() {
        Product product = TestDataFactory.validProduct();

        product.setId(2L);
        kafkaService.sendNewProduct(product);
    }

    public void triggerProductWithVariantsCreated() {
        Product product = TestDataFactory.validProduct();

        product.setId(2L);
        product.setVariants(Set.of(TestDataFactory.validProductVariant()));

        kafkaService.sendNewProduct(product);
    }


    @Configuration
    @EnableKafka
    static class TestConfig {

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
            return receive(destination, 15, TimeUnit.SECONDS, contract);
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
