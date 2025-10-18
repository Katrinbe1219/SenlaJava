package task_3_3;

public class Test {
    public static void main(String[] args) {

        ILineStep case_step= new ILineCase();
        ILineStep temple_step = new ILineTemples();
        ILineStep lense_step = new ILineLenses();
        IProduct glasses = new Glasses();

        iAssemblyLine assembly = new GlassesAssembly( case_step, temple_step, lense_step );


        assembly.assembleProduct(glasses);
    }
}
