package com.example.application.model;

import java.util.Comparator;

public class Author implements Comparable<Author>{
    // author_id |  name  |   paternal    | surname
    String name;
    String surname;
    String paternal;
    Long id;

    public Author(String name, String surname, String paternal, Long id) {
        this.name = name;
        this.surname = surname;
        this.paternal = paternal;
        this.id = id;
    }

    public String getInfo(){
        return "Автор: " + this.name + " " + this.paternal +  " " + this.surname;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int compareTo(Author author) {
        return Comparator
                .comparing(Author::getId)
                .compare(this, author);
    }
}
