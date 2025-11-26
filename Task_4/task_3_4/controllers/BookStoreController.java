package task_3_4.controllers;

import task_3_4.model.Book;
import task_3_4.model.Customer;
import task_3_4.model.Order;
import task_3_4.model.Warehouse;
import task_3_4.repositories.BookRepository;
import task_3_4.repositories.OrderRepository;
import task_3_4.repositories.PropertiesRepository;
import task_3_4.repositories.RequestRepository;
import task_3_4.serialization.BookStoreSystem;
import task_3_4.services.BookService;
import task_3_4.services.BookShopFacade;
import task_3_4.services.OrderFileService;
import task_3_4.services.SettingsService;
import task_3_4.views.ConsoleUIFactory;
import task_3_4.views.UIComponent;
import task_3_4.views.UIFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookStoreController {
    UIComponent menuComponent;
    UIComponent bookComponent;
    UIComponent orderComponent;
    UIComponent requestComponent;
    UIComponent settingsComponent;
    UIFactory consoleFactory;

    BookController bookController;
    OrderController orderController;
    RequestController requestController;
    OrderFileService orderFileService;
    SettingController settingController;

    BookStoreSystem bookStoreSystem;

    private final String FILENAME = "bookstore_system.dat";


    public BookStoreController() {
        consoleFactory = new ConsoleUIFactory();
        menuComponent = consoleFactory.createMainMenu();
        bookComponent = consoleFactory.createBookMenu();
        orderComponent = consoleFactory.createOrderMenu();
        requestComponent = consoleFactory.createRequestMenu();
        settingsComponent = consoleFactory.createSettingMenu();

        this.bookStoreSystem = loadOrCreateSystem();

        Warehouse warehouse = bookStoreSystem.getWarehouse();
        BookRepository br = new BookRepository(warehouse);
        OrderRepository or = new OrderRepository(bookStoreSystem.getBookshop());
        RequestRepository rr = new RequestRepository(warehouse);
        PropertiesRepository pr = new PropertiesRepository();


        BookService bs = new BookService(br,rr, or);
        BookShopFacade bsf = new BookShopFacade(or, rr);
        SettingsService ss = new SettingsService(pr);

        bookController = new BookController(bs);
        orderController = new OrderController(bsf);
        requestController = new RequestController(bs);

        orderFileService = new OrderFileService(or, br);

        settingController = new SettingController(ss);

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
                    handleSettingsSection();
                    break;
                }
                case "5": {
                    saveSystem();
                    return;
                }
                default:{break;}
            }
        }
    }

    void handleSettingsSection(){
        String choice;
        String success;
        while(true){
            settingsComponent.display(null);
            choice = settingsComponent.input();

            switch (choice){
                case "1": {
                    settingsComponent.display("Введите количество месяц, превышение которого ведет к причислению книгу к залежавшейся");
                    choice = settingsComponent.input();
                    success = settingController.changeNumberOfMonth(choice);

                    if (success != null) settingsComponent.display(success);
                    else settingsComponent.display("Все изменено успешно");

                    break;
                }
                case "2": {
                    int numberOfMonth = settingController.getNumberOfMonth();
                    settingsComponent.display("Книга считается залежавшейся, если срок его пролеживания более: " + numberOfMonth);
                    break;
                }
                case "3": {
                    settingsComponent.display("Введите true/false для функции");
                    choice = settingsComponent.input();
                    success = settingController.setWarehouseFunction(choice);
                    if (success != null) settingsComponent.display(success);
                    else settingsComponent.display("Все прошло успешно");

                    break;
                }
                case "4": {
                    String warehouseFunction = settingController.getWarehouseOption();
                    settingsComponent.display(warehouseFunction);
                    break;
                }
                case "5": {
                    return;
                }
                default: {
                    settingsComponent.display("Такого выбора не существует");
                    break;}
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
                    // если введено не то, то тогда получаем выбор NONE
                    int numberOfMonth = settingController.getNumberOfMonth();
                    books = bookController.displayLongLiedBooks(choice, numberOfMonth);

                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    break;
                }
                case "3":{
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    // Если не найдена книга,  то возвращается Не найдено
                    String description  = bookController.displayBookDescription(choice);
                    orderComponent.display(description);
                    break;
                }
                case"4":{
                    bookComponent.display(bookController.getForDisplayType("book"));
                    choice = bookComponent.input();
                    // если введено не из диапазона, то статус ALL
                    books = bookController.displaySortedBooks(choice);
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    break;
                }
                case "5" :{
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    orderComponent.display(bookController.checkBook(choice));
                    break;
                }
                case "6" :{
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    // если есть ошибка, то возвращается текст, а не пустая строка
                    String success = bookController.exportBook(choice);
                    if (!success.isEmpty()) bookComponent.display(success);

                    break;
                }
                case "7" :{
                    bookComponent.display("Введите название файла, находящегося в данном каталоге");
                    choice = bookComponent.input();
                    String success = bookController.importBook(choice);
                    // если есть ошибка, то возвращается текст, а не пустая строка
                    if (!success.isEmpty()) bookComponent.display(success);
                    break;
                }

                case "8":{
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
                        orderController.incrementMaxId();

                    }
                    orderComponent.display("Ваш заказ добавлен в историю со статусом " + order.getStatus());
                    break;
                }
                case "2":{
                        orderComponent.display("Какой id заказа, который вы хотите удалить?");
                        choice = orderComponent.input();
                        order = orderController.getOrderById(choice);
                        if (order == null) {
                            orderComponent.display("Ваш id не был корректен");
                            break;
                        }
                    System.out.println(order.getCustomer().getCsvInfo());
                        Boolean cancelled = orderController.deleteOrder(order);
                        if (cancelled) {
                            requestController.deleteRequestByOrder(order);
                            order = null;
                        }else{
                            requestComponent.display("Заказ для удаления не был найден. Создайте заказ");
                        }


                        break;
                }
                case "3":{

                    orderComponent.display("Какой id заказа, про который вы хотите узнать?");
                    choice = orderComponent.input();
                    order = orderController.getOrderById(choice);
                    if (order == null) {
                        orderComponent.display("Ваш id не был корректен");
                        break;
                    }
                    
                    String details = orderController.getOrderDetails(order);
                    orderComponent.display(details);
                    break;
                }
                case"4":{
                    orderComponent.display(orderController.getOrderTypes());
                    choice = orderComponent.input();
                    // если веден индекс вне диапазона, выдается DATE_UP
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

                    if (orders == null) {
                        orderComponent.display("Не найдено заказов");
                        break;
                    }

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
                    if (amount != -1) orderComponent.display("Количество заказов " + amount);
                    break;
                }
                case "7" :{
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first =orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    double income =  orderController.displayIncomeInDiapazon(first, second);
                    if (income != -1) orderComponent.display("Размер прибыли " + income);

                    break;
                }
                case "8" :{
                    orderComponent.display("Введите наименование файла");
                    choice = orderComponent.input();
                    String success = orderFileService.importOrder(choice);
                    if (success != null) orderComponent.display(success);
                    break;
                }
                case "9" :{
                    orderComponent.display("Введите id заказа");
                    choice = orderComponent.input();
                    String success = orderFileService.exportOrder(choice);
                    if (success != null) orderComponent.display(success);
                    break;
                }
                case "10" :{
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
                    if (requests == null){
                        requestComponent.display("Запрос не было найдено");
                        break;
                    }
                    for (List<Object> request : requests){
                        System.out.println(request.toString());
                    }
                    break;
                }
                case "2":{
                        requestComponent.display("Введите наименование книги");

                        String name =  requestComponent.input();

                        Boolean checking = bookController.receiveBook(name);
                        if (!checking){
                            requestComponent.display("Такой книги не было найдено");
                            break;
                        }
                        orders = orderController.getAllOrders("6");
                        for (Order o: orders){
                            toChangeLastPurchase = orderController.changeOrderStatus(o, name);
                            if (toChangeLastPurchase){
                                bookController.setLastPurchase(o.getBooks());
                            }
                        }

                        String warehouseFunction = settingController.getWarehouseOption();
                        if (warehouseFunction.equals("true")){
                            book = bookController.getBookByTitle(name);
                            requestController.deleteRequestByBook(book);
                        }


                    break;
                }

                case "3" :{
                    requestComponent.display("Введите название файла");
                    choice = requestComponent.input();
                    String success = requestController.importRequest(choice);
                    if (success!=null) requestComponent.display(success);
                    break;
                }
                case "4" :{
                    requestComponent.display("Введите id запроса");
                    choice = requestComponent.input();
                    String success = requestController.exportRequest(choice);
                    if (success!=null) requestComponent.display(success);
                    break;

                }
                case "5" :{
                    return;
                }
                default: {
                    break;
                }
            }
        }
    }

    Order createOrder(){
        // добавить счетсик заказов
        int max = orderController.getMaxId()+1;
        Order order = new Order( max);

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
            try {
                if (Integer.parseInt(flag) < books.size()) {
                    order.addBook(books.get(Integer.parseInt(flag)));
                    orderComponent.display("Добавлена книга "  + books.get(Integer.parseInt(flag)).getTitle() );
                }
                else {orderComponent.display("Неправильный индекс");}

                flag = orderComponent.input();
            }catch(NumberFormatException e){
                orderComponent.display("Вы вели некорректное число");
            }


        }

        orderComponent.display("Есть ли книги,  которые вы все таки хотите удалить из заказа? (или -1)");
        flag =orderComponent.input();
        while(!flag.equals("-1")){
            try {
                if (Integer.parseInt(flag) < books.size()) {
                    order.delBook(books.get(Integer.parseInt(flag)));
                    orderComponent.display("Удалена книга "  + books.get(Integer.parseInt(flag)).getTitle() );
                }
                else orderComponent.display("Неправильный индекс");

                flag = orderComponent.input();
            }catch (NumberFormatException e){
                orderComponent.display("Вы вели некорректно число");
            }


        }

    return order;


    }

    private BookStoreSystem loadOrCreateSystem(){
        if (BookStoreSystem.systemFileExists(FILENAME)){
            try {
                return BookStoreSystem.loadSystem(FILENAME);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Проблема при загрузке дерева: " + e.getMessage());
                System.out.println("Будет создана новая система");
            }
        }
        BookStoreSystem newSystem = new BookStoreSystem();
        newSystem.initializeSystem(true);
        return newSystem;
    }

    private void saveSystem(){
        try {
            bookStoreSystem.saveSystem(FILENAME);
            System.out.println("Система сохранена");
        } catch (IOException e) {
            System.out.println("Неудача сохранения системы " + e.getMessage());
        }
    }
}
