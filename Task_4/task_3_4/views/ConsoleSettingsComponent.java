package task_3_4.views;

import java.io.Console;
import java.util.Scanner;

public class ConsoleSettingsComponent  implements  UIComponent{
    private Scanner sc;

    ConsoleSettingsComponent() {
        sc = new Scanner(System.in);
    }
    @Override
    public void display(String what) {
        if (what == null){
            System.out.println("\nРАЗДЕЛ ЗАПРОСОВ:");
            System.out.println("1. Задать срок месяцев для залежавшейся книги");
            System.out.println("2. Посмотреть срок месяцев для залежавшейся книги");
            System.out.println("3. Настроить функцию помечания заявок как выполненные при добавлении книги на склад.");
            System.out.println("4. Посмотреть настройку функции помечания ");
            System.out.println("5. Выход");
            return;
        };

        System.out.println(what);
    }

    @Override
    public String input() {
        return sc.nextLine();
    }
}
