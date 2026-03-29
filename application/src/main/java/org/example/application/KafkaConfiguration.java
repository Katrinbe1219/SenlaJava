package org.example.application;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
@EnableScheduling
@ComponentScan(basePackages = "org.example.application")
public class KafkaConfiguration {

    @Bean
    public ProducerFactory<String, SendingInformation> producerFactory(){
        //Integer - ип ключа с которым будут отправляться сообщения
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka-senla-1:9092,kafka-senla-2:9093,kafka-senla-3:9094"
        );

        configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "org.apache.kafka.clients.producer.RoundRobinPartitioner");
        // идемпотентной считается операция, которая при многократном выполнении
        // дает тот же результат, что и при однократном
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
        // все реплики должны подтвердить запись
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // для exactly once нужно фиксировать смещение самому
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tr-1" + UUID.randomUUID());

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SendingInformation> kafkaTemplate(){
        // главный компонент для отправки сообщений
        return new KafkaTemplate<>(producerFactory());
    }

    // менеджер транзакций для использования @Transactional
    @Bean
    public KafkaTransactionManager<String, SendingInformation> transactionManager(
            ProducerFactory<String, SendingInformation> producerFactory
    ){
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka-senla-1:9092,kafka-senla-2:9093,kafka-senla-3:9094");
        return new KafkaAdmin(props);
    }

    @Bean
    public NewTopic senlaTopic(){
        return TopicBuilder.name("senla")
                .partitions(3)
                .replicas(3)
                .build();
    }






}
