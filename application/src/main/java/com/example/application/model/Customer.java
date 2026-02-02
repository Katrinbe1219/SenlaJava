package com.example.application.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Comparator;

@Entity(name = "customers")
public class Customer  implements Comparable<Customer>, Serializable {

    @Column(name="name")
    String name;
    @Column(name="surname")
    String surname;
    @Column(name="email")
    String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    Integer customer_id;

    public Customer(String name, String surname, String email){
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
    public Customer(){}

    public Integer getCustomerId(){
        return customer_id;
    }

    public void setCustomerId(Integer customer_id){
        this.customer_id = customer_id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getEmail(){
        return email;
    }

    public String toString(){
        return String.format("Name: %s\nSurname: %s\nEmail: %s", name, surname, email);
    }

    @Override
    public int compareTo(Customer o) {
        return Comparator
                .comparing(Customer::getName)
                .thenComparing(Customer::getSurname)
                .thenComparing(Customer::getEmail)
                .compare(this, o);
    }

    public String getCsvInfo(){
        return name + ";" + surname + ";" + email;
    }
}
