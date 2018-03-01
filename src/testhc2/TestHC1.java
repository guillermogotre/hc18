/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testhc2;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author guillermo
 */
public class TestHC1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        final String fichero_in = "src/input/busy_day.in";
        final String fichero_out = "src/salidas/busy_day.out";
        Parser parser;
        Problema problema;
        try {
            parser = new Parser(fichero_in);
            problema = parser.parse();
            
            problema.setFileDest(fichero_out);
            problema.start();
        } catch (Exception ex) {
            Logger.getLogger(TestHC1.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
        }
        
    }
    
}
