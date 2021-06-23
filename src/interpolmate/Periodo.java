/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author MURILO
 */
public class Periodo {

    
    private Date DataInicial; //data do MTG inicial
    private Date DataFinal; //data do MTG final
    
    private int diferenca_dias; //diferença de dias entre os dois MTGs base
    
    
    public Periodo()
    {
    }
    
    
    
     /**
     * Armazena a data inicial e final e calcula o numero de dias entre as 2 datas.
     *
     * @param d1    a data inicial
     * @param d2    a data final
     *
     * @return      O numero de dia entre duas datas.
     *              ou,     
     *              -1 caso a "data final" inserida como parametro seja maior
     *              que a data inicial.
     */
    public int setDatasEGetDiasEntre (Date Data_inic, Date Data_fin)
    {
                
         DataInicial = Data_inic;
         DataFinal = Data_fin;
    
         
        Calendar d1 = Calendar.getInstance(); //data do MTG inicial
        d1.setTime(DataInicial);
        
        Calendar d2 = Calendar.getInstance(); //data do MTG final
        d2.setTime(DataFinal);
        
        if (d1.after(d2))  
        {  // se a data inicial for maior que a data final:
            return -1; //"-1" representando ERRO
        }

        //recebe a diferença do numero de dias do ano atual das datas
        diferenca_dias = d2.get(java.util.Calendar.DAY_OF_YEAR) - d1.get(java.util.Calendar.DAY_OF_YEAR);

        //se os anos das datas forem diferentes:
        if (d1.get(java.util.Calendar.YEAR) != d2.get(java.util.Calendar.YEAR)) 
        {
            d1 = (java.util.Calendar) d1.clone(); //copia a data

            //enquanto o ano da "data inicial" nao alcançar o ano da "data final":
            while (d1.get(java.util.Calendar.YEAR) != d2.get(java.util.Calendar.YEAR)) 
            {
                diferenca_dias += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR); //adiciona em "dias" o numero total de dias do ano
                d1.add(java.util.Calendar.YEAR, 1); //adiciona mais um ano para a "data inicial"
            } 
        }

        return diferenca_dias; //retorna a diferença de numero de dias total  
    }
    
   
    public int getDiferencaDias ()
    {
        return diferenca_dias;
    }
    
    
    
}


