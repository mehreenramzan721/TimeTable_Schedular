package com.timetable.ui.views;

import com.timetable.data.AppData;
import com.timetable.models.ScheduledClass;
import com.timetable.models.Timetable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableView {

    private Timetable timetable;
    private AppData appData;
    private VBox view;
    private TableView<Map<String, Object>> table;
    private TextField batchFilter;
    private ComboBox<String> sectionFilter;
    private TextField teacherFilter;

    public TimetableView(Timetable timetable, AppData appData) {
        this.timetable = timetable;
        this.appData = appData;
        initializeView();
    }

    private void initializeView() {
        view = new VBox(15);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: #f4f1f8;");

        Label title = new Label("Generated Timetable Grid");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Filter Controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        batchFilter = new TextField();
        batchFilter.setPromptText("Batch/Student Name");
        
        sectionFilter = new ComboBox<>();
        sectionFilter.getItems().addAll("All", "A", "B", "C", "D", "E", "Repeater");
        sectionFilter.getSelectionModel().select("All");

        teacherFilter = new TextField();
        teacherFilter.setPromptText("Teacher Name");

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnSearch.setOnMouseEntered(e -> btnSearch.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnSearch.setOnMouseExited(e -> btnSearch.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnSearch.setOnAction(e -> applyFilter());
        
        Button btnClear = new Button("Clear Filter");
        btnClear.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnClear.setOnMouseEntered(e -> btnClear.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnClear.setOnMouseExited(e -> btnClear.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnClear.setOnAction(e -> {
            batchFilter.clear();
            teacherFilter.clear();
            applyFilter();
        });

        filterBox.getChildren().addAll(
            new Label("Batch:"), batchFilter, 
            new Label("Section:"), sectionFilter, 
            new Label("  OR  Teacher:"), teacherFilter, 
            btnSearch, btnClear
        );

        table = new TableView<>();
        table.setFixedCellSize(80); // Taller cells for multiline text
        setupTableColumns();

        view.getChildren().addAll(title, filterBox, table);
        
        // Show everything by default initially
        applyFilter();
    }

    @SuppressWarnings("unchecked")
    private void setupTableColumns() {
        String[] columns = {"Timeslot", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        for (String colName : columns) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(colName);
            
            column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get(colName)));
            column.setPrefWidth(160);

            // Custom Cell Renderer for colors
            column.setCellFactory(tc -> new TableCell<Map<String, Object>, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        setStyle("");
                    } else {
                        if (item instanceof String) {
                            // This is the Timeslot column
                            setText((String) item);
                            setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-background-color: #ecf0f1;");
                            setGraphic(null);
                        } else if (item instanceof List) {
                            // These are the classes for that day
                            List<ScheduledClass> classes = (List<ScheduledClass>) item;
                            if (classes.isEmpty()) {
                                setGraphic(null);
                                setText(null);
                                setStyle("");
                            } else {
                                VBox box = new VBox(5);
                                box.setAlignment(Pos.CENTER);
                                box.setPadding(new Insets(5));
                                
                                // We assume mostly 1 class per slot per section, if overlap happens we show both
                                ScheduledClass sc = classes.get(0);
                                String courseName = sc.getSession().getSubject().getName();
                                String roomName = sc.getRoom().getName();

                                Label lblCourse = new Label(courseName);
                                lblCourse.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                                
                                String subText;
                                if (!teacherFilter.getText().trim().isEmpty()) {
                                    subText = sc.getSession().getSection().getName();
                                } else {
                                    subText = sc.getSession().getTeacher().getName();
                                }
                                
                                Label lblSubText = new Label(subText + " (" + roomName + ")");
                                lblSubText.setStyle("-fx-text-fill: white;");
                                
                                box.getChildren().addAll(lblCourse, lblSubText);
                                
                                // Generate color based on course name
                                String colorHex = getColorForCourse(courseName);
                                box.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5;");
                                
                                setGraphic(box);
                                setText(null);
                                setStyle("-fx-padding: 2;"); // small padding around colored box
                            }
                        }
                    }
                }
            });
            table.getColumns().add(column);
        }
    }

    private String getColorForCourse(String courseName) {
        // List of nice aesthetic colors
        String[] colors = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f1c40f", "#e67e22", "#1abc9c", "#34495e"};
        int index = Math.abs(courseName.hashCode()) % colors.length;
        return colors[index];
    }

    private void applyFilter() {
        String batch = batchFilter.getText().trim();
        String section = sectionFilter.getValue();
        String teacher = teacherFilter.getText().trim();

        List<ScheduledClass> filteredClasses = timetable.getClasses();

        if (!teacher.isEmpty()) {
            filteredClasses = filteredClasses.stream()
                    .filter(c -> c.getSession().getTeacher().getName().equalsIgnoreCase(teacher))
                    .collect(Collectors.toList());
        } else if (!batch.isEmpty()) {
            if ("Repeater".equals(section)) {
                for (com.timetable.models.RepeaterStudent rs : timetable.getRepeaterSchedules().keySet()) {
                    if (rs.getStudentName().equalsIgnoreCase(batch)) {
                        filteredClasses = timetable.getRepeaterSchedules().get(rs);
                        break;
                    }
                }
            } else if ("All".equals(section)) {
                filteredClasses = filteredClasses.stream()
                        .filter(c -> c.getSession().getSection().getName().startsWith(batch))
                        .collect(Collectors.toList());
            } else {
                String fullSectionName = batch + "-" + section;
                filteredClasses = filteredClasses.stream()
                        .filter(c -> c.getSession().getSection().getName().equals(fullSectionName))
                        .collect(Collectors.toList());
            }
        }

        populateTable(filteredClasses);
    }

    private void populateTable(List<ScheduledClass> classes) {
        table.getItems().clear();
        
        List<String> startTimes = new ArrayList<>();
        List<String> endTimes = new ArrayList<>();
        
        int startHour = 8;
        int startMin = 30;
        
        while (startHour < 17) {
            int endHour = startHour;
            int endMin = startMin + 30;
            if (endMin >= 60) {
                endHour++;
                endMin -= 60;
            }
            startTimes.add(String.format("%02d:%02d", startHour, startMin));
            endTimes.add(String.format("%02d:%02d", endHour, endMin));
            startHour = endHour;
            startMin = endMin;
        }

        for (int i = 0; i < startTimes.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            String timeRange = startTimes.get(i) + " - " + endTimes.get(i);
            row.put("Timeslot", timeRange);

            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            for (String day : days) {
                final String currentTime = startTimes.get(i);
                final String currentDay = day;

                List<ScheduledClass> classesInSlot = classes.stream()
                        .filter(c -> c.getTimeslot().getDayOfWeek().equals(currentDay) && c.getTimeslot().getStartTime().equals(currentTime))
                        .collect(Collectors.toList());

                row.put(day, classesInSlot);
            }
            table.getItems().add(row);
        }
    }

    public VBox getView() {
        return view;
    }
}
