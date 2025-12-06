package com.example.application.serialization;

import com.example.application.model.BookShop;
import com.example.application.model.Warehouse;

import java.io.*;

public class BookStoreSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Warehouse warehouse;
    private BookShop bookshop;
    private boolean initialized = false;

    public BookStoreSystem() {
        this.warehouse = new Warehouse();
        this.bookshop = new BookShop();
    }

    public void initializeSystem(boolean loadSampleData){
        if (!this.initialized){
            if (loadSampleData){
                // если файл с историей не был найден, то используем изначальные данные для склада
                warehouse.initializeData();
            }
            this.initialized = true;
        }
    }

    public static BookStoreSystem loadSystem(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ob = new ObjectInputStream(
                new FileInputStream(filename)
        )){
            BookStoreSystem system = (BookStoreSystem) ob.readObject();
            system.initialized = true;
            return system;
        }
    }

    public void saveSystem(String filename) throws IOException{
        try (ObjectOutputStream on= new ObjectOutputStream(
                new FileOutputStream(filename)
        )){
            on.writeObject(this);
        }
    }

    public static boolean systemFileExists(String filename){
        return new File(filename).exists();
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public BookShop getBookshop() {
        return bookshop;
    }

    public boolean getInitialized() {
        return initialized;
    }


}
