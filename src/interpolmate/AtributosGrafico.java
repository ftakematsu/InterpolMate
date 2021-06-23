/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

/**
 *
 * @author MURILO
 */
public class AtributosGrafico {

   private double compgalhos_no_est;
   private int folhassurgidas_no_est;
   private int folhascaidas_no_est;
   private double areafoliar_no_est;

   private int numero_metameros_no_est;
   private double tamanho_medio_folhas_no_est;


    
   /*public AtributosGrafico (double compgalhos, int folhassurgidas, int folhascaidas, double areafoliar)
   {
       compgalhos_no_est = compgalhos;
       folhassurgidas_no_est = folhassurgidas;
       folhascaidas_no_est = folhascaidas;
       areafoliar_no_est = areafoliar;
   }*/

   
   // Novo construtor
   public AtributosGrafico (double compgalhos, int folhassurgidas, int folhascaidas, double areafoliar, int num_met, double tam_folha)
   {
       compgalhos_no_est = compgalhos;
       folhassurgidas_no_est = folhassurgidas;
       folhascaidas_no_est = folhascaidas;
       areafoliar_no_est = areafoliar;
       numero_metameros_no_est = num_met;
       tamanho_medio_folhas_no_est = tam_folha;
   }



   public double getCompGalhos() { return compgalhos_no_est;}
   public int    getFolhasSurgidas() { return folhassurgidas_no_est;}
   public int    getFolhasCaidas() { return folhascaidas_no_est;}
   public double getAreaFoliar() { return areafoliar_no_est;}

   
    public int getNumeroMetamerosAtribGraf() {
        return numero_metameros_no_est;
    }

    public double getTamanhoMedioFolhasAtribGraf() {
        return tamanho_medio_folhas_no_est;
    }

    
}
