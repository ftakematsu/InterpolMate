/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author murilo
 */
public class Suporte implements Serializable {
    
    private float angulo_alfa_sup; //angulo de inclinacao do suporte
    
    private ArrayList<EntrenoSuporte> EN;
 
    private ArrayList<Galho> Galhos;
    
    private int cod_pai;
    
    
    //construtor:
    public Suporte ()
    {
        EN = new ArrayList<EntrenoSuporte>();
        Galhos = new ArrayList<Galho>();
    }
    
    
    
    public void adicionarEntreno(double medida)
    {
        int codigo_do_entreno = EN.size()+1;
       
        EN.add(new EntrenoSuporte(codigo_do_entreno, medida));
    }
    
    
    
    
    public void adicionarGalho(Galho G)
    {
        Galhos.add(G);
    }
 
    public Galho getGalho(int index)
    {
        return Galhos.get(index);
    }
    
    public void setCodPai(int codigo_pai)
    {
        cod_pai = codigo_pai;
    }
    
    public int getCodPai()
    {
        return cod_pai;
    }
    
    public void setAnguloAlfa(float alfa)
    {
        angulo_alfa_sup = alfa;
    }
    
    public float getAnguloAlfa()
    {
        return angulo_alfa_sup;
    }  
    
    public ArrayList<EntrenoSuporte> recebeListaEntreno()
    {
        return EN;
    }
    
    public EntrenoSuporte getEntreno(int index)
    {
        return EN.get(index);
    }
    
    public int getQtdeEntrenos()
    {
        return EN.size();
    }
    
    public int getQtdeGalhos()
    {
        return Galhos.size();
    }

    
    public void imprimeListaEntreno()
    {   
        for (int i=0; i<EN.size(); i++)
        {
            //System.out.println("(SUPORTE) codigo: " + EN.get(i).getCod() + ", medida: " + EN.get(i).getComp());
        }   
    }
    

}
