/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.File;
import java.util.Date;

/**
 *
 * @author murilo
 */
public class MTGbase {
    
    
    private boolean inicial; //indica se o mtg eh o inicial da interpolacao ou nao. true = inicial e false = final
    private File Arquivo; //referencia ao arquivo contendo o mtg base.
    private Date Data;
    private String Data_str;
    private boolean extensao_xls; //verdadeiro se o arquivo estiver no formato de Excel (xls), e falso se for texto (mtg)
    private Planta p; //estrutura que guardara toda  topologia da planta (apos ler de File Arquivo)
    
    
    
    public MTGbase (boolean inic) 
    {
        inicial = inic;      
    }
    
    public void setArquivo(File f)
    {
        Arquivo = f;
       
    }
    
    public File getArquivo()
    {
        return Arquivo;
    }  
       
    public String getNomeArquivo() //retorna o nome do arquivo
    {    
        return Arquivo.getName();
    }
    
    public String getNomeArquivoSemExtenxao() //retorna o nome do arquivo sem a extensao (desconta os ultimos 4 caracteres)
    {    
        String nome_arq = Arquivo.getName();
    
        //variável 'nome_arq1_sem_ext' recebe o nome do arquivo sem extensao
        String nome_arq_sem_ext = nome_arq.substring(0, nome_arq.length() - 4 );

        return nome_arq_sem_ext;
    }

    public void setData(Date d)
    {
        Data = d;
    }
    
    public Date getData()
    {
        return Data;
    }   
    
    public void setDataString(String str_data)
    {
        Data_str = str_data;
    }
    
    public String getDataString()
    {
        return Data_str;
    }    

    public boolean isInicial()
    {
        return inicial;
    }   
    
    //verifica se a extensa do arquivo deste MTGbase eh ".xls"
    public boolean isXLS()
    {
        String nome_arq = Arquivo.getName();
    
        //variável 'nome_arq1_sem_ext' recebe o nome do arquivo sem extensao
        String extensao = nome_arq.substring(nome_arq.length()-3);

        if (extensao.equals("xls"))
            return true; 
        else
            return false;
        
    }
    
    public Planta getPlanta()
    {
        return p;
    }  
    
    public void setPlanta(Planta planta)
    {
        p = planta;
    }      
    
    
}
