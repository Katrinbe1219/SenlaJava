package task_3_4.model;

import java.util.Comparator;

public class Customer  implements Comparable<Customer>{
    String name;
    String surname;
    String email;

    public Customer(String name, String surname, String email){
        this.name = name;
        this.surname = surname;
        this.email = email;
    }


    String getName(){
        return name;
    }

    String getSurname(){
        return surname;
    }

    String getEmail(){
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
