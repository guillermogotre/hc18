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
    
//Guillermo ini
    public boolean validar(int t, int c){
        int actual_fin = 
    }
    //G end
    //J ini
    public List<Objet> crear_hermano(){
        Random r = new Random();
        while(true){
            int t = r.nexRandom(5);
        }
    }
    //J end
    //A ini
    public void aplicar_cambios(int t, int c){
        for(int i = 0; i < sol[0].length; i++){
            if(sol[c][i].t_ini)
        }
    }
    //A end
}
