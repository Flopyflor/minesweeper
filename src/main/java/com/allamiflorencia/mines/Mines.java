/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.allamiflorencia.mines;
import com.allamiflorencia.mines.Controller.PrimaryController;
import com.allamiflorencia.mines.View.View;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author flopy
 * 
 */
public class Mines extends Application {
    
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {              
        scene = new Scene(loadFXML("primary"), 640, 480);
        scene.getStylesheets().add(Mines.class.getResource("styles.css").toExternalForm());
        stage.setTitle("Buscaminas");
        stage.setScene(scene);
        
        try {
            stage.getIcons().add(new Image(Mines.class.getResource("images/icon.png").toExternalForm()));
            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.show();
        
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Mines.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void playCLI() {
        //deprecated
        
        try (Scanner sc = new Scanner(System.in)) {
            
            View game = new View();
           
            game.start_game(10, 10);
            game.display_board();
            
            
            while(game.is_playing()) {
                System.out.println("0: reveal; 1: flag; 2: complete");
                int order = sc.nextInt();
                int x = sc.nextInt();
                int y = sc.nextInt();
                
                switch (order) {
                    case 1:
                        game.toggle_flag(x, y);
                        break;
                    case 2:
                        game.complete_cell(x, y);
                        break;
                    default:
                        game.reveal_cell(x, y);
                        break;
                }
                game.check_progress();
            }
        }
        
    }
}
