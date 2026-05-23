package com.timetable.ui.views;

import com.timetable.data.AppData;
import com.timetable.data.DataManager;
import com.timetable.models.ScheduledClass;
import com.timetable.models.Timetable;
import com.timetable.scheduler.TimetableGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GenerateView {

    private AppData appData;
    private VBox view;
    private Timetable generatedTimetable;
    

    private VBox statsPanel;
    private Label lblStatus;
    private Label lblTotalClasses;
    private Label lblTotalTeachers;
    private Label lblTotalRooms;
    private Label lblRepeaters;

    public GenerateView(AppData appData) {
        this.appData = appData;
        initializeView();
    }

    private void initializeView() {
        view = new VBox(20);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);
        view.setStyle("-fx-background-color: #f4f1f8;");

        Label title = new Label("Timetable Generation Engine");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Click below to run the scheduling algorithm and automatically resolve all clashes.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Button btnGenerate = new Button("Run Scheduler Engine");
        btnGenerate.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 25 12 25; -fx-cursor: hand;");
        btnGenerate.setOnMouseEntered(e -> btnGenerate.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 25 12 25; -fx-cursor: hand;"));
        btnGenerate.setOnMouseExited(e -> btnGenerate.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 25 12 25; -fx-cursor: hand;"));
        
        lblStatus = new Label("Status: Ready to Generate");
        lblStatus.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        statsPanel = new VBox(15);
        statsPanel.setAlignment(Pos.CENTER);
        statsPanel.setVisible(false);
        statsPanel.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20; -fx-background-radius: 10;");
        statsPanel.setMaxWidth(400);

        Label statsTitle = new Label("Generation Statistics");
        statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-underline: true;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        lblTotalClasses = new Label("0");
        lblTotalTeachers = new Label("0");
        lblTotalRooms = new Label("0");
        lblRepeaters = new Label("0");

        String statStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980b9;";
        lblTotalClasses.setStyle(statStyle);
        lblTotalTeachers.setStyle(statStyle);
        lblTotalRooms.setStyle(statStyle);
        lblRepeaters.setStyle(statStyle);

        grid.add(new Label("Total Classes Scheduled:"), 0, 0);
        grid.add(lblTotalClasses, 1, 0);
        
        grid.add(new Label("Total Teachers Assigned:"), 0, 1);
        grid.add(lblTotalTeachers, 1, 1);
        
        grid.add(new Label("Total Rooms Utilized:"), 0, 2);
        grid.add(lblTotalRooms, 1, 2);
        
        grid.add(new Label("Repeater Students Placed:"), 0, 3);
        grid.add(lblRepeaters, 1, 3);

        statsPanel.getChildren().addAll(statsTitle, grid);

        btnGenerate.setOnAction(e -> runGeneration());

        view.getChildren().addAll(title, subtitle, btnGenerate, lblStatus, statsPanel);
    }

    private void runGeneration() {
        lblStatus.setText("Status: Running Algorithm...");
        lblStatus.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
        statsPanel.setVisible(false);

        try {
            TimetableGenerator generator = new TimetableGenerator(appData);
            generatedTimetable = generator.generate();
            
            // Calculate stats
            // We count distinct sessions so a 1.5-hour class (3 blocks) only counts as "1 Class" in the stats.
            long totalClasses = generatedTimetable.getClasses().stream().map(ScheduledClass::getSession).distinct().count();
            long uniqueTeachers = generatedTimetable.getClasses().stream().map(c -> c.getSession().getTeacher()).distinct().count();
            long uniqueRooms = generatedTimetable.getClasses().stream().map(ScheduledClass::getRoom).distinct().count();
            int repeaters = generatedTimetable.getRepeaterSchedules().size();

            lblTotalClasses.setText(String.valueOf(totalClasses));
            lblTotalTeachers.setText(String.valueOf(uniqueTeachers));
            lblTotalRooms.setText(String.valueOf(uniqueRooms));
            lblRepeaters.setText(String.valueOf(repeaters));

            lblStatus.setText("Status: Success! 100% Clash-Free");
            lblStatus.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            statsPanel.setVisible(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Timetable Generated Successfully!");
            alert.setHeaderText(null);
            alert.showAndWait();

        } catch (Exception ex) {
            lblStatus.setText("Status: Generation Failed");
            lblStatus.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Conflict Error");
            alert.setHeaderText("Scheduling Failed");
            
            javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(ex.getMessage());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        }
    }

    public VBox getView() {
        return view;
    }

    public Timetable getGeneratedTimetable() {
        return generatedTimetable;
    }
}
