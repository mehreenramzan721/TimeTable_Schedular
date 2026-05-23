package com.timetable.ui;

import com.timetable.data.DataManager;
import com.timetable.scheduler.TimetableGenerator;
import com.timetable.models.Timetable;
import com.timetable.ui.views.TimetableView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Timetable currentTimetable;
    private BorderPane root;
    private com.timetable.ui.views.GenerateView generateView;

    @Override
    public void start(Stage primaryStage) {
        // Load data on startup
        DataManager.loadData();

        primaryStage.setTitle("Clash-Free Timetable Generator");

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f1f8;");


        // Sidebar for navigation
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #004d40;");


        sidebar.setPrefWidth(200);

        Button btnManage = createSidebarButton("Manage Data");
        btnManage.setOnAction(e -> showDataEntryView());

        Button btnGenerate = createSidebarButton("Generate Timetable");
        btnGenerate.setOnAction(e -> showGenerateView());

        Button btnView = createSidebarButton("View Timetable");
        btnView.setOnAction(e -> showTimetableView());

        sidebar.getChildren().addAll(btnManage, btnGenerate, btnView);
        root.setLeft(sidebar);

        // Center content area
        Label welcomeLabel = new Label("Welcome to Clash-Free Timetable Generator");
        welcomeLabel.setStyle("-fx-font-size: 24px;");
        root.setCenter(welcomeLabel);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #26a69a; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #00796b; -fx-text-fill: white; -fx-font-size: 14px;"));



        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));
        return btn;
    }

    private void showGenerateView() {
        if (generateView == null) {
            generateView = new com.timetable.ui.views.GenerateView(DataManager.getCurrentData());
        }
        root.setCenter(generateView.getView());
    }

    private void showTimetableView() {
        // Get the timetable from the generate view if it exists
        if (generateView != null && generateView.getGeneratedTimetable() != null) {
            currentTimetable = generateView.getGeneratedTimetable();
        }

        if (currentTimetable == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please generate a timetable first.");
            alert.showAndWait();
            return;
        }
        com.timetable.ui.views.TimetableView view = new com.timetable.ui.views.TimetableView(currentTimetable, DataManager.getCurrentData());
        root.setCenter(view.getView());
    }

    private void showDataEntryView() {
        com.timetable.ui.views.DataEntryView view = new com.timetable.ui.views.DataEntryView(DataManager.getCurrentData());
        root.setCenter(view.getView());
    }
}
