package com.vivek.algofocus;

public class User {

    private String Email;
    private String Contact_Number;
    private String Name;

    public User(String email,String contact_Number, String name) {
       Email = email;
        Contact_Number = contact_Number;
        Name = name;
    }
    public User(){

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }



    public String getContact_Number() {
        return Contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        Contact_Number = contact_Number;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
