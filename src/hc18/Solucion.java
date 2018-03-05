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
                pw.flush();
                for(int j = 0; j < solucion.get(i).size(); j++){
                    pw.print(solucion.get(i).get(j)[0]+" ");
                    pw.flush();
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
    public int validar_crear(int t, int c){
        final int T_INI = 1;
        final int T_FIN = 2;
        final int T_MIN = 4;
        final int T_MAX = 5;
        int result;
        List<Integer> resultados = new ArrayList();
        
        ListIterator<int[]> it = solucion.get(c).listIterator();
        int[] el,viaje_ant = null, viaje_post= null,candidato = null,viaje_antnew = null, viaje_postnew= null;
        
        if(solucion.get(c) == null || solucion.get(c).isEmpty()){
            result = cabe(viaje_ant,viaje_post,t,c);
            if(result != -1){
                resultados.add(result);
            }
        }
        else{
            boolean fin = false;
            int t_min = rides[t][T_MIN];
            int t_max = rides[t][T_MAX];

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
            
            it = solucion.get(c).listIterator();
            if(viaje_ant != null){
                while(it.hasNext()){
                    el = it.next();
                    if(el[0] == viaje_ant[0]){
                        break;
                    }
                }
            }
            fin = false;
            if(viaje_ant != null) viaje_antnew = viaje_ant.clone();
            while(it.hasNext() && !fin){
                el = it.next();
                viaje_postnew = el.clone();
                result = cabe(viaje_antnew,viaje_postnew,t,c);
                if(result != -1){
                    resultados.add(result);
                }
                viaje_antnew = viaje_postnew.clone();
                if(viaje_post != null && viaje_antnew[0] == viaje_post[0]){
                    fin = true;
                }
            }
            if(!fin || viaje_post == null){
                result = cabe(viaje_postnew,null,t,c);
                if(result != -1){
                    resultados.add(result);
                }
            }
        }
        
        if(resultados.isEmpty()) return -1;
        int eleccion = (int) (Math.random() * resultados.size());
        
        return resultados.get(eleccion);
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
    
    public int donde(int c, int posini){
        LinkedList<int[]> lista = solucion.get(c);
        int pos = 0;
        int posfin = 0;
        boolean salir = false;
        
        ListIterator<int[]> it = lista.listIterator();
        while(it.hasNext() && !salir){
            int[] el = it.next();
            if(el[1] >= posini ){
                salir = true;
                posfin = pos;
            }
            pos++;
        }
        
        if(!salir) return pos+1;
        return posfin;
    }
    
    public int distancia(int a, int b, int x, int y){
        return Math.abs(a-x)+Math.abs(b-y);
    }
    public List<Object> crear_hermano(){
        Random r = new Random();
        int validar;
        int scorenew = 0;
        int nveces = 0;
        int t,c;
        int[] el = new int[3], next = new int[3];
        List<Object> res = new ArrayList();
        while(true){
//            do{
//                t = r.nextInt(RIDES);
//                c = r.nextInt(VEHICLES);
//                nveces++;
//            }while(t > 0 && nveces < 20);
            t = r.nextInt(RIDES);
            c = r.nextInt(VEHICLES);
            if(viajes[t] >=0){
                validar = validar_eliminar(t, viajes[t]);
                scorenew = score;
                int orix = rides[t][0];
                int oriy = rides[t][1];
                int destx = rides[t][2];
                int desty = rides[t][3];
                scorenew -= distancia(orix, oriy, destx, desty);
                LinkedList<int[]> cp = new LinkedList();
                for(int i =0; i < solucion.get(viajes[t]).size(); i++){
                    cp.add(solucion.get(viajes[t]).get(i).clone());
                }
                ListIterator<int[]> it = cp.listIterator();
                while(it.hasNext()){
                    el = it.next();
                    if(el[0] == t) {
                        break;
                    }
                }
                
                if(validar == rides[t][4]) scorenew-=BONUS; 
////                it.remove();
                if(it.hasNext())
                    next = it.next();

                if(next != null)
                    scorenew += simularActualizarSolucion(viajes[t], next)*BONUS;
                
                res.add(scorenew);
                res.add(t);
                res.add(viajes[t]);
                res.add(validar);
                return res;
            }
            validar = validar_crear(t, c);
            if(validar >= 0){
                scorenew = score;
                if(validar == rides[t][4]) scorenew += BONUS;
                int orix = rides[t][0];
                int oriy = rides[t][1];
                int destx = rides[t][2];
                int desty = rides[t][3];
                scorenew += distancia(orix, oriy, destx, desty);
                res.add(scorenew);
                res.add(t);
                res.add(c);
                res.add(validar);
                return res;
            }
        }
    }
    //J end
    //A ini
//    public void aplicar_cambios(int t, int c,int pos){
//        boolean eliminar_trayecto = viajes[t] >= 0;
//        //Eliminar trayecto
//        int dif_score = distancia(rides[t][0], rides[t][1], rides[t][2], rides[t][3]);
//        if(eliminar_trayecto)
//        {
//            dif_score *= -1;
//            ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
//            while(it.hasNext()){
//                int[] el = it.next();
//                if(el[0] == t) {
//                    //Si bonus
//                    if(el[1]==rides[t][4])
//                        dif_score -= BONUS;
//                    //No bonus
//                    break;
//                }
//                
//            } 
//            
//            it.remove();
//            viajes[t] = -1;
//        }
//        //Añadir trayecto
//        else{
//            //System.out.println(viajes[t]);
//            viajes[t] = c;
//            int actual_ini = rides[t][4];
//            ListIterator<int[]> it = solucion.get(c).listIterator();
//            boolean fin = false;
//            int[] viaje_ant = null, viaje_post = null;
//            while(it.hasNext() && !fin){
//                viaje_ant = viaje_post;
//                viaje_post = it.next();
//                fin = viaje_post[1] >= actual_ini;
//            }
//            int[] trayecto = new int[3];
//            trayecto[0] = t;
//            trayecto[1] = cabe(viaje_ant, viaje_post, t, c);
//            trayecto[2] = trayecto[1] + dif_score;
//            if(it.hasPrevious())
//                it.previous();
//            it.add(trayecto);
//            if(trayecto[1]==rides[t][4])
//                dif_score += BONUS;
//        }
//        score += dif_score;
//    }
    
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
    
    public int simularActualizarSolucion(int c, int[] siguiente){
        int[] a = null, b = null;
        LinkedList<int[]> cp = new LinkedList();
        for(int i =0; i < solucion.get(c).size(); i++){
            cp.add(solucion.get(c).get(i).clone());
        }
        ListIterator<int[]> it = cp.listIterator();
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

    
        public void aplicar_cambios(int t, int c,int pos){
        boolean eliminar_trayecto = viajes[t] >= 0;
        int[] next = null;
        //Eliminar trayecto
        int dif_score = distancia(rides[t][0], rides[t][1], rides[t][2], rides[t][3]);
        if(eliminar_trayecto)
        {
            ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
            dif_score*=-1;
            while(it.hasNext()){
                int[] el = it.next();
                if(el[0] == t) {
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
                score += actualizarSolucion(c, next)*BONUS;
            
            score += dif_score;
        }
        //Añadir trayecto
        else{
            //System.out.println(viajes[t]);
            int[] nuevo = new int[3];
            nuevo[0] = t;
            nuevo[1] = pos;
            nuevo[2] = pos+dif_score;
            int donde = donde(c,pos);
            if(pos == rides[t][4]) score+= BONUS;
            score+=dif_score;
            viajes[t] = c;
            
            if(donde >= solucion.get(c).size()) solucion.get(c).add(nuevo);
            else solucion.get(c).add(donde,nuevo);
    }
}
    
//    public int cabe(int[] anterior, int[] ultimo, int trayecto, int coche){
//        // nuevo_t_ini = max(t_fin+d, t_min)
//        // nuevo_t_fin = t_min + d
//        final int ROW_ORI = 0;
//        final int COL_ORI = 1;
//        final int T_MIN = 4;
//        final int T_MAX = 5;
//        final int T_INI = 1;
//        final int T_FIN = 2;
//        
//        int t_ini_a, t_ini_b, t_fin_a, t_fin_b;
//        int d_p, d_pp;
//
//        if(anterior == ultimo && anterior != null)
//            return -1;
//        
//        ListIterator<int[]> it = solucion.get(coche).listIterator();
//        int a[] = null, b[] = null;
//        if(anterior != null && ultimo != null){
//            while(it.hasNext()){
//                a = b;
//                
//            }
//        }
//        else if(anterior == null && ultimo == null){
//            return Math.max(distancia(0,0, rides[trayecto][ROW_ORI], rides[trayecto][COL_ORI]),rides[trayecto][T_MIN]);
//        }
//        else if(anterior == null){
//            
//        }
//        //ultimo == null
//        else{
//            
//        }
//        
//    }

    public int cabe(int[] anterior, int[] siguiente, int trayecto, int coche){
        int t_fina = 0,t_finb = 0,t_inia = 0,t_inib = 0;
        int d_p = 0,d_pp = 0, d = distancia(rides[trayecto][0], rides[trayecto][1], rides[trayecto][2], rides[trayecto][3]);
        if(anterior == null && siguiente == null){
            return Math.max(distancia(0,0, rides[trayecto][0], rides[trayecto][1]),rides[trayecto][4]);
        }
        else if(anterior == null){
            t_inia = 0;
            t_fina = 0;
            t_inib = siguiente[1];
            t_finb = siguiente[2];
            d_p = distancia(0, 0, rides[trayecto][0], rides[trayecto][1]);
            d_pp = distancia(rides[trayecto][2], rides[trayecto][3], rides[siguiente[0]][0], rides[siguiente[0]][1]);
        }
        else if(siguiente == null){
            t_inia = anterior[1];
            t_fina = anterior[2];
            t_inib = rides[trayecto][5];
            t_finb = rides[trayecto][5];
            d_p = distancia(rides[anterior[0]][2], rides[anterior[0]][3], rides[trayecto][0], rides[trayecto][1]);
            d_pp = 0;
        }
        else{
            t_inia = anterior[1];
            t_fina = anterior[2];
            t_inib = siguiente[1];
            t_finb = siguiente[2];
            d_p = distancia(rides[anterior[0]][2], rides[anterior[0]][3], rides[trayecto][0], rides[trayecto][1]);
            d_pp = distancia(rides[trayecto][2], rides[trayecto][3], rides[siguiente[0]][0], rides[siguiente[0]][1]);
        }
        int fin = Math.min(t_inib, rides[trayecto][5]);
        if(Math.max(rides[trayecto][4], t_fina+d_p)+d+d_pp <= Math.min(fin, TIME)){
            return Math.max(rides[trayecto][4], t_fina+d_p);
        }
        return -1;
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
    //A end
}
