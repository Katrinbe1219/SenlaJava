package com.example.application.controllers;

import com.example.application.dao.BookImplementation;
import com.example.application.model.*;

import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.services.OrderFileService;
import com.example.application.views.ConsoleUIFactory;
import com.example.application.views.UIComponent;
import com.example.application.views.UIFactory;
import com.example.custom_applications.Inject;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Inject
public class BookStoreController {
    UIComponent menuComponent;

    UIComponent bookComponent;
    UIComponent orderComponent;
    UIComponent requestComponent;
    UIComponent settingsComponent;
    UIFactory consoleFactory;

    @Inject
    BookController bookController;
    @Inject
    OrderController orderController;
    @Inject
    RequestController requestController;
    @Inject
    OrderFileService orderFileService;
    @Inject
    SettingController settingController;
//    @Inject
//    BookStoreSystem bookStoreSystem;

    @Inject
    BookImplementation bookDb;

    private final String FILENAME = "bookstore_system.dat";
    private static final Logger logger = LogManager.getLogger(BookStoreController.class.getName());


    public BookStoreController() throws Exception {
        consoleFactory = new ConsoleUIFactory();
        menuComponent = consoleFactory.createMainMenu();
        bookComponent = consoleFactory.createBookMenu();
        orderComponent = consoleFactory.createOrderMenu();
        requestComponent = consoleFactory.createRequestMenu();
        settingsComponent = consoleFactory.createSettingMenu();



////        this.bookStoreSystem = loadOrCreateSystem();
//        Warehouse warehouse = bookStoreSystem.getWarehouse();
//        BookShop bookshop = bookStoreSystem.getBookshop();
//
//        InjectAnnotationProcessor di = InjectAnnotationProcessor.getInstance();
//        di.registerSingleton(Warehouse.class, warehouse);
//        di.registerSingleton(BookShop.class, bookshop);

        // функция возвращает новый экзмепляр, а также сохраняет его у себя в di контейнере
//        di.getInstance(BookRepository.class);
//
//
//
//        di.getInstance(OrderRepository.class);
//        di.getInstance(RequestRepository.class);

        // так было до второго задания
//        ConfigurationAnnotationProcessor processor = new ConfigurationAnnotationProcessor();
//        processor.loadProperties(pr);


//        di.getInstance(BookService.class);
//       di.getInstance(BookShopFacade.class);
//        di.getInstance(SettingsService.class);

//        bookController = di.getInstance(BookController.class);
//        orderController = di.getInstance(OrderController.class);
//        requestController = di.getInstance(RequestController.class);
//
//        orderFileService = di.getInstance(OrderFileService.class);
//
//        settingController = di.getInstance(SettingController.class);

    }

    public void run(){
        Scanner input_  = new Scanner(System.in);
        String choice;
        while(true){
            menuComponent.display(null);
            choice = input_.nextLine();
            logger.info("Был выбран пользователем отсек: " + choice);

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
                    System.out.println("Пока");
//                    saveSystem();
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

        Optional<List<Book>> books_;
        List<Book> books;
        String choice;
        while(true){
            bookComponent.display(null);
            choice = bookComponent.input();
            switch(choice){
                case "1": {
                    logger.info("Выбрана команда пользователем номер 1 в отсеке книг");
                    books_ = bookController.displayAllBooks(logger);
                    if (books_.isEmpty()){
                        bookComponent.display("Книг нет");
                    }else{
                        for (Book book : books_.get()) {
                            bookComponent.display(book.getDescription());
                        }
                    }
                    logger.info("Обработка команды номер 1 в отсеке книг завершена");
                    break;
                }
                case "2":{
                    logger.info("Выбрана команда пользователем номер 2 в отсеке книг");
                    bookComponent.display(bookController.getForDisplayType("Lbook"));
                    choice = bookComponent.input();
                    // если введено не то, то тогда получаем выбор NONE
                    int numberOfMonth = settingController.getNumberOfMonth();
                    books = bookController.displayLongLiedBooks(choice, numberOfMonth, logger);

                    if (books == null || books.isEmpty()) {
                        bookComponent.display("Таких книг нет");
                        break;
                    }
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    logger.info("Обработка команды номер 2 в отсеке книг завершена");
                    break;
                }
                case "3":{
                    logger.info("Выбрана команда пользователем номер 3 в отсеке книг");
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    // Если не найдена книга,  то возвращается Не найдено
                    String description  = bookController.displayBookDescription(choice, logger);
                    orderComponent.display(description);
                    logger.info("Обработка команды номер 3 в отсеке книг завершена");
                    break;
                }
                case"4":{
                    logger.info("Выбрана команда пользователем номер 4 в отсеке книг");
                    bookComponent.display(bookController.getForDisplayType("book"));
                    choice = bookComponent.input();
                    // если введено не из диапазона, то статус ALL
                    books = bookController.displaySortedBooks(choice, logger);
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    logger.info("Обработка команды номер 4 в отсеке книг завершена");
                    break;
                }
                case "5" :{
                    logger.info("Выбрана команда пользователем номер 5 в отсеке книг");
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    orderComponent.display(bookController.checkBook(choice, logger));
                    logger.info("Обработка команды номер 5 в отсеке книг завершена");
                    break;
                }
                case "6" :{
                    logger.info("Выбрана команда пользователем номер 6 в отсеке книг");
                    bookComponent.display("Функция выключена");
//                    choice = bookComponent.input();
//                    // если есть ошибка, то возвращается текст, а не пустая строка
//                    String success = bookController.exportBook(choice);
//                    if (!success.isEmpty()) bookComponent.display(success);
                    logger.info("Обработка команды номер 6 в отсеке книг завершена");

                    break;
                }
                case "7" :{
                    logger.info("Выбрана команда пользователем номер 7 в отсеке книг");
//                    bookComponent.display("Введите название файла, находящегося в данном каталоге");
//                    choice = bookComponent.input();
//                    String success = bookController.importBook(choice);
//                    // если есть ошибка, то возвращается текст, а не пустая строка
//                    if (!success.isEmpty()) bookComponent.display(success);
                    bookComponent.display("Функция выключена");
                    logger.info("Обработка команды номер 7 в отсеке книг завершена");
                    break;
                }

                case "8":{
                    logger.info("Выбрана команда возврата");
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
                    logger.info("Выбрана команда пользователем номер 1 в отсеке заказов");
                    order = createOrder();
                    ArrayList<Integer> done = orderController.createOrder(order, logger);
                    if (done == null) {
                        bookController.setLastPurchase(order.getBooks());


                    }else {
                        for (Integer i : done) {
                            orderComponent.display("Создан новый запрос с id " + i);
                        }
                    }
                    orderComponent.display("Ваш заказ добавлен в историю со статусом " + order.getStatus());
                    logger.info("Обработка команды номер 1 в отсеке заказов завершена");
                    break;
                }
                case "2":{
                        logger.info("Выбрана команда пользователем номер 2 в отсеке заказов");
                        orderComponent.display("Какой id заказа, который вы хотите удалить?");
                        choice = orderComponent.input();
                        order = orderController.getOrderById(choice, logger);
                        if (order == null) {
                            orderComponent.display("Ваш id не был корректен");
                            break;
                        }

                        Boolean cancelled = orderController.deleteOrder(order, logger);
                        if (cancelled) {
                            requestController.deleteRequestByOrder(order, logger);
                            order = null;
                        }else{
                            requestComponent.display("Заказ для удаления не был найден. Создайте заказ");
                        }

                        logger.info("Обработка команды номер 2 в отсеке заказов завершена");
                        break;
                }
                case "3":{
                    logger.info("Выбрана команда пользователем номер 3 в отсеке заказов");
                    orderComponent.display("Какой id заказа, про который вы хотите узнать?");
                    choice = orderComponent.input();
                    order = orderController.getOrderById(choice, logger);
                    if (order == null) {
                        orderComponent.display("Ваш id не был корректен");
                        break;
                    }
                    
                    String details = orderController.getOrderDetails(order);
                    orderComponent.display(details);
                    logger.info("Обработка команды номер 3 в отсеке заказов завершена");
                    break;
                }
                case"4":{
                    logger.info("Выбрана команда пользователем номер 4 в отсеке заказов");
                    orderComponent.display(orderController.getOrderTypes());
                    choice = orderComponent.input();
                    // если веден индекс вне диапазона, выдается DATE_UP
                    orders = orderController.getAllOrders(choice, logger);
                    if (orders == null ){
                        orderComponent.display("Заказов нет");
                        break;
                    }
                    for (Order o: orders){
                        orderComponent.display(o.toString());
                    }
                    logger.info("Обработка команды номер 4 в отсеке заказов завершена");
                    break;
                }
                case "5" :{
                    logger.info("Выбрана команда пользователем номер 5 в отсеке заказов");
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first = orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    orderComponent.display(orderController.getOrderTypes());
                    choice = orderComponent.input();
                    orders  = orderController.displayOrdersInDiapazon(first, second, choice, logger);

                    if (orders == null) {
                        orderComponent.display("Не найдено заказов");
                        break;
                    }

                    for (Order o: orders){
                        orderComponent.display(o.toString());
                    }
                    logger.info("Обработка команды номер 5 в отсеке заказов завершена");
                    break;
                }
                case "6" :{
                    logger.info("Выбрана команда пользователем номер 6 в отсеке заказов");
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first = orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second = orderComponent.input();
                    int amount = orderController.displayOrderAmountInDiapazon(first, second, logger);
                    if (amount != -1) orderComponent.display("Количество заказов " + amount);
                    logger.info("Обработка команды номер 6 в отсеке заказов завершена");
                    break;
                }
                case "7" :{
                    logger.info("Выбрана команда пользователем номер 7 в отсеке заказов");
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first =orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    double income =  orderController.displayIncomeInDiapazon(first, second, logger);
                    if (income != -1) orderComponent.display("Размер прибыли " + income);
                    logger.info("Обработка команды номер 7 в отсеке заказов завершена");
                    break;
                }
                case "8" :{
                    logger.info("Выбрана команда пользователем номер 8 в отсеке заказов");
                    orderComponent.display("В данный момент недоступно");
//                    choice = orderComponent.input();
//                    String success = orderFileService.importOrder(choice);
//                    if (success != null) orderComponent.display(success);
                    logger.info("Обработка команды номер 8 в отсеке заказов завершена");
                    break;
                }
                case "9" :{
                    logger.info("Выбрана команда пользователем номер 9 в отсеке заказов");
                    orderComponent.display("В данный момент недоступно");
//                    choice = orderComponent.input();
//                    String success = orderFileService.exportOrder(choice);
//                    if (success != null) orderComponent.display(success);
                    logger.info("Обработка команды номер 9 в отсеке заказов завершена");
                    break;
                }
                case "10" :{
                    logger.info("Команда возврата");
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
                    logger.info("Выбрана команда пользователем номер 1 в отсеке запросов");
                    requests = requestController.getAllRequests(requestController.getRequestTypes(), logger);
                    if (requests == null){
                        requestComponent.display("Запрос не было найдено");
                        break;
                    }
                    for (List<Object> request : requests){
                        requestComponent.display(request.toString());
                    }
                    logger.info("Обработка команды номер 1 в отсеке запросов завершена");
                    break;
                }
                case "2":{
                        logger.info("Выбрана команда пользователем номер 2 в отсеке запросов");
                        requestComponent.display("Введите наименование книги");

                        String name =  requestComponent.input();

                        Boolean checking = bookController.receiveBook(name, logger);
                        if (!checking){
                            requestComponent.display("Такой книги не было найдено");
                            break;
                        }
                        orders = orderController.getAllOrders("6", logger);
                        for (Order o: orders){
                            toChangeLastPurchase = orderController.changeOrderStatus(o, name, logger);
                            if (toChangeLastPurchase){
                                bookController.setLastPurchase(o.getBooks());
                            }
                        }

                        String warehouseFunction = settingController.getWarehouseOption();
                        if (warehouseFunction.equals("true")){
                            Optional<Book> book_ = bookController.getBookByTitle(name, logger);
                            if (book_.isPresent()){
                                requestController.deleteRequestByBook(book_.get().getId(), logger);
                            }

                        }


                    logger.info("Обработка команды номер 2 в отсеке запросов завершена");
                    break;
                }

                case "3" :{
                    logger.info("Выбрана команда пользователем номер 3 в отсеке запросов");
                    requestComponent.display("В данный момент не доступно");
//                    choice = requestComponent.input();
//                    String success = requestController.importRequest(choice);
//                    if (success!=null) requestComponent.display(success);
                    logger.info("Обработка команды номер 3 в отсеке запросов завершена");
                    break;
                }
                case "4" :{
                    logger.info("Выбрана команда пользователем номер 4 в отсеке запросов");
                    requestComponent.display("В данный момент недоступно");
//                    choice = requestComponent.input();
//                    String success = requestController.exportRequest(choice);
//                    if (success!=null) requestComponent.display(success);
                    logger.info("Обработка команды номер 4 в отсеке запросов завершена");
                    break;

                }
                case "5" :{
                    logger.info("Команда Возврата");
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

        Order order = new Order( );

        orderComponent.display("Введите свое имя");
        String name = orderComponent.input();
        orderComponent.display("Введите свою фамилию");
        String surname = orderComponent.input();
        orderComponent.display("Введите свою почту");
        String email = orderComponent.input();
        Customer customer = new Customer(name, surname, email);
        order.setCustomer(customer);

        Optional<List<Book>> books_ = bookController.displayAllBooks(logger);
        if (books_.isEmpty()){
            orderComponent.display("Книг нет, заказ невозможен");
            return null;
        }
        List<Book> books = books_.get();
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
                flag =orderComponent.input();
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


//    private void saveSystem(){
//        try {
//            bookStoreSystem.saveSystem(FILENAME);
//            System.out.println("Система сохранена");
//        } catch (IOException e) {
//            System.out.println("Неудача сохранения системы " + e.getMessage());
//        }
//    }
}
