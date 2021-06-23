/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;

import java.io.*;

/**
 *
 * @author Fabio
 */
public class TesteArquivo {
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        File arq = new File("file.dat");
        PrintWriter new_file = new PrintWriter(new FileOutputStream(arq));
        
        System.out.println("Absolute path: " + arq.getAbsolutePath());
        System.out.println("Canonical path: " + arq.getCanonicalPath());
        System.out.println("Name: " + arq.getName());
        System.out.println("Parent: " + arq.getParent());
        System.out.println("Path: " + arq.getPath());
        
        
        
        InputStream is = new FileInputStream("teste.txt"); 
        
        //InputStreamReader é uma classe para converter os bytes em char 
        InputStreamReader isr = new InputStreamReader(is); 
        
           

        //BufferedReader é uma classe para armazenar os chars em memoria 
        BufferedReader br = new BufferedReader(isr); 
        String s = br.readLine(); //primeira linha 
        new_file.println(s);
        
        // A leitura está sendo feita por linha 
        while (s != null){ 
            System.out.println(s); 
            s = br.readLine();
            if (s!=null) new_file.println(s);
            
        } 
        
        new_file.append("<FIM DE ARQUIVO>");
        
        isr.close();
        new_file.close();
    }
}
