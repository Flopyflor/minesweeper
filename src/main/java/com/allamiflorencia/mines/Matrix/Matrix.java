/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.allamiflorencia.mines.Matrix;

/**
 *
 * @author flopy
 * 
 * Use Reference
 * 
 * This matrix has the methods needed to be built. It knows about
 * bombs, nearby bombs, hidden or not status, flagged or not status.
 */

public class Matrix {
    private int[][] matrix;
    private int bombs;
    private int length;
    private int flag_count;
    
    public int get_bombs() {
        return this.bombs;
    }
    
    public int get_length() {
        return this.length;
    }
    
    public int get_cell(int x, int y) {
        return this.matrix[y][x];
    }
    
    private int[][] put_bombs() {
        this.matrix = new int[this.length][this.length];
        
        for (int i = 0; i < this.bombs; i ++) {
            boolean set = false;
            while (!set) {
                int pos_x = (int) Math.floor(Math.random() * this.length);
                int pos_y = (int) Math.floor(Math.random() * this.length);
                
                if (!this.is_bomb(pos_x, pos_y)) {
                    this.matrix[pos_y][pos_x] = Reference.BOMB;
                    set = true;
                }
            }
        }
        
        return matrix;
    }
    
    //debugging func
    public void print_matrix() {
        for (int i = 0; i < this.length; i++){
            for (int j = 0; j < this.length; j++){
                System.out.print(this.matrix[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
    
    private int calculate_number_in(int pos_x, int pos_y){
        //calculates the number of surrounding bombs
        if (this.is_bomb(pos_x, pos_y)) {
            return Reference.BOMB;
        }
        int count = 0;
        for (int y = pos_y - 1; y <= pos_y + 1; y ++) {
            for (int x = pos_x - 1; x <= pos_x + 1; x ++) {
                if (x >= 0 && x < this.length &&
                    y >= 0 && y < this.length &&
                    !(x == pos_x && y == pos_y)){
                    
                    if (this.is_bomb(x, y)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public int get_surrounding_flags(int pos_x, int pos_y) {
        int count = 0;
        for (int y = pos_y - 1; y <= pos_y + 1; y ++) {
            for (int x = pos_x - 1; x <= pos_x + 1; x ++) {
                if (x >= 0 && x < this.length &&
                    y >= 0 && y < this.length &&
                    !(x == pos_x && y == pos_y)){
                    
                    if (this.is_flagged(x, y)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    public boolean is_undiscovered_bomb(int pos_x, int pos_y){
        return this.matrix[pos_y][pos_x] == Reference.BOMB + Reference.HIDDEN;
    }
    
    public boolean is_bomb(int pos_x, int pos_y) {
        return this.matrix[pos_y][pos_x] == Reference.BOMB || 
               this.matrix[pos_y][pos_x] == Reference.BOMB + Reference.HIDDEN ||
               this.matrix[pos_y][pos_x] == Reference.BOMB + Reference.FLAGGED;
    }

    public boolean is_visible_bomb(int x, int y) {
        return this.matrix[y][x] == Reference.BOMB;
    }

    public boolean is_visible_empty(int x, int y) {
        return this.matrix[y][x] == Reference.EMPTY;
    }

    public boolean is_hidden(int x, int y) {
        return Reference.HIDDEN <= this.matrix[y][x] &&
               Reference.FLAGGED > this.matrix[y][x];
    }

    public boolean is_flagged(int x, int y) {
        return Reference.FLAGGED <= this.matrix[y][x];
    }

    public boolean is_visible(int x, int y) {
        return this.matrix[y][x] < Reference.HIDDEN;
    }
    
    private void fill_matrix() {
        for (int x = 0; x < this.length; x++) {
            for (int y = 0; y < this.length; y++) {
                this.matrix[y][x] = this.calculate_number_in(x, y) + Reference.HIDDEN;
            }
        }
    }

    public void prepare_for_play(int length, int bombs) {
        this.bombs = bombs;
        this.length = length;
        this.flag_count = 0;
        this.put_bombs();
        this.fill_matrix();
    }

    public void reveal_cell(int x, int y) {
        if (this.is_hidden(x, y)) {
            this.matrix[y][x] -= Reference.HIDDEN;
        }
    }

    public void flag_cell(int x, int y) {
        if (this.is_hidden(x, y)) {
            this.matrix[y][x] += Reference.FLAGGED - Reference.HIDDEN;
            this.flag_count += 1;
        }
    }

    public void unflag_cell(int x, int y) {
        if (this.is_flagged(x, y)) {
            this.matrix[y][x] += Reference.HIDDEN - Reference.FLAGGED;
            this.flag_count -= 1;
        }
    }

    public boolean finished() {
        //return true if you won the game
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.length; j++) {
                if (this.is_hidden(i, j) && !this.is_bomb(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int get_flag_count(){
        return this.flag_count;
    }
    
    public boolean is_within_bounds(int x, int y) {
        return 0 <= x && x < this.length && 0 <= y && y < this.length;
    }
}
