/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.lang.Math; // Necessário para implementar as funções matemáticas

/**
 *
 * @author Fabio
 */
public class FuncaoMatematica {

    //final int GRAU = 6;
    private double a,b,c,d,e,f,g,h; // Os coeficientes
    private boolean constante;

    public FuncaoMatematica()
    {
        a=0;
        b=0;
        c=0;
        d=0;
        e=0;
        f=0;
        g=0;
        constante = false;
    }

    public void DefinirEquacao(double a, double b, double c, double d, double e, double f, double g)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
    }

    public void DefinirEquacao(double a, double b, double c, double d, double e, double f, double g, double h)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
    }

    public double ValorY(double x)
    {
        double y = 0;
        y = a + b*Math.log1p(x) + c*Math.pow(Math.log1p(x),2) + d*Math.pow(Math.log1p(x),3)  + e*Math.pow(Math.log1p(x),4) + f*Math.pow(Math.log1p(x),5) + g*Math.pow(Math.log1p(x),6);
        return y;
    }

    public double ValorY_Funcao2(double x)
    {
        double y = 0;
        y = a + b*x + c*Math.pow(x,2) + d/x + e/Math.pow(x,2);
        return y;
    }

    public double ValorY_Grau7(double x)
    {
        double y = 0;
        y = a + b*Math.log1p(x) + c*Math.pow(Math.log1p(x),2) + d*Math.pow(Math.log1p(x),3)  + e*Math.pow(Math.log1p(x),4) + f*Math.pow(Math.log1p(x),5) + g*Math.pow(Math.log1p(x),6) + h*Math.pow(Math.log1p(x),7);
        return y;
    }

    public double ValorY_Grau5(double x)
    {
        double y = 0;
        y = a + b*Math.log1p(x) + c*Math.pow(Math.log1p(x),2) + d*Math.pow(Math.log1p(x),3)  + e*Math.pow(Math.log1p(x),4) + f*Math.pow(Math.log1p(x),5);
        return y;
    }

    public double ValorY_Grau2(double x)
    {
        double y = 0;
        y = a + b*Math.log1p(x) + c*Math.pow(Math.log1p(x),2) + d*Math.pow(Math.log1p(x),3);
        return y;
    }



    // Função em observação
    public void DefineComoConstante()
    {
        a=0;
        b=0;
        c=0;
        d=0;
        e=0;
        f=0;
        g=0;
        h=0;
        constante = true;
    }

    public boolean Constante()
    {
        if (constante) return true;
        else return false;
    }

    public void ImprimeFuncao()
    {
        System.out.println( a + " + " + b + " x^(1) + " + c + " x^(2) + " + d +
                            " x^(3) + " + e + " x^(4) + " + f + " x^(5) + " + g + " x^(6)");
    }

}
