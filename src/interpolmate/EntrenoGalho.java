/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author murilo
 */
public class EntrenoGalho implements Serializable  {

    private int cod;    //ranking
    private int ordem;  //ordem de ramificacao
    private double comp; //comprimento
    private int un; //unidade de crescimento

    private boolean tem_folha; //flag indicando se este entrenó tem folha
    private double area_folha; //area foliar deste entreno
    private double alfa_folha; //angulo alfa da folha deste entreno
    
    private boolean tem_ram; //flag indicando se este entrenó tem ramificacao
    
    private ArrayList<EntrenoGalho> EN;
    
    
    //Construtor simples:
    public EntrenoGalho(int codigo, int ord, int unid_cresc)
    {
        cod = codigo;
        ordem = ord;
        un = unid_cresc;
        
        EN = new ArrayList<EntrenoGalho>();
    }
            
    //Construtor sem passar a folha junto por parametro:
    public EntrenoGalho(int codigo, int ord, double comprimento, int unid_cresc, boolean tem_ramif)
    {
        cod = codigo;
        ordem = ord;
        comp = comprimento;
        un = unid_cresc;
        tem_ram = tem_ramif;
        
        tem_folha = false;
        
        EN = new ArrayList<EntrenoGalho>();
    }
    
    //Construtor passando a folha junto por parametro:
    public EntrenoGalho(int codigo, int ord, double comprimento, int unid_cresc, boolean tem_ramif, double area_f, double alfa_f)
    {
        cod = codigo;
        ordem = ord;
        comp = comprimento;
        un = unid_cresc;
        tem_ram = tem_ramif;
        
        tem_folha = true;
        area_folha = area_f;
        alfa_folha = alfa_f;
        
        EN = new ArrayList<EntrenoGalho>();
    }    
    
    
    public void setCod(int codigo) { cod = codigo; }
    public void setOrdem(int ord) { ordem = ord; }    
    public void setComp(double comprimento) { comp = comprimento; }
    public void setUN(int unid_cresc) { un = unid_cresc; }
    public void setTemRamificacao(boolean tem_ramif) { tem_ram = tem_ramif; }
    public void setTemFolha(boolean tem_fol) { tem_folha = tem_fol; }
    public void setAreaFolha(double area_f) { area_folha = area_f; }
    public void setAlfaFolha(double alfa_f) { alfa_folha = alfa_f; }
    
    public int getCod() { return cod; }
    public int getOrdem() { return ordem; }    
    public double getComp() { return comp; }
    public int getUN() { return un; }
    public boolean temRamificacao() { return tem_ram; }
    public boolean temFolha() { return tem_folha; }
    public double getAreaFolha() { return area_folha; }
    public double getAlfaFolha() { return alfa_folha; }
    

    public void adicionarEntreno(int ordem, double medida, int un, boolean tem_ram)
    {
        int codigo_do_entreno = EN.size()+1;
          
        EN.add(new EntrenoGalho(codigo_do_entreno, ordem, medida, un, tem_ram));
    }
    
    
    public void adicionarEntreno(int ordem, double medida, int un, boolean tem_ram, double area_folha, double alfa_folha)
    {
        
        int codigo_do_entreno; 
        
        if (EN.isEmpty()) codigo_do_entreno = 1;
        else              codigo_do_entreno = EN.size()+1;
            

        EN.add(new EntrenoGalho(codigo_do_entreno, ordem, medida, un, tem_ram, area_folha, alfa_folha));
    }
    
    
    
    public EntrenoGalho getEntreno(int index)
    {
        return EN.get(index);
    }
    
    public EntrenoGalho getUltimoEntreno()
    {
        int tamanho;
        
        if (EN.isEmpty()) tamanho = 0;
        else              tamanho = EN.size();
        
        return EN.get(tamanho-1);
    }
    
    public ArrayList<EntrenoGalho> recebeListaEntreno()
    {
        return EN;
    }
    
    public int getQtdeEntrenosRam()
    {
        return EN.size();
    }
   
    
    //recebe a quantidade de entrenos absoluta de toda a ramificacao
    public int getQtdeEntrenosRamAbsoluta()
    {  
        int contador=0; //inicializa o contador

        for (int i=0; i<EN.size(); i++) //para cada entreno deste entreno 
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                contador = LeQtdeEntrenosRamificacao(EN.get(i), contador); 
            }
            
            contador++; //incrementa o contador de entrenos lidos ate o momento.
        }
        
        return contador; //retorna a quantidade de entrenos absoluta
    }  
    
    
    
     /* metodo para ler a quantidade de entrenos recursivamente num eixo 
    (sempre que encontra uma ramificacao, chama a mesma funcao para contar o numero de entrenos). 
    Recebe por parametro o entreno e o contador atual, e retorna o contador de entrenos atualizado 
     */
    private int LeQtdeEntrenosRamificacao(EntrenoGalho EN, int contador)
    {
        for (int k=0; k<EN.getQtdeEntrenosRam(); k++) //para cada entreno da ramificacao do entreno passado por parametro:
        {                      
            if (EN.getEntreno(k).temRamificacao()) //se este entreno da ramificacao possui tambem uma ramificacao, 
            {      
                 contador = LeQtdeEntrenosRamificacao(EN.getEntreno(k), contador); //chama a mesma funcao recursivamente.
            }
      
            contador++; //incrementa o contador
        }
       
        return contador; //retorna o contador de entrenos atualizado
    }
    
    //recebe a quantidade de ramificacoes relativa (apenas deste eixo)
    public int getQtdeRamRelativa()
    {
        int qtde_rams = 0;
        
        for (int i=0; i<EN.size(); i++)
        {
            if (EN.get(i).temRamificacao())
                    qtde_rams++;
        }
                
        return qtde_rams;        
    }  
    
    //recebe a quantidade de ramificacoes de todo este galho (incluindo todas as ramificacoes de ramificacoes)
    public int getQtdeRamAbsoluta()
    {
        int qtde_rams=0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do eixo principal deste galho
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   
                qtde_rams++; //incrementa a quantidade de ramificacoes lidas ate o momento.
                
                //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                qtde_rams = LeQtdeRamRamificacao(EN.get(i), qtde_rams); 
            }
            
        }
        
        return qtde_rams; //retorna a quantidade de entrenos de todo o galho
    }      
    
    /* metodo para ler a quantidade de ramificacoes recursivamente num eixo 
    (sempre que encontra uma ramificacao, chama a mesma funcao para contar o numero de entrenos). 
    Recebe por parametro o entreno e o contador atual, e retorna o contador de ramificacoes atualizado 
     */    
    private int LeQtdeRamRamificacao(EntrenoGalho EN, int qtde_rams)
    {
        for (int k=0; k<EN.getQtdeEntrenosRam(); k++) //para cada entreno da ramificacao do entreno passado por parametro:
        {                      
            if (EN.getEntreno(k).temRamificacao()) //se este entreno da ramificacao possui tambem uma ramificacao, 
            {      
                 qtde_rams++; //incrementa o contador
                 
                 qtde_rams = LeQtdeRamRamificacao(EN.getEntreno(k), qtde_rams); //chama a mesma funcao recursivamente.
            }
        }
       
        return qtde_rams; //retorna o contador de ramificacoes atualizado
    }    
    
    
    

    //recebe o comprimento total relativo da ramificacao 
    public double getComprimentoRamRelativa()
    {
        double comp_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
            comp_total = comp_total + EN.get(i).getComp();
          
        return comp_total;
    }   
    
    
    //recebe o comprimento total (absoluto) de toda ramificacao
    public double getComprimentoRamAbsoluto()
    {
        double comp_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno deste entreno
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                comp_total = LeComprimentoRamificacao(EN.get(i), comp_total); 
            }
            
            comp_total = comp_total + EN.get(i).getComp(); //incrementa o comprimento lido até o momento.
        }
        
        return comp_total; //retorna o comprimento absoluto deste galho
    } 
    
    
    
    /* metodo para ler o comprimento de todos os entrenos recursivamente num eixo 
    (sempre que encontra uma ramificacao, chama a mesma funcao para contar os comprimentos de entrenos). 
    Recebe por parametro o entreno e o contador atual, e retorna o contador do comprimento atualizado 
     */
    private double LeComprimentoRamificacao(EntrenoGalho EN, double comp_total)
    {
        for (int k=0; k<EN.getQtdeEntrenosRam(); k++) //para cada entreno da ramificacao do entreno passado por parametro:
        {                      
            if (EN.getEntreno(k).temRamificacao()) //se este entreno da ramificacao possui tambem uma ramificacao, 
            {      
                 comp_total = LeComprimentoRamificacao(EN.getEntreno(k), comp_total); //chama a mesma funcao recursivamente.
            }
      
            comp_total = comp_total + EN.getEntreno(k).getComp(); //incrementa o comprimento lido até o momento.
        }
       
        return comp_total; //retorna o contador de entrenos atualizado
    }
 
    
    
    
    //recebe a area foliar da ramificacao (relativa)
    public double getAreaFoliarRamRelativa()
    {
        double area_foliar_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
        {
            if (EN.get(i).temFolha()) 
            area_foliar_total = area_foliar_total + EN.get(i).getAreaFolha();
        }  
        return area_foliar_total;
    }   
    
    
    
    
    //recebe a area foliar total da ramificacao (absoluta)
    public double getAreaFoliarRamAbsoluta()
    {
        double area_foliar_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do entreno
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                area_foliar_total = LeAreaFoliarRamificacao(EN.get(i), area_foliar_total); 
            }
            
            if (EN.get(i).temFolha()) 
            area_foliar_total = area_foliar_total + EN.get(i).getAreaFolha(); //incrementa a area foliar lida até o momento.
        }
        
        return area_foliar_total; //retorna a area foliar absoluta deste galho
    } 
    
    
    
    /* metodo para ler a area foliar de todos os entrenos recursivamente num eixo 
    (sempre que encontra uma ramificacao, chama a mesma funcao para contar os comprimentos de entrenos). 
    Recebe por parametro o entreno e o contador atual, e retorna o contador do comprimento atualizado 
     */
    private double LeAreaFoliarRamificacao(EntrenoGalho EN, double area_foliar_total)
    {
        for (int k=0; k<EN.getQtdeEntrenosRam(); k++) //para cada entreno da ramificacao do entreno passado por parametro:
        {                      
            if (EN.getEntreno(k).temRamificacao()) //se este entreno da ramificacao possui tambem uma ramificacao, 
            {      
                 area_foliar_total = LeAreaFoliarRamificacao(EN.getEntreno(k), area_foliar_total); //chama a mesma funcao recursivamente.
            }
      
            if (EN.getEntreno(k).temFolha())
            area_foliar_total = area_foliar_total + EN.getEntreno(k).getAreaFolha(); //incrementa o comprimento lido até o momento.
        }
       
        return area_foliar_total; //retorna o contador de entrenos atualizado
    }
    
    
    //recebe a quantidade de folhas da ramificacao (relativa)
    public int getQtdeFolhasRamRelativa()
    {
        int qtde_folhas = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
        {
            if (EN.get(i).temFolha()) qtde_folhas++;    
        }
            
        return qtde_folhas;
    }   
    
    
    
    
    //recebe a quantidade de folhas da ramificacao (absoluta)
    public int getQtdeFolhasRamAbsoluta()
    {
        int qtde_folhas = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do entreno deste galho
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                qtde_folhas = LeQtdeFolhasRamificacao(EN.get(i), qtde_folhas); 
            }
            
            if (EN.get(i).temFolha()) qtde_folhas++; //incrementa a qtde de folhas lida até o momento.
        }
        
        return qtde_folhas; //retorna a qtde de folhas absoluta deste galho
    } 
    
    
    /* metodo para ler a quantidade de folhas de todos os entrenos recursivamente num eixo 
    (sempre que encontra uma ramificacao, chama a mesma funcao para contar os comprimentos de entrenos). 
    Recebe por parametro o entreno e o contador atual, e retorna o contador do comprimento atualizado 
     */
    private int LeQtdeFolhasRamificacao(EntrenoGalho EN, int qtde_folhas)
    {
        for (int k=0; k<EN.getQtdeEntrenosRam(); k++) //para cada entreno da ramificacao do entreno passado por parametro:
        {                      
            if (EN.getEntreno(k).temRamificacao()) //se este entreno da ramificacao possui tambem uma ramificacao, 
            {      
                 qtde_folhas = LeQtdeFolhasRamificacao(EN.getEntreno(k), qtde_folhas); //chama a mesma funcao recursivamente.
            }
      
            if (EN.getEntreno(k).temFolha()) qtde_folhas++; //incrementa a qtde de folhas lida até o momento.
        }
       
        return qtde_folhas; //retorna o contador de folhas atualizado
    };
    
    
    public void CresceFolha(double af_inc)
    {
        area_folha = area_folha + af_inc;
    }

    
    // ***************************************
    // * Novas funções
    // ***************************************


}
