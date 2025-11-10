package task_3_4.controllers;

import task_3_4.model.Book;
import task_3_4.model.Customer;
import task_3_4.model.Order;
import task_3_4.model.Warehouse;
import task_3_4.repositories.BookRepository;
import task_3_4.repositories.OrderRepository;
import task_3_4.repositories.RequestRepository;
import task_3_4.services.BookService;
import task_3_4.services.BookShopFacade;
import task_3_4.views.ConsoleUIFactory;
import task_3_4.views.UIComponent;
import task_3_4.views.UIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookStoreController {
    UIComponent menuComponent;
    UIComponent bookComponent;
    UIComponent orderComponent;
    UIComponent requestComponent;
    UIFactory consoleFactory;

    BookController bookController;
    OrderController orderController;
    RequestController requestController;


    public BookStoreController() {
        consoleFactory = new ConsoleUIFactory();
        menuComponent = consoleFactory.createMainMenu();
        bookComponent = consoleFactory.createBookMenu();
        orderComponent = consoleFactory.createOrderMenu();
        requestComponent = consoleFactory.createRequestMenu();

        Warehouse warehouse = Warehouse.getInstance();
        BookRepository br = new BookRepository(warehouse);
        OrderRepository or = new OrderRepository();
        RequestRepository rr = new RequestRepository(warehouse);

        BookService bs = new BookService(br,rr);
        BookShopFacade bsf = new BookShopFacade(or, rr);

        bookController = new BookController(bs);
        orderController = new OrderController(bsf);
        requestController = new RequestController(bs);

    }

    public void run(){
        Scanner input_  = new Scanner(System.in);
        String choice;
        while(true){
            menuComponent.display(null);
            choice = input_.nextLine();

            switch (choice){
                case "1": {
                    handleBookSection();
                    break;
                }
                case "2" :{
                    handleOrderSection();
                    break;
                }
                case "3": {
                    handleRequestSection();
                    break;
                }
                case "4": {
                    return;
                }
                default:{break;}
            }
        }
    }

    void handleBookSection(){

        List<Book> books;
        String choice;
        while(true){
            bookComponent.display(null);
            choice = bookComponent.input();
            switch(choice){
                case "1": {
                    books = bookController.displayAllBooks();
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    break;
                }
                case "2":{
                    bookComponent.display(bookController.getForDisplayType("Lbook"));
                    choice = bookComponent.input();
                    books = bookController.displayLongLiedBooks(choice);
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    break;
                }
                case "3":{
                    System.out.println("Введите название книги");
                    choice = bookComponent.input();
                    String description  = bookController.displayBookDescription(choice);
                    orderComponent.display(description);
                    break;
                }
                case"4":{
                    bookComponent.display(bookController.getForDisplayType("book"));
                    choice = bookComponent.input();
                    books = bookController.displaySortedBooks(choice);
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    break;
                }
                case "5" :{
                    System.out.println("Введите название книги");
                    choice = bookComponent.input();
                    orderComponent.display(bookController.checkBook(choice));
                    break;
                }
                case "6" :{
                    return;
                }
                default: {
                    break;
                }
            }
        }


    }

    void handleOrderSection(){
        // удалить можно будет тот заказ, который был создан недавно, в том же нахождении этой секции
        Order order = null;
        List<Order> orders;

        String choice;
        while(true){
            orderComponent.display(null);
            choice = orderComponent.input();
            switch(choice){
                case "1": {
                    order = createOrder();
                    Boolean done = orderController.createOrder(order);
                    if (done) {
                        bookController.setLastPurchase(order.getBooks());

                    }
                    orderComponent.display("Ваш заказ добавлен в историю со статусом " + order.getStatus());
                    break;
                }
                case "2":{
                        if (order == null){
                           orderComponent.display("Создайте, чтобы отменить");
                           break;
                        }
                        Boolean cancelled = orderController.deleteOrder(order);
                        if (cancelled) {
                            requestController.deleteRequestByOrder(order);
                            order = null;
                        }


                        break;
                }
                case "3":{
                    if (order == null){
                        orderComponent.display("Создайте, чтобы получить детали");
                        break;
                    }
                    String details = orderController.getOrderDetails(order);
                    orderComponent.display(details);
                    break;
                }
                case"4":{
                    orderComponent.display(orderController.getOrderTypes());
                    choice = orderComponent.input();
                    orders = orderController.getAllOrders(choice);
                    for (Order o: orders){
                        orderComponent.display(o.toString());
                    }
                    break;
                }
                case "5" :{
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first = orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    orderComponent.display(orderController.getOrderTypes());
                    choice = orderComponent.input();
                    orders  = orderController.displayOrdersInDiapazon(first, second, choice);

                    for (Order o: orders){
                        orderComponent.display(o.toString());
                    }

                    break;
                }
                case "6" :{
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first = orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second = orderComponent.input();
                    int amount = orderController.displayOrderAmountInDiapazon(first, second);
                    orderComponent.display("Количество заказов " + amount);
                    break;
                }
                case "7" :{
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first =orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    double income =  orderController.displayIncomeInDiapazon(first, second);
                    orderComponent.display("Размер прибыли " + income);
                    break;
                }
                case "8" :{
                    return;
                }
                default: {
                    break;
                }
            }
        }
    }

    void handleRequestSection(){
        String choice;
        List<List<Object>> requests;
        Boolean toChangeLastPurchase;
        List<Order> orders;
        Book book;

        while(true){
            requestComponent.display(null);
            choice = requestComponent.input();
            switch(choice){
                case "1": {
                    requests = requestController.getAllRequests(requestController.getRequestTypes());
                    for (List<Object> request : requests){
                        System.out.println(request.toString());
                    }
                    break;
                }
                case "2":{
                        System.out.println("Введите наименование книги");

                        String name =  requestComponent.input();

                        Boolean checking = bookController.receiveBook(name);
                        if (!checking){
                            orderComponent.display("Такой книги не было найдено");
                            break;
                        }
                        orders = orderController.getAllOrders("6");
                        for (Order o: orders){
                            toChangeLastPurchase = orderController.changeOrderStatus(o, name);
                            if (toChangeLastPurchase){
                                bookController.setLastPurchase(o.getBooks());
                            }
                        }

                        book = bookController.getBookByTitle(name);
                        requestController.deleteRequestByBook(book);

                    break;
                }

                case "3" :{
                    return;
                }
                default: {
                    break;
                }
            }
        }
    }

    Order createOrder(){
        Order order = new Order();

        orderComponent.display("Введите свое имя");
        String name = orderComponent.input();
        orderComponent.display("Введите свою фамилию");
        String surname = orderComponent.input();
        orderComponent.display("Введите свою почту");
        String email = orderComponent.input();
        Customer customer = new Customer(name, surname, email);
        order.setCustomer(customer);

        List<Book> books = bookController.displayAllBooks();
        orderComponent.display("Выберите книги, отправив индекс, начиная с 0\nПри окончании введите -1");
        for (int i=0; i<books.size(); i++){
            orderComponent.display(i + " " + books.get(i).getTitle());
        }
        String flag =orderComponent.input();
        while(!flag.equals("-1")){
            if (Integer.parseInt(flag) < books.size()) {
                order.addBook(books.get(Integer.parseInt(flag)));
                orderComponent.display("Добавлена книга "  + books.get(Integer.parseInt(flag)).getTitle() );
            }
            else {orderComponent.display("Неправильный индекс");}

            flag = orderComponent.input();

        }

        orderComponent.display("Есть ли книги,  которые вы все таки хотите удалить из заказа? (или -1)");
        flag =orderComponent.input();
        while(!flag.equals("-1")){
            if (Integer.parseInt(flag) < books.size()) {
                order.delBook(books.get(Integer.parseInt(flag)));
                orderComponent.display("Удалена книга "  + books.get(Integer.parseInt(flag)).getTitle() );
            }
            else orderComponent.display("Неправильный индекс");

            flag = orderComponent.input();

        }

    return order;


    }
}
