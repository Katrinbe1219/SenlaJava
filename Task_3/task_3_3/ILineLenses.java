package task_3_3;

public class ILineLenses implements ILineStep{

    @Override
    public IProductPart buildProductPart() {
        System.out.println("ILineLenses созданы линзы для очков");
        return new Lenses();
    }
}
