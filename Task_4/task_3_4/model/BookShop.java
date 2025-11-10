package task_3_4.model;

import java.util.ArrayList;
import java.util.List;

public class BookShop {
    double totalIncome;
    ArrayList<Order> orders;


    public BookShop() {

        this.totalIncome = 0;
        this.orders = new ArrayList<>();
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
