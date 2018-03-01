package hc18;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.IntStream;


 
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
    
    // Juanca ini
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
    
    public void generar_salida(String file_out){
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            //fichero = new FileWriter(file_out);
            pw = new PrintWriter(file_out);

            for(int i = 0; i < VEHICLES; i++){
                pw.print(solucion.get(i).size()+" ");
                for(int j = 0; j < solucion.get(i).size(); j++){
                    pw.print(solucion.get(i).get(j)[0]+" ");
                }
                pw.println();
                pw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        Object obj = super.clone();
        Solucion s = (Solucion)obj;
        
        // Clonar viajes
        s.viajes = this.viajes.clone();
        
       // Clonar solucion
       
        return s;
    }
    // Juanka fin
    
//Guillermo ini
    //out: -1 no valido , it si valido
    public int validar_crear(int t, int c){
        int actual_fin = rides[t][5];
        int actual_ini = rides[t][4];
        ListIterator<int[]> it = solucion.get(c).listIterator();
        boolean fin = false;
        boolean valido = false;
        int[] viaje_ant = null, viaje_post = null;
        while(it.hasNext() && !fin){
            viaje_ant = viaje_post;
            viaje_post = it.next();
            fin = viaje_post[1] >= actual_ini;
        }
         
       //si cabe
       return cabe(viaje_ant, viaje_post, t);
    }
    
    public int validar_eliminar(int t, int c){
        ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
        while(it.hasNext()){
            int[] el = it.next();
            if(el[0] == t)
                return el[1];
        }
        return -1;
    }
    //G end
    //J ini
    public int distancia(int a, int b, int x, int y){
        return Math.abs(a-x)+Math.abs(b-y);
    }
    public List<Object> crear_hermano(){
        Random r = new Random();
        int validar;
        int scorenew = 0;
        List<Object> res = new ArrayList();
        
        while(true){
            int t = r.nextInt(RIDES);
            int c = r.nextInt(VEHICLES);
            if(viajes[t] >=0){
                validar = validar_eliminar(t, c);
                scorenew = score;
                if(rides[t][4] == validar){
                    scorenew-=BONUS;
                }
                int orix = rides[t][0];
                int oriy = rides[t][1];
                int destx = rides[t][2];
                int desty = rides[t][3];
                scorenew -= distancia(orix, oriy, destx, desty);
                res.add(scorenew);
                res.add(t);
                res.add(-1);
                return res;
            }
            validar = validar_crear(t, c);
            if(validar >= 0){
                scorenew = score;
                if(rides[t][4] == validar){
                    scorenew+=BONUS;
                }
                int orix = rides[t][0];
                int oriy = rides[t][1];
                int destx = rides[t][2];
                int desty = rides[t][3];
                scorenew += distancia(orix, oriy, destx, desty);
                res.add(scorenew);
                res.add(t);
                res.add(c);
                return res;
            }
        }
    }
    //J end
    //A ini
    public void aplicar_cambios(int t, int c){
        int[] aux = new int[3];
        for(int i=0; i< solucion.get(0).size(); i++){
            if(this.solucion.get(c).get(i)[1] > this.rides[t][5]){
                aux[0]=t;
                aux[1]=0;
                aux[2]=0;
                this.solucion.get(c).add(i, aux);
            }
                
        }
    }
    
    public int cabe(int[] ant, int[] sig, int t){
        
    }
    //A end
}
