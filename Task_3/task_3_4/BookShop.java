package task_3_4;

import task_3_4.books.IBook;
import task_3_4.order.Order;
import task_3_4.types.BookStatus;
import task_3_4.types.OrderStatus;
import task_3_4.warehouse_work.Request;
import task_3_4.warehouse_work.Warehouse;

import java.util.ArrayList;

public class BookShop {
    double totalIncome;
    Warehouse warehouse;
    ArrayList<Order> orders;


    BookShop(Warehouse warehouse){
        this.warehouse = warehouse;
        this.totalIncome  = 0;
        this.orders = new ArrayList<>();
    }

    double getTotalIncome(){
        return this.totalIncome;
    }
    void addTotalIncome(double add){
        this.totalIncome += add;
    }

    void subTotalIncome(double sub){
        this.totalIncome -= sub;
    }

    void createOrder(Order order){
        System.out.println("Обработка заказа в BookShop");
        boolean checking = false;

        for(IBook book: order.getBooks()){
            if (book.getStatus() == BookStatus.OutOfStock){
                checking = true;
                warehouse.addRequest(new Request(book, order));
            }
        }

        if (!checking){ order.setStatus(OrderStatus.DONE); }
        System.out.println("Статус заказа " + order.getStatus());
        this.orders.add(order);
        System.out.println("заказ добавлен в историю ");
    }

    void removeOrder(Order order){
        System.out.println("Запрос на отмену заказа");
        for (Order o: this.orders){
            if (o.equals(order)){
                o.setStatus(OrderStatus.CANCELLED);
                warehouse.cancellOrderRequests(order);
                System.out.println("Заказ Отменен");
                break;
            }
        }
    }

    ArrayList<IBook> getAllBookList(){
        System.out.println("Получение списка всех книг");
        return warehouse.getBooks();
    }

    void receiveDelivery(ArrayList<String> booksTitles){
        for (String bookTitle: booksTitles){
            System.out.println("Была завезена книга "+ bookTitle);
            warehouse.receiveBook(bookTitle);
            updateOrdersStatus(bookTitle);
        }
    }

    void updateOrdersStatus(String bookTitle){
        for (Order order: orders){
            if (order.getStatus() == OrderStatus.NEW){
                order.checkUpdateByBook(bookTitle);
            }
        }
    }




}
