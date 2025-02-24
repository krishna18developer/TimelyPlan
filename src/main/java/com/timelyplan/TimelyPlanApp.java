package com.timelyplan;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TimelyPlanApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TimelyPlanApp.class.getResource("/view/MainView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            primaryStage.setTitle("TimelyPlan - Timetable Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
} 