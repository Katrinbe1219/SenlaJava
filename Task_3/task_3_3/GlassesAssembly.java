package task_3_3;

public class GlassesAssembly implements iAssemblyLine{
    ILineStep stepCase;
    ILineStep stepTemples;
    ILineStep stepLenses;

    @Override
    public IProduct assembleProduct(IProduct product) {
        IProductPart case_part = stepCase.buildProductPart();
        IProductPart temples_part = stepTemples.buildProductPart();
        IProductPart lenses_part = stepLenses.buildProductPart();

        product.installFirstPart(case_part);
        product.installSecondPart(temples_part);
        product.installThirdPart(lenses_part);

        System.out.println("Glasses are ready");
        return product;
    }

    GlassesAssembly(ILineStep case_step, ILineStep temples_step, ILineStep lenses_step) {
        stepCase = case_step;
        System.out.println("GlassesAssembly: добавлена сборка корпуса очков case_step");
        stepTemples = temples_step;
        System.out.println("GlassesAssembly: добавлена сборка дужков очков temples_step");
        stepLenses = lenses_step;
        System.out.println("GlassesAssembly: добавлена сборка линз очков lenses_step");
    }
}
