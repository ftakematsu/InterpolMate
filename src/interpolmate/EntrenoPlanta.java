/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.Serializable;

/**
 *
 * @author murilo
 */
public class EntrenoPlanta implements Serializable {
    
    
    private int cod;    //ranking
    private double comp; //comprimento
    
    
    
    public EntrenoPlanta(int codigo, double comprimento)
    {
        cod = codigo;
        comp = comprimento;
    }
    
    public int getCod() { return cod; }
    public double getComp() { return comp; }

}
