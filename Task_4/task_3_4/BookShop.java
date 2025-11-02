package task_3_4;

import task_3_4.books.Book;
import task_3_4.order.Order;
import task_3_4.types.*;
import task_3_4.warehouse_work.Request;
import task_3_4.warehouse_work.Warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        for(Book book: order.getBooks()){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                checking = true;
                warehouse.addRequest(new Request(book, order));
            }
        }

        if (!checking){
            order.setStatus(OrderStatus.DONE);
            warehouse.setLastPurchase(order.getBooks());
        }
        System.out.println("Статус заказа " + order.getStatus());
        this.orders.add(order);
        System.out.println("заказ добавлен в историю ");
    }

    void removeOrder(Order order){
        System.out.println("Запрос на отмену заказа");
        for (Order o: this.orders){
            if (o.getStatus() != OrderStatus.DONE && o.equals(order)){
                o.setStatus(OrderStatus.CANCELLED);
                warehouse.cancellOrderRequests(order);
                System.out.println("Заказ Отменен");
                break;
            }
        }
    }

    ArrayList<Book> getAllBookList(){
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
                OrderStatus newStatus= order.checkUpdateByBook(bookTitle);

                if (newStatus == OrderStatus.DONE){
                    warehouse.setLastPurchase(order.getBooks());
                }
            }
        }
    }

    List<Order> getSortedOrders(OrderSorting sortingType){

        return switch (sortingType) {
            case DONE -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .toList();
            case CANCELLED -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.CANCELLED)
                    .toList();
            case NEW -> orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.NEW)
                    .toList();
            case PRICE_UP -> orders.stream()
                    .sorted(Comparator.comparing(Order::getTotalCost))
                    .toList();
            case PRICE_DOWN -> orders.stream()
                    .sorted(Comparator.comparing(Order::getTotalCost).reversed())
                    .toList();
            case DATE_UP -> orders.stream()
                    .filter(p -> p.getStatus()== OrderStatus.DONE)
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
            case DATE_DOWN -> orders.stream()
                    .filter(p -> p.getStatus()== OrderStatus.DONE)
                    .sorted(Comparator.comparing(Order::getCompletionDate).reversed())
                    .toList();
            default -> orders.stream().toList();
        };
    }

    List<Order> getDoneOrdersInDiapazon(LocalDate start, LocalDate end, OrderSorting sortingType){
        if (sortingType == OrderSorting.PRICE_UP){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getTotalCost))
                    .toList();
        } else if (sortingType == OrderSorting.PRICE_DOWN){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getTotalCost).reversed())
                    .toList();
        } else if (sortingType == OrderSorting.DATE_UP){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
        } else if (sortingType == OrderSorting.DATE_DOWN){
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .sorted(Comparator.comparing(Order::getCompletionDate))
                    .toList();
        }else{
            return orders.stream()
                    .filter(p -> p.getStatus() == OrderStatus.DONE)
                    .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                    .toList();
        }


    }

    List<Book> getSortedBooks(BookSorting sortingType){
        return warehouse.getSortedBooks(sortingType);
    }

    List<List<Object>> getSortedRequests(RequestSorting sortingType){
        return warehouse.getSortedRequests(sortingType);
    }

    String getBookDescription(String bookName){
        return warehouse.getBookDescription(bookName);
    }

    String getOrderDetails(Order order){
        return order.toString();
    }

    List<Book> getLongLiedBooks(LongLiedBookSorting sortingType){
        return warehouse.getLongLiedBooks(sortingType);
    }

    Integer getOrdersAmountInDiapazon(LocalDate start, LocalDate end){
        int amount = 0;

        for (Order order: orders){
            if (order.getStatus() == OrderStatus.DONE){
                if (order.getCompletionDate().isBefore(end) && order.getCompletionDate().isAfter(start)){
                    amount ++;
                }
            }
        }

        return amount;

    }

    Double getIncomeInDiapazon(LocalDate start, LocalDate end){
        double amount = 0;

        for (Order order: orders){
            if (order.getStatus() == OrderStatus.DONE){

                if (order.getCompletionDate().isBefore(end) && order.getCompletionDate().isAfter(start)){
                    amount += order.getTotalCost();
                }
            }
        }

        return amount;
    }




}
