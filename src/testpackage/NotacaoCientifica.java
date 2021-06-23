/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;

import java.io.*;
import java.lang.Math;
import interpolmate.*;

/**
 *
 * @author Administrador
 */
public class NotacaoCientifica {

    public static void main(String[] args) {

        System.out.println("" + (char)92);
        for (int i=0; i<256; i++)
        {
            System.out.println(" decimal: " + i + "\tASCII: " + (char)i);
        }


        
        FuncaoMatematica funcao_aux = new FuncaoMatematica();
        
        double mol = 6.0221415E+23;
        double num;

        funcao_aux.DefinirEquacao(123451.8, -170.50216, 0.087638061, -39419360, 4.6843167E+09, 0, 0);

        num = funcao_aux.ValorY_Funcao2(495);

        //System.out.println(" ValorY: " + num);
        //System.out.println(" Chao Teto: " + Math.ceil(13.4564));
        //System.out.println(" Elevado: " + Math.pow(9, 1.7582));
        //System.out.printf("\n%.7e\n", mol);

        double Vet[] = new double[5];
        F1(2,Vet);

        for (int i =0; i<Vet.length; i++)
        {
            System.out.println("Vet: " + Vet[i]);
        }
    }

    public static void F1(double xxx, double Vet[])
    {
        for (int i =0; i<Vet.length; i++)
        {
            Vet[i] = xxx;
            xxx++;
        }
    }



}
