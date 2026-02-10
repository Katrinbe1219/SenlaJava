package com.example.application.controllers;
import com.example.application.model.*;

import com.example.application.model.Book;
import com.example.application.model.Customer;
import com.example.application.model.Order;
import com.example.application.model.types.OrderStatus;

import com.example.application.views.ConsoleUIFactory;
import com.example.application.views.UIComponent;
import com.example.application.views.UIFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;



@Component
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


    SettingController settingController;

    private final String FILENAME = "bookstore_system.dat";
    private static final Logger logger = LogManager.getLogger(BookStoreController.class.getName());


    public BookStoreController(BookController bc, OrderController oc,RequestController rc, SettingController sc ) throws Exception {
        consoleFactory = new ConsoleUIFactory();
        menuComponent = consoleFactory.createMainMenu();
        bookComponent = consoleFactory.createBookMenu();
        orderComponent = consoleFactory.createOrderMenu();
        requestComponent = consoleFactory.createRequestMenu();
        settingsComponent = consoleFactory.createSettingMenu();

        this.bookController = bc;
        this.orderController = oc;
        this.requestController = rc;
        this.settingController = sc;

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
                    logger.info("Выбрана команда пользователем изменения месяцев для залежавшихся книг");
                    settingsComponent.display("Введите количество месяц, превышение которого ведет к причислению книгу к залежавшейся");
                    choice = settingsComponent.input();
                    success = settingController.changeNumberOfMonth(choice);

                    if (success != null) settingsComponent.display(success);
                    else settingsComponent.display("Все изменено успешно");
                    logger.info("Закончено команда пользователем изменения месяцев для залежавшихся книг");


                    break;
                }
                case "2": {
                    logger.info("Выбрана команда пользователем получения длительности залежавшихся книг");
                    int numberOfMonth = settingController.getNumberOfMonth();
                    settingsComponent.display("Книга считается залежавшейся, если срок его пролеживания более: " + numberOfMonth);
                    logger.info("Закончена команда пользователем получения длительности залежавшихся книг");

                    break;
                }
                case "3": {
                    logger.info("Выбрана команда пользователем редактирования функции");
                    settingsComponent.display("Введите true/false для функции");
                    choice = settingsComponent.input();
                    success = settingController.setWarehouseFunction(choice);
                    if (success != null) settingsComponent.display(success);
                    else settingsComponent.display("Все прошло успешно");
                    logger.info("Закончена команда пользователем редактирования функции");

                    break;
                }
                case "4": {
                    logger.info("Выбрана команда пользователем получения значения функции ");
                    String warehouseFunction = settingController.getWarehouseOption();
                    settingsComponent.display(warehouseFunction);
                    logger.info("Закончена команда пользователем получения значения функции ");

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

                    logger.info("Выбрана команда пользователем получения всех книг");
                    books = bookController.displayAllBooks(logger);
                    if (books == null){
                        bookComponent.display("Книг нет");
                    }else{
                        for (Book book : books) {
                            bookComponent.display(book.getDescription());
                        }
                    }
                    logger.info("Закончена команда пользователем получения всех книг");

                    break;
                }
                case "2":{
                    logger.info("Выбрана команда пользователем получение залежавшихся книг");
                    bookComponent.display(bookController.getForDisplayType("Lbooks"));
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
                    logger.info("Закончена команда пользователем получение залежавшихся книг");

                    break;
                }
                case "3":{
                    logger.info("Выбрана команда пользователем получения описания книги");
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    // Если не найдена книга,  то возвращается Не найдено
                    String description  = bookController.displayBookDescription(choice, logger);
                    orderComponent.display(description);
                    logger.info("Закончена команда пользователем получения описания книги");

                    break;
                }
                case"4":{
                    logger.info("Выбрана команда пользователем получение сортированных книг");
                    bookComponent.display(bookController.getForDisplayType("book"));
                    choice = bookComponent.input();
                    // если введено не из диапазона, то статус ALL
                    books = bookController.displaySortedBooks(choice, logger);
                    for (Book book : books) {
                        bookComponent.display(book.getDescription());
                    }
                    logger.info("Закончена команда пользователем получение сортированных книг");

                    break;
                }
                case "5" :{
                    logger.info("Выбрана команда пользователем: проверка книги на наличие");
                    bookComponent.display("Введите название книги");
                    choice = bookComponent.input();
                    orderComponent.display(bookController.checkBook(choice, logger));
                    logger.info("Закончена команда пользователем: проверка книги на наличие");

                    break;
                }
                case "6" :{
                    logger.info("Выбрана команда пользователем: экспорта книги");
                    bookComponent.display("Функция выключена");
//                    choice = bookComponent.input();
//                    // если есть ошибка, то возвращается текст, а не пустая строка
//                    String success = bookController.exportBook(choice);
//                    if (!success.isEmpty()) bookComponent.display(success);
                    logger.info("Обработка команды номер экспорта в отсеке книг завершена");

                    break;
                }
                case "7" :{
                    logger.info("Выбрана команда пользователем: импорта книги");
//                    bookComponent.display("Введите название файла, находящегося в данном каталоге");
//                    choice = bookComponent.input();
//                    String success = bookController.importBook(choice);
//                    // если есть ошибка, то возвращается текст, а не пустая строка
//                    if (!success.isEmpty()) bookComponent.display(success);
                    bookComponent.display("Функция выключена");
                    logger.info("Обработка команды импорта в отсеке книг завершена");
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
                    logger.info("Выбрана команда пользователем: создание заказа");
                    order = createOrder();
                    ArrayList<Integer> done = orderController.createOrder(order, logger);
                    if (done == null) {
                        bookController.setLastPurchase(order.getBooks(), logger);


                    }else {
                        for (Integer i : done) {
                            orderComponent.display("Создан новый запрос с id " + i);
                        }
                    }
                    orderComponent.display("Ваш заказ добавлен в историю со статусом " + order.getStatus());
                    logger.info("Обработка команды номер создания  в отсеке заказов завершена");
                    break;
                }
                case "2":{
                        logger.info("Выбрана команда пользователем: удаление заказа");
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

                        logger.info("Обработка команды удаления в отсеке заказов завершена");
                        break;
                }
                case "3":{
                    logger.info("Выбрана команда пользователем : получение деталей заказа");
                    orderComponent.display("Какой id заказа, про который вы хотите узнать?");
                    choice = orderComponent.input();
                    order = orderController.getOrderById(choice, logger);
                    if (order == null) {
                        orderComponent.display("Ваш id не был корректен");
                        break;
                    }
                    
                    String details = orderController.getOrderDetails(order);
                    orderComponent.display(details);
                    logger.info("Обработка команды получения деталей в отсеке заказов завершена");
                    break;
                }
                case"4":{
                    logger.info("Выбрана команда пользователем: получение списка заказов");
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
                    logger.info("Обработка команды получения списка в отсеке заказов завершена");
                    break;
                }
                case "5" :{
                    logger.info("Выбрана команда пользователем6 получение заказов за период");
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
                    logger.info("Обработка команды получения списка за период в отсеке заказов завершена");
                    break;
                }
                case "6" :{
                    logger.info("Выбрана команда пользователем получение количества заказов за период");
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first = orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second = orderComponent.input();
                    int amount = orderController.displayOrderAmountInDiapazon(first, second, logger);
                    if (amount != -1) orderComponent.display("Количество заказов " + amount);
                    logger.info("Обработка команды получения количества за период в отсеке заказов завершена");
                    break;
                }
                case "7" :{
                    logger.info("Выбрана команда пользователем: вывод заказов за период");
                    orderComponent.display("Введите дату начала в формате год-месяц-день");
                    String first =orderComponent.input();
                    orderComponent.display("Введите дату конца в формате год-месяц-день");
                    String second =orderComponent.input();
                    double income =  orderController.displayIncomeInDiapazon(first, second, logger);
                    if (income != -1) orderComponent.display("Размер прибыли " + income);
                    logger.info("Обработка команды вывода списка за период в отсеке заказов завершена");
                    break;
                }
                case "8" :{
                    logger.info("Выбрана команда пользователем импорта заказов");
                    orderComponent.display("В данный момент недоступно");
//                    choice = orderComponent.input();
//                    String success = orderFileService.importOrder(choice);
//                    if (success != null) orderComponent.display(success);
                    logger.info("Обработка команды импорта в отсеке заказов завершена");
                    break;
                }
                case "9" :{
                    logger.info("Выбрана команда пользователем экспорта заказов");
                    orderComponent.display("В данный момент недоступно");
//                    choice = orderComponent.input();
//                    String success = orderFileService.exportOrder(choice);
//                    if (success != null) orderComponent.display(success);
                    logger.info("Обработка команды экспорта в отсеке заказов завершена");
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
        List<RequestResult> requests;
        Boolean toChangeLastPurchase;
        List<Order> orders;
        Book book;

        while(true){
            requestComponent.display(null);
            choice = requestComponent.input();
            switch(choice){
                case "1": {
                    logger.info("Выбрана команда пользователем: Получение всех запросов");
                    requests = requestController.getAllRequests(requestController.getRequestTypes(), logger);
                    if (requests == null){
                        requestComponent.display("Запрос не было найдено");
                        break;
                    }
                    for (RequestResult request : requests){
                        requestComponent.display(request.toString());
                    }
                    logger.info("Обработка команды получения списка в отсеке запросов завершена");
                    break;
                }
                case "2":{
                        logger.info("Выбрана команда пользователем: завоза книги");
                        requestComponent.display("Введите наименование книги");

                        String name =  requestComponent.input();

                        Boolean checking = bookController.receiveBook(name, logger);
                        if (!checking){
                            requestComponent.display("Такой книги не было найдено");
                            break;
                        }
                        orders = orderController.getAllOrders("6", logger);

                        List<Order> toChange = new  ArrayList<>();
                        List<Book> toChangeBooks = new ArrayList<>();
                        for (Order o: orders){

                                if (o.checkUpdateByBook(name) == OrderStatus.DONE){
                                    toChange.add(o);
                                    toChangeBooks.addAll(o.getBooks());
                                }
                        }

                        if (!toChange.isEmpty()){
                            orderController.changeOrderStatus(toChange, name, logger);
                            bookController.setLastPurchase(toChangeBooks, logger);
                        }

                        String warehouseFunction = settingController.getWarehouseOption();
                        if (warehouseFunction.equals("true")){
                            book = bookController.getBookByTitle(name, logger);
                            if (book != null){
                                requestController.deleteRequestByBook(book, logger);
                            }

                        }


                    logger.info("Обработка команды заквоза книги в отсеке запросов завершена");
                    break;
                }

                case "3" :{
                    logger.info("Выбрана команда пользователем: импорт");
                    requestComponent.display("В данный момент не доступно");
//                    choice = requestComponent.input();
//                    String success = requestController.importRequest(choice);
//                    if (success!=null) requestComponent.display(success);
                    logger.info("Обработка команды импорта в отсеке запросов завершена");
                    break;
                }
                case "4" :{
                    logger.info("Выбрана команда пользователем экспорта");
                    requestComponent.display("В данный момент недоступно");
//                    choice = requestComponent.input();
//                    String success = requestController.exportRequest(choice);
//                    if (success!=null) requestComponent.display(success);
                    logger.info("Обработка команды экспорта в отсеке запросов завершена");
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

        List<Book> books = bookController.displayAllBooks(logger);
        if (books == null){
            orderComponent.display("Книг нет, заказ невозможен");
            return null;
        }
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
