import com.sun.org.apache.xalan.internal.xsltc.dom.BitArray;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
 
public class Solucion{
    final int ROW;
    final int COLUMN;
    final int VEHICLES;
    final int RIDES;
    final int BONUS;
    final int TIME;
    
    BitArray viajes;
    List<LinkedList<int[]>> solucion;
    int[][] rides;
    
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
    }
    
//Guillermo ini
    public boolean validar(int t, int c){
        int actual_fin = 
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
