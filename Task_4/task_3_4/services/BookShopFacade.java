package task_3_4.services;

import task_3_4.model.Book;
import task_3_4.model.Order;
import task_3_4.model.Request;
import task_3_4.model.types.BookStatus;
import task_3_4.model.types.OrderSorting;
import task_3_4.model.types.OrderStatus;
import task_3_4.repositories.OrderRepository;
import task_3_4.repositories.RequestRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookShopFacade {
    private OrderRepository orderRepository;
    private RequestRepository requestRepository;

    public BookShopFacade(OrderRepository orderRepository,  RequestRepository requestRepository) {
        this.orderRepository = orderRepository;
        this.requestRepository = requestRepository;
    }

    public boolean createOrder(Order order){
        System.out.println("Обработка заказа в BookShop");
        boolean checking = false;

        for(Book book: order.getBooks()){
            if (book.getStatus() == BookStatus.OUT_OF_STOCK){
                checking = true;
                requestRepository.add(new Request(book, order));
            }
        }

        if (!checking){
            order.setStatus(OrderStatus.DONE);
            order.setCompletionDate(LocalDate.now());

        }
        orderRepository.addOrder(order);
        if (order.getStatus() == OrderStatus.DONE){ return true;}
        else {return false;}

    }


    public boolean removeOrder(Order order){
        System.out.println("Запрос на отмену заказа");
        List<Order> orders = orderRepository.getOrders();
        for (Order o: orders){
            if (o.getStatus() != OrderStatus.DONE && o.equals(order)){
                o.setStatus(OrderStatus.CANCELLED);
                return true;


            }
        }
        return false;
    }

    public String getOrderDetails(Order order){
        return order.toString();
    }

    public List<Order> getSortedOrders(OrderSorting sortingType){
        List<Order> orders = orderRepository.getOrders();
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

    public List<Order> getDoneOrdersInDiapazon(LocalDate start, LocalDate end, OrderSorting sortingType){
        List<Order> orders = orderRepository.getOrders();
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

    public Integer getOrdersAmountInDiapazon(LocalDate start, LocalDate end){
        List<Order> orders = orderRepository.getOrders();
        return orders.stream()
                .filter(p -> p.getStatus() == OrderStatus.DONE)
                .filter(p -> p.getCompletionDate().isAfter(start) && p.getCompletionDate().isBefore(end))
                .toList().size();


    }

    public Double getIncomeInDiapazon(LocalDate start, LocalDate end){
        double amount = 0;
        List<Order> orders = orderRepository.getOrders();
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
