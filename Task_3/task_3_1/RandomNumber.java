package task_3_1;

public class RandomNumber {
     public static void main(String[] args){
        int newNum = new java.util.Random().nextInt(10000);
        int secondNum = new java.util.Random().nextInt(10000);
        int thirdNum = new java.util.Random().nextInt(10000);
        int sum = newNum /1000 + secondNum / 1000 + thirdNum / 1000;
        System.out.println(sum);
    }
}
