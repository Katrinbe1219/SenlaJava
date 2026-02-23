package org.example.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageSender {

    private KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(MessageSender.class.getName());
    private final String topic = "senla";
    // с помощью счетчика удобно убедиться в работе exactly one
    // kafkaTemplate асинхронный, поэтому обычный int не подойдет
    private final AtomicInteger counter = new AtomicInteger(0);

    public MessageSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 200) // fixedRate подойдет для нескольких потоков
    @Transactional
    public void sendMessage(){
        String message = "# " + counter.incrementAndGet() + " Message";

        kafkaTemplate.executeInTransaction(operations -> {
            try {
                SendResult<String,String> result = operations
                        .send(topic, message)
                        .get(5, TimeUnit.SECONDS); // для exactly once мы должны зафиксировать
                        // что сообщение точно доставлено, а не отправка произошла, но транзакция не закоммитилась или упала вообще
                        // если 5 секунд вышло, то бросается исключение -> транзакция не завершилась успешно

                // здесь уже сообщение должно было отправиться

                logger.info("Message  " + result.getRecordMetadata() + " was sent");
                return true; // успех - коммит
            }catch(TimeoutException | InterruptedException | ExecutionException e){
                logger.error("Error while sending message: " + message + " ; exception: " + e);
                throw new RuntimeException("Отправка не выполнилась ща 5 секунд: "+ e);
                // вызовет откат транзакции
            }
        });
    }

}
