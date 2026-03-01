package org.example.application;

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

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public int getBalance() {
        return balance;
    }


}
