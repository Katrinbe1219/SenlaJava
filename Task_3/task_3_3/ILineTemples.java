package task_3_3;

public class ILineTemples implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("ILineTemples созданы дужки для очков");
        return new Temples();
    }
}
