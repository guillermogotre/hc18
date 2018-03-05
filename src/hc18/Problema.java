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
    int rows, columns, n_vehicles, n_rides, bonus, n_steps;
    int rides[][];
    String file_out;
    Solucion solucion;
    
    final int ITER = 10000000;
    final int c_ini = 10000000;
    final double t_ini = 200.0;
    final double alpha = 0.99;
    final int iter_por_temp = 1000000;
    int scoreMax;
    Solucion solucionMax;
    boolean guardado = false;
    
    public Problema(int rows, int columns, int n_vehicles, int n_rides, int bonus, int n_steps,
                    int rides_from_to[][], int rides_start_finish[][]){
        this.rows = rows;
        this.columns = columns;
        this.n_vehicles = n_vehicles;
        this.n_rides = n_rides;
        this.bonus = bonus;
        this.n_steps = n_steps;
        
        // [row_ori, col_ori, row_fin, col_fin, start, finish]
        rides = new int[n_rides][6];
        
        for (int i = 0; i < n_rides; i++) {
            for (int j = 0; j < 4; j++) {
                rides[i][j] = rides_from_to[i][j];
            }
            rides[i][4] = rides_start_finish[i][0];
            rides[i][5] = rides_start_finish[i][1];
        }
        
        scoreMax = 0;
        Solucion solucionMax;
    }
    
    public void setFileDest(String file_out){
        this.file_out = file_out;
    }
    
    public void start() throws CloneNotSupportedException{     
        // Solucion inicial
        solucion = new Solucion(rows, columns,n_vehicles, n_rides,bonus,n_steps, rides);
        boolean even = true;
        
        // Logica de recocido simulado
        
        int score = 0;
        for(int iter=0; iter<ITER; iter++){
            int c_k = c_ini;
            double t_k = t_ini;
            int cont = 0;
            int n = 0;
            int vamos = 0;
            
            while (c_k >= 0) {            
                for (int i = 0; i < iter_por_temp; i++) {
                    // double score, int t(indice viaje), int c(indice coche), int pos(indice insertar new ride)
                    List<Object> score_prima = solucion.crear_hermano();
                    //float p = 
                    if((int) score_prima.get(0) > score || getProbabilidad(score, (int) score_prima.get(0), t_k) > Math.random()){
                        solucion.aplicar_cambios((int) score_prima.get(1), (int) score_prima.get(2), (int) score_prima.get(3));

                        //System.out.println("" + c_k + " " + score + " " + scorePrima_and_cambio[0]);
                        c_k = c_ini;


                        score = (int) score_prima.get(0);
                        //if(scoreMax < score && i%10 == 0){
                            if(scoreMax < score){
                            //solucionMax = solucion.clone();
                            //Si cambias la linea anterior por la siguiente
                            //va más rápido pero falla a veces (i%10 == 0)
                            //En verdad no estoy seguro
                                solucionMax= (Solucion)solucion.clone();
                            
                            scoreMax = score;
                            guardado = false;
                        }
                    }
                    else{
                        c_k--;
                    }
//                    solucion.mostrar();
                }
                t_k *= alpha;
                System.out.println(t_k + " " + scoreMax + " " + score);
                vamos++;
//                t_k = t_ini/(1+vamos*5);
                cont++;
//                if(cont%10 == 0 ){
                    if(!guardado){
                        solucionMax.generar_salida( file_out + n);
                        even = !even;
                        guardado = true;
                        n++;
                        if(n >= 5) n = 0;
                    }
                    cont = 0;
//                }
            }
            solucion = (Solucion) solucionMax.clone();
            score = scoreMax;
        }
        
        solucionMax.generar_salida(file_out);
    }
    
    public double getProbabilidad(int score, int scorePrima, double t_k){
        int dif = scorePrima-score;
        if(dif==0) return 0;
        double p = Math.exp(1.0*dif / t_k);
        //System.out.println(p);
        return p;
    }
    
}
