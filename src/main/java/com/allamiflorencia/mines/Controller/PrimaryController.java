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
    private boolean playing;
    private final Matrix matrix = new Matrix();
    
    //these should come from config. obviously. TODO
    private int CONFIG_length = 10;
    private int CONFIG_bombs = 10;
    
    

    protected void start_game(){
        this.mainGrid.setOnMouseClicked(null);
        
        this.playing = true;
        this.mainGrid.getChildren().clear();
        this.matrix.prepare_for_play(CONFIG_length, CONFIG_bombs);
        
        
        for (int i = 0; i < CONFIG_length; i++){
            for (int j = 0; j < CONFIG_length; j++) {
                
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
    
    @FXML
    protected void clickStartGame(ActionEvent event) {
        this.start_game();
    }

    public boolean is_playing(){
        return this.playing;
    }
    
    
    //weird bug - la primera vez que lo hago, tarda. dps ya no
    public void await_new_game(){
       this.display_full_board();
        this.mainGrid.setOnMouseClicked((MouseEvent e) -> {
            //el this.playing no parece afectar mucho pero. por las dudas??
            if(this.playing && e.getButton() == MouseButton.PRIMARY) {
                this.playing = false;
            }
            else if(!this.playing && e.getButton() == MouseButton.PRIMARY){
                this.start_game();
            }
         });

    }

    public void check_win() {
        if (this.matrix.finished()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Ganaste");
            alert.setHeaderText("Ganaste! :)");
            alert.showAndWait()
               .filter(response -> response == ButtonType.OK);
            this.display_full_board();
            this.await_new_game();
        }
    }

    public void lost_game() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Perdiste");
        alert.setHeaderText("Perdiste :(");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK);
        this.display_full_board();
        this.await_new_game();
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
        //if I reveal a bomb
        if (this.matrix.is_bomb(x, y)) {
            Rectangle cell = new Rectangle(size/2, size/2, FLAG);
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, NUMBER_BG));
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);
            
            this.lost_game();
        } else {
            //make label
            Label cell = new Label(Integer.toString(this.matrix.get_cell(x, y)));
            cell.setPrefSize(size, size);

            //add css
            cell.getStyleClass().add("revealed");
            cell.getStyleClass().add("number"+Integer.toString(this.matrix.get_cell(x, y)));

            //take pane from grid, erease rectangle, add new rectangle and number (label)
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, NUMBER_BG));
            ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);

            //these checks happen AFTER revealing the clicked cell
            //if visible empty reveal surrounding
            if (this.matrix.is_visible_empty(x, y)) {
                this.reveal_surrounding(x, y);
            }

            //if I really revealed a cell, could I have won...?
            this.check_win();
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
}
