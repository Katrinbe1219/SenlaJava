package com.example.application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Comparator;

@Entity
@Table(name = "authors")
public class Author implements Comparable<Author>{
    // author_id |  name  |   paternal    | surname

    @Column(name="name")
    String name;

    @Column(name="surname")
    String surname;

    @Column(name="paternal")
    String paternal;

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getPaternal(){
        return paternal;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="author_id")
    Long id;

    public Author(String name, String surname, String paternal, Long id) {
        this.name = name;
        this.surname = surname;
        this.paternal = paternal;
        this.id = id;
    }

    public Author() {}
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
