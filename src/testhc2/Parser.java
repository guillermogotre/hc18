/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testhc2;

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
        //  rows, columns, drones, turns, max_payload
        int rows, columns, n_drones, turns, max_payload;
        
        Scanner scanner = new Scanner(new File(fich_in));
        rows = scanner.nextInt();
        columns = scanner.nextInt();
        n_drones = scanner.nextInt();
        turns = scanner.nextInt();
        max_payload = scanner.nextInt();
        
        // product _types, products_weighs[]
        int n_product_types, products_weighs[];
        n_product_types = scanner.nextInt();
        
        products_weighs = new int[n_product_types];
        
        for (int i = 0; i < n_product_types; i++) {
            products_weighs[i] = scanner.nextInt();
        }
        
        // warehouses
        int n_warehouses, disponibilidad[][], ware_pos[];
        n_warehouses = scanner.nextInt();
        
        ware_pos = new int[n_warehouses*2];
        disponibilidad = new int[n_warehouses][n_product_types];
        
        for (int i = 0; i < n_warehouses; i++) {
            ware_pos[i*2] = scanner.nextInt();
            ware_pos[(i*2)+1] = scanner.nextInt();
            for (int j = 0; j < n_product_types; j++) {
                disponibilidad[i][j] = scanner.nextInt();
            }
        }
        
        // orders
        int num_orders, order_pos[], orders_without_t[][];
        num_orders = scanner.nextInt();
        
        order_pos = new int[num_orders*2];
        orders_without_t = new int[num_orders][n_product_types];
        inicializarOrdersWithoutT(orders_without_t, num_orders, n_product_types);
        
        for (int i = 0; i < num_orders; i++) {
            order_pos[i*2] = scanner.nextInt();
            order_pos[(i*2)+1] = scanner.nextInt();
            int num_items_for_order = scanner.nextInt();
            for (int j = 0; j < num_items_for_order; j++) {
                orders_without_t[i][scanner.nextInt()]++;
            }
        }
        
        ponerAMenosUnoOrdersWithoutTConCeros(orders_without_t, num_orders, n_product_types);
        
        problema = new Problema(rows, columns, n_drones, n_product_types, n_warehouses, num_orders, turns, max_payload, products_weighs, 
                                disponibilidad, orders_without_t, order_pos, ware_pos);
        return problema;
    }
    
    public void inicializarOrdersWithoutT(int orders_without_t[][], int num_orders, int n_product_types){
        for (int i = 0; i < num_orders; i++) {
            for (int j = 0; j < n_product_types; j++) {
                orders_without_t[i][j] = 0;
            }
        }
    }
    
    public void ponerAMenosUnoOrdersWithoutTConCeros(int orders_without_t[][], int num_orders, int n_product_types){
        for (int i = 0; i < num_orders; i++) {
            for (int j = 0; j < n_product_types; j++) {
                if(orders_without_t[i][j] == 0)
                    orders_without_t[i][j] = -1;
            }
        }
    }
}
