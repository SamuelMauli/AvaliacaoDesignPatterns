package com.bank.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class BankingApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Sistema Bancário");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/bank-icon.png")));
        } catch (Exception e) {
            System.out.println("Ícone da aplicação não encontrado, continuando sem ícone.");
        }

        showLoginView();
    }

    public static void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("/fxml/LoginView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("/fxml/RegisterView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showDashboardView(com.bank.model.User loggedInUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("/fxml/DashboardView.fxml"));
        Parent root = fxmlLoader.load();
        DashboardController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setLoggedInUser(loggedInUser);
        }
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

