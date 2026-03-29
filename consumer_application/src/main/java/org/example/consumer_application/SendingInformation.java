package org.example.consumer_application;

public class SendingInformation {
    private int receiver_id;
    private int sender_id;
    private  int balance;
    private int  numOfMessage;

    public SendingInformation(){}
    public SendingInformation(int receiver_id, int sender_id, int balance, int numOfMessage) {
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.balance = balance;
        this.numOfMessage = numOfMessage;
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

    public void setNumOfMessage(int numOfMessage) {
        this.numOfMessage = numOfMessage;
    }

    public int getNumOfMessage() {
        return numOfMessage;
    }


}
