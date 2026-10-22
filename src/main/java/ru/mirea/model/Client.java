package ru.mirea.model;

import java.time.LocalDate;

public class Client {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private LocalDate registrationDate;

    // Пустой конструктор
    public Client() {}

    // Конструктор со всеми полями
    public Client(int id, String fullName, String phoneNumber, String email, LocalDate registrationDate) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    // Геттеры и сеттеры (реализация инкапсуляции)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}