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
public class Galho implements Serializable  {

    
    
    private float angulo_alfa; //angulo de inclinacao do suporte
    
    private ArrayList<EntrenoGalho> EN;
 
    private int cod_pai;
    
    
    //construtor:
    public Galho (float ang_alfa)
    {
        angulo_alfa = ang_alfa;
        
        EN = new ArrayList<EntrenoGalho>();
    }
    
    public float getCompTotalEP()
    {
        
        return 0;
    }
    
    
    public void adicionarEntreno(int ordem, double medida, int un, boolean tem_ram)
    {
        int codigo_do_entreno = EN.size()+1;
          
        EN.add(new EntrenoGalho(codigo_do_entreno, ordem, medida, un, tem_ram));
    }
    
    
    public void adicionarEntreno(int ordem, double medida, int un, boolean tem_ram, double area_folha, double alfa_folha)
    {
        int codigo_do_entreno = EN.size()+1;
          
        EN.add(new EntrenoGalho(codigo_do_entreno, ordem, medida, un, tem_ram, area_folha, alfa_folha));
    }
    
    public EntrenoGalho getUltimoEntreno()
    {
        int tamanho = EN.size();
        
        return EN.get(tamanho-1);
    }
    
    
    public void setCodPai(int codigo_pai)
    {
        cod_pai = codigo_pai;
    }
    
    public int getCodPai()
    {
        return cod_pai;
    }
    
    public void setAnguloAlfa(float alfa)
    {
        angulo_alfa = alfa;
    }
    
    public float getAnguloAlfa()
    {
        return angulo_alfa;
    }  
    
    
    
    //recebe a lista de entreno para associar aogalho
    public ArrayList<EntrenoGalho> recebeListaEntreno()
    {
        return EN;
    }
    
    //imprima a lista de entreno do eixo principal do galho
    public void imprimeListaEntreno()
    {
        
        for (int i=0; i<EN.size(); i++)
        {
            //System.out.println("(GALHO) codigo: " + EN.get(i).getCod() + ", medida: " + EN.get(i).getComp());
        }
        
    }
    
    
    //recebe a quantidade de entrenos do eixo principal 
    public int getQtdeEntrenosRelativa()
    {
        return EN.size();
    } 
    
    //recebe a quantidade de entrenos de todo esta galho (incluindo todos os entrenos de ramificacoes)
    public int getQtdeEntrenosAbsoluta()
    {
        
        int contador=0; //inicializa o contador
        

        for (int i=0; i<EN.size(); i++) //para cada entreno do eixo principal deste galho
        { 
            if (EN.get(i).temRamificacao()) //se este entreno tem uma ramificacao
            {   //entra na funcao recursiva, que ira retornar o valor do contador atualizado:
                contador = LeQtdeEntrenosRamificacao(EN.get(i), contador); 
            }
            
            contador++; //incrementa o contador de entrenos lidos ate o momento.
        }
        
        return contador; //retorna a quantidade de entrenos de todo o galho
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
    
    
    //recebe a quantidade de ramificacoes do eixo principal 
    public int getQtdeRamEixoPrincipal()
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
    
    
    
    //recebe o comprimento total do eixo principal 
    public double getComprimentoEixoPrincipal()
    {
        double comp_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
            comp_total = comp_total + EN.get(i).getComp();
          
        return comp_total;
    }   
    
    
    //recebe o comprimento total de todo o galho incluindo as ramificacoes 
    public double getComprimentoAbsoluto()
    {
        double comp_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do eixo principal deste galho
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
 
    
    
    
    //recebe a area foliar do eixo principal 
    public double getAreaFoliarEixoPrincipal()
    {
        double area_foliar_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
        {
            if (EN.get(i).temFolha())
            area_foliar_total = area_foliar_total + EN.get(i).getAreaFolha();
        }
            
        return area_foliar_total;
    }   
    
    
    
    
    //recebe a area foliar total de todo o galho incluindo as ramificacoes 
    public double getAreaFoliarAbsoluta()
    {
        double area_foliar_total = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do eixo principal deste galho
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
    
    
    //recebe a quantidade de folhas do eixo principal:
    public int getQtdeFolhasEixoPrincipal()
    {
        int qtde_folhas = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++)
        {
            if (EN.get(i).temFolha()) qtde_folhas++;    
        }
            
        return qtde_folhas;
    }   
    
    
    
    
    //recebe a quantidade de folhas de todo o galho incluindo as ramificacoes 
    public int getQtdeFolhasAbsoluta()
    {
        int qtde_folhas = 0; //inicializa o contador
        
        for (int i=0; i<EN.size(); i++) //para cada entreno do eixo principal deste galho
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
    }
    
    
    
    
}
