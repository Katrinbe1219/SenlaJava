package org.example.consumer_application;

import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class MessageReceiver {

    private static final Logger logger = LogManager.getLogger(MessageReceiver.class.getName());

    @KafkaListener(topics = "senla")
    @Transactional
    // в аргументе мог быть и просто String, но тогда не было бы информации о партиции, смещении
    public void receiverMessage(List<ConsumerRecord<String, String>> records) {

        System.out.println("Я в receiveMessage " + records.size());
        for (ConsumerRecord<String, String> record : records) {
            String key = record.key();
            String message = record.value();
            int partition = record.partition();
            long offset = record.offset();
            System.out.println("Я в receiveMessage " + message);
            String basicInfo = "partition=" + partition +
                    ", offset=" + offset + ", key=" + key + ", message=" + message;

            logger.info("Starting of preprocessing: " + basicInfo);
            prepocessInfo(record);
        }


        try{
            // заносим информацию о транзакции в бд
        }catch(Exception e){
            // заносим информацию об ошибке в бд
            throw new RuntimeException("Transfer failed", e);
            // сообщение будет перечитано, но в данный момент не проходит по каким-то условиям
        }

    }

    private void prepocessInfo(ConsumerRecord<String, String> record){

    }
}
