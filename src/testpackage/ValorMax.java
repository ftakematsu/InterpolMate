/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;
import interpolmate.*;
/**
 *
 * @author Administrador
 */
public class ValorMax {
    public static void main(String[] args)
    {
        FuncaoMatematica funcao1 = new FuncaoMatematica();
        FuncaoMatematica funcao2 = new FuncaoMatematica();
        FuncaoMatematica funcao3 = new FuncaoMatematica();
        FuncaoMatematica funcao4 = new FuncaoMatematica();
        double x;
        double max;

     funcao1.DefinirEquacao(-1420.0731,6.6302203,-0.011376819,132115.71,-4475062.2, 0, 0);
        funcao2.DefinirEquacao(5214.9957,-12.870994,0.011735118,-924572.91,60551717, 0, 0);
        funcao3.DefinirEquacao(-137447.95,184.43593,-0.092524471,45374612,-5.5969247E+09, 0, 0);
        funcao4.DefinirEquacao(1538588.9,-1581.1928,0.60848558,-6.6443092E+08,1.0744445E+11, 0, 0);

        max = 90;
        System.out.print(" UC1 ");
        for (x=90; x<=180; x++)
        {
            if (funcao1.ValorY_Funcao2(x) > funcao1.ValorY_Funcao2(max)) max = x;
        }
        System.out.println ("" + max);

        max = 241;
        System.out.print(" UC2 ");
        for (x=241; x<=330; x++)
        {
            if (funcao2.ValorY_Funcao2(x) > funcao2.ValorY_Funcao2(max)) max = x;
        }
        System.out.println ("" + max);

        max =451;
        System.out.print(" UC3 ");
        for (x=451; x<=540; x++)
        {
            if (funcao3.ValorY_Funcao2(x) > funcao3.ValorY_Funcao2(max)) max = x;
        }
        System.out.println ("" + max);

        max = 601;
        System.out.print(" UC4 ");
        for (x=601; x<=690; x++)
        {
            if (funcao4.ValorY_Funcao2(x) > funcao4.ValorY_Funcao2(max)) max = x;
        }
        System.out.println ("" + max);

    }


    
}
