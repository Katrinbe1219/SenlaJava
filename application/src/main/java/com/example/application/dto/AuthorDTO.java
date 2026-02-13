package com.example.application.dto;

import com.example.application.model.Author;

public class AuthorDTO {
    String name;
    String surname;
    String paternal;

    public AuthorDTO() {}

    public AuthorDTO(String name, String surname, String paternal) {
        this.name = name;
        this.surname = surname;
        this.paternal = paternal;
    }

    public String getSurname(){
        return surname;
    }

    public String getName(){
        return name;
    }

    public String getPaternal(){
        return paternal;
    }
}
