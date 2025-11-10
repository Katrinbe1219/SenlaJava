package task_3_4.views;

public class ConsoleUIFactory implements UIFactory {
    @Override
    public UIComponent createMainMenu() {
        return new ConsoleMenuComponent();
    }

    @Override
    public UIComponent createBookMenu() {
        return new ConsoleBookComponent();
    }

    @Override
    public UIComponent createOrderMenu() {
        return new ConsoleOrderComponent();
    }

    @Override
    public UIComponent createRequestMenu() {
        return new ConsoleRequestComponent();
    }
}
