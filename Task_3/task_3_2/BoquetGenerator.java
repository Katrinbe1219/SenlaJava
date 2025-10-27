package task_3_2;

import java.util.ArrayList;
import java.util.Scanner;

public class BoquetGenerator {
    public static void main(String[] args) {
        ArrayList<Flower> boquet = new ArrayList<>();
        int cost = 0;
        int choice;
        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.println("Enter a number to add a flower in boquet");
            System.out.println("1 - Rose");
            System.out.println("2 - Hydrangea");
            System.out.println("3 - Lilac Bush");
            System.out.println("4 - Hyacinth");
            System.out.println("0 - STOP");

            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    boquet.add(new Rose());
                    cost += boquet.getLast().getCost();
                    break;
                case 2:
                    boquet.add(new Hydrangea());
                    cost += boquet.getLast().getCost();
                    break;
                case 3:
                    boquet.add(new LilacBush());
                    cost += boquet.getLast().getCost();
                    break;
                case 4:
                    boquet.add(new Hyacinth());
                    cost += boquet.getLast().getCost();
                    break;
                case 0:
                    System.out.println("Cost is: " + cost);

                    System.exit(0);
            }


        }
    }
}
