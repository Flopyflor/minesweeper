/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.allamiflorencia.mines.Controller;

import com.allamiflorencia.mines.Matrix.Matrix;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author flopy
 */
public class PrimaryController {
    @FXML GridPane mainGrid;
    
    private final double size = 20;
    private final Color FLAG = Color.RED;
    private final Color EMPTY = Color.GRAY;
    private final Color NUMBER_BG = Color.LIGHTGRAY;
    private final String BOMB = "*";
    private boolean playing;
    private final Matrix matrix = new Matrix();
    
    
    @FXML
    protected void setUpGrid(ActionEvent event){
        int lenght = 10;
        this.start_game(lenght, 10);
        
        
        for (int i = 0; i < lenght; i++){
            for (int j = 0; j < lenght; j++) {
                
                //cell pane
                StackPane cell = new StackPane();
                cell.setPrefSize(this.size, this.size);
                
                //rectangle
                Rectangle rect = new Rectangle(size, size, EMPTY);
                
                //css to pane
                cell.getStyleClass().add("cell");
                
                //add an empty rectangle
                cell.getChildren().add(rect);
                
                //who needs confusion- not me
                int column = i;
                int row = j;
                
                //adding click events to pane
                cell.setOnMouseClicked((MouseEvent e) -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        this.reveal_cell(column, row);
                        
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        this.toggle_flag(column, row);
                    }
                    
                });
                
                //adding the cell to the grid
                mainGrid.add(cell, column, row);
            }
        }
    }
    
    public void start_game(int length, int bombs) {
        this.playing = true;
        this.matrix.prepare_for_play(length, bombs);
    }

    public boolean is_playing(){
        return this.playing;
    }

    public void check_progress() {
        if (this.matrix.no_hidden_cells()) {
            Alert alert = new Alert(AlertType.INFORMATION, "Ganaste!");
            alert.showAndWait()
               .filter(response -> response == ButtonType.OK);
            this.display_full_board();
            this.playing = false;
        }
    }

    public void lost_game() {
        Alert alert = new Alert(AlertType.INFORMATION, "Perdiste");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK);
        this.display_full_board();
        this.playing = false;
    }
    
    //TODO
    public void display_full_board() {
        //hacer que todos los botones muestren sus valores
    }

    public void reveal_cell(int x, int y) {
        //if the input is out of bounds
        if (!this.matrix.is_within_bounds(x, y)) {
            return;
        }
        
        //if it's already flagged
        if (this.matrix.is_flagged(x, y)) {
            return;
        }
        
        //a "complete" number
        if (this.matrix.is_visible(x, y) 
                && !this.matrix.is_visible_empty(x, y)
                && this.matrix.get_surrounding_flags(x, y) == this.matrix.get_cell(x, y)){ 
            this.reveal_surrounding(x, y);
            return;
        }
        
        //just a number
        if (this.matrix.is_visible(x, y)) {
            return;
        }

        //now comes the revealing in matrix
        this.matrix.reveal_cell(x, y);
        
        //reveal cell on gui
        //make label
        Label cell = new Label(this.get_cell(x, y));
        cell.setPrefSize(size, size);
        
        //add css
        cell.getStyleClass().add("revealed");
        cell.getStyleClass().add("number"+this.get_cell(x, y));
        
        //take pane from grid, erease rectangle, add new rectangle and number (label)
        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, NUMBER_BG));
        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);

        //these checks happen AFTER revealing the clicked cell
        //bomb if bomb
        if (this.matrix.is_visible_bomb(x, y)) {
            this.lost_game();
        }
        
        //if visible empty reveal surrounding
        if (this.matrix.is_visible_empty(x, y)) {
            this.reveal_surrounding(x, y);
        }
        
    }

    private void reveal_surrounding(int x, int y) {
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if ((i != x || j != y)
                        && this.matrix.is_within_bounds(i, j)
                        && !this.matrix.is_visible(i, j)) {
                    //reveal only if it's not the one I started with and also if it isn't already visible
                    this.reveal_cell(i, j);
                }
            }
        }
    }
    

    public void toggle_flag(int x, int y) {
        if (this.matrix.is_visible(x, y)) {
            return;
        }
        if (this.matrix.is_flagged(x, y)) {
            this.matrix.unflag_cell(x, y);
            //empty
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, EMPTY));
        } else {
            this.matrix.flag_cell(x, y);
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, FLAG));
        }
    }

    public void complete_cell(int x, int y) {
        if (this.matrix.is_visible(x, y) && this.matrix.get_cell(x, y) == this.matrix.get_surrounding_flags(x, y)) {
            this.reveal_surrounding(x, y);
        }
    }
    
    public String get_cell(int x, int y){
        if (this.matrix.is_flagged(x, y) && this.matrix.is_bomb(x, y)) {
            return "%";
        } else if (this.matrix.is_bomb(x, y)) {
           return this.BOMB;
        } else {
            return Integer.toString(this.matrix.get_cell(x, y));
        }
    }
        
    
}
