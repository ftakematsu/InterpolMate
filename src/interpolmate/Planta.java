/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author murilo
 */
public class Planta implements Serializable {
    

    private String sexo; //"M" = macho, "F" = femea
    
    private String ambiente; //"SOMBRA" ou "SOL"
    
    private float comp_tronco; //comprimento total do tronco
    
    private float angulo_alfa_tronco; //angulo de inclinacao do tronco 
    
    private float total_area_foliar; //somatoria de area foliar de toda a planta
    
    //indicam a quantidade de folhas que surgiram e cairam deste o estagio de crescimento precedente:
    private int folhas_surgidas_neste_estagio;
    private int folhas_caidas_neste_estagio;
    
    private double alometria_folha;

    private ArrayList<EntrenoPlanta> EN;
    
    private ArrayList<Suporte> Suportes;
    
    
    // Dados sobre produtividade final da erva-mate
    private double biomassa_util_folhas;
    private double biomassa_galhos;
    private double massa_util_folhas;
    private double massa_util_galhos;
    
    private double volume_madeira;
    private double peso_folhas;

    
    // Constantes definindo a massa específica de uma folha por ambiente
    private final double massa_especifca_folha_mo = 0.01888; //0.04497; 
    private final double massa_especifca_folha_fus = 0.01088; //0.0447; //0.01888; // g/cm²
    private final double massa_especifica_galho = 0.5; // g/cm³ // 0.8333333
    
    private final double MS_folha = 0.4461; // Fator de massa seca de folhas
    private final double MS_galho = 0.60; // Fator de massa seca de galhos
    
    
    //construtor:
    public Planta ()
    {
        EN = new ArrayList<EntrenoPlanta>();
        
        Suportes = new ArrayList<Suporte>();
        total_area_foliar=0; //inicializa o total de area foliar a ser lido da planta
        biomassa_util_folhas=0;
        biomassa_galhos=0;
        volume_madeira=0;
        peso_folhas=0;
    }

    public Planta (Planta copia_da_planta) //para copiar uma planta
    {
          this.sexo = copia_da_planta.getSexo();
          this.ambiente = copia_da_planta.getAmbiente();
          this.comp_tronco = copia_da_planta.getCompTronco();
          this.angulo_alfa_tronco = copia_da_planta.getAlfaTronco();
          this.total_area_foliar = (float)copia_da_planta.getTotalAreaFoliar();
          this.EN = copia_da_planta.recebeListaEntreno();
          this.Suportes = copia_da_planta.recebeListaSuportes();
    }

    
    //metodos getters e setters basicos: 
    public void setSexo(String s) { sexo = s; }
    public String getSexo() { return sexo; }
    public void setAmbiente(String a) { ambiente = a; }
    public String getAmbiente() { return ambiente; }
    public void setCompTronco(String ct) { comp_tronco  = Float.parseFloat(ct); } 
    public float getCompTronco() { return comp_tronco; }
    public void setAlfaTronco(String at) { angulo_alfa_tronco = Float.parseFloat(at); }
    public float getAlfaTronco() { return angulo_alfa_tronco; }   
    
    /*
    public void incrementarAreaFoliar(float area_folha)
    {
        total_area_foliar = total_area_foliar + area_folha; //acrescenta a area da folha ao total de area foliar da planta
    }
            
    public float getTotalAreaFoliar()
    {
        return total_area_foliar;
    }*/
    
    public void adicionarEntreno(double medida)
    {
        int codigo_do_entreno = EN.size()+1;
       
        EN.add(new EntrenoPlanta(codigo_do_entreno, medida));
        
        
    }
    
    public ArrayList<EntrenoPlanta> recebeListaEntreno()
    {
        return EN;
    }
    
    public EntrenoPlanta getEntreno(int index)
    {
        return EN.get(index);
    }
      
    public int getQtdeEntreno()
    {
        return EN.size();
    }
    
    
    public ArrayList<Suporte> recebeListaSuportes()
    {
        return Suportes;
    }   
    
    
    public Suporte getSuporte(int index)
    {
        return Suportes.get(index);
    }   
    
    
    
    
    public void imprimeListaEntreno()
    {
        for (int i=0; i<EN.size(); i++)
        {
            //System.out.println("codigo: " + EN.get(i).getCod() + ", medida: " + EN.get(i).getComp());
        }
    }
    
    
    
    public void adicionarSuporte(Suporte s)
    {
        Suportes.add(s);  
    }
    
    public int getQtdeSuportes()
    {
        return Suportes.size();
    }
    
    public int getQtdeTotalGalhos()
    {
        int qtde_galhos=0;
        
        for (int s=0; s<getQtdeSuportes(); s++)
        {
            qtde_galhos = qtde_galhos + getSuporte(s).getQtdeGalhos();
        }
        
        return qtde_galhos;
    }   
    
    
    public int getCodSuporteDesteGalho(int galho)
    {
        int suporte;
        int galho_temp = galho;
        
        for (suporte=0; suporte<getQtdeSuportes(); suporte++)
        {
           if (galho_temp < getSuporte(suporte).getQtdeGalhos())
               return suporte;
           else
               galho_temp = galho_temp - getSuporte(suporte).getQtdeGalhos();
        }
        
        return -1;
    }

    public int getCodGalhoDesteGalho(int galho)
    {
        int galho_temp = galho;
        
        for (int suporte=0; suporte<getQtdeSuportes(); suporte++)
        {
            
           if (galho_temp < getSuporte(suporte).getQtdeGalhos())
               return galho_temp;
           else
               galho_temp = galho_temp - getSuporte(suporte).getQtdeGalhos();
        }
        
        return -1;
    }
    
    public int getQtdeFolhas()
    {
        int qtde_folhas=0;
        
        for (int s=0; s<this.getQtdeSuportes(); s++)
        {
            for (int g=0; g<this.getSuporte(s).getQtdeGalhos(); g++)
            {
                qtde_folhas = qtde_folhas + this.getSuporte(s).getGalho(g).getQtdeFolhasAbsoluta();
            }
            
        }
        
        return qtde_folhas;
    }
    
    public int getQtdeEntrenos()
    {
        int qtde_entrenos=0;
        
        for (int s=0; s<this.getQtdeSuportes(); s++)
        {
            for (int g=0; g<this.getSuporte(s).getQtdeGalhos(); g++)
            {
                qtde_entrenos = qtde_entrenos + this.getSuporte(s).getGalho(g).getQtdeEntrenosAbsoluta();
            }
            
        }
        
        return qtde_entrenos;
    }    
    
    public double getTotalAreaFoliar()
    {
        double area_foliar_total=0;
        
        for (int s=0; s<this.getQtdeSuportes(); s++)
        {
            for (int g=0; g<this.getSuporte(s).getQtdeGalhos(); g++)
            {
                area_foliar_total = area_foliar_total + this.getSuporte(s).getGalho(g).getAreaFoliarAbsoluta();
            }
            
        }
        
        return area_foliar_total;
    }      
              
    
    
    public int getQtdeRams()
    {
        int qtde_rams=0;
        
        for (int s=0; s<this.getQtdeSuportes(); s++)
        {
            for (int g=0; g<this.getSuporte(s).getQtdeGalhos(); g++)
            {
                qtde_rams = qtde_rams + this.getSuporte(s).getGalho(g).getQtdeRamAbsoluta();
            }
            
        }
        
        return qtde_rams;
    }       

    public double getCompTotalGalhos()
    {
        double comp_total_galhos=0;
        
        for (int s=0; s<getQtdeSuportes(); s++)
        {
            for (int g=0; g<getSuporte(s).getQtdeGalhos(); g++)
            {
                comp_total_galhos = comp_total_galhos + getSuporte(s).getGalho(g).getComprimentoAbsoluto(); 
            }
            
        }
        return comp_total_galhos;
    }      


    // Nova função - Tamanho Médio de Folhas
    public double getTamanhoMedioFolhas()
    {
        double qtd_fls = this.getQtdeFolhas();
        double area_foliar_aux = this.getTotalAreaFoliar();
        double tam_medio = area_foliar_aux/qtd_fls;

        return tam_medio;
    }
    



    public void setFolhasSurgidasNesteEstagio(int folhas) { folhas_surgidas_neste_estagio = folhas; }  
    public void setFolhasCaidasNesteEstagio(int folhas) { folhas_caidas_neste_estagio = folhas; }    
    public int getFolhasSurgidasNesteEstagio() { return folhas_surgidas_neste_estagio; }
    public int getFolhasCaidasNesteEstagio() { return folhas_caidas_neste_estagio; }
    
    public void setAlometriaFolha(double alom) { alometria_folha = alom; }
    public double getAlometriaFolha() { return alometria_folha; }

    
    
    
    
    
    
    // Obtém o comprimento total do eixo principal
    public double getCompTotalEP()
    {
        double soma_ep = 0;
        for (int s=0; s < this.getQtdeSuportes(); s++)
        {    
            for (int g=0; g < this.getSuporte(s).getQtdeGalhos(); g++)
            {
                soma_ep = soma_ep + getSuporte(s).getGalho(g).getComprimentoEixoPrincipal();
            }
        }
        return soma_ep;
    }

    public int getQtdEP()
    {
        int soma_qtd=0;
        for (int s=0; s < this.getQtdeSuportes(); s++)    
            soma_qtd = soma_qtd + this.getSuporte(s).getQtdeGalhos();
        return soma_qtd;
    }
    
    
    // ************************************
    // * Novas funções
    // ************************************
    public int QtdEnUC(ArrayList<EntrenoGalho> en, int uc)
    {
        int n = 0;
        for (int i=0; i<en.size() && en.get(i).getUN()<uc+1; i++)
        {
            if (en.get(i).getUN()==uc) n++;
        }
        return n;
    }

    
    void NumeroEntrenosPorUC()
    {
        System.out.println(" \n************************************");
        ArrayList<EntrenoGalho> galho_atual = new ArrayList<EntrenoGalho>();
        for (int s=0; s < this.getQtdeSuportes(); s++) // Pegando suporte s
        {
            for (int g=0; g < this.getSuporte(s).getQtdeGalhos(); g++) // e o galho g - trabalhando neste galho
            {
                galho_atual = this.getSuporte(s).getGalho(g).recebeListaEntreno();
                System.out.println("*\n* Quantidade de entrenos por UC eixo principal. Galho " + s + " suporte " + g);
                System.out.println(" >>> UC1: " + QtdEnUC(galho_atual,1) + " entrenos");
                System.out.println(" >>> UC2: " + QtdEnUC(galho_atual,2) + " entrenos");
                System.out.println(" >>> UC3: " + QtdEnUC(galho_atual,3) + " entrenos");
                System.out.println(" >>> UC4: " + QtdEnUC(galho_atual,4) + " entrenos");
            }
        }
        System.out.println("************************************\n");
    }
    
    
    // ****************************************************************************
    public double MassaBrutaTotal()
    {
        
        double massa = 0;
        for (int s=0; s < this.getQtdeSuportes(); s++) // Pegando suporte s
        {
            for (int g=0; g < this.getSuporte(s).getQtdeGalhos(); g++) // e o galho g - trabalhando neste galho
            {
                massa += MassaBrutaGalhoPlanta(this.getSuporte(s).getGalho(g).recebeListaEntreno(), 0);
            }
        }
        
        double biomassa_folha = 0;
        // Calculando a produção de folhas
        if (getAmbiente().equals("SOL")) // No ambiente sol, a massa foliar específica é maior
        {
            biomassa_folha = (this.getTotalAreaFoliar())*massa_especifca_folha_mo*(1/MS_folha);
        }
        else
        {
            biomassa_folha = (this.getTotalAreaFoliar())*massa_especifca_folha_fus*(1/MS_folha);
        }
        
        // 59681.0206
//        System.out.println(" Area foliar MTG: " + this.getTotalAreaFoliar());  //*0.65
//        //System.out.println(" Area foliar: " + this.getTotalAreaFoliar());  //*0.65
//        System.out.println(" Massa 1: " + massa + " folhas: " + biomassa_folha);
//        System.out.println(" BIOMASSA TOTAL: " + (massa + biomassa_folha));
//        System.out.println();
        return massa + biomassa_folha;
    };
    
    
    
    
    public double MassaBrutaGalhoPlanta(ArrayList<EntrenoGalho> galho_atual, int ordem)
    {
        double volume = 0;
        double diam = 0;
        
        if (ordem == 0) diam = 1.2;
        else if (ordem == 1) diam = 0.7;
        else if (ordem == 2) diam = 0.5;
        else if (ordem == 3) diam = 0.4;
        
        for (int e=0; e<galho_atual.size(); e++)
        {
            if (galho_atual.get(e).temRamificacao()) // Chamada recursiva para percorrer a ramificação deste entrenó
            {
                volume += MassaBrutaGalhoPlanta(galho_atual.get(e).recebeListaEntreno(), ordem+1);
            }
            
            volume += (Math.PI*Math.pow(diam/2, 2) + Math.PI*Math.pow(diam/2, 2))*(galho_atual.get(e).getComp()/2);       //Is = (g1+g2 ) . l/2   Math.PI*((diam/2)*(diam/2))*galho_atual.get(e).getComp()
            //volume += volume*MS_galho*massa_especifica_galho;
        }

        return volume*massa_especifica_galho*(1/MS_galho); // *(MS_galho)
    };

    
    // Massa dos galhos
    public double MassaBrutaGalho(ArrayList<EntrenoGalho> galho_atual, int ordem)
    {
        double volume = 0;
        double diam = 0;
        
        if (ordem == 0) diam = 1.2;
        else if (ordem == 1) diam = 0.7;
        else if (ordem == 2) diam = 0.5;
        else if (ordem == 3) diam = 0.4;
        
        for (int e=0; e<galho_atual.size(); e++)
        {
            if (galho_atual.get(e).temRamificacao()) // Chamada recursiva para percorrer a ramificação deste entrenó
            {
                volume += MassaBrutaGalho(galho_atual.get(e).recebeListaEntreno(), ordem+1);
            }
            
            volume += (Math.PI*Math.pow(diam/2, 2) + Math.PI*Math.pow(diam/2, 2))*(galho_atual.get(e).getComp()/2);       //Is = (g1+g2 ) . l/2   Math.PI*((diam/2)*(diam/2))*galho_atual.get(e).getComp()
            //volume += volume*MS_galho*massa_especifica_galho;
        }
        
        return volume*massa_especifica_galho*(1/MS_galho);
    }
    
    public double BiomassaGalhos(int ordem)
    {
         double volume = 0;
         double diam = 1;
         biomassa_galhos = 0;
        
         // Calculando massa bruta da madeira dos galhos
         for (int s=0; s < this.getQtdeSuportes(); s++) // Pegando suporte s
         {
            for (int g=0; g < this.getSuporte(s).getQtdeGalhos(); g++) // e o galho g - trabalhando neste galho
            {   
                biomassa_galhos += MassaBrutaGalho(this.getSuporte(s).getGalho(g).recebeListaEntreno(), 0);
            }
         }
        
        return biomassa_galhos;
    }
    
    
   
    public double getBiomassa_util_folhas() {
        return biomassa_util_folhas;
    }

    public double getBiomassa_util_galhos() {
        return biomassa_galhos;
    }

    public double getBiomassaUtilTotal()
    {
        return (biomassa_util_folhas*MS_folha) + (biomassa_galhos*MS_galho);
    }

    public double getVolumeMadeira()
    {
        return volume_madeira;
    }
    
    public double getMassaUtilTotal()
    {
        return (massa_util_folhas*MS_folha) + (massa_util_galhos*MS_galho);
    }
    
    /*
    public Produtividade getProducao()
    {
        return prod;
    }*/
    // ****************************************************************************

    public Planta deepCopy() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream byteArrOs = new ByteArrayOutputStream();
        ObjectOutputStream objOs = new ObjectOutputStream(byteArrOs);
        objOs.writeObject(this);

        ByteArrayInputStream byteArrIs = new ByteArrayInputStream(byteArrOs.toByteArray());
        ObjectInputStream objIs = new ObjectInputStream(byteArrIs);
        Object deepCopy = objIs.readObject();  
        Planta planta_a_retornar = (Planta)deepCopy;
        
        return planta_a_retornar;
    }
    
    
    
    @Override
      public Object clone() throws CloneNotSupportedException {
    return super.clone();
    }
    
    
    

}
