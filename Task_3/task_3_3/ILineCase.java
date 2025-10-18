package task_3_3;

public class ILineCase implements  ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("ILineCase создан case для очков");
        return new Case();
    }
}
