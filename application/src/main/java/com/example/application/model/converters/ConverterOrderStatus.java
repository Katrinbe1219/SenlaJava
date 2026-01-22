package com.example.application.model.converters;

import com.example.application.model.types.OrderStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ConverterOrderStatus implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return switch (orderStatus){
            case NEW -> "N";
            case DONE -> "D";
            case CANCELLED -> "C";
        };
    }

    @Override
    public OrderStatus convertToEntityAttribute(String s) {
        return switch (s){
            case "N" -> OrderStatus.NEW;
            case "D" -> OrderStatus.DONE;
            case "C" -> OrderStatus.CANCELLED;
            default -> null;
        };
    }
}
