package task_3_4.views;

import java.util.Scanner;

public class ConsoleOrderComponent implements UIComponent{

    private Scanner sc;

    ConsoleOrderComponent(){
        sc = new Scanner(System.in);
    }
    @Override
    public void display(String what) {
        if (what == null){
            System.out.println("\nРАЗДЕЛ ЗАКАЗОВ:");
            System.out.println("1. Сделать заказ");
            System.out.println("2. Отменить заказ");
            System.out.println("3. Получить детали заказа");
            System.out.println("4. Получить список заказов");
            System.out.println("5. Получить список заказов за определенное время");
            System.out.println("6. Получить количество заказов за определенное время");
            System.out.println("7. Получить доход за определенное время");
            System.out.println("8. Выход");
            return;
        }
        System.out.println(what);

    }

    @Override
    public String input() {
        return sc.nextLine();
    }
}
