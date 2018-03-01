/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hc18;

import java.util.List;

/**
 *
 * @author jose
 */
public class Problema {
    int rows, columns;
    int num_drones, num_types_products, num_warehouses, num_orders;
    int turns;
    int max_payload;
    int products_weighs[];
    int disponibilidad[][];
    int orders_without_t[][];
    int order_pos[];
    int ware_pos[];
    String file_out;
    Solucion solucion;
    
    final int ITER = 1000;
    final int c_ini = 1000;
    final double t_ini = 10000.0;
    final double alpha = 0.99;
    final int iter_por_temp = 1000;
    double scoreMax;
    Solucion solucionMax;
    boolean guardado = false;
    
    public Problema(int rows, int columns, int num_drones, int num_types_products, int num_warehouses,
                    int num_orders, int turns, int max_payload, int products_weighs[],
                    int disponibilidad[][], int orders_without_t[][], int order_pos[],
                    int ware_pos[]){
        this.rows = rows;
        this.columns = columns;
        this.num_drones = num_drones;
        this.num_types_products = num_types_products;
        this.num_warehouses = num_warehouses;
        this.num_orders = num_orders;
        this.turns = turns;
        this.max_payload = max_payload;
        this.products_weighs = products_weighs;
        this.disponibilidad = disponibilidad;
        this.orders_without_t = orders_without_t;
        this.order_pos = order_pos;
        this.ware_pos = ware_pos;
        scoreMax = 0.0;
        Solucion solucionMax;
    }
    
    public void setFileDest(String file_out){
        this.file_out = file_out;
    }
    
    public void start() throws CloneNotSupportedException{     
        // Solucion inicial
        solucion = new Solucion(rows, columns, num_drones, turns, max_payload, products_weighs,
                                disponibilidad, order_pos, orders_without_t, ware_pos);
        boolean even = true;
        
        // Logica de recocido simulado
        
        double score = solucion.getScore();
        for(int iter=0; iter<ITER; iter++){
            int c_k = c_ini;
            double t_k = t_ini;
            int cont = 0;
            int n = 0;
            
            while (c_k >= 0) {            
                for (int i = 0; i < iter_por_temp; i++) {
                    List<Object> score_prima = solucion.crear_vecino();
                    //float p = 
                    if((double) score_prima.get(0) > score || getProbabilidad(score, (double) score_prima.get(0), t_k) > Math.random()){
                        solucion.aplicar_cambio((Solucion.SolCel) score_prima.get(1), (int) score_prima.get(2), (int) score_prima.get(3));

                        //System.out.println("" + c_k + " " + score + " " + scorePrima_and_cambio[0]);
                        c_k = c_ini;


                        score = (double) score_prima.get(0);
                        //if(scoreMax < score && i%10 == 0){
                            if(scoreMax < score){
                            //solucionMax = solucion.clone();
                            //Si cambias la linea anterior por la siguiente
                            //va más rápido pero falla a veces (i%10 == 0)
                            //En verdad no estoy seguro
                                solucionMax= solucion;
                            
                            scoreMax = score;
                            guardado = false;
                        }
                    }
                    else{
                        c_k--;
                    }
                }
                t_k *= alpha;
                System.out.println(t_k + " " + scoreMax);
                //t_k = t_ini/(1+t_k*3);
                cont++;
                if(cont%100 == 0 ){
                    if(!guardado){
                        solucionMax.generar_salida( file_out + n);
                        even = !even;
                        guardado = true;
                        n++;
                        if(n >= 10) n = 0;
                    }
                    cont = 0;
                }
            }
            solucion = (Solucion) solucionMax.clone();
            score = scoreMax;
        }
        
        solucionMax.generar_salida(file_out);
    }
    
    public double getProbabilidad(double score, double scorePrima, double t_k){
        double dif = scorePrima-score;
        if(dif==0) return 0;
        double p = Math.exp(dif / t_k);
        //System.out.println(p);
        return p;
    }
    
}
