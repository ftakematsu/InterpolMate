/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;

import interpolmate.Database;
import metodos_numericos.*;

/**
 *
 * @author Administrador
 */
public class Calc_Prop {
    
    public static void main(String[] args){
        
        double diaX[] = {1,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
        double N_EN_SOMBRA[] = {1.64,5.08,6.74,11.21,15.34,20.91,24.75,27.89,29.04,30.01,31.47,33.97,35.96,39.10,44.29,45.04,46.06,46.06,45.97,45.95,45.95,51.34,57.22,60.93,63.32,63.43,63.54,63.52,63.52,63.52,66.50,69.68,78.97,80.11,80.20,80.67,81.11,81.11};
        double N_EN_SOL[] = {0.00,0.00,0.00,0.00,9.21,20.06,24.31,30.71,34.50,38.91,42.68,47.09,53.60,59.03,67.34,70.72,73.15,73.15,73.15,73.15,73.15,80.89,88.65,93.93,96.56,97.11,97.44,97.44,97.44,97.44,99.43,102.46,108.49,110.06,111.23,111.84,113.20,113.20};

        int obs_inicial = 11;
        int obs_final = 37;

        double[] X = new double[obs_final-obs_inicial+1];
        double[] Y = new double[obs_final-obs_inicial+1];

        //System.arraycopy(from[], fromIndex, to[], toIndex, count);
        System.arraycopy(diaX, obs_inicial, X, 0, obs_final-obs_inicial+1);
        System.arraycopy(N_EN_SOL, obs_inicial, Y, 0, obs_final-obs_inicial+1);

        double[] Comp_Galhos = new double[obs_final-obs_inicial+1];


        double comp_galho_planta = 13601.97;
        Comp_Galhos[0] = 1275.3;

        for (int i=1; i<X.length; i++)
        {
            Comp_Galhos[i] = comp_galho_planta*Y[i]/Y[X.length-1];
        }

        for (int i=0; i<Comp_Galhos.length; i++)
        {
            System.out.println("" + Comp_Galhos[i]);
        }
        
    }
}
