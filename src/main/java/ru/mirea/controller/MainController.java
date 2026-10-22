package ru.mirea.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.exception.BusinessException;
import ru.mirea.model.WasteRequest;
import ru.mirea.service.WasteRequestService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private TableView<WasteRequest> requestTable;
    @FXML
    private TableColumn<WasteRequest, Integer> idColumn;
    @FXML
    private TableColumn<WasteRequest, Integer> clientColumn;
    @FXML
    private TableColumn<WasteRequest, String> addressColumn;
    @FXML
    private TableColumn<WasteRequest, String> typeColumn;
    @FXML
    private TableColumn<WasteRequest, Double> volumeColumn;
    @FXML
    private TableColumn<WasteRequest, String> statusColumn;
    @FXML
    private TableColumn<WasteRequest, LocalDate> dateColumn;

    private WasteRequestService service;
    private ObservableList<WasteRequest> masterData;

    @FXML
    public void initialize() {
        service = new WasteRequestService();
        masterData = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("wasteType"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volumeM3"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));

        loadData();
    }

    private void loadData() {
        masterData.setAll(service.getAllRequests());
        requestTable.setItems(masterData);
    }

    @FXML
    private void handleAdd() {
        WasteRequest tempRequest = new WasteRequest();
        boolean okClicked = showRequestDialog(tempRequest, "Добавление заявки");
        if (okClicked) {
            try {
                service.createRequest(tempRequest);
                loadData();
            } catch (BusinessException e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        WasteRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean okClicked = showRequestDialog(selected, "Редактирование заявки");
            if (okClicked) {
                try {
                    service.updateRequest(selected);
                    loadData();
                } catch (BusinessException e) {
                    showError(e.getMessage());
                }
            }
        } else {
            showError("Пожалуйста, выберите заявку в таблице для редактирования.");
        }
    }

    @FXML
    private void handleDelete() {
        WasteRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удалить заявку #" + selected.getId() + "?");
            alert.setContentText("Вы уверены, что хотите удалить эту запись?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.deleteRequest(selected.getId());
                loadData();
            }
        } else {
            showError("Пожалуйста, выберите заявку в таблице для удаления.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleSearchAndFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/search-dialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Поиск, Фильтры и Сортировка");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SearchDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isApplyClicked()) {
                List<WasteRequest> all = service.getAllRequests();

                String address = controller.getAddressSearch();
                if (address != null && !address.trim().isEmpty()) {
                    all = all.stream().filter(r -> r.getAddress().toLowerCase().contains(address.toLowerCase())).collect(Collectors.toList());
                }

                String type = controller.getTypeSearch();
                if (type != null && !type.equals("Любой")) {
                    all = all.stream().filter(r -> r.getWasteType().equalsIgnoreCase(type)).collect(Collectors.toList());
                }

                ru.mirea.model.RequestStatus status = controller.getStatusFilter();
                if (status != null) {
                    all = all.stream().filter(r -> r.getStatus() == status).collect(Collectors.toList());
                }

                String volStr = controller.getVolumeFilter();
                if (volStr != null && !volStr.trim().isEmpty()) {
                    try {
                        double minVol = Double.parseDouble(volStr.replace(",", "."));
                        all = all.stream().filter(r -> r.getVolumeM3() >= minVol).collect(Collectors.toList());
                    } catch (NumberFormatException ignored) {}
                }

                String sort = controller.getSortSelection();
                if ("По дате (возрастание)".equals(sort)) {
                    all = all.stream().sorted(java.util.Comparator.comparing(WasteRequest::getScheduledDate)).collect(Collectors.toList());
                } else if ("По объему (убывание)".equals(sort)) {
                    all = all.stream().sorted(java.util.Comparator.comparing(WasteRequest::getVolumeM3).reversed()).collect(Collectors.toList());
                }

                masterData.setAll(all);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStatistics() {
        long total = masterData.size();
        long newReqs = masterData.stream().filter(r -> r.getStatus() == ru.mirea.model.RequestStatus.NEW).count();
        long compReqs = masterData.stream().filter(r -> r.getStatus() == ru.mirea.model.RequestStatus.COMPLETED).count();
        double totalVol = masterData.stream().mapToDouble(WasteRequest::getVolumeM3).sum();
        double maxVol = masterData.stream().mapToDouble(WasteRequest::getVolumeM3).max().orElse(0.0);

        String statText = String.format("1. Всего заявок: %d\n2. Новых заявок: %d\n3. Завершенных: %d\n4. Общий объем: %.2f м3\n5. Максимальный объем: %.2f м3",
                total, newReqs, compReqs, totalVol, maxVol);
        
        showInfo("Статистика системы", statText);
    }

    @FXML
    private void handleExport() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Сохранить экспорт");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        
        java.io.File file = fileChooser.showSaveDialog(requestTable.getScene().getWindow());
        if (file != null) {
            ru.mirea.util.ExcelExporter.exportRequests(masterData, file.getAbsolutePath());
            showInfo("Экспорт завершен", "Данные успешно сохранены в файл:\n" + file.getAbsolutePath());
        }
    }

    private boolean showRequestDialog(WasteRequest request, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/request-dialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RequestDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setRequest(request);

            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}