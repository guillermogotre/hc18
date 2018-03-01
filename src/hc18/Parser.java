/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hc18;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author jose
 */
public class Parser {
    Problema problema;
    String fich_in;
    
    public Parser(String fich_in){
        this.fich_in = fich_in;
    }
    
    public Problema parse() throws FileNotFoundException{
        //  rows, columns, n_vehicles, n_rides, bonus, n_steps
        int rows, columns, n_vehicles, n_rides, bonus, n_steps;
        
        Scanner scanner = new Scanner(new File(fich_in));
        rows = scanner.nextInt();
        columns = scanner.nextInt();
        n_vehicles = scanner.nextInt();
        n_rides = scanner.nextInt();
        bonus = scanner.nextInt();
        n_steps = scanner.nextInt();
        
        // rides_from_to[][], rides_start_finish[][]
        int rides_from_to[][], rides_start_finish[][];

        rides_from_to = new int[n_rides][4];
        rides_start_finish = new int[n_rides][2];
        
        for (int i = 0; i < n_rides; i++) {
            rides_from_to[i][0] = scanner.nextInt();
            rides_from_to[i][1] = scanner.nextInt();
            rides_from_to[i][2] = scanner.nextInt();
            rides_from_to[i][3] = scanner.nextInt();
            rides_start_finish[i][0] = scanner.nextInt();
            rides_start_finish[i][1] = scanner.nextInt();
        }
        
        /*
        int rows, int columns, int n_vehicles, int n_rides, int bonus, int n_steps,
                    int rides_from_to[][], int rides_start_finish[][]
        */
        
        problema = new Problema(rows,columns,n_vehicles,n_rides,bonus,n_steps,rides_from_to,rides_start_finish);
        return problema;
    }

}
