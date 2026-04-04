package com.example.application.model.converters;


import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.types.BookTypes;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BookTypesConverter implements AttributeConverter<BookTypes, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookTypes bookTypes)  throws CanNotMakeExecution {
        if (bookTypes == null){
            return null;
        }
        switch (bookTypes){
            case BookTypes.CLASSICAL -> {return 1;}
            case BookTypes.HISTORY -> {return 2;}
            case BookTypes.FANTASY -> {return 3;}
            default -> {throw new CanNotMakeExecution("Такого типа статуса нет BookTypesConverter convertToDatabaseColumn");}

        }
    }

    @Override
    public BookTypes convertToEntityAttribute(Integer s) throws CanNotMakeExecution {
        if (s == null){
            return null;
        }
        switch (s){
            case 1 -> {
                return BookTypes.CLASSICAL;
            }
            case 2 -> {return BookTypes.HISTORY;}
            case 3 -> {
                return BookTypes.FANTASY;
            }
            default -> {
                 throw new CanNotMakeExecution("Такого типа статуса нет BookTypesConverter convertToEntityAttribute");
            }
        }
    }
}
