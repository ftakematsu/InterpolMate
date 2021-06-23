/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;

import metodos_numericos.Splines;

/**
 *
 * @author Fabio
 */
public class DaylyStepModel {
    
    // Transforma um conjunto de valores de escala 
    public static double[] DayScale(int qtd_mes, int dias, double diaX[], double p[])
    {
        double soma = 0;
        double a[] = new double[qtd_mes]; // Vetor que guardará os valores acumulados
        double v[] = new double[dias];
        Splines s = new Splines();
        
        a[0] = p[0];
        
        for (int i=1; i<qtd_mes; i++)
        {
            soma = soma + p[i];
            a[i] = soma;
        }
        
        System.out.println("a " + diaX.length + " b " + a.length);
        
        s.CalculaSplineCubico(diaX, a); // Calcula splines para os valores mensais acumulados
        
        for (int d=1; d<dias; d++)
        {
            v[d] = s.AvaliaFuncaoSpline(diaX, d); // Aqui gerou-se o vetor resultante da transformação na escala de dias
        }
        return v;
    };
    
    public static int[] DayScaleDiscreto(int qtd_mes, int dias, double diaX[], double p[])
    {
        double soma = 0;
        double a[] = new double[qtd_mes]; // Vetor que guardará os valores acumulados
        int v[] = new int[dias];
        Splines s = new Splines();
        
        a[0] = p[0];
        
        for (int i=1; i<qtd_mes; i++)
        {
            soma = soma + p[i];
            a[i] = soma;
        }
        
        System.out.println("a " + diaX.length + " b " + a.length);
        
        s.CalculaSplineCubico(diaX, a); // Calcula splines para os valores mensais acumulados
        
        for (int d=1; d<dias; d++)
        {
            v[d] = (int)Math.ceil(s.AvaliaFuncaoSpline(diaX, d)); // Aqui gerou-se o vetor resultante da transformação na escala de dias
        }
        return v;
    };
    
    
    public static int[] CalculaDiferencaDisc(int v[], int dias)
    {
        // Calcula diferenças para ser utilizado na simulação daily-step
        int dv[] = new int[v.length];
        for (int d=1; d<dias; d++)
        {
            dv[d] = (int)Math.round(v[d]-v[d-1]); // Transforma em inteiro
            if (dv[d]<0) dv[d] = 0;
        }
        return dv;
    };
    
    
    public static void PrintVetor2(String s, double v[], int len)
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < len; i++)
        {
                System.out.print(", " + v[i]);
                if ((double)i%30==0) System.out.println();
        }
    };
    
    public static void PrintVetor2(String s, int v[], int len)
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < len; i++)
        {
                System.out.print(", " + v[i]);
                if (i%30==0) System.out.println();
        }
    };
    
    public static int SomaVet(int v[])
    {
        int soma = 0;
        for (int i=0; i<v.length; i++)
        {
            soma+= v[i];
        }
        return soma;
    };
    
    public static void main(String[] args){
        int a_vetor[];
        double p[] = { 2.50858692, 0.228053356, 0.0, 5.33301377, 13.20133391, 8.552480207, 1.456106713, 0.0, 0.228053356, 8.289120483, 0.0, 0.0, 0.0, 0.0, 0.0, 4.148853701, 19.20133391, 3.684160069, 0.0, 0.0, 2.052480207, 15.26240103, 0.0, 0.0, 0.0};
        double diasX[] = {1,30,60,105,135,165,180,225,240,285,315,330,360,390,420,450,495,525,540,585,615,645,675,705,720};
        
        
        
        
        a_vetor = DayScaleDiscreto(25,720,diasX,p);
        PrintVetor2(" => ", a_vetor, a_vetor.length);

        PrintVetor2(" => ", CalculaDiferencaDisc(a_vetor,a_vetor.length), a_vetor.length);
        
        System.out.println("SOMA ==> " + SomaVet(CalculaDiferencaDisc(a_vetor,a_vetor.length)));
    }
}
