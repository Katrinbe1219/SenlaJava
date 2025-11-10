package task_3_4;

import task_3_4.model.Book;
import task_3_4.model.BookShop;
import task_3_4.model.Customer;
import task_3_4.model.Order;
import task_3_4.model.types.*;
import task_3_4.model.Warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test2 {
//    public static void main(String[] args) {
//        // Creating system----------------------------------------
//        Book book1 = new Book("Harry Potter 1", "J.K.Rowlling",
//                2024, BookStatus.IN_STOCK, 1500, BookTypes.FANTASY);
//        Book book2 = new Book("Peter The First", "A.Tolstoy",
//                2024, BookStatus.IN_STOCK, 2100, BookTypes.HISTORY);
//        Book book3 = new Book("The Great Expectations", "C.Dickens",
//                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
//        Book book4 = new Book("War and Piece", "L.Tolstoy",
//                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
//        Book book5 = new Book("Oblomov", "I.Goncharov",
//                2023, BookStatus.OUT_OF_STOCK, 1400, BookTypes.HISTORY);
//
//        ArrayList<Book> system_books = new ArrayList<>();
//        system_books.add(book1);
//        system_books.add(book2);
//        system_books.add(book4);
//        system_books.add(book5);
//        system_books.add(book3);
//
//        Warehouse warehouse = new Warehouse(system_books);
//        BookShop shop = new BookShop(warehouse);
//
//        //--------------------------------------------------
//
//        ArrayList<Book> books  = shop.getAllBookList();
//        // здесь пользователь просмотрел бы все книги и выбрал индексы тех, которые ему бы понравились
//
//        Order order1 = new Order();
//        Customer customer1 = new Customer("Ekaterina", "Frolova", "jkl@mail.ru");
//        order1.addBook(books.getFirst());
//        order1.addBook(books.getLast());
//        order1.setCustomer(customer1);
//        shop.createOrder(order1);
//
//        System.out.println("\nЗапрос у магазина предоставить детали заказа:");
//        System.out.println( shop.getOrderDetails(order1) + "\n");
//        ArrayList<String> deliveryBooks = new ArrayList<>();
//        deliveryBooks.add("The Great Expectations");
//        shop.receiveDelivery(deliveryBooks);
//
//        // в конструкторе для тестов добавлена по умолчанию дата последней покупки 09.01.2025
//        System.out.println("Получение книг, которые давно не были проданы");
//        List<Book> longLiedBooks = shop.getLongLiedBooks(LongLiedBookSorting.NONE);
//        for (Book book : longLiedBooks) {
//            System.out.println(book.getTitle());
//        }
//
//        System.out.println("\nПолучение описание книги");
//        System.out.println(shop.getBookDescription("Oblomov") + "\n");
//
//        // Создаются заказы с книгами, которых нет в наличии
//        Order order2 = new Order();
//        order2.addBook(books.get(2));
//        order2.setCustomer(customer1);
//        shop.createOrder(order2);
//
//        System.out.println();
//        System.out.println();
//
//        Order order3 = new Order();
//        order3.addBook(books.get(2));
//        order3.addBook(books.get(3));
//        order3.setCustomer(customer1);
//        shop.createOrder(order3);
//
//        // Получение списка запросов
//        System.out.println("\nПолучение списка запросов, сортируя по количеству");
//        List<List<Object>> requests = shop.getSortedRequests(RequestSorting.AMOUNT_UP);
//        for (List<Object> request : requests) {
//            System.out.println(request.get(0) +  ", " + request.get(1));
//        }
//
//
//        System.out.println();
//        System.out.println();
//
//        // Получить список книг, сортируя по алфавиту
//        System.out.println("Получение списка книг, отсортированных по алфавиту");
//        List<Book> booksSorted = shop.getSortedBooks(BookSorting.ALPHABETICAL_UP);
//        for (Book book: booksSorted){
//            System.out.println(book.getTitle());
//        }
//
//
//        System.out.println();
//        System.out.println();
//
//        // Получить список книг, сортируя по цене
//        System.out.println("Получение списка книг, отсортированных по алфавиту");
//        booksSorted = shop.getSortedBooks(BookSorting.PRICE_DOWN);
//        for (Book book: booksSorted){
//            System.out.println(book.getTitle() + " " + book.getPrice());
//        }
//
//        System.out.println();
//        System.out.println();
//        // Для проверки заработанных денег
//
//        deliveryBooks = new ArrayList<>();
//        deliveryBooks.add("War and Piece");
//        shop.receiveDelivery(deliveryBooks);
//
//        System.out.print("Сумма заработанных денег за неделю 1 вариант: ");
//        System.out.println(shop.getIncomeInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5)));
//        System.out.print("Выполненные заказы за неделю 1 вариант: ");
//        List<Order> doneRequests = shop.getDoneOrdersInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5), OrderSorting.PRICE_DOWN);
//        for (Order order : doneRequests) {
//            System.out.println(order);
//        }
//        System.out.print("Выполненные заказы за неделю 2 вариант количество:  ");
//        System.out.println(shop.getOrdersAmountInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5)) + "\n");
//
//        // проверка, что считается только то, что входит в диапазон
//        order2.setCompletionDate(LocalDate.of(2025,9,22));
//
//        System.out.print("\nСумма заработанных денег за неделю 2 вариант: ");
//        System.out.println(shop.getIncomeInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5)));
//        System.out.print("Выполненные заказы за неделю 2 вариант: ");
//        doneRequests = shop.getDoneOrdersInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5), OrderSorting.PRICE_DOWN);
//        for (Order order : doneRequests) {
//            System.out.println(order);
//
//        }
//
//        System.out.print("Выполненные заказы за неделю 2 вариант количество:  ");
//        System.out.println(shop.getOrdersAmountInDiapazon(LocalDate.of(2025,10,21), LocalDate.of(2025,11,5)) + "\n");
//
//        System.out.println();
//        System.out.println();
//
//        // orders - only NEW
////        List<Order> orders = shop.getSortedOrders(OrderSorting.NEW);
////        for (Order order : orders) {
////            System.out.println(order);
////        }
//
//
//        // orders - by price
////        System.out.println("Only with price orders");
////        orders = shop.getSortedOrders(OrderSorting.PRICE_DOWN);
////        for (Order order : orders) {
////            System.out.println(order);
//        //}
//    }
}
