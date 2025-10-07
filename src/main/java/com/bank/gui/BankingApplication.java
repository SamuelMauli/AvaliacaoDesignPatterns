package com.bank.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principal da aplicação JavaFX para o Sistema Bancário.
 * Esta classe estende {@code Application} e é responsável por inicializar
 * a interface gráfica do usuário.
 *
 * <p>A aplicação segue o padrão **Model-View-Controller (MVC)**, onde:
 * <ul>
 *     <li>**Model**: Representado pelas classes de negócio existentes (Account, BankingFacade, etc.)</li>
 *     <li>**View**: Arquivos FXML que definem a interface do usuário</li>
 *     <li>**Controller**: Classes que gerenciam a interação entre View e Model</li>
 * </ul>
 *
 * <p>Este design promove a **separação de responsabilidades** e facilita a manutenção
 * e teste da aplicação, aderindo aos princípios SOLID já aplicados no sistema.
 */
public class BankingApplication extends Application {

    /**
     * Método principal de inicialização da aplicação JavaFX.
     * Carrega a tela de login como ponto de entrada da aplicação.
     *
     * @param stage O palco principal da aplicação JavaFX.
     * @throws IOException Se houver erro ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML da tela de login
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        // Configurações da janela principal
        stage.setTitle("Sistema Bancário - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        
        // Adiciona ícone da aplicação (se disponível)
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/bank-icon.png")));
        } catch (Exception e) {
            // Ícone não encontrado, continua sem ícone
            System.out.println("Ícone da aplicação não encontrado, continuando sem ícone.");
        }
        
        stage.show();
    }

    /**
     * Método principal para iniciar a aplicação.
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        launch();
    }
}
