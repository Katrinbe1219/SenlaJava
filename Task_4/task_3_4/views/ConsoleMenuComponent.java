package task_3_4.views;

import java.util.Scanner;

public class ConsoleMenuComponent implements  UIComponent{
    private Scanner sc;

    ConsoleMenuComponent() {
        sc = new Scanner(System.in);
    }

    @Override
    public void display(String what) {
        if (what == null) {
            System.out.println("КНИЖНЫЙ МАГАЗИН");
            System.out.println("1. Книги");
            System.out.println("2. Заказы");
            System.out.println("3. Запросы");
            System.out.println("4. Выход");
            System.out.println("Введите индекс интересующего вас раздела");
        }else {
            System.out.println(what);
        }

    }
    @Override
    public String input() {
        return sc.nextLine();
    }
}
