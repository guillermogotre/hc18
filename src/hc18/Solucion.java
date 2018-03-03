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


 
public class Solucion implements Cloneable{
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
    public Solucion clone() throws CloneNotSupportedException{
        Object obj = super.clone();
        Solucion s = (Solucion)obj;
        
        s.score =this.score;
        // Clonar viajes
        s.viajes = this.viajes.clone();
        
       // Clonar solucion
       s.solucion = new ArrayList<>();
       this.solucion.forEach((l) -> {
           s.solucion.add((LinkedList < int[] >)l.clone());
        });
        return s;
    }
    // Juanka fin
    
//Guillermo ini
    //out: -1 no valido , it si valido
    public int validar_crear(int t, int c){
        final int T_INI = 1;
        final int T_FIN = 2;
        final int T_MIN = 4;
        final int T_MAX = 5;
        
        int t_min = rides[t][T_MIN];
        int t_max = rides[t][T_MAX];
        ListIterator<int[]> it = solucion.get(c).listIterator();
        boolean fin = false;
        int[] el, viaje_ant = null, viaje_post= null, candidato=null;
        while(it.hasNext() && !fin){
            el = it.next();
            if(viaje_ant == null){
                if(el[T_INI] > t_min)
                    viaje_ant = candidato;
                else
                    candidato = el;
            }
            if(el[T_FIN] > t_max){
                viaje_post = el;
                fin = true;
            }
        }
       //si cabe
       List<int[]> tramos = cabe(viaje_ant, viaje_post, t, c);
       Random r = new Random();
       if(tramos.size() > 0){
           int[] tramo = tramos.get(r.nextInt(tramos.size()));
           int tramo_length = tramo[1]-tramo[0];
           if(tramo_length >= 0){
               return tramo[0];
           }
           else{
               return -1;
           }
       }
       else{
           return -1;
       }
    }
    
    public int validar_eliminar(int t, int c){
        ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
        int[] next;
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
                res.add(validar);
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
                res.add(validar);
                
                int dist = distancia(orix, oriy, destx, desty);
                int max = rides[t][5];
                if(validar+dist>max){
                    System.out.println("CAOS");
                }
                return res;
            }
        }
    }
    //J end
    //A ini
    public int aplicar_cambios(int t, int c, int iter){
        boolean eliminar_trayecto = viajes[t] >= 0;
        //Eliminar trayecto
        int dif_score = distancia(rides[t][0], rides[t][1], rides[t][2], rides[t][3]);
        int index;
        if(eliminar_trayecto)
        {   
            c = viajes[t];
            dif_score *= -1;
            int[] next = null;
            ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
            while(it.hasNext()){
                int[] el = it.next();
                if(el[0] == t) {
                    /*
                    if(it.hasNext()){
                        next = it.next();
                        it.previous();
                    }
                    */
                    //Si bonus
                    if(el[1]==rides[t][4])
                        dif_score -= BONUS;
                    //No bonus
                    break;
                }
                
            } 
            it.remove();
            if(it.hasNext())
                next = it.next();
            viajes[t] = -1;
            
            if(next != null)
                dif_score += actualizarSolucion(c, next)*BONUS;
        }
        //Añadir trayecto
        else{
            //System.out.println(viajes[t]);
            viajes[t] = c;
            int actual_ini = rides[t][4];
            ListIterator<int[]> it = solucion.get(c).listIterator();
            boolean fin = false;
            int[] viaje_ant = null, viaje_post = null;
            index = 0;
            while(it.hasNext() && !fin){
                viaje_ant = viaje_post;
                viaje_post = it.next();
                fin = viaje_post[1] >= iter;
                index++;
            }
            if(fin) index--;
            int[] trayecto = new int[3];
            trayecto[0] = t;
            trayecto[1] = iter;
            trayecto[2] = trayecto[1] + dif_score;
            //if(fin)
                //it.previous();
                solucion.get(c).add(index, trayecto);
            //else
                //solucion.get(c).add(0, trayecto);
            //it.add(trayecto);
            if(trayecto[1]==rides[t][4])
                dif_score += BONUS;
        }
        score += dif_score;
        //if(!validarSolucion()){
        //    System.out.println("");
        //}
        return score;
    }
    
    public List<int[]> cabe(int[] anterior, int[] ultimo, int trayecto, int coche){
        // nuevo_t_ini = max(t_fin+d, t_min)
        // nuevo_t_fin = t_min + d
        final int ROW_ORI = 0;
        final int COL_ORI = 1;
        final int ROW_END = 2;
        final int COL_END = 3;
        final int T_MIN = 4;
        final int T_MAX = 5;
        final int T_INI = 1;
        final int T_FIN = 2;
        
        int t_ini_a, t_ini_b, t_fin_a, t_fin_b, t_min, t_max;
        int d, d_a, d_b;
        int[] trayecto_anterior, trayecto_posterior, trayecto_actual;
        
        
        trayecto_actual = rides[trayecto];
        d = distancia(rides[trayecto][ROW_ORI], rides[trayecto][COL_ORI], rides[trayecto][ROW_END], rides[trayecto][COL_END]);
        t_min = rides[trayecto][T_MIN];
        t_max = rides[trayecto][T_MAX];
        
        
        List<int[]> posibilidades = new ArrayList<>();
        
        //Si anterior y siguiente son las misma
            //No hay opciones
        if(anterior == ultimo && anterior != null)
            return posibilidades;
        
        ListIterator<int[]> it = solucion.get(coche).listIterator();
        int a[] = null, b[] = null;
        
        boolean listo = false;
        if(anterior != null && ultimo != null){
            while(it.hasNext()){
                a = b;
                b = it.next();
                listo = listo || (a == anterior);
                if(listo){
                    t_fin_a = a[T_FIN];
                    t_ini_b = b[T_INI];
                    
                    trayecto_anterior = rides[a[0]];
                    trayecto_posterior = rides[b[0]];
                    
                    d_a = distancia(trayecto_anterior[ROW_ORI], trayecto_anterior[COL_ORI], trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                    d_b = distancia(trayecto_actual[ROW_END], trayecto_actual[COL_END], trayecto_posterior[ROW_ORI], trayecto_posterior[COL_ORI]);
                    
                    if(
                        //t_min+d+d_b <= t_ini_b 
                    //&& 
                    //t_ini_b -(d+d_b) - t_fin_a >= d_a
                    //&&
                        Math.max(t_fin_a+d_a, t_min) + d + d_b <= Math.min(t_min, t_ini_b))
                    {
                        int[] tramo = new int[2];
                        tramo[0] = Math.max(d_a+t_fin_a,t_min);
                        //tramo[1] = Math.max(t_ini_b - d - d_b, t_fin_a+d_a);   
                        tramo[1] = tramo[0]+d;
                        posibilidades.add(tramo);
                    }
                }
                if(b==ultimo)
                    break;
            }
            
            
        }
        else if(anterior == null && ultimo == null){
            if(solucion.get(coche).isEmpty()){
                int[] tramo = new int[2];
                t_fin_a = 0;
                t_ini_b = rides[trayecto][T_MAX];

                d_a = distancia(0,0, rides[trayecto][ROW_ORI], rides[trayecto][COL_ORI]);
                d_b = 0;

                if(
                    //t_min+d+d_b <= t_ini_b 
                    //&& 
                    //t_ini_b -(d+d_b) - t_fin_a >= d_a
                    //&&
                    Math.max(t_fin_a+d_a, t_min) + d + d_b <= Math.min(t_max, t_ini_b))
                {
                    tramo[0] = Math.max(d_a+t_fin_a,t_min);
                    tramo[1] = Math.max(t_ini_b - d - d_b, t_fin_a+d_a);   
                    posibilidades.add(tramo);
                }
            }
            //
            else{
                boolean first = true;
                while(first || b != null){                
                    first = false;
                    //Actualizar valores
                    a = b;

                    if(it.hasNext()){
                        b = it.next();
                    }
                    else{
                        b = null;
                    }
                    
                    if(a==null){
                        t_fin_a = 0;
                        d_a = distancia(0,0, trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                    }
                    else{
                        trayecto_anterior = rides[a[0]];
                        t_fin_a = a[T_FIN];
                        d_a = distancia(trayecto_anterior[ROW_END], trayecto_anterior[COL_END], trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                    }
                    
                    if(b == null){
                        //t_ini_b = b[T_INI];
                        t_ini_b = trayecto_actual[T_MAX];
                        d_b = 0;
                    }else{
                        t_ini_b = b[T_INI];
                        trayecto_posterior = rides[b[0]];
                        d_b = distancia(trayecto_actual[ROW_END], trayecto_actual[COL_END], trayecto_posterior[ROW_ORI], trayecto_posterior[COL_ORI]);
                    }

                    if(
                        //t_min+d+d_b <= t_ini_b 
                    //&& 
                    //t_ini_b -(d+d_b) - t_fin_a >= d_a
                    //&&
                        Math.max(t_fin_a+d_a, t_min) + d + d_b <= Math.min(t_max, t_ini_b))
                    {
                        int[] tramo = new int[2];
                        tramo[0] = Math.max(d_a+t_fin_a,t_min);
                        tramo[1] = Math.max(t_ini_b - d - d_b, t_fin_a+d_a);   
                        posibilidades.add(tramo);
                    }
                }
            }
        }
        else if(anterior == null){
            while(it.hasNext()){
                a = b;
                b = it.next();
                if(a==null){
                    t_fin_a = 0;
                    d_a = distancia(0,0, trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                }
                else{
                    trayecto_anterior = rides[a[0]];
                    t_fin_a = a[T_FIN];
                    d_a = distancia(trayecto_anterior[ROW_END], trayecto_anterior[COL_END], trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                }
                
                t_ini_b = b[T_INI];
                trayecto_posterior = rides[b[0]];
                d_b = distancia(trayecto_actual[ROW_END], trayecto_actual[COL_END], trayecto_posterior[ROW_ORI], trayecto_posterior[COL_ORI]);

                if(
                    //t_min+d+d_b <= t_ini_b 
                    //&& 
                    //t_ini_b -(d+d_b) - t_fin_a >= d_a
                    //&&
                    Math.max(t_fin_a+d_a, t_min) + d + d_b <= Math.min(t_max, t_ini_b))
                {
                    int[] tramo = new int[2];
                    tramo[0] = Math.max(d_a +t_fin_a,t_min);
                    tramo[1] = Math.max(t_ini_b - d - d_b, t_fin_a+d_a);   
                    posibilidades.add(tramo);
                }

                if(b==ultimo)
                    break;
            }
            
        }
        //ultimo == null
        else{
            b = it.next();
            while(b != null){                
                //Actualizar valores
                a = b;
                if(it.hasNext()){
                    b = it.next();
                }
                else{
                    b = null;
                }
                
                listo = listo || (a == anterior);
                if(listo){
                    
                    t_fin_a = a[T_FIN];
                    trayecto_anterior = rides[a[0]];
                    d_a = distancia(trayecto_anterior[ROW_END], trayecto_anterior[COL_END], trayecto_actual[ROW_ORI], trayecto_actual[COL_ORI]);
                    
                    
                    if(b == null){
                        //t_ini_b = b[T_INI];
                        t_ini_b = trayecto_actual[T_MAX];
                        d_b = 0;
                    }else{
                        t_ini_b = b[T_INI];
                        trayecto_posterior = rides[b[0]];
                        d_b = distancia(trayecto_actual[ROW_END], trayecto_actual[COL_END], trayecto_posterior[ROW_ORI], trayecto_posterior[COL_ORI]);
                    }
                    
                    if(
                        t_min+d+d_b <= t_ini_b 
                        && 
                        t_ini_b -(d+d_b) - t_fin_a >= d_a
                        &&
                        Math.max(t_fin_a+d_a, t_min) + d + d_b < Math.min(t_max, t_ini_b))
                    {
                        int[] tramo = new int[2];
                        tramo[0] = Math.max(d_a + t_fin_a,t_min);
                        tramo[1] = Math.max(t_ini_b - d - d_b, t_fin_a+d_a);   
                        posibilidades.add(tramo);
                    }
                }
            }
            
            
        }
        /*
        for(int i=0; i<posibilidades.size(); i++){
            int[] pos = posibilidades.get(i);
            int[] ride = rides[trayecto];
            int dist_ori = distancia(0,0,ride[0], ride[1]);
            int dist = distancia(ride[0], ride[1], ride[2], ride[3]);
            if(pos[0] + dist > ride[5])
                System.out.println("CAOSS!!");
        }
        */
        return posibilidades;
        
    }
    
    public void mostrar(){
        for(int i = 0; i < VEHICLES; i++){
            int tam = solucion.get(i).size();
           for(int j = 0; j < tam; j++){
               System.out.print(solucion.get(i).get(j)[0]+" ");
           }
           System.out.println();
        }
        System.out.println("# " + score);
        System.out.println("####");
    }
    
    public int actualizarSolucion(int c, int[] siguiente){
        int[] a = null, b = null;
        ListIterator<int[]> it = solucion.get(c).listIterator();
        int a_row, a_col, b_row, b_col, dist_from_a, dist_b;
        int a_fin_iter;
        int bonus_counter = 0;
        int[] a_ride, b_ride;
        boolean empieza = false;
        while(it.hasNext()){
            a = b;
            b = it.next();
            empieza = empieza || b == siguiente;
            if(empieza){
                int old_iter_to_a, new_iter_to_a;
                if(a==null){
                    a_row = 0;
                    a_col = 0;
                    a_fin_iter = 0;
                }else{
                    a_ride = rides[a[0]];
                    a_row = a_ride[2];
                    a_col = a_ride[3];
                    a_fin_iter = a[2];
                }
                b_ride = rides[b[0]];
                b_row = b_ride[0];
                b_col = b_ride[1];
                dist_from_a = distancia(a_row, a_col, b_row, b_col);
                dist_b = b[2]-b[1];
                
                if(b_ride[4]==b[1]){
                    bonus_counter -= 1;
                }
                
                b[1] = Math.max(a_fin_iter+dist_from_a, b_ride[4]);
                
                if(b_ride[4]==b[1]){
                    bonus_counter += 1;
                }
                int old_b2 = b[2];
                b[2] = b[1] + dist_b;
                if(old_b2 == b[2])
                    break;
            }
        }
        return bonus_counter;
    }
    
    public boolean validarSolucion(){
        boolean valido = true;
        int ori_r, ori_c, dest_r, dest_c, iter;
        for (LinkedList<int[]> solCoche : solucion) {
            ori_r = 0;
            ori_c = 0;
            iter=0;
            ListIterator<int[]> it = solCoche.listIterator();
            while(it.hasNext()){
                int[] el = it.next();
                int[] viaje = rides[el[0]];
                int d_a = distancia(ori_r,ori_c,viaje[0],viaje[1]);
                int d_b = distancia(viaje[0], viaje[1], viaje[2], viaje[3]);
                iter = Math.max(iter+d_a, viaje[4]);
                iter += d_b;
                valido = valido && (iter <= viaje[5]);
                if(!valido){
                    int a = 0;
                }
                ori_r = viaje[2];
                ori_c = viaje[3];
            }
        }
        if(!valido)
            System.out.println("## DAMN!");
        return valido;
    }
    //A end
}
