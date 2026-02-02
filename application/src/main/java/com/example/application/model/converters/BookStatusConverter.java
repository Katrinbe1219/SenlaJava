package com.example.application.model.converters;

import com.example.application.model.types.BookStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // autoApply=true применит ко всем полям BookStatus
public class BookStatusConverter implements AttributeConverter<BookStatus, String> {
    @Override
    public String convertToDatabaseColumn(BookStatus bookStatus) {
        return switch (bookStatus) {
            case BookStatus.IN_STOCK -> "I";
            case BookStatus.OUT_OF_STOCK -> "O";
        };
    }

    @Override
    public BookStatus convertToEntityAttribute(String s) {
        return switch (s){
            case "I" -> BookStatus.IN_STOCK;
            case "O" -> BookStatus.OUT_OF_STOCK;
            default -> BookStatus.IN_STOCK;
        };
    }
}
