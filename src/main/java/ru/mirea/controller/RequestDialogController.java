package ru.mirea.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mirea.model.RequestStatus;
import ru.mirea.model.WasteRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RequestDialogController {

    @FXML
    private ComboBox<Integer> clientComboBox;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField volumeField;
    @FXML
    private ComboBox<RequestStatus> statusComboBox;

    private Stage dialogStage;
    private WasteRequest request;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        clientComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        typeComboBox.setItems(FXCollections.observableArrayList("Бытовой", "Строительный", "Опасный"));
        statusComboBox.setItems(FXCollections.observableArrayList(RequestStatus.values()));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setRequest(WasteRequest request) {
        this.request = request;
        if (request.getId() != 0) {
            clientComboBox.setValue(request.getClientId());
            addressField.setText(request.getAddress());
            typeComboBox.setValue(request.getWasteType());
            volumeField.setText(String.valueOf(request.getVolumeM3()));
            statusComboBox.setValue(request.getStatus());
        } else {
            statusComboBox.setValue(RequestStatus.NEW);
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            request.setClientId(clientComboBox.getValue());
            request.setAddress(addressField.getText());
            request.setWasteType(typeComboBox.getValue());
            request.setVolumeM3(Double.parseDouble(volumeField.getText().replace(",", ".")));
            request.setStatus(statusComboBox.getValue());
            
            if (request.getId() == 0) {
                request.setCreatedAt(LocalDateTime.now());
                request.setScheduledDate(LocalDate.now().plusDays(1));
            }
            
            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (clientComboBox.getValue() == null) {
            errorMessage += "Не выбран клиент!\n";
        }
        if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
            errorMessage += "Поле адреса не может быть пустым!\n";
        }
        if (typeComboBox.getValue() == null) {
            errorMessage += "Не выбран тип отходов!\n";
        }
        if (statusComboBox.getValue() == null) {
            errorMessage += "Не выбран статус!\n";
        }
        if (volumeField.getText() == null || volumeField.getText().trim().isEmpty()) {
            errorMessage += "Поле объема не может быть пустым!\n";
        } else {
            try {
                double v = Double.parseDouble(volumeField.getText().replace(",", "."));
                if (v <= 0) {
                    errorMessage += "Объем должен быть больше нуля!\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "Объем должен быть числовым значением!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Ошибка валидации");
            alert.setHeaderText("Исправьте некорректные поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}