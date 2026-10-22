package ru.mirea.service;

import ru.mirea.exception.BusinessException;
import ru.mirea.model.RequestStatus;
import ru.mirea.model.WasteRequest;
import ru.mirea.repository.WasteRequestRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WasteRequestService {
    private final WasteRequestRepository repository;

    public WasteRequestService() {
        this.repository = new WasteRequestRepository();
    }

    public void createRequest(WasteRequest request) {
        validateRequest(request);
        repository.create(request);
    }
    
    public void updateRequest(WasteRequest request) {
        validateRequest(request);
        repository.update(request);
    }

    public void deleteRequest(int id) {
        repository.delete(id);
    }

    public List<WasteRequest> getAllRequests() {
        return repository.findAll();
    }

    public List<WasteRequest> searchByAddress(String keyword) {
        return getAllRequests().stream()
                .filter(req -> req.getAddress().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<WasteRequest> searchByWasteType(String type) {
        return getAllRequests().stream()
                .filter(req -> req.getWasteType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<WasteRequest> filterByStatus(RequestStatus status) {
        return getAllRequests().stream()
                .filter(req -> req.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<WasteRequest> filterByMinVolume(double minVolume) {
        return getAllRequests().stream()
                .filter(req -> req.getVolumeM3() >= minVolume)
                .collect(Collectors.toList());
    }

    public List<WasteRequest> sortByScheduledDate() {
        return getAllRequests().stream()
                .sorted(Comparator.comparing(WasteRequest::getScheduledDate))
                .collect(Collectors.toList());
    }

    public List<WasteRequest> sortByVolumeDesc() {
        return getAllRequests().stream()
                .sorted(Comparator.comparing(WasteRequest::getVolumeM3).reversed())
                .collect(Collectors.toList());
    }

    public void printStatistics() {
        List<WasteRequest> all = getAllRequests();
        
        long totalRequests = all.size();
        long newRequests = all.stream().filter(r -> r.getStatus() == RequestStatus.NEW).count();
        long completedRequests = all.stream().filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();
        double totalVolume = all.stream().mapToDouble(WasteRequest::getVolumeM3).sum();
        double maxVolume = all.stream().mapToDouble(WasteRequest::getVolumeM3).max().orElse(0.0);

        System.out.println("Всего заявок: " + totalRequests + 
                           " | Новых: " + newRequests + 
                           " | Завершенных: " + completedRequests + 
                           " | Общий объем: " + totalVolume + " м3" +
                           " | Макс. объем: " + maxVolume + " м3");
    }

    private void validateRequest(WasteRequest request) {
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new BusinessException("Ошибка: Адрес заявки не может быть пустым.");
        }
        if (request.getVolumeM3() <= 0) {
            throw new BusinessException("Ошибка: Объем отходов должен быть больше 0.");
        }
        if (request.getWasteType() == null || request.getWasteType().trim().isEmpty()) {
            throw new BusinessException("Ошибка: Необходимо указать тип отходов.");
        }
        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new BusinessException("Ошибка: Новая заявка не может быть сразу завершена.");
        }
        if (request.getClientId() <= 0) {
            throw new BusinessException("Ошибка: Заявка должна быть привязана к существующему клиенту.");
        }
    }
}