import com.sun.org.apache.xalan.internal.xsltc.dom.BitArray;
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
    List<List<int[]>> solucion;
    List<int[]> rides;
    
//Guillermo ini
    public boolean validar(int t, int c){
        
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
    public void aplicar_cambios(Trayecto t, int c){

    }
    //A end
}
