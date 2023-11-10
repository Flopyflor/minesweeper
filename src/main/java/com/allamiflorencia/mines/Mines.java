/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.allamiflorencia.mines;
import com.allamiflorencia.mines.View.View;
import java.util.Scanner;

/**
 *
 * @author flopy
 * 
 * cosas que faltan:
 * - mejores estructuras?
 * - gui, obv
 */
public class Mines {

    public static void main(String[] args) {
        
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
                    case 1 -> game.toggle_flag(x, y);
                    case 2 -> game.complete_cell(x, y);
                    default -> game.reveal_cell(x, y);
                }
                game.check_progress();
            }
        }
        
    }
}
