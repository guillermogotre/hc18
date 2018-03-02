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
//    public int validar_crear(int t, int c){
//        final int T_INI = 1;
//        final int T_FIN = 2;
//        final int T_MIN = 4;
//        final int T_MAX = 5;
//        
//        int t_min = rides[t][T_MIN];
//        int t_max = rides[t][T_MAX];
//        ListIterator<int[]> it = solucion.get(c).listIterator();
//        boolean fin = false;
//        int[] el, viaje_ant = null, viaje_post= null, candidato=null;
//        while(it.hasNext() && !fin){
//            el = it.next();
//            if(viaje_ant == null){
//                if(el[T_INI] > t_min)
//                    viaje_ant = candidato;
//                else
//                    candidato = el;
//            }
//            if(el[T_FIN] > t_max){
//                viaje_post = el;
//                fin = true;
//            }
//        }
//       //si cabe
//       return cabe(viaje_ant, viaje_post, t);
//    }
    
    public int validar_crear(int t, int c){
        final int T_INI = 1;
        final int T_FIN = 2;
        final int T_MIN = 4;
        final int T_MAX = 5;
        int result;
        List<Integer> resultados = new ArrayList();
        
        ListIterator<int[]> it = solucion.get(c).listIterator();
        int[] el,viaje_ant = null, viaje_post= null;
        
        if(solucion.get(c).isEmpty()){
            result = cabe(viaje_ant,viaje_post,t,c);
            if(result != -1){
                resultados.add(result);
            }
        }
        else{
            while(it.hasNext()){
                el = it.next();
                viaje_post = el;
                result = cabe(viaje_ant,viaje_post,t,c);
                if(result != -1){
                    resultados.add(result);
                }
                viaje_ant = viaje_post;
            }
            if(viaje_ant != null && viaje_post == null){
                result = cabe(viaje_ant,viaje_post,t,c);
                if(result != -1){
                    resultados.add(result);
                }
            }
        }
        
        if(resultados.isEmpty()) return -1;
        int eleccion = (int) Math.random()*resultados.size();
        
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
        boolean salir = false;
        int pos = 0;
        int posfin = 0;
        
        ListIterator<int[]> it = lista.listIterator();
        while(it.hasNext() && !salir){
            int[] el = it.next();
            if(el[2] < posini ){
                posfin = pos;
            }
            else salir = true;
            pos++;
        }
        
        return posfin;
    }
    
    public int distancia(int a, int b, int x, int y){
        return Math.abs(a-x)+Math.abs(b-y);
    }
    public List<Object> crear_hermano(){
        Random r = new Random();
        int validar;
        int scorenew = 0;
        List<Object> res = new ArrayList();
        int cont = 0;
        int t = 0;
        while(true){
            cont = 0;
            do{
                t = r.nextInt(RIDES);
                cont++;
            }while(t >= 0 && cont < 100);
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
    
        public void aplicar_cambios(int t, int c,int pos){
        boolean eliminar_trayecto = viajes[t] >= 0;
        //Eliminar trayecto
        int dif_score = distancia(rides[t][0], rides[t][1], rides[t][2], rides[t][3]);
        if(eliminar_trayecto)
        {
            ListIterator<int[]> it = solucion.get(viajes[t]).listIterator();
            while(it.hasNext()){
                int[] el = it.next();
                if(el[0] == t) {
                    break;
                }
                
            } 
            
            it.remove();
            if(pos == rides[t][4]) score-=BONUS;
            score-=dif_score;
            viajes[t] = -1;
        }
        //Añadir trayecto
        else{
            //System.out.println(viajes[t]);
            int[] nuevo = new int[3];
            nuevo[0] = t;
            nuevo[1] = pos;
            nuevo[2] = pos+distancia(rides[t][0], rides[t][1], rides[t][2], rides[t][3]);
            int donde = donde(c,pos);
            if(pos == rides[t][4]) score+= BONUS;
            score+=dif_score;
            viajes[t] = c;
                
            solucion.get(c).add(donde,nuevo);
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
        if((Math.max(rides[trayecto][4], t_fina)+d+d_pp <= t_inib) && (t_inib-(d+d_pp)-t_fina) >= d_p){
            return t_fina+d_p;
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
