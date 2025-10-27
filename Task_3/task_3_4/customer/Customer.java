package task_3_4.customer;

public class Customer {
    String name;
    String surname;
    String email;

    public Customer(String name, String surname, String email){
        this.name = name;
        this.surname = surname;
        this.email = email;
    }


    public String toString(){
        return String.format("Name: %s\nSurname: %s\nEmail: %s", name, surname, email);
    }
}
