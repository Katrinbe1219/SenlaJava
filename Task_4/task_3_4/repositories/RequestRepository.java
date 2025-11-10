package task_3_4.repositories;

import task_3_4.model.Book;
import task_3_4.model.Order;
import task_3_4.model.Request;
import task_3_4.model.Warehouse;

import java.util.List;

public class RequestRepository {
    private Warehouse warehouse;

    public RequestRepository(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<Request> getRequests(){
        return warehouse.getRequests();
    }


    public void deleteRequest(Request request){
        warehouse.deleteRequest(request);
    }

    public void add(Request request){
        warehouse.addRequest(request);
    }

}
