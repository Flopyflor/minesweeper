package com.allamiflorencia.mines.View;

import com.allamiflorencia.mines.Matrix.Matrix;

public class View {
    private String FLAG = "%";
    private String EMPTY = "-";
    private String BOMB = "*";
    private boolean playing;
    private Matrix matrix = new Matrix();
    
    public void start_game(int length, int bombs) {
        this.playing = true;

        this.matrix.prepare_for_play(length, bombs);
    }

    public boolean is_playing(){
        return this.playing;
    }

    public void check_progress() {
        if (this.matrix.no_hidden_cells()) {
            System.out.println("Ganaste");
            display_full_board();
            this.playing = false;
        } else if (this.playing) {
            this.display_board();
        }
    }

    public void lost_game() {
        System.out.println("Perdiste");
        this.playing = false;
        this.display_full_board();
    }

    public void display_board() {
        for (int y = 0; y < this.matrix.get_length(); y++) {
            for (int x = 0; x < this.matrix.get_length(); x++){
                if (this.matrix.is_visible(x, y)) {
                    if (this.matrix.is_visible_bomb(x, y)) {
                        System.out.print(this.BOMB);
                    } else {
                        System.out.print(this.matrix.get_cell(x, y));
                    }
                } else if (this.matrix.is_flagged(x, y)){
                    System.out.print(FLAG);
                } else {
                    System.out.print(this.EMPTY);
                }

                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    public void display_full_board() {
        for (int y = 0; y < this.matrix.get_length(); y++) {
            for (int x = 0; x < this.matrix.get_length(); x++){
                System.out.print(this.get_cell(x, y));
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    public void reveal_cell(int x, int y) {
        if (x < 0 || x >= this.matrix.get_length() 
          ||y < 0 || y >= this.matrix.get_length()) {
            return;
        }
        
        if (this.matrix.is_visible(x, y) || this.matrix.is_flagged(x, y)) {
            return;
        }

        this.matrix.reveal_cell(x, y);
        
        if (this.matrix.is_visible_bomb(x, y)) {
            this.lost_game();
        } else if (this.matrix.is_visible_empty(x, y)) {
            this.reveal_surrounding(x, y);
        }
    }

    private void reveal_surrounding(int x, int y) {
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i != x || j != y) {
                    this.reveal_cell(i, j);
                }
            }
        }
    }

    public void toggle_flag(int x, int y) {
        if (this.matrix.is_flagged(x, y)) {
            this.matrix.unflag_cell(x, y);
        } else {
            this.matrix.flag_cell(x, y);
        }
    }

    public void complete_cell(int x, int y) {
        if (this.matrix.is_visible(x, y) && this.matrix.get_cell(x, y) == this.matrix.get_surrounding_flags(x, y)) {
            this.reveal_surrounding(x, y);
        }
    }
    
    public String get_cell(int x, int y){
        if (this.matrix.is_flagged(x, y) && this.matrix.is_bomb(x, y)) {
            return this.FLAG;
        } else if (this.matrix.is_bomb(x, y)) {
           return this.BOMB;
        } else {
            return Integer.toString(this.matrix.get_cell(x, y));
        }
    }

}
