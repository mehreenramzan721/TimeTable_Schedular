package com.timetable.ui.views;

import com.timetable.data.AppData;
import com.timetable.data.DataManager;
import com.timetable.models.CourseAssignment;
import com.timetable.models.RepeaterStudent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class DataEntryView {

    private AppData appData;
    private TabPane tabPane;
    private TableView<CourseAssignment> assignmentTable;
    private TableView<RepeaterStudent> repeaterTable;

    public DataEntryView(AppData appData) {
        this.appData = appData;
        initializeView();
    }

    private void initializeView() {
        tabPane = new TabPane();
        
        Tab assignmentTab = new Tab("Batch Courses", createAssignmentView());
        assignmentTab.setClosable(false);
        
        Tab repeaterTab = new Tab("Repeater Students", createRepeaterView());
        repeaterTab.setClosable(false);
        
        tabPane.getTabs().addAll(assignmentTab, repeaterTab);
    }

    private VBox createAssignmentView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: #f4f1f8;");

        Label title = new Label("Register Courses for Batches");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField courseField = new TextField();
        courseField.setPromptText("e.g. Data Structures");
        
        TextField teacherField = new TextField();
        teacherField.setPromptText("e.g. Dr. Smith");
        
        TextField labTeacherField = new TextField();
        labTeacherField.setPromptText("e.g. Engr. Bilal");
        labTeacherField.setDisable(true); // default disabled

        TextField batchField = new TextField();
        batchField.setPromptText("e.g. SP25-BCS");

        ComboBox<String> sectionCombo = new ComboBox<>();
        sectionCombo.getItems().addAll("A", "B", "C", "D", "E");
        sectionCombo.getSelectionModel().selectFirst();
        
        ComboBox<Integer> creditsCombo = new ComboBox<>();
        creditsCombo.getItems().addAll(2, 3, 4);
        creditsCombo.getSelectionModel().selectFirst();

        CheckBox labCheck = new CheckBox("Requires Lab");
        labCheck.setOnAction(e -> labTeacherField.setDisable(!labCheck.isSelected()));

        form.add(new Label("Course Name:"), 0, 0);
        form.add(courseField, 1, 0);
        form.add(new Label("Theory Teacher:"), 0, 1);
        form.add(teacherField, 1, 1);
        form.add(labCheck, 0, 2);
        form.add(new Label("Lab Teacher:"), 0, 3);
        form.add(labTeacherField, 1, 3);
        form.add(new Label("Batch Name:"), 0, 4);
        form.add(batchField, 1, 4);
        form.add(new Label("Section:"), 0, 5);
        form.add(sectionCombo, 1, 5);
        form.add(new Label("Credit Hours:"), 0, 6);
        form.add(creditsCombo, 1, 6);

        Button btnAdd = new Button("Add Course");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        
        btnAdd.setOnAction(e -> {
            String course = courseField.getText().trim();
            String teacher = teacherField.getText().trim();
            String labTeacher = labTeacherField.getText().trim();
            String batch = batchField.getText().trim();
            String section = sectionCombo.getValue();
            Integer credits = creditsCombo.getValue();
            boolean lab = labCheck.isSelected();

            if (course.isEmpty() || teacher.isEmpty() || batch.isEmpty() || (lab && labTeacher.isEmpty())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields.");
                return;
            }

            CourseAssignment assignment = new CourseAssignment(course, teacher, labTeacher, batch, section, credits, lab);
            appData.getAssignments().add(assignment);
            DataManager.saveData();
            refreshAssignmentTable();
            
            courseField.clear();
            teacherField.clear();
            labTeacherField.clear();
            batchField.clear();
            labCheck.setSelected(false);
            labTeacherField.setDisable(true);
        });

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnDelete.setOnAction(e -> {
            CourseAssignment selected = assignmentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                appData.getAssignments().remove(selected);
                DataManager.saveData();
                refreshAssignmentTable();
            }
        });

        HBox buttons = new HBox(10, btnAdd, btnDelete);

        assignmentTable = new TableView<>();
        TableColumn<CourseAssignment, String> colCourse = new TableColumn<>("Course Name");
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        
        TableColumn<CourseAssignment, String> colTeacher = new TableColumn<>("Theory Teacher");
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        
        TableColumn<CourseAssignment, String> colLabTeacher = new TableColumn<>("Lab Teacher");
        colLabTeacher.setCellValueFactory(new PropertyValueFactory<>("labTeacherName"));

        TableColumn<CourseAssignment, String> colBatch = new TableColumn<>("Batch");
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchName"));

        TableColumn<CourseAssignment, String> colSection = new TableColumn<>("Section");
        colSection.setCellValueFactory(new PropertyValueFactory<>("sectionName"));
        
        TableColumn<CourseAssignment, Integer> colCredits = new TableColumn<>("Credits");
        colCredits.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        
        TableColumn<CourseAssignment, Boolean> colLab = new TableColumn<>("Has Lab");
        colLab.setCellValueFactory(new PropertyValueFactory<>("requiresLab"));

        assignmentTable.getColumns().addAll(colCourse, colTeacher, colLabTeacher, colBatch, colSection, colCredits, colLab);
        refreshAssignmentTable();

        view.getChildren().addAll(title, form, buttons, assignmentTable);
        return view;
    }

    private VBox createRepeaterView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: #f4f1f8;");

        Label title = new Label("Manage Repeater Students");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        
        TextField batchField = new TextField();
        batchField.setPromptText("e.g. SP23-BCS");

        TextField sectionField = new TextField();
        sectionField.setPromptText("e.g. C");

        TextField coursesField = new TextField();
        coursesField.setPromptText("e.g. Data Structures, Algorithms (comma separated)");

        form.add(new Label("Student Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Original Batch:"), 0, 1);
        form.add(batchField, 1, 1);
        form.add(new Label("Original Section:"), 0, 2);
        form.add(sectionField, 1, 2);
        form.add(new Label("Courses To Repeat:"), 0, 3);
        form.add(coursesField, 1, 3);

        Button btnAdd = new Button("Add Repeater");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        
        btnAdd.setOnAction(e -> {
            String name = nameField.getText().trim();
            String batch = batchField.getText().trim();
            String section = sectionField.getText().trim();
            String courses = coursesField.getText().trim();

            if (name.isEmpty() || batch.isEmpty() || section.isEmpty() || courses.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
                return;
            }

            RepeaterStudent student = new RepeaterStudent(name, batch, section);
            Arrays.stream(courses.split(",")).map(String::trim).forEach(student.getCoursesToRepeat()::add);
            
            appData.getRepeaterStudents().add(student);
            DataManager.saveData();
            refreshRepeaterTable();
            
            nameField.clear();
            batchField.clear();
            sectionField.clear();
            coursesField.clear();
        });

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15 8 15; -fx-cursor: hand;"));
        btnDelete.setOnAction(e -> {
            RepeaterStudent selected = repeaterTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                appData.getRepeaterStudents().remove(selected);
                DataManager.saveData();
                refreshRepeaterTable();
            }
        });

        HBox buttons = new HBox(10, btnAdd, btnDelete);

        repeaterTable = new TableView<>();
        TableColumn<RepeaterStudent, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        
        TableColumn<RepeaterStudent, String> colBatch = new TableColumn<>("Original Batch");
        colBatch.setCellValueFactory(new PropertyValueFactory<>("originalBatch"));
        
        TableColumn<RepeaterStudent, String> colSection = new TableColumn<>("Original Section");
        colSection.setCellValueFactory(new PropertyValueFactory<>("originalSection"));
        
        TableColumn<RepeaterStudent, String> colCourses = new TableColumn<>("Courses to Repeat");
        colCourses.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.join(", ", data.getValue().getCoursesToRepeat())));

        repeaterTable.getColumns().addAll(colName, colBatch, colSection, colCourses);
        refreshRepeaterTable();

        view.getChildren().addAll(title, form, buttons, repeaterTable);
        return view;
    }

    private void refreshAssignmentTable() {
        ObservableList<CourseAssignment> items = FXCollections.observableArrayList(appData.getAssignments());
        assignmentTable.setItems(items);
    }

    private void refreshRepeaterTable() {
        ObservableList<RepeaterStudent> items = FXCollections.observableArrayList(appData.getRepeaterStudents());
        repeaterTable.setItems(items);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public TabPane getView() {
        return tabPane;
    }
}
