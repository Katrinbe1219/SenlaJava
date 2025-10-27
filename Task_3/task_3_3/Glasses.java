package task_3_3;

public class Glasses implements IProduct{
    IProductPart first_part; // for lenses
    IProductPart second_part; // for case
    IProductPart third_part; // for temples

    @Override
    public void installFirstPart(IProductPart part) {
        first_part = part;
        System.out.println("Glasses: установлен корпус очков ");
    }

    @Override
    public void installSecondPart(IProductPart part) {
        second_part = part;
        System.out.println("Glasses: установлены дужки очков ");
    }

    @Override
    public void installThirdPart(IProductPart part) {
        third_part = part;
        System.out.println("Glasses: установлены линзы очков ");
    }

    Glasses(){
        System.out.println("Создан обьект очков");
    }
}
