/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import jxl.Sheet;

/**
 *
 * @author murilo
 */



public class ConversorXLSParaTexto 
{
    
    private String nome_arquivo; //o nome do arquivo Texto (MTG) a ser escrito
    private Sheet sheet;         //possui os dados em XLS a serem escritos em texto (.mtg)


    //construtor:
    public ConversorXLSParaTexto(String nome_arq, Sheet s)
    {
        nome_arquivo = nome_arq;
        sheet = s;
    }
    
    
    public void converte() throws FileNotFoundException
    {
    
        PrintWriter prwriter = new PrintWriter(new FileOutputStream(nome_arquivo));


        for (int l=0; l<sheet.getRows(); l++)
        {
            for (int c=0; c<sheet.getColumns(); c++)
            {
                String str_celula = sheet.getCell(c,l).getContents().replace(',', '.');
                
                if (c>0) prwriter.print("\t");
                
                
                //escreve no arquivo, filtrando a coluna da data (para nao ter problemas)
                if (c!=LeituraMTG.COLUNA_DAY)
                {
                    prwriter.print(str_celula); //Escreve no arquivo
                }
                else
                {
                    if (str_celula.compareTo("Day")==0)
                        prwriter.print(str_celula); //Escreve no arquivo
                }
                    
            }
            prwriter.print("\n");
        }
        
        prwriter.close();
        
    }
    
    
}
