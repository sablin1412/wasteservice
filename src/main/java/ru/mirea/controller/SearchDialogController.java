package ru.mirea.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mirea.model.RequestStatus;

public class SearchDialogController {

    @FXML
    private TextField addressSearchField;
    @FXML
    private ComboBox<String> typeSearchBox;
    @FXML
    private ComboBox<RequestStatus> statusFilterBox;
    @FXML
    private TextField volumeFilterField;
    @FXML
    private ComboBox<String> sortBox;

    private Stage dialogStage;
    private boolean applyClicked = false;

    @FXML
    private void initialize() {
        typeSearchBox.setItems(FXCollections.observableArrayList("Любой", "Бытовой", "Строительный", "Опасный"));
        typeSearchBox.setValue("Любой");

        statusFilterBox.setItems(FXCollections.observableArrayList(RequestStatus.values()));

        sortBox.setItems(FXCollections.observableArrayList("Без сортировки", "По дате (возрастание)", "По объему (убывание)"));
        sortBox.setValue("Без сортировки");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isApplyClicked() {
        return applyClicked;
    }

    public String getAddressSearch() { return addressSearchField.getText(); }
    public String getTypeSearch() { return typeSearchBox.getValue(); }
    public RequestStatus getStatusFilter() { return statusFilterBox.getValue(); }
    public String getVolumeFilter() { return volumeFilterField.getText(); }
    public String getSortSelection() { return sortBox.getValue(); }

    @FXML
    private void handleApply() {
        applyClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleReset() {
        addressSearchField.clear();
        typeSearchBox.setValue("Любой");
        statusFilterBox.setValue(null);
        volumeFilterField.clear();
        sortBox.setValue("Без сортировки");
        applyClicked = true;
        dialogStage.close();
    }
}