package task_3_1;

public class RandomNumber {
     public static void main(String[] args){
        int newNum = new java.util.Random().nextInt(900) + 100;
        int secondNum = new java.util.Random().nextInt(900) + 100;
        int thirdNum = new java.util.Random().nextInt(900) + 100;
        int sum = newNum /100 + secondNum / 100 + thirdNum / 100;
        System.out.println(sum);
    }
}
