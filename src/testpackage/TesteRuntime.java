/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;

import java.io.IOException;
import java.lang.Runtime;


public class TesteRuntime {
    
    public static void main(String args[]) throws IOException
    {
        System.out.println("\nTeste da classe Runtime\n");
        Runtime rt = Runtime.getRuntime();
        String caminho_amapmod = "xxx";

        // Executa o prompt de comando cmd
        Process p = rt.exec("cmd.exe /c start dir");
    }
}
