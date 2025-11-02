package task_3_4;


import task_3_4.books.Book;
import task_3_4.customer.Customer;
import task_3_4.order.Order;
import task_3_4.types.BookStatus;
import task_3_4.types.BookTypes;
import task_3_4.warehouse_work.Warehouse;


import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {

        // Creating system----------------------------------------
        Book book1 = new Book("Harry Potter 1", "J.K.Rowlling",
                2024, BookStatus.IN_STOCK, 1500, BookTypes.FANTASY);
        Book book2 = new Book("Peter The First", "A.Tolstoy",
                2024, BookStatus.IN_STOCK, 2100, BookTypes.HISTORY);
        Book book3 = new Book("The Great Expectations", "C.Dickens",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);
        Book book4 = new Book("War and Piece", "L.Tolstoy",
                2025, BookStatus.OUT_OF_STOCK, 1500, BookTypes.CLASSICAL);

        ArrayList<Book> system_books = new ArrayList<>();
        system_books.add(book1);
        system_books.add(book2);
        system_books.add(book4);
        system_books.add(book3);

        Warehouse warehouse = new Warehouse(system_books);
        BookShop shop = new BookShop(warehouse);

        //--------------------------------------------------

        ArrayList<Book> books  = shop.getAllBookList();
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
