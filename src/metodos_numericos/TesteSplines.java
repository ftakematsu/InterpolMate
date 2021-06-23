/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metodos_numericos;

/**
 *
 * @author Administrador
 */
public class TesteSplines {

    public static void main(String[] args)
    {
        double X[] = {1,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
        double Y[] = {1.04,1.76,0.85,3.47,1.56,3.06,3.66,2.36,1.02,0.26,0.56,1.43,1.24,1.60,3.27,0.61,0.78,0.00,0.00,0.00,0.00,1.86,4.01,3.40,1.60,0.07,0.11,0.00,0.00,0.00,2.93,2.93,1.65,0.69,0.07,0.48,0.43,0.00};
        
        Splines S = new Splines();
        S.CalculaSplineCubico(X, Y);
        double x_atual, entrada_X;
        entrada_X = 475;

        double folhas_emitidas_atual = 0;
        
        for (x_atual=1; x_atual<=entrada_X; x_atual++)
        {
            folhas_emitidas_atual = folhas_emitidas_atual + S.AvaliaFuncaoSpline(X, x_atual);
        }

        System.out.println(" Somatorio acumulado = " + (int)folhas_emitidas_atual);
        
//        System.out.println(" S = " + S.AvaliaFuncaoSpline(0, 15));
//        System.out.println(" S* = " + S.AvaliaFuncaoSpline(X,15));
//        System.out.println(" S = " + S.AvaliaFuncaoSpline(1, 45));
//        System.out.println(" S* = " + S.AvaliaFuncaoSpline(X,45));
//        System.out.println(" S = " + S.AvaliaFuncaoSpline(2, 70));
//        System.out.println(" S* = " + S.AvaliaFuncaoSpline(X,70));
//        System.out.println(" S = " + S.AvaliaFuncaoSpline(3, 100));
        //System.out.println("\n\n\n CONSTANTES");
        //S.MostraConst();
    }

}
