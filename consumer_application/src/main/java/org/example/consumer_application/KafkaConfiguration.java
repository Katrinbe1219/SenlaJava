package org.example.consumer_application;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ComponentScan(basePackages = "org.example")
public class KafkaConfiguration {



    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        Map<String, Object> props = new HashMap<>();

        // и получателю и отправителю нужно настраивать брокер

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  "kafka-senla-1:9092,kafka-senla-2:9093,kafka-senla-3:9094");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"senla");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");

        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10240);  // 10KB
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 5000); // 5 секунд
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1048576);

        return new DefaultKafkaConsumerFactory<>(props);


    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // batch mode!
        factory.setBatchListener(true);
        return factory;
    }

}

