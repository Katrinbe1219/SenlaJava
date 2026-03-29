package org.example.consumer_application.jdbc;

import jakarta.persistence.*;
import org.springframework.data.relational.core.mapping.Table;


@Entity(name = "user_transitions")
@Table(name ="user_transitions")
public class TransactionsTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    @Column(name= "amount")
    private int amount;

    @Column(name="status")
    private char status;

    public TransactionsTable() {}

    public TransactionsTable(Account sender, Account receiver, int amount, char status) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.status = status;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Account getSender() {
        return sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public int getAmount() {
        return amount;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public char getStatus() {
        return status;
    }

}
