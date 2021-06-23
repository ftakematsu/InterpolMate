/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;

import interpolmate.Database;

/**
 *
 * @author Fabio
 */
public class TesteDiaMes {
    
    private static int NumeroUC_Atual(double entrada_X)
    {
        if (entrada_X>0 && entrada_X<=90) return 1;
        else if (entrada_X>=91 && entrada_X<=225) return 1;
        else if (entrada_X>=226 && entrada_X<=239) return 1;
        else if (entrada_X>=240 && entrada_X<=330) return 2;     
        else if (entrada_X>=331 && entrada_X<=420) return 2;
        else if (entrada_X>=421 && entrada_X<=540) return 3;
        else if (entrada_X>=541 && entrada_X<=615) return 3;
        else if (entrada_X>=616 && entrada_X<=705) return 4;
        return 4;
    };
    
    private  static int RetornaMesUC(int diaX, int reg_uc)
    {
        int dia_ref = 0;
        if (reg_uc==1) dia_ref = 0;
        else if (reg_uc==2) dia_ref = 7;
        else if (reg_uc==3) dia_ref = 14;
        else if (reg_uc==4) dia_ref = 20;
        
        return (diaX/30)-dia_ref;
    };
    
    
    public static void main(String[] args) {
        for (int i=0; i<Database.diaX25.length; i++)
        {
            System.out.println(" Mes " + i + " retorno: "+ RetornaMesUC((int)Database.diaX25[i],1) );
        }
    }
    
}
