package task_3_4;

import task_3_4.controllers.BookStoreController;
import task_3_4.model.Book;
import task_3_4.model.types.BookStatus;
import task_3_4.model.types.BookTypes;

import java.util.ArrayList;

public class Test3 {
    public static void main(String[] args) {
        // Creating system----------------------------------------


        BookStoreController bc = new BookStoreController();
        bc.run();
    }
}
