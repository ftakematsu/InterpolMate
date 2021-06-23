
package metodos_numericos;

import java.util.ArrayList;
import java.io.*;
import java.util.Collections;
/**
 *
 * @author Fabio
 */
public class Splines
{
    private ArrayList<Poly3> P = new ArrayList<Poly3>();


    // Calcula funções Splines nos pontos (X,Y)
    public void CalculaSplineCubico(double X[], double Y[])
    {
        int i;
        int n = X.length;

        System.out.println(" nX = " + X.length + " nY = " + Y.length);

        // Declaração das constantes dos polinômios
        double a[] = new double[X.length+1];
        double b[] = new double[X.length+1];
        double c[] = new double[X.length+1];
        double d[] = new double[X.length+1];
        double h[] = new double[X.length+1];
        double alfa[] = new double[X.length+1];
        double l[] = new double[X.length+1];
        double u[] = new double[X.length+1];
        double z[] = new double[X.length+1];

        // Valor de a[]
        a = Y;

        // Passo 1: Determinando os valores de h(i)
        // Os termos X(i+1)-X(i) serao usados varias vezes na determinacao dos Splines, por isso, ele sera simplificado na notacao h(i)
        for (i=0; i<n-1; i++)
            h[i] = X[i+1] - X[i];

        //for (i=0; i<n-1; i++) System.out.print("\nh:\t" + h[i]);

        // Passo 2: Determinando os valores de Alfa
        alfa[0] = 0;
        for (i=1; i<n-1; i++)
            alfa[i] = (3/h[i])*(a[i+1]-a[i]) - (3/h[i-1])*(a[i]-a[i-1]);

        //for (i=0; i<n-1; i++) System.out.print("\nAlfa:\t" + alfa[i]);

        // * Resolvendo o sistema linear tridiagonal (passos 3-6)
        // Método utilizando o algoritmo de Crout para Sistemas Lineares Tridiagonais
        l[0] = 0; u[0] = 0; z[0] = 0;

        // Passo 4
        //System.out.println("\n\nVetor L\t\t\tVetor U\t\t\tVetor Z");
        for (i=1; i<n-1; i++)
        {
            l[i] = 2*(X[i+1]-X[i-1]) - h[i-1]*u[i-1];
            u[i] = h[i]/l[i];
            z[i] = (alfa[i] - h[i-1]*z[i-1])/l[i];
            //System.out.println(l[i] + "\t\t" + u[i] + "\t\t" + z[i]);
        }

        // Passo 5
        l[n] = 0; z[n] = 0;
        c[n-1] = 0;

        // Passo 6
        // Calculando a função Spline em si
        //System.out.println("\n\n Calculando Splines");
        int npol = 0;
        Poly3 pol[] = new Poly3[n+1];

        for (i=0; i<n+1; i++)
            pol[i] = new Poly3();

        for (int j=n-2; j>=0; j=j-1)
        {
            
            c[j] = z[j]-u[j]*c[j+1];
            b[j] = (a[j+1]-a[j])/h[j] - h[j]*(c[j+1] + 2*c[j])/3;
            d[j] = (c[j+1]-c[j])/(3*h[j]);

            //System.out.println("c: " + c[j] + ", b: " + b[j] + ", d: " + d[j] + ", X: " + X[j]);
                    
            pol[npol].DefinePoly(a[j], b[j], c[j], d[j], X[j]);
            P.add(pol[npol]);
            //P[npol] = pol[npol];
            npol++;
        }
        // Arrumando a Array para que as funções Splines fiquem na posição correta
        Collections.reverse(P);
    }

    
    // Avalia um ponto X na função Spline p
    public double AvaliaFuncaoSpline(int p, double X)
    {
        Poly3 pol = new Poly3();
        pol = P.get(p);
        return pol.Horner(X);
    }


    // Avalia um ponto x detectando automaticamente a função Spline correspondente à esse ponto
    public double AvaliaFuncaoSpline(double X[], double x)
    {
        int p = 0; // Função Spline p
        Poly3 pol = new Poly3();
        
        

        while (!(x>=X[p] && x<=X[p+1]))
        {
            p++;
        }
        
        pol = P.get(p);
        
        if (pol.Horner(x)<0) return 0;
        else return pol.Horner(x);
    }
    

    public void MostraConst()
    {
        //System.out.println("\n\nVALOR");
        for (int i=0; i<P.size(); i++)
        {
            Poly3 pol = new Poly3();
            pol = P.get(i);
            pol.Disp();
        }
    }

}
