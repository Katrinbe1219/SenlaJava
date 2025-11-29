package com.example.application.repositories;

import com.example.application.model.Book;
import com.example.application.model.Order;
import com.example.application.model.Request;
import com.example.application.model.Warehouse;
import com.example.custom_annotations.Inject;

import java.util.List;

@Inject
public class RequestRepository {
    @Inject
    private Warehouse warehouse;

//    public RequestRepository(Warehouse warehouse) {
//        this.warehouse = warehouse;
//    }

    public List<Request> getRequests(){
        return warehouse.getRequests();
    }


    public void deleteRequest(Request request){
        warehouse.deleteRequest(request);
    }

    public void add(Request request){
        warehouse.addRequest(request);
    }

    public int getCurrentMaxRequestId(){
        return warehouse.getCountAllRequests();
    }

    public void incrementMaxRequestId(){
        warehouse.incrementCountAllRequests();
    }

    public void checkMaxRequestId(int id){
        warehouse.checkMaxRequestId(id);
    }

}
