package task_3_4.views;

import java.util.Scanner;

public class ConsoleRequestComponent  implements UIComponent{
    private Scanner sc;

    ConsoleRequestComponent(){
        sc = new Scanner(System.in);
    }
    @Override
    public void display(String what) {
        if (what == null){
            System.out.println("\nРАЗДЕЛ ЗАПРОСОВ:");
            System.out.println("1. Список запросов");
            System.out.println("2. Получить книгу и удалить запросы");
            System.out.println("3. Выход");
            return;
        };

        System.out.println(what);
    }

    @Override
    public String input() {
        return sc.nextLine();
    }
}
