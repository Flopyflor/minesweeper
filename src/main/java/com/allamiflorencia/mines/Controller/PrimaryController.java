/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.allamiflorencia.mines.Controller;

import com.allamiflorencia.mines.Matrix.Matrix;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author flopy
 */
public class PrimaryController {
    @FXML GridPane mainGrid;
    @FXML Label remainingBombs;
    
    private final double size = 20;
    private final Color RED = Color.RED;
    private final Color EMPTY = Color.GRAY;
    private final Color NUMBER_BG = Color.LIGHTGRAY;
    private boolean playing;
    private final Matrix matrix = new Matrix();
    
    private Image bomb_image;
    private Image flag_image;
        
    //these should come from config. obviously. TODO
    private int CONFIG_length = 10;
    private int CONFIG_bombs = 10;

    public PrimaryController() {
        try {
            FileInputStream bomb_stream = new FileInputStream(".\\images\\bomb.png");
            this.bomb_image = new Image(bomb_stream, size, size, false, true);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            FileInputStream flag_stream = new FileInputStream(".\\images\\flag.png");
            this.flag_image = new Image(flag_stream, size, size, false, true);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void update_remaining_bombs(){
        this.remainingBombs.setText("Remaining Bombs: "+(this.matrix.get_bombs()-this.matrix.get_flag_count()));
    }
    

    protected void start_game(){        
        //grid setup
        this.mainGrid.setOnMouseClicked(null);
        
        this.set_playing(true);
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
                    if(e.getButton() == MouseButton.PRIMARY && this.is_playing()){
                        this.reveal_cell(column, row);
                        this.check_win();
                        
                    } else if (e.getButton() == MouseButton.SECONDARY && this.is_playing()) {
                        this.toggle_flag(column, row);
                    }
                    
                });
                
                //adding the cell to the grid
                mainGrid.add(cell, column, row);
            }
        }
        
        //bomb count
        this.update_remaining_bombs();
    }
    
    @FXML
    protected void clickStartGame(ActionEvent event) {
        this.start_game();
    }
    
    @FXML
    protected void clickDifficulty(ActionEvent event){
        difficultyMenu();
    }
    
    /*  SOME ALTERNATIVE DIFFICULTY MENUES I EXPERIMENTED WITH.
        I DIDN'T LIKE THEM BUT, WHY THROW SEMI-USABLE STUFF?
    
    // can see the stuff. no bg (could be fixed?) no close button, dependant on
    // owner window. closes with esc. not modal
    public static void PopupControlExperiment(ActionEvent event){
        PopupControl menu = new PopupControl();
        
        VBox comp = new VBox();
        TextField nameField = new TextField("Name");
        TextField phoneNumber = new TextField("Phone Number");
        comp.getChildren().add(nameField);
        comp.getChildren().add(phoneNumber);
        
        menu.getScene().setRoot(comp);
        menu.show(((Button)event.getSource()).getScene().getWindow());
    }
    
    //blue option bg. dependant. not modal. hides when out of focus. 
    public static void ContextMenuExperiment(ActionEvent event){
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(new MenuItem("hi"));
        menu.getItems().add(new MenuItem("hello"));
        
        VBox comp = new VBox();
        TextField nameField = new TextField("Name");
        TextField phoneNumber = new TextField("Phone Number");
        comp.getChildren().add(nameField);
        comp.getChildren().add(phoneNumber);
        
        menu.getItems().add(new CustomMenuItem(comp));
        menu.show(((Button)event.getSource()).getScene().getWindow());
    }
    
    //dependant, not modal, no bg, closes with esc
    public static void PopupExperiment(ActionEvent event){
        Popup menu = new Popup();
        menu.getContent().add(new Pane());
        ((Pane) menu.getContent().get(0)).getChildren().add(new TextField("Size"));
        menu.getContent().add(new TextField("Mines"));
        menu.show(((Button)event.getSource()).getScene().getWindow());
    }
    */
    
    public void difficultyMenu(){
        Stage newStage = new Stage(); //stage     
        VBox container = new VBox();
        container.setPadding(new Insets(20, 0, 10, 10));
        
        //grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(20);
        
        //creating inputs
        //size
        Label sizeLabel = new Label("Size:");
        TextField sizeInput = new TextField("10");
        grid.add(sizeLabel, 0, 0);
        grid.add(sizeInput, 1, 0);
        
        //bombs
        Label bombsLabel = new Label("Bombs:");
        TextField bombsInput = new TextField("10");
        grid.add(bombsLabel, 0, 1);
        grid.add(bombsInput, 1, 1);
        
        container.getChildren().add(grid);
        
        //adding controller
        final EventHandler<KeyEvent> controller = (final KeyEvent e) -> {
            //problematic key and its ascii: esc 27 backspace 8 enter 13
            int ascii = ( int)e.getCharacter().charAt(0);
            
            if((!(ascii == 27 || ascii == 8 || ascii == 13) && !e.getCharacter().matches("\\d"))
                    || ((TextField) e.getTarget()).getCharacters().length() > 3){
                ((TextField) e.getTarget()).deletePreviousChar();
            }
        };
        
        sizeInput.setOnKeyTyped(controller);
        bombsInput.setOnKeyTyped(controller);
        
        //add button
        Button btn = new Button("Set");
        btn.setOnMouseClicked((MouseEvent e)->{
            int newSize = Integer.parseInt(sizeInput.getCharacters().toString());
            int newBombs = Integer.parseInt(bombsInput.getCharacters().toString());
            
            this.CONFIG_length = Math.min(Math.max(3,newSize), 30);
            this.CONFIG_bombs = Math.min(Math.max(1, newBombs), this.CONFIG_length * this.CONFIG_length -1);
            
            this.start_game();
            newStage.close();
        });
        
        container.getChildren().add(btn);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(30);
        
        //setting the container in the scene in the stage
        newStage.initModality(Modality.APPLICATION_MODAL);
        Scene stageScene = new Scene(container, 300, 200);
        newStage.setScene(stageScene);
        newStage.show();
    }

    public boolean is_playing(){
        return this.playing;
    }
    
    public void set_playing(boolean val) {
        this.playing = val;
    }
    
    
    //weird bug - la primera vez que lo hago, tarda. dps ya no
    public void await_new_game(){
        this.display_full_board();
        this.mainGrid.setOnMouseClicked((MouseEvent e) -> {
            //el this.playing no parece afectar mucho pero. por las dudas??
            if(this.playing && e.getButton() == MouseButton.PRIMARY) {
                //this bit activates when i press the alert
                this.set_playing(false);
            }
            else if(!this.playing && e.getButton() == MouseButton.PRIMARY){
                //this bit is when I actually press the grid
                this.start_game();
            }
         });

    }

    public void check_win() {
        if (this.matrix.finished() && this.is_playing()) {
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
    
    public void display_full_board() {
        //hacer que todos los botones muestren sus valores
        for (int x = 0; x < this.CONFIG_length; x++){
            for (int y = 0; y < this.CONFIG_length; y++){
                if (this.matrix.is_undiscovered_bomb(x, y)){
                    if (this.bomb_image != null) {
                        ImageView cell = new ImageView(this.bomb_image);
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, EMPTY));
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);
                    } else {
                        Rectangle cell = new Rectangle(size/2, size/2, RED);
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, EMPTY));
                        ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);
                    }
                }
            }
        }
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
            //TODO this will be an image, if it exists
            
            if (this.bomb_image != null) {
                ImageView cell = new ImageView(this.bomb_image);
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, NUMBER_BG));
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);
            } else {
                Rectangle cell = new Rectangle(size/2, size/2, RED);
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, NUMBER_BG));
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(cell);
            }
            
            
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
            if (this.flag_image != null){
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().add(new Rectangle(size, size, EMPTY));
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren()
                        .add(new ImageView(this.flag_image));
            } else {
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren().clear();
                ((StackPane) this.mainGrid.getChildren().get(this.matrix.get_length()*x+y)).getChildren()
                        .add(new Rectangle(size, size, RED));
            }
        }
        this.update_remaining_bombs();
    }
}
