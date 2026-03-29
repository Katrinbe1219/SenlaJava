package org.example.application.hibernate;

import jakarta.persistence.*;

@Entity(name="account")
@Table(name = "user_account")
public class AccountEntity {

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

    public AccountEntity(){}

    public AccountEntity(  int balance){

        this.balance = balance;
    }
}
