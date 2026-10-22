package ru.mirea.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WasteRequest {
    private int id;
    private int clientId;
    private String address;
    private String wasteType;
    private double volumeM3;
    private RequestStatus status; // Используем наш enum
    private LocalDateTime createdAt;
    private LocalDate scheduledDate;

    // Пустой конструктор
    public WasteRequest() {}

    // Конструктор со всеми полями
    public WasteRequest(int id, int clientId, String address, String wasteType, double volumeM3, RequestStatus status, LocalDateTime createdAt, LocalDate scheduledDate) {
        this.id = id;
        this.clientId = clientId;
        this.address = address;
        this.wasteType = wasteType;
        this.volumeM3 = volumeM3;
        this.status = status;
        this.createdAt = createdAt;
        this.scheduledDate = scheduledDate;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public double getVolumeM3() { return volumeM3; }
    public void setVolumeM3(double volumeM3) { this.volumeM3 = volumeM3; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
}