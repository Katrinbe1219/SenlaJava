package task_3_4.exceptions;

public class OrderCanNotBeCreated extends RuntimeException {
    public OrderCanNotBeCreated(String message) {
        super(message);
    }
}
