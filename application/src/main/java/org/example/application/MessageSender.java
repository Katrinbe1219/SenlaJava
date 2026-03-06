package org.example.application;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.hibernate.AccountEntity;
import org.example.application.hibernate.HIbernateImpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageSender {

    private KafkaTemplate<String, SendingInformation> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(MessageSender.class.getName());
    private final String topic = "senla";
    // с помощью счетчика удобно убедиться в работе exactly one
    // kafkaTemplate асинхронный, поэтому обычный int не подойдет
    private final AtomicInteger counter = new AtomicInteger(0);
    private Map<Integer, AccountEntity> accounts = new HashMap<>();


    private HIbernateImpl repo;

    public MessageSender(KafkaTemplate<String, SendingInformation> kafkaTemplate, HIbernateImpl repo) {
        this.kafkaTemplate = kafkaTemplate;
        this.repo = repo;
    }

    @Scheduled(fixedDelay = 200) // fixedRate подойдет для нескольких потоков
    @Transactional
    public void sendMessage(){
        //String message = "# " + counter.incrementAndGet() + " Message";

        kafkaTemplate.executeInTransaction(operations -> {
            try {
                SendResult<String,SendingInformation> result = operations
                        .send(topic, generateMessage())
                        .get(5, TimeUnit.SECONDS); // для exactly once мы должны зафиксировать
                        // что сообщение точно доставлено, а не отправка произошла, но транзакция не закоммитилась или упала вообще
                        // если 5 секунд вышло, то бросается исключение -> транзакция не завершилась успешно

                // здесь уже сообщение должно было отправиться

                logger.info("Message  " + result.getRecordMetadata() + " was sent");
                return true; // успех - коммит
            }catch(TimeoutException | InterruptedException | ExecutionException e){
                logger.error("Error while sending message exception: " + e);
                throw new RuntimeException("Отправка не выполнилась за 5 секунд: "+ e);
                // вызовет откат транзакции
            }
        });
    }


    @PostConstruct
    public void checkDatabase(){
        try {

            List<AccountEntity> list = repo.findAll();

            if (list.isEmpty()){
                int balance;
                Integer i=0;
                for (i=0; i<1000; i++){
                    balance = 1000 + 90 + i + i%34;
                    list.add(new AccountEntity( balance));
                }

                repo.insertAccounts(list);
            }

            for (AccountEntity accountEntity : list){
                this.accounts.put(accountEntity.getId(), accountEntity);
            }
            System.out.println("added");
        }catch (Exception e){
            logger.error("Error while checking database: " + e);
        }

    }

    @Transactional
    protected SendingInformation generateMessage(){
        int secondIndex;

        int firstIndex = ThreadLocalRandom.current().nextInt(0,accounts.size());
        do {
            secondIndex = ThreadLocalRandom.current().nextInt(0,accounts.size());
        }while (firstIndex == secondIndex);

        int transfer = ThreadLocalRandom.current().nextInt(0,10000);

        return new SendingInformation(firstIndex,secondIndex,transfer,counter.incrementAndGet());
    }

}
