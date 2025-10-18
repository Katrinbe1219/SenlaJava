package task_3_2;

abstract public class Flower {
    int cost;
    void setCost(int cost) {
        this.cost = cost;
    }
    int getCost(){
        return this.cost;
    }

    Flower(){
        this.cost = 120;
    }

    Flower(int cost){
        this.cost = cost;
    }
}
