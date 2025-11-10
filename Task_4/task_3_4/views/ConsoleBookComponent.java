package task_3_4.views;

import java.util.Scanner;

public class ConsoleBookComponent implements UIComponent{
    private Scanner sc;

    ConsoleBookComponent() {
        sc = new Scanner(System.in);
    }
    @Override
    public void display(String what) {
        if (what == null) {
            System.out.println("\nКНИЖНЫЙ РАЗДЕЛ:");
            System.out.println("1. Вывести все книги");
            System.out.println("2. Вывод залежавшихся книг");
            System.out.println("3. Найти описание книги по названию");
            System.out.println("4. Отфильтровать книги");
            System.out.println("5. Проверить наличие книги в магазине");
            System.out.println("6. Назад");
            return;

        }
        System.out.println(what);

    }

    @Override
    public String input() {
        return sc.nextLine();
    }

}
