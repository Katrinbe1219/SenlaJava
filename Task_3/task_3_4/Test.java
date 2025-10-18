package task_3_4;

import task_3_4.books.ClassicalBook;
import task_3_4.books.FantasyBook;
import task_3_4.books.HistoryBook;
import task_3_4.books.IBook;
import task_3_4.customer.Customer;
import task_3_4.order.Order;
import task_3_4.types.BookStatus;
import task_3_4.warehouse_work.Warehouse;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {

        // Creating system----------------------------------------
        IBook book1 = new FantasyBook("Harry Potter 1", "J.K.Rowlling",
                2024, BookStatus.InStock, 1500);
        IBook book2 = new HistoryBook("Peter The First", "A.Tolstoy",
                2024, BookStatus.InStock, 2100);
        IBook book3 = new ClassicalBook("The Great Expectations", "C.Dickens",
                2025, BookStatus.OutOfStock, 1500);
        IBook book4 = new ClassicalBook("War and Piece", "L.Tolstoy",
                2025, BookStatus.OutOfStock, 1500);

        ArrayList<IBook> system_books = new ArrayList<>();
        system_books.add(book1);
        system_books.add(book2);
        system_books.add(book4);
        system_books.add(book3);

        Warehouse warehouse = new Warehouse(system_books);
        BookShop shop = new BookShop(warehouse);

        //--------------------------------------------------

        ArrayList<IBook> books  = shop.getAllBookList();
        // здесь пользователь просмотрел бы все книги и выбрал индексы тех, которые ему бы понравились

        Order order1 = new Order();
        Customer customer1 = new Customer("Ekaterina", "Frolova", "jkl@mail.ru");
        order1.addBook(books.getFirst());
        order1.addBook(books.getLast());
        order1.setCustomer(customer1);
        order1.addBook(books.get(1));
        order1.delBook(books.get(1));
        shop.createOrder(order1);

        ArrayList<String> deliveryBooks = new ArrayList<>();
        deliveryBooks.add("The Great Expectations");
        shop.receiveDelivery(deliveryBooks);


        System.out.println();
        System.out.println();

        Order order2 = new Order();
        order2.setCustomer(customer1);
        order2.addBook(books.getFirst());
        order2.addBook(books.get(2));

        shop.createOrder(order2);
        shop.removeOrder(order2);





    }
}
