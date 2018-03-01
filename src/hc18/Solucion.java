package hc18;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


 
public class Solucion{
    final int ROW;
    final int COLUMN;
    final int VEHICLES;
    final int RIDES;
    final int BONUS;
    final int TIME;
    final int[][] rides;
    
    int[] viajes;
    List<LinkedList<int[]>> solucion;
    
    
    int score;
    
    // Juanca constructor
    public Solucion(int row, int column, int vehicles, int n_rides, int bonus, int time, int rides[][]){
        this.ROW = row;
        this.COLUMN = column;
        this.VEHICLES = vehicles;
        this.RIDES = n_rides;
        this.BONUS = bonus;
        this.TIME = time;
        this.rides = rides;
        
        this.viajes = new int[RIDES];
        Arrays.fill(viajes, -1);
        
        this.solucion = new ArrayList<>(VEHICLES);
        for(int i=0; i<VEHICLES; i++){
            this.solucion.add(new LinkedList<>());
        }
        
    }
    
//Guillermo ini
    //out: -1 no valido , it si valido
    public int validar_crear(int t, int c){
        int actual_fin = rides[t][5];
        ListIterator<int[]> it = solucion.get(c).listIterator();
        boolean fin = false;
        boolean valido = false;
        int[] viaje_ant, viaje_post;
        if(it.hasNext()){
            
        }
        while(it.hasNext() && !false){
            viaje = it.next();
            fin = viaje[1] >= actual_fin
        }
    }
    
    public int validar_eliminar(int t, int c){
        
    }
    //G end
    //J ini
    public List<Object> crear_hermano(){
        Random r = new Random();
        boolean validar;
        List<Object> res = new ArrayList();
        
        while(true){
            int t = r.nextInt(RIDES);
            if(viajes.get(t)){
                t = viajes.nextClearBit(t);
            }
            int c = r.nextInt(VEHICLES);
            validar = validar(t,c);
            if(validar){
                res.add()
            }
        }
    }
    //J end
    //A ini
    public void aplicar_cambios(Trayecto t, int c){

    }
    //A end
}
