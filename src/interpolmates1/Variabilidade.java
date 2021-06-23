/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;
import interpolmate.*;
import metodos_numericos.Splines;

/**
 *
 * @author Fabio
 */
public class Variabilidade {
    // Interpolação da variabilidade com relação aos limites superiores e inferiores
    // Aumento do número de metâmeros
    Splines NMi = new Splines();
    Splines NMs = new Splines();
    // Alongamento de Caule
    Splines ACi = new Splines();
    Splines ACs = new Splines();
    // Aumento do número de folhas
    Splines ANFi = new Splines();
    Splines ANFs = new Splines();
    // Queda de folhas
    Splines QFi = new Splines();
    Splines QFs = new Splines();
    // Área foliar total
    Splines AFi = new Splines();
    Splines AFs = new Splines();
    // Tamanho médio de folhas
    Splines TFi = new Splines();
    Splines TFs = new Splines();
    // Comprimento médio de um entrenó
    Splines CENi = new Splines();
    Splines CENs = new Splines();
    
    int ambiente;
    String sexo;
    
    // Vetor de strings com os prefixos de todos os arquivos de variabilidade
    String file_name[] = {"anummet","acaule","anumfolhas","quedafolhas","areafoliar","amfolha","comp_ent"};
    String sufix = new String();
    
    public Variabilidade(Planta p)
    {
        sufix = "";
        if (p.getAmbiente().equals("SOL")) ambiente = 1;
        else ambiente = 2;
        sufix = ""+ambiente+p.getSexo()+".dat";
    }
    
    public Variabilidade(int ambient, String sex)
    {
        sufix = "";
        sufix = ""+ambient+sex+".dat";
    }
    
    
    
    
}
