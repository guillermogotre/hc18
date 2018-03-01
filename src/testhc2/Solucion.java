/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testhc2;

import static java.lang.Math.sqrt;
import java.io.FileWriter;
import java.io.PrintWriter;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * @author jose
 */
public class Solucion implements Cloneable{
    
    final int DRONES;
    final int WAREHOUSES;
    final int TIME;
    final int PRODUCTS;
    final int ORDERS;
    final int ROWS;
    final int COLUMS;
    final int MAXT;
    final int MAXCARGADRON;
    //[WAREHOUSE]*[PRODUCTOS]
    final int[][] DISPONIBILIDAD;
    //[ORDERS]*[PRODUCTOS]
    final int [][] ORDERS_SIN_T;
    
    // sol --> DRONES*TIME
    SolCel[][] sol;
    //ware --> WARHOUSES*TIME*PRODUCTS
    int[][][] ware;
    //orders --> ORDERS*TOME*PRODUCTS
    int[][][] orders;
    //ware_pos --> indica la posicion de los warhouses
    int[] ware_pos;
    //order_pos --> indica la posicion del destino
    int[] order_pos;
    //order_completado --> indica la t(tiempo) en el que se hemos completado el pedido
    int[] order_completado;
    //order_total --> Indica el numero total de productos que le quedan a cada pedido;
    // ORDERS*TIME
    int[][] order_total;
    //pesos_drones --> para cada indice que indica el producto, tenemos el peso del mismo
    int[] pesos_productos;
    //DRON*TIME*PRODUCTO
    int[][][] productos_en_dron; //Meter en clone
    //DRON*TIME
    int[][] dron_peso; //Meter en clone
    
    
    int score;
    
    
        
    //disponibilidad --> WARHOUSES*PRODUCTS
    public Solucion(int filas, int columnas, int ndrones, int maxt, int maxcargadron, int[] pesos_producto,
            int[][] disponibilidad,int[] order_pos, int[][] orders_sin_t, int[] ware_pos){
        
        this.ROWS=filas;
        this.COLUMS=columnas;
        this.DRONES=ndrones;
        this.MAXT=maxt;
        this.TIME=maxt;
        this.MAXCARGADRON=maxcargadron;
        this.pesos_productos=pesos_producto;
        this.WAREHOUSES=disponibilidad.length;
        this.ORDERS=orders_sin_t.length;
        this.PRODUCTS=disponibilidad[0].length;
        this.score=0;
        this.DISPONIBILIDAD=disponibilidad;
        this.ORDERS_SIN_T=orders_sin_t;
        //Disponibilidad
        //ware --> WARHOUSES*TIME*PRODUCTS
        this.ware = new int[WAREHOUSES][TIME][PRODUCTS];
        IntStream.range(0, WAREHOUSES)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        this.ware[i][j] = disponibilidad[i].clone();
                    }
                });
        this.orders = new int[ORDERS][TIME][PRODUCTS];
        IntStream.range(0, ORDERS)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        this.orders[i][j] = orders_sin_t[i].clone();
                    }
                });
        
        this.order_pos=order_pos;
        
        //paralelo
        for(int i = 0; i < ORDERS; i++){
            order_completado[i] = -1;
        }
    }
    
    public class SolCel{
        char accion;
        boolean primero;
        int duracion;
        int indice;
        int x_0,y_0,x_d,y_d;
        int destino;
        int idproducto,cantidad;
        
        public SolCel(char accion, boolean primero, int duracion, int indice, int x_0, int y_0,
                        int x_d, int y_d, int destino, int idproducto, int cantidad){
            this.accion = accion;
            this.primero = primero;
            this.duracion = duracion;
            this.indice = indice;
            this.x_0 = x_0;
            this.y_0 = y_0;
            this.x_d = x_d;
            this.y_d = y_d;
            this.destino = destino;
            this.idproducto = idproducto;
            this.cantidad = cantidad;
        }
        
        public SolCel(){}
    }
    
    //out: [score int, cambio SolCel, d dron, t iteracion]
    public List<Object> crear_vecino(){
        Random r = new Random();
        SolCel accion;
        List<Object> res = new ArrayList();
        boolean validar;
        double newscore = 0.0;
        while(true){
            int d = r.nextInt(DRONES);
            int t = r.nextInt(TIME);
            if(sol[d][t] == null){
                accion = accion_azar(d, t);
                validar = validar(accion, d, t);
                if(validar){
                    if(accion.accion == 'D'){
                        int cantidad = order_total[accion.destino][t]-accion.cantidad;
                        if(cantidad == 0) newscore = 1.0*(TIME-(t+accion.duracion))/TIME+score;
                        res.add(newscore);
                        res.add(accion);
                        res.add(d);
                        res.add(t);
                        return res;
                    }
                    res.add(score);
                    res.add(accion);
                    res.add(d);
                    res.add(t);
                    return res;
                }
                else{
                    validar = test_eliminar(d, t);
                    if(validar){
                        if(sol[d][t].accion == 'D'){
                            int cantidad = order_total[sol[d][t].destino][t];
                            if(cantidad == 0) newscore = score-1.0*(TIME-order_completado[accion.destino])/TIME;
                            res.add(newscore);
                            res.add(accion);
                            res.add(d);
                            res.add(t);
                            return res;
                        }
                        res.add(score);
                        res.add(accion);
                        res.add(d);
                        res.add(t);
                        return res;
                    }
                }
            }
        }
    }
    
    public int distancia_euclidea(int x1, int y1, int x2, int y2){
        return (int)Math.ceil(sqrt(  Math.pow((x1-x2),2) + Math.pow((y1-y2),2)));
    }
    
    // d = dron aletearorio
    // t = iteracion_aleatoria
    public SolCel accion_azar(int dron_aleatorio, int iteracion_aleatoria){
        
        SolCel ac = new SolCel();
        
        //accion = {D, L, U}
        switch((int)(Math.random()*3)){
            case 0:
                ac.accion='D';
            break;
            case 1:
                ac.accion='L';
            break;
            case 2:
                ac.accion='U';
            break;
        }
        
        //primero={true}
        ac.primero=true;
        
        
        
        //indice=iteracion_aleatoria
        ac.indice=iteracion_aleatoria;
        
        //destino={random*WAREHOUSES (si accion={L o U}) random*ORDERS (si accion={D}) }
        if(ac.accion=='L' || ac.accion=='U')
            ac.destino=(int)(Math.random()*this.WAREHOUSES);
        else
            ac.destino=(int)(Math.random()*this.ORDERS);
        
        
        //x0,y0
        int i=ac.indice;
        while(this.sol[dron_aleatorio][i]==null) i--;
        ac.x_0=this.sol[dron_aleatorio][i].x_d;
        ac.y_0=this.sol[dron_aleatorio][i].y_d;
        
        //xd,yd
        if(ac.accion=='L' || ac.accion=='U'){
            ac.x_d= this.ware_pos[2*ac.destino];
            ac.y_d= this.ware_pos[2*ac.destino+1];
        }
        else{ 
            ac.x_d= this.order_pos[2*ac.destino];
            ac.x_d= this.order_pos[2*ac.destino+1];
        }
        
        //duracion= {distancia euclidea de origen a destino}
        ac.duracion=this.distancia_euclidea(ac.x_0, ac.y_0, ac.x_d, ac.y_d)+1;
        
        
        
        //idProducto
        ac.idproducto=(int)(Math.random()*this.PRODUCTS);
        
        //cantidad
        //Random r = new Random();
        //r.nextInt(50-1)+1;
        if(ac.accion=='D')
            ac.cantidad= (int)(Math.random()*this.orders[ac.destino][ac.indice][ac.idproducto]);            
        else if(ac.accion=='L')
            ac.cantidad= (int)(Math.random()*this.ware[ac.destino][ac.indice][ac.idproducto]);
        else
            ac.cantidad= (int)(Math.random()*this.productos_en_dron[dron_aleatorio][ac.indice][ac.idproducto]);
        if(ac.cantidad==0) ac.cantidad=1;
        
        
        
        return ac;
    }
    
    public boolean llega(int fin, int dron_aleatorio, SolCel accion){
        //el siguiente llega
        boolean valido = true;
        
        //has next?
        int i = fin+1;
        SolCel next = null;
        for(; i<TIME && next == null; i++){
            next = sol[dron_aleatorio][i];
        }
        // can next arrive from new origin?
        valido =(
                    next == null ||
                    next.indice + next.duracion - distancia_euclidea(accion.x_d,accion.y_d,next.x_d,next.y_d) >= accion.indice + accion.duracion
                );
        return valido;
    }
    
    // d = dron aletearorio
    // t = iteracion_aleatoria
    //out: [score int, valido int]
    //añadir
    public boolean validar(SolCel accion, int dron_aleatorio, int iteracion_aleatoria){
        //Comprabar si cabe
        boolean valido = true;
        int ini = accion.indice;
        int fin = ini+accion.duracion-1;
        for(int i=ini; i<= fin && valido; i++){
            valido = sol[dron_aleatorio][i] == null;
        }
        if(valido){
            //Comprobar si incompatible
            switch (accion.accion){
                case 'D':
                    valido = (
                            productos_en_dron[dron_aleatorio][fin][accion.idproducto] >= accion.cantidad
                            &&
                            orders[accion.destino][fin][accion.idproducto] >= accion.cantidad
                            );
                    //->D*
                    valido = valido && !IntStream.range(fin+1,TIME)
                            .parallel()
                            .anyMatch(t->{
                                return orders[accion.destino][t][accion.idproducto] < accion.cantidad;
                            });
                    
                    valido = valido && !IntStream.range(fin+1, TIME)
                            .parallel()
                            .anyMatch(t->{
                                return productos_en_dron[dron_aleatorio][t][accion.idproducto] < accion.cantidad;
                            });
                    
                    //el siguiente llega
                    valido = valido & llega(fin, dron_aleatorio, accion);
                    break;
                case 'L':
                    valido = (
                            //dron_peso[dron_aleatorio][fin] >= pesos_productos[accion.idproducto]*accion.cantidad
                            dron_peso[dron_aleatorio][fin] + pesos_productos[accion.idproducto]*accion.cantidad <= MAXCARGADRON
                            &&
                            ware[accion.destino][fin][accion.idproducto] >= accion.cantidad
                            );
                    //Comprobar en warehouse
                    valido = valido && !IntStream.range(fin+1,TIME)
                            .parallel()
                            .anyMatch(t->{
                                return ware[accion.destino][t][accion.idproducto] < accion.cantidad;
                            });
                    //Comprobar en dron
                    valido = valido && !IntStream.range(fin+1, TIME)
                            .parallel()
                            .anyMatch(t->{
                                //DRON*TIME
                                return dron_peso[dron_aleatorio][t]+pesos_productos[accion.idproducto]*accion.cantidad > MAXCARGADRON;
                            });
                    valido = valido && llega(fin, dron_aleatorio, accion);
                    break;
                case 'U':
                    valido = (
                            productos_en_dron[dron_aleatorio][fin][accion.idproducto] >= accion.cantidad
                            );
                    
                    valido = valido && !IntStream.range(fin+1, TIME)
                            .parallel()
                            .anyMatch(t->{
                                return productos_en_dron[dron_aleatorio][t][accion.idproducto] < accion.cantidad;
                            });
                    
                    valido = valido && llega(fin, dron_aleatorio, accion);
                    break;
            }
        }
        return valido;
    }
    
    // d = dron aletearorio
    // t = iteracion_aleatoria
    //out: [score int, valido int] 
    //eliminar
    public boolean test_eliminar(int dron_aleatorio, int iteracion_aleatoria){
        boolean valido = true;
        SolCel accion = sol[dron_aleatorio][iteracion_aleatoria];
        int fin = accion.indice + accion.duracion;
        switch (accion.accion){
                case 'D':
                    //Comprobar Ld
                    valido = !IntStream.range(fin,TIME)
                            .parallel()
                            .anyMatch(t->{
                                //return productos_en_dron[dron_aleatorio][t][accion.idproducto] < accion.cantidad;
                                return dron_peso[dron_aleatorio][t]  >= pesos_productos[accion.idproducto]*accion.cantidad;
                            });
                    break;
                case 'L':
                    //Comprobar Ud
                    valido = !IntStream.range(fin,TIME)
                            .parallel()
                            .anyMatch(t->{
                                return productos_en_dron[dron_aleatorio][t][accion.idproducto] < accion.cantidad;
                            });
                    //Comprobar Dd
                    break;
                case 'U':
                    //Comprobar L*
                    //Comprobar en warehouse
                    valido = valido && !IntStream.range(fin,TIME)
                            .parallel()
                            .anyMatch(t->{
                                return ware[accion.destino][t][accion.idproducto] < accion.cantidad;
                            });
                    //Comprobar en dron
                    valido = valido && !IntStream.range(fin, TIME)
                            .parallel()
                            .anyMatch(t->{
                                //DRON*TIME
                                return dron_peso[dron_aleatorio][t]+pesos_productos[accion.idproducto]*accion.cantidad <= MAXCARGADRON;
                            });
                    break;
            }
        return valido;
    }
    
    // d = dron aletearorio
    // t = iteracion_aleatoria
    public void aplicar_cambio(SolCel accion, int dron_aleatorio,int iteracion_aleatoria){
        if(accion == null){
            SolCel ant = sol[dron_aleatorio][iteracion_aleatoria];
            //Buscar siguiente
            int salida;
            //For vacio
            for(salida = accion.indice + accion.duracion; salida < TIME && sol[dron_aleatorio][salida] == null; salida++){}

            if(salida != TIME){
                boolean salir = false;
                int indice = salida;
                salida = iteracion_aleatoria;
                for(int i = iteracion_aleatoria; i >= 0 && !salir; i--,salida--){
                    salir = sol[dron_aleatorio][i] != null;
                }
                if(salir){
                    sol[dron_aleatorio][indice].x_0 = sol[dron_aleatorio][salida].x_d;
                    sol[dron_aleatorio][indice].y_0 = sol[dron_aleatorio][salida].y_d;
                }
                else{
                    sol[dron_aleatorio][indice].x_0 = ware_pos[0];
                    sol[dron_aleatorio][indice].y_0 = ware_pos[1];
                }
                //Calcular dist euclidea
                int x1 = sol[dron_aleatorio][indice].x_0;
                int y1 = sol[dron_aleatorio][indice].y_0;
                int x2 = sol[dron_aleatorio][indice].x_d;
                int y2 = sol[dron_aleatorio][indice].y_d;
                int dist = distancia_euclidea(x1, y1, x2, y2)+1;

                //si la distancia es mayor, agrandar
                int dif = dist-sol[dron_aleatorio][indice].duracion;
                
                SolCel newsol = new SolCel(sol[dron_aleatorio][indice].accion,true,dist,
                            sol[dron_aleatorio][indice].indice-dif,
                            sol[dron_aleatorio][indice].x_0,sol[dron_aleatorio][indice].y_0,
                            sol[dron_aleatorio][indice].x_d,sol[dron_aleatorio][indice].y_d
                            ,sol[dron_aleatorio][indice].destino,
                            sol[dron_aleatorio][indice].idproducto,
                            sol[dron_aleatorio][indice].cantidad);

                    SolCel newsol2 = new SolCel(sol[dron_aleatorio][indice].accion,false,dist,
                            sol[dron_aleatorio][indice].indice-dif,
                            sol[dron_aleatorio][indice].x_0,sol[dron_aleatorio][indice].y_0,
                            sol[dron_aleatorio][indice].x_d,sol[dron_aleatorio][indice].y_d
                            ,sol[dron_aleatorio][indice].destino,
                            sol[dron_aleatorio][indice].idproducto,
                            sol[dron_aleatorio][indice].cantidad);
                    
                if(dif > 0){
                    
                    for(int i = newsol.indice+1; i < newsol.indice+newsol.duracion;i++){
                        sol[dron_aleatorio][i] = newsol2;
                    }
                    sol[dron_aleatorio][newsol.indice] = newsol;
                //si es menor, reducir
                }
                else{
                    for(int i = sol[dron_aleatorio][indice].indice; i < newsol.indice; i++ ){
                        sol[dron_aleatorio][i] = null;
                    }
                    sol[dron_aleatorio][newsol.indice] = newsol;                    
                    for(int i = newsol.indice+1; i < newsol.indice+newsol.duracion;i++){
                        sol[dron_aleatorio][i] = newsol2;
                    }
                }
            }
            switch(ant.accion){
                //Cancelar delivery
                case 'D':
                    //Sumamos en ordes
                    for (int i = iteracion_aleatoria+ant.duracion-1; i < TIME; i++) {
                        orders[ant.destino][i][ant.idproducto]+=ant.cantidad;
                        productos_en_dron[dron_aleatorio][i][ant.idproducto]+=ant.cantidad;
                        order_total[dron_aleatorio][i]+=ant.cantidad;
                        dron_peso[dron_aleatorio][i] += ant.cantidad*pesos_productos[ant.idproducto];
                    }
                    //Se cancela el completado si estuveira
                    order_completado[ant.destino] = -1; 
                break;
                case 'L':
                    for (int i = iteracion_aleatoria+ant.duracion-1; i < TIME; i++) {
                        //Restar cantidad productos del dron
                            //DRON*TIME*PRODUCTO
                        productos_en_dron[dron_aleatorio][i][ant.idproducto]-=ant.cantidad;
                        
                        //Sumar cantidad productos al warehouse
                        ware[ant.destino][i][ant.idproducto] += ant.cantidad;
                        
                        //int[][] dron_peso; //Meter en clone
                            //DRON*TIME
                        dron_peso[dron_aleatorio][i] -= ant.cantidad*pesos_productos[ant.idproducto];                        
                    }
                    
                    
                    break;
                case 'U':
                    //Sumamos en ordes
                    for (int i = iteracion_aleatoria+ant.duracion-1; i < TIME; i++) {
                        ware[ant.destino][i][ant.idproducto]-=ant.cantidad;
                        productos_en_dron[dron_aleatorio][i][ant.idproducto]+=ant.cantidad;
                        dron_peso[dron_aleatorio][i] += ant.cantidad*pesos_productos[ant.idproducto];
                    }
                break;
            }
            //Borrar Solcels de la accion
            for(int i = iteracion_aleatoria; i < accion.indice+accion.duracion; i++){
                sol[dron_aleatorio][i] = null;
            }
        }
        else{
            SolCel parte2accion = new SolCel(accion.accion,false,accion.duracion,
                            accion.indice,
                            accion.x_0,accion.y_0,
                            accion.x_d,accion.y_d,
                            accion.destino,
                            accion.idproducto,
                            accion.cantidad);
            //Buscar siguiente
            int salida;
            //For vacio
            for(salida = accion.indice + accion.duracion; salida < TIME && sol[dron_aleatorio][salida] == null; salida++){}
            //usar salida como indice de la siguiente accion
            
            if(salida != TIME){
                sol[dron_aleatorio][salida].x_0 = accion.x_d;
                sol[dron_aleatorio][salida].y_0 = accion.y_d;
                //Calcular dist euclidea
                int x1 = sol[dron_aleatorio][salida].x_0;
                int y1 = sol[dron_aleatorio][salida].y_0;
                int x2 = sol[dron_aleatorio][salida].x_d;
                int y2 = sol[dron_aleatorio][salida].y_d;
                int dist = distancia_euclidea(x1, y1, x2, y2)+1;

                //si la distancia es mayor, agrandar
                int dif = dist-sol[dron_aleatorio][salida].duracion;
                
                SolCel newsol = new SolCel(sol[dron_aleatorio][salida].accion,true,dist,
                            sol[dron_aleatorio][salida].indice-dif,
                            sol[dron_aleatorio][salida].x_0,sol[dron_aleatorio][salida].y_0,
                            sol[dron_aleatorio][salida].x_d,sol[dron_aleatorio][salida].y_d
                            ,sol[dron_aleatorio][salida].destino,
                            sol[dron_aleatorio][salida].idproducto,
                            sol[dron_aleatorio][salida].cantidad);

                SolCel newsol2 = new SolCel(sol[dron_aleatorio][salida].accion,false,dist,
                        sol[dron_aleatorio][salida].indice-dif,
                        sol[dron_aleatorio][salida].x_0,sol[dron_aleatorio][salida].y_0,
                        sol[dron_aleatorio][salida].x_d,sol[dron_aleatorio][salida].y_d
                        ,sol[dron_aleatorio][salida].destino,
                        sol[dron_aleatorio][salida].idproducto,
                        sol[dron_aleatorio][salida].cantidad);
                
                if(dif > 0){
                    
                    for(int i = newsol.indice+1; i < newsol.indice+newsol.duracion;i++){
                        sol[dron_aleatorio][i] = newsol2;
                    }
                    sol[dron_aleatorio][newsol.indice] = newsol;
                //si es menor, reducir
                }
                else{
                    for(int i = sol[dron_aleatorio][salida].indice; i < newsol.indice; i++ ){
                        sol[dron_aleatorio][i] = null;
                    }
                    sol[dron_aleatorio][newsol.indice] = newsol;                    
                    for(int i = newsol.indice+1; i < newsol.indice+newsol.duracion;i++){
                        sol[dron_aleatorio][i] = newsol2;
                    }
                }
            }
            switch(accion.accion){
                //Añadir delivery
                case 'D':
                    //Sumamos en ordes
                    for (int i = iteracion_aleatoria+accion.duracion; i < TIME; i++) {
                        orders[accion.destino][i][accion.idproducto]-=accion.cantidad;
                        productos_en_dron[dron_aleatorio][i][accion.idproducto]-=accion.cantidad;
                        order_total[dron_aleatorio][i]-=accion.cantidad;
                        if(order_total[dron_aleatorio][i] == 0 && order_completado[accion.destino] == -1){
                            order_completado[accion.destino] = i;
                        }
                        else if(order_total[dron_aleatorio][i] == 0 && order_completado[accion.destino] > i){
                            order_completado[accion.destino] = i;
                        }
                        dron_peso[dron_aleatorio][i] -= accion.cantidad*pesos_productos[accion.idproducto];
                    }
                break;
                case 'L':
                    for (int i = iteracion_aleatoria+accion.duracion-1; i < TIME; i++) {
                        //sumas cantidad productos del dron
                            //DRON*TIME*PRODUCTO
                        productos_en_dron[dron_aleatorio][i][accion.idproducto]+=accion.cantidad;
                        
                        //restar cantidad productos al warehouse
                        ware[accion.destino][i][accion.idproducto] -= accion.cantidad;
                        
                        //int[][] dron_peso; //Meter en clone
                            //DRON*TIME
                        dron_peso[dron_aleatorio][i] += accion.cantidad*pesos_productos[accion.idproducto];                        
                    }
                    break;
                case 'U':
                    for (int i = iteracion_aleatoria+accion.duracion-1; i < TIME; i++) {
                        ware[accion.destino][i][accion.idproducto]-=accion.cantidad;
                        productos_en_dron[dron_aleatorio][i][accion.idproducto]+=accion.cantidad;
                        dron_peso[dron_aleatorio][i] += accion.cantidad*pesos_productos[accion.idproducto];
                    }
                    break; 
            }

            //Añadir Solcels de la accion
            for(int i = iteracion_aleatoria; i < accion.indice+accion.duracion; i++){
                sol[dron_aleatorio][i] = parte2accion;
            }
            sol[dron_aleatorio][accion.indice] = accion;
        }
    }
    
    //Calcula el score en order_comp y lo devuelve
    // score = (T-t_k)/T*100
    public int calcular_coste(){
        
        int[][][] ware_cont = new int[WAREHOUSES][TIME][PRODUCTS];
        IntStream.range(0, WAREHOUSES)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        ware_cont[i][j] = this.DISPONIBILIDAD[i].clone();
                    }
                });
        int[][][] orders_cont = new int[ORDERS][TIME][PRODUCTS];
        IntStream.range(0, ORDERS)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        ware_cont[i][j] = this.ORDERS_SIN_T[i].clone();
                    }
                });
        
        for(int i=0; i < this.sol.length; i++){
            for(int j=0; j < this.sol[0].length; j++){
                if(this.sol[i][j].accion=='L')
                    if(this.sol[i][j].primero){
                        ware_cont[this.sol[i][j].destino][j+this.sol[i][j].duracion][this.sol[i][j].idproducto]--;
                        if(ware_cont[this.sol[i][j].destino][j+this.sol[i][j].duracion][this.sol[i][j].idproducto] == -1) return -1;
                    }
                if(this.sol[i][j].accion=='U')
                    if(this.sol[i][j].primero)
                        ware_cont[this.sol[i][j].destino][j+this.sol[i][j].duracion][this.sol[i][j].idproducto]++;
                if(this.sol[i][j].accion=='D')
                    if(this.sol[i][j].primero){
                        orders_cont[this.sol[i][j].destino][j+this.sol[i][j].duracion][this.sol[i][j].idproducto]--;
                        if(orders_cont[this.sol[i][j].destino][j+this.sol[i][j].duracion][this.sol[i][j].idproducto] == -1) return -1;         
                    }
            }
        }
        
        int suma=0;
        
        for(int i=0; i < this.order_total.length; i++){
            for(int j=0; j < this.order_total[0].length; j++){
                if(this.order_total[i][j]==0){
                    suma+=(  (this.TIME - j)/this.TIME  );
                    break;
                }
            }
        }
        
        return suma;
    }
    
    //Escribir el resultado final en el fichero para evaluar
    public void generar_salida(String file_out){
        
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        Object obj = super.clone();
        Solucion s = (Solucion)obj;
        // sol --> DRONES*TIME
        s.sol = new SolCel[DRONES][TIME];
        IntStream.range(0, DRONES)
                .parallel()
                .forEach(i->{
                    System.arraycopy(this.sol[i], 0, s.sol[i], 0, TIME);
                });
        
        //ware --> WARHOUSES*TIME*PRODUCTS
        s.ware = new int[WAREHOUSES][TIME][PRODUCTS];
        IntStream.range(0, WAREHOUSES)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        s.ware[i][j] = this.ware[i][j].clone();
                    }
                });
        
        //orders --> ORDERS*TOME*PRODUCTS
        s.orders = new int[ORDERS][TIME][PRODUCTS];
        IntStream.range(0, ORDERS)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++){
                        s.orders[i][j] = this.orders[i][j].clone();
                    }
                });
        //ware_pos --> indica la posicion de los warhouses
        s.ware_pos = this.ware_pos.clone();
        
        //order_pos --> indica la posicion del destino
        s.order_pos = this.order_pos.clone();
        
        //order_completado --> indica la t(tiempo) en el que se hemos completado el pedido
        s.order_completado = this.order_completado.clone();
        //order_total --> Indica el numero total de productos que le quedan a cada pedido;
        // ORDERS*TIME
        s.order_total = new int[ORDERS][TIME];
        IntStream.range(0, ORDERS)
                .parallel()
                .forEach(i->{
                    s.order_total[i] = this.order_total[i].clone();
                });
        
        //pesos_drones --> para cada indice que indica el producto, tenemos el peso del mismo
        s.pesos_productos = this.pesos_productos.clone();
        
        //DRON*TIME*PRODUCTO
        //int[][][] productos_en_dron; //Meter en clone
        //DRON*TIME
        //int[][] dron_peso; //Meter en clone
        s.productos_en_dron = new int[DRONES][TIME][PRODUCTS];
        IntStream.range(0, DRONES)
                .parallel()
                .forEach(i->{
                    for(int j=0; j<TIME; j++)
                        s.productos_en_dron[i][j] = this.productos_en_dron[i][j].clone();
                });
        
        s.dron_peso = new int[DRONES][TIME];
        IntStream.range(0, DRONES)
                .parallel()
                .forEach(i->{
                    s.dron_peso[i] = this.dron_peso[i].clone();
                });
        
        return s;
    }
    
    public double getScore(){
        return score;
    }
}

