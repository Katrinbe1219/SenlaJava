package org.example.consumer_application;

public class SendingInformation {
    private int receiver_id;
    private int sender_id;
    private  int balance;

    public SendingInformation(){}

    public SendingInformation(int receiver_id, int sender_id, int balance) {
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.balance = balance;
    }

    public int getReceiverId() {
        return receiver_id;
    }

    public int getSenderId(){
        return sender_id;
    }

    public int getBalance() {
        return balance;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }




}
