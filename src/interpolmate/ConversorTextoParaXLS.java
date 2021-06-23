/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;


/**
 *
 * @author murilo
 */
public class ConversorTextoParaXLS {

    
    File arquivo;           //possui o arquivo Texto (MTG) a ser lido
    WritableSheet sheet;    //onde sera guardado os dados em XLS

    Date data;              //data do MTG
    
    
    //construtor:
    public ConversorTextoParaXLS(File arq, WritableSheet s)
    {
        arquivo = arq;
        sheet = s;
        data = new Date(); 
    }
    
    
   /* Funcao para tentar converter o arquivo que esta em texto (MTG) para o formato XLS: 
    * 
    *   Retorna: 'true' se conseguiu converter, ou 'false' se nao conseguiu converter.
    */
   public boolean converte() throws FileNotFoundException, IOException, WriteException
   {
        FileReader fr = new FileReader(arquivo); //leitor de arquivo (para poder percorrer seu conteudo)
        
        String str_temp="";
        int linhaAtual=0; //representara a linha atual da planilha em XLS sendo criada
        int colunaAtual=0; //representara a coluna atual da planilha em XLS sendo criada
        
        WritableCellFormat dateFormat = new WritableCellFormat(new DateFormat("dd/MM/yyyy"));
        
        //le o arquivo caractere por caracter ate encontrar o fim do arquivo: 
        for (int char_int = fr.read(); char_int >= 0; char_int = fr.read()) 
        {  
            char c = (char) char_int;
           
            if (c == '\n' || c =='\t') //se caracter for 'pula linha' ou 'tabulacao':
            {   

                if (str_temp.compareTo("")!=0) //se houver conteudo na string sendo concatenada:
                {
 
                    //LeituraMTG leitMTG = null; //instancia uma variavel de leitura apenas p/ pode resgatar sua constante COLUNA_DAY
                    
                    if (colunaAtual == LeituraMTG.COLUNA_DAY) //se estiver na coluna da data:
                    {
                        
                        if (str_temp.compareTo("Day")!=0) //se string nao for day  
                        {
                            if (ConverteStringParaData(str_temp)==true) //tenta converter de String para Date:
                            {                           
                                sheet.addCell(new DateTime(colunaAtual, linhaAtual, data, dateFormat)); //se conseguiu, adiciona uma celula de data na planilha
                            }
                            else //se nao conseguiu,
                            {   //retorna erro:                                     //"Erro ao converter a string '"        //"' para data."
                                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO34") + str_temp + InterpolMateView.colecaomsgs.getString("ERRO35"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);  
                                return false;
                            }
                        }
                    }
                    else //se nao estiver na coluna da data:
                    sheet.addCell(new Label(colunaAtual, linhaAtual, str_temp)); //adiciona o conteudo da String numa celula de label (na planilha)
                    
                }

                //ja que encontrou \n ou \t:
                str_temp = ""; //limpa a string sendo concatenada
                colunaAtual++; //e avanca para a proxima coluna
  
                
                if (c == '\n') //se pulou linha:
                {
                    linhaAtual++; //entao avanca para a proxima linha
                    colunaAtual=0; //e zera a coluna
                }
                
            }
            else //se caractere nao for 'pula linha' nem 'tabulacao':
            {
                str_temp = str_temp + c; //concatena este carcatere sendo lido na string
            }
        }        
        
        
        fr.close(); //fecha o leitor de arquivo apos ter chego ao final
       
        return true; //retorna true (se chegou ate aqui eh porque tudo deu certo)
       
       
   }
    


   
   
   /* Funcao para tentar converter uma string para a data deste objeto.
    * 
    *   Recebe por parametro: 
    *                  str:     string a ser convertida
    * 
    *   Retorna: 'true' se conseguiu converter, ou 'false' se nao conseguiu converter.
    */
    private boolean ConverteStringParaData(String str)
    {

        java.text.SimpleDateFormat formato; //objeto onde sera setado o formato da data

        if (str.length() == 8) //se string possuir 8 caracteres (provavelmente é XX/XX/XX)
        {
            formato = new SimpleDateFormat("dd/MM/yy"); //e seta o formato como sendo dd/MM/yy
        }
        else if (str.length() == 10) //se string possuir 10 caracteres (provavelmente é XX/XX/XXXX)
        {
            formato = new SimpleDateFormat("dd/MM/yyyy"); //e seta o formato como sendo dd/MM/yyyy
        }
        else return false; //se string nao possuir tamanho 8 nem 10, entao retorna falso.
 
        //System.out.println("-------> DIA: " + str.substring(0,2) + ", MES: " + str.substring(3,5) + ", ANO: " + str.substring(6));

        if (str.charAt(2) == '/' && str.charAt(5) == '/') //se caractere na posicao 2 ou 5 for barra
        {
            try 
            {  
                data = formato.parse(str);
                //System.out.println("Convertido de String para Data com sucesso");
            } 
            catch (java.text.ParseException e) 
            {   //se nao conseguir, erro:                       //"Erro ao converter a string '"             //"' para data."
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO34") + str + InterpolMateView.colecaomsgs.getString("ERRO35"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);  
                e.printStackTrace();  
                return false;
            }  

            return true; //se deu tudo certo, retorna true
        }
        else  return false; //se nao deu, retorna falso
    }
    
    

    
    
}
