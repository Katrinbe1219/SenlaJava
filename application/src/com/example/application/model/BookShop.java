package com.example.application.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookShop implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    double totalIncome;
    ArrayList<Order> orders;
    int countAllOrder;


    public BookShop() {
        this.countAllOrder = 0;
        this.totalIncome = 0;
        this.orders = new ArrayList<>();
    }

    public int getCountAllOrder() {
        return this.countAllOrder;
    }

    public void incrementAllOrder() {
        this.countAllOrder++;
    }

    public void checkMaxCountOrder(int num){
        countAllOrder = Math.max(num, countAllOrder);
    }

    public double getTotalIncome() {
        return this.totalIncome;
    }

    void addTotalIncome(double add) {
        this.totalIncome += add;
    }

    void subTotalIncome(double sub) {
        this.totalIncome -= sub;
    }

    // added - keep
    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

}
