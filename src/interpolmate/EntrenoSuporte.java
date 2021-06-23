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
public class EntrenoSuporte implements Serializable  {

    private int cod;    //ranking
    private double comp; //comprimento
    
    
    
    public EntrenoSuporte(int codigo, double comprimento)
    {
        cod = codigo;
        comp = comprimento;
    }
    
    public int getCod() { return cod; }
    public double getComp() { return comp; }
    
}
