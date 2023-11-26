/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.allamiflorencia.mines.Controller;

import com.allamiflorencia.mines.Mines;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 *
 * @author flopy
 */
public class PrimaryController {
    @FXML GridPane mainGrid;
    
    @FXML
    protected void setUpGrid(ActionEvent event){
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++) {
                Button btn = new Button("a");
                int column = i;
                int row = j;
                btn.setOnAction((ActionEvent e) -> {
                    System.out.println("currently pressing down on col "+column+" row "+row); 
                });
                mainGrid.add(btn, column, row);
            }
        }
    }
        
    
}
