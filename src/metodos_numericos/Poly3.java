/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metodos_numericos;

import java.lang.Math;

// Classe representando os polinômios de Grau 3
public class Poly3
{
    private double a,b,c,d;
    private double xj;


    public Poly3()
    {
        a=0; b=0; c=0; d=0; xj=0;
    }

    // Polinômio = a + b*x^2 + c*x^3
    public void DefinePoly(double a, double b, double c, double d, double x)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.xj = x;
    }

    // Avalia o polinômio em um valor X
    public double Horner(double X)
    {
        double Y;
        Y = a + b*(X-xj) + c*Math.pow((X-xj),2) + d*Math.pow((X-xj),3);
        return Y;
    }

    public void Disp()
    {
        System.out.println("a: " + a + ", b: " + b + ", c: " + c + ", d: " + d + ", X: " + xj + "\n");
    }

}
