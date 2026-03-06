package org.example.consumer_application;

import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.consumer_application.jdbc.Account;
import org.example.consumer_application.jdbc.HibernateImpl;
import org.example.consumer_application.jdbc.TransactionsTable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class MessageReceiver {

    private static final Logger logger = LogManager.getLogger(MessageReceiver.class.getName());

    private HibernateImpl repo;
    public MessageReceiver(HibernateImpl repo) {

        this.repo = repo;
    }


    @KafkaListener(topics = "senla")
    @Transactional
    // в аргументе мог быть и просто String, но тогда не было бы информации о партиции, смещении
    public void receiverMessage(List<ConsumerRecord<String, SendingInformation>> records) throws Exception{

        System.out.println("Я в receiveMessage " + records.size());
        System.out.println("Я в receiveMessage " + records.get(0).value().getNumOfMessage());
        for (ConsumerRecord<String, SendingInformation> record : records) {
            String key = record.key();
            SendingInformation message = record.value();
            int partition = record.partition();
            long offset = record.offset();

            String basicInfo = "partition=" + partition +
                    ", offset=" + offset + ", key=" + key + ", message=" + message;

            logger.info("Starting of preprocessing: " + basicInfo);
            try{
                prepocessInfo(message);
                logger.info("End of successfully processing: " + basicInfo);

            }catch(Exception e){
                logger.error("End of error processing: " + basicInfo);
                throw new Exception("Transfer failed: "+  e.getMessage());

                // сообщение будет перечитано, но в данный момент не проходит по каким-то условиям
            }
        }
    }



    private void prepocessInfo(SendingInformation record) throws Exception{

        Account firstAccount = repo.getAccount(record.getSender_id());
        Account secondAccount = repo.getAccount(record.getReceiver_id());

        if (firstAccount == null || secondAccount == null) {
            logger.debug("Таких аккаунтов не существует");
        }

        if (firstAccount.getBalance() - record.getBalance() < 0){
            repo.insertTransaction(new TransactionsTable(
                    firstAccount, secondAccount, record.getBalance(), 'N'
            ));
            logger.debug("У отправителя не хватает средств");
        }

        firstAccount.decreaseBalance(record.getBalance());
        secondAccount.increaseBalance(record.getBalance());

        repo.changeBalance(firstAccount);
        repo.changeBalance(secondAccount);
        repo.insertTransaction(new TransactionsTable(
                firstAccount, secondAccount, record.getBalance(), 'R'
        ));

    }
}
