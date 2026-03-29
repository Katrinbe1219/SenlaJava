package org.example.consumer_application.jdbc;

import jakarta.persistence.*;
import org.springframework.data.relational.core.mapping.Table;


@Entity(name = "user_account")
@Table(name = "user_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "balance")
    private int balance;

    public Integer getId(){
        return id;
    }

    public int getBalance(){
        return balance;
    }

    public Account(){}

    public Account( Integer id, int balance){
        this.id = id;
        this.balance = balance;
    }

    public void decreaseBalance(int sum){
        balance -= sum;
    }

    public void increaseBalance(int sum){
        balance += sum;
    }
}
