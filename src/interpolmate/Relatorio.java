/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.util.ArrayList;

/**
 *
 * @author MURILO
 */
public class Relatorio {

    
    private ArrayList<Planta> Plantas;
    

    private boolean filtro_total_folhas;
    private boolean filtro_folhas_surgidas;
    private boolean filtro_folhas_caidas;
    private boolean filtro_area_foliar_total;
    private boolean filtro_ganho_area_foliar;
    private boolean filtro_comp_total_galhos;
    private boolean filtro_along_galhos;
    private boolean filtro_total_rams;            
    private boolean filtro_ram_surgidas;
    private boolean filtro_total_en;
    private boolean filtro_en_surgidos;
    
    //private boolean filtro_prod_total;
    
    
    //variaveis p/ armazenar os valores a serem calculados de uma planta em um determinado estágio:
    int total_folhas;
    int folhas_surgidas;
    int folhas_caidas;
    double area_foliar;
    double ganho_area_fol;
    double comp_total;
    double along_galhos;
    int total_rams;
    int rams_surgidas;
    int qtde_entrenos;
    int en_surgidos;
    //variaveis auxiliares p/ armazenar alguns valores da planta do estagio precedente
    int total_folhas_precedente;
    int qtde_entrenos_precedente;             
    double area_foliar_precedente;
    int total_rams_precedente;
    double comp_total_precedente;
    
    double biomassa_util_folhas, massa_util;
    double biomassa_util_galhos, massa_util_galhos;
    double volume_madeira ;
    
    int estagio;
    int galho;
    
    static final int TODOS_OS_ESTAGIOS=-1;
    static final int TODOS_OS_GALHOS=1;
    static final int NENHUM_GALHO=0;
    static final boolean COM_ESPACO=true;
    static final boolean SEM_ESPACO=false;
     
    String TextoRelatorio;

    final static char ASPAS = (char)34; 
    
    public Relatorio (ArrayList<Planta> ListaPlantas, int est, int gal, boolean f1, boolean f2, boolean f3, boolean f4, 
            boolean f5, boolean f6, boolean f7, boolean f8, boolean f9, boolean f10, boolean f11)
    {
       Plantas = ListaPlantas;
       
       
       
       //estagio e galho a serem exibidos no log:
       estagio = est;
       galho = gal;
       
       //filtros recebidos pelas opcoes dos checkboxes exibidos na interface grafica:
       filtro_total_folhas = f1;
       filtro_folhas_surgidas = f2;
       filtro_folhas_caidas = f3;
       filtro_area_foliar_total = f4;
       filtro_ganho_area_foliar = f5;
       filtro_comp_total_galhos = f6;
       filtro_along_galhos = f7;
       filtro_total_rams = f8;      
       filtro_ram_surgidas = f9;
       filtro_total_en = f10;
       filtro_en_surgidos = f11;       
    }
    
    
    public String criarTexto()
    {
        TextoRelatorio="";
        
        
        if (estagio==TODOS_OS_ESTAGIOS)
        {
            //para cada planta da lista:
            for (int p=0; p<Plantas.size(); p++)
            {
                imprimirInformacoesDeUmEstagio(p);
                
                if (galho==TODOS_OS_GALHOS)
                {
                    int galho_print=0;
                    for (int s=0; s<Plantas.get(p).getQtdeSuportes(); s++)
                    {
                        for (int g=0; g<Plantas.get(p).getSuporte(s).getQtdeGalhos(); g++)
                        {
                            galho_print++;
                            imprimirInformacoesDeUmGalho(p, s, g, galho_print);
                        }
                    }
                    
                }

            }
        }
        else
        {
            imprimirInformacoesDeUmEstagio(estagio);
            
            if (galho>NENHUM_GALHO)
            {
                if (galho==TODOS_OS_GALHOS)
                {  
                    int galho_print=0;
                    for (int s=0; s<Plantas.get(estagio).getQtdeSuportes(); s++)
                    {
                        for (int g=0; g<Plantas.get(estagio).getSuporte(s).getQtdeGalhos(); g++)
                        {
                            galho_print++;
                            imprimirInformacoesDeUmGalho(estagio, s, g, galho_print);
                        }
                    }
                    
                }
                else
                {
                    int s_cod = Plantas.get(estagio).getCodSuporteDesteGalho(galho-2);
                    int g_cod = Plantas.get(estagio).getCodGalhoDesteGalho(galho-2);    
                    
                    imprimirInformacoesDeUmGalho(estagio, s_cod, g_cod, galho-1);
                }
                
            }
            
        }


        return TextoRelatorio;
    }
    
    
    
    
    
    
    
   public void limpaVariaveis()
   {
        total_folhas=0;
        folhas_surgidas=0;
        folhas_caidas=0;
        area_foliar=0;
        ganho_area_fol=0;
        comp_total=0;
        along_galhos=0;
        total_rams=0;
        rams_surgidas=0;
        qtde_entrenos=0;
        en_surgidos=0;    
        total_folhas_precedente=0;
        qtde_entrenos_precedente=0;             
        area_foliar_precedente=0;
        total_rams_precedente=0;
        comp_total_precedente=0;
   }
    
    
   public void gerarImpressaoAtributos(boolean dar_espaco)
   {
        String espaco="";
        if (dar_espaco) espaco = "     ";
                                                                 //"     Total de folhas:            "
        if (filtro_total_folhas)      TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO1") + total_folhas + "\n";
                                                                                //"     Folhas surgidas:            "
        if (filtro_folhas_surgidas)   TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO2") + folhas_surgidas + "\n";
                                                                                //"     Folhas caídas:              "
        if (filtro_folhas_caidas)     TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO3") + folhas_caidas + "\n";
                                                                                //"     Área Foliar total (cm²):    "
        if (filtro_area_foliar_total) TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO4") + ((double)Math.round(area_foliar*(double)100)/(double)100) + "\n";
                                                                                //"     Ganho de área foliar (cm²): "
        if (filtro_ganho_area_foliar) TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO5") + ((double)Math.round(ganho_area_fol*(double)100)/(double)100) + "\n";
                                                                                //"     Comp. total de galhos (cm): "
        if (filtro_comp_total_galhos) TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO6") + ((double)Math.round(comp_total*(double)100)/(double)100) + "\n";
                                                                                //"     Along de galhos (cm):       "
        if (filtro_along_galhos)      TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO7") + ((double)Math.round(along_galhos*(double)100)/(double)100) + "\n";
                                                                                //"     Total de ramificações:      "
        if (filtro_total_rams)        TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO8") + total_rams + "\n";
                                                                                //"     Ramificações surgidas:      "
        if (filtro_ram_surgidas)      TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO9") + rams_surgidas + "\n";
                                                                                //"     Total de entrenós:          "
        if (filtro_total_en)          TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO10") + qtde_entrenos + "\n";
                                                                                //"     Entrenós surgidos:          "
        if (filtro_en_surgidos)       TextoRelatorio = TextoRelatorio + espaco + "     " + InterpolMateView.colecaomsgs.getString("RELATORIO11") + en_surgidos + "\n";
        
        //producao_folhas = (double)Math.round(producao_folhas*100)/(double)100;
        
        // Producao total
        TextoRelatorio = TextoRelatorio + "\n * Volume da madeira: " + volume_madeira + " cm³ ";
        TextoRelatorio = TextoRelatorio + "\n * Biomassa util de folhas: " + biomassa_util_folhas + " g " + "(" + (double)Math.round(((biomassa_util_folhas*0.001))*100)/(double)100 + " Kg)\n";
        TextoRelatorio = TextoRelatorio + "\n * Biomassa util de galhos: " + biomassa_util_galhos + " g " + "(" + (double)Math.round(((biomassa_util_galhos*0.001))*100)/(double)100 + " Kg)\n";
        TextoRelatorio = TextoRelatorio + "\n * Biomassa util total: " + (biomassa_util_folhas+biomassa_util_galhos) + " g " + "(" + (double)Math.round(((biomassa_util_folhas+biomassa_util_galhos)*0.001)*100)/(double)100 + " Kg)\n";
        TextoRelatorio = TextoRelatorio + "\n * Biomassa útil total: " + massa_util + " g " + "(" + (double)Math.round((massa_util*0.001)*100)/(double)100 + " Kg)\n";
      
        TextoRelatorio = TextoRelatorio + "\n\n";
   }
           
   
   public void imprimirInformacoesDeUmEstagio(int estagio)
   {
        limpaVariaveis();
       
        Planta p = Plantas.get(estagio);
        Planta p_precedente; 
        
        String StrAuxiliar = "";
        int dia;
        
        if (estagio == 0)                 
        {                                                                     //"Estágio Base Inicial"
            StrAuxiliar = "- " + ASPAS + InterpolMateView.colecaomsgs.getString("Estagio_Base_Inicial") + ASPAS;
            dia=0;
        }
        else
        if (estagio == (Plantas.size()-1)) 
        {                                                                     //"Estágio Base Final"
            StrAuxiliar = "- " + ASPAS + InterpolMateView.colecaomsgs.getString("Estagio_Base_Final") + ASPAS;
            dia = InterpolMateView.periodo.getDiferencaDias();
        }            
        else
        {
            dia = InterpolMateView.ListaMTGsAGerar.get(estagio-1).getDia();       
        }
        
                                                                                //"* PLANTA NO ESTÁGIO "                                                           //"Dia"
        TextoRelatorio = TextoRelatorio + InterpolMateView.colecaomsgs.getString("PLANTA_NO_ESTAGIO") + (estagio+1) + " (" + InterpolMateView.colecaomsgs.getString("Dia") + " " + dia +") " + StrAuxiliar + "\n";

        
        total_folhas = p.getQtdeFolhas();
        qtde_entrenos = p.getQtdeEntrenos();
        area_foliar = p.getTotalAreaFoliar();
        total_rams = p.getQtdeRams();
        comp_total = p.getCompTotalGalhos();  
        folhas_surgidas = p.getFolhasSurgidasNesteEstagio();
        folhas_caidas = p.getFolhasCaidasNesteEstagio();
        
        
        // Definindo a massa bruta
        biomassa_util_folhas = p.getBiomassa_util_folhas();
        biomassa_util_galhos = p.getBiomassa_util_galhos();
        volume_madeira = p.getVolumeMadeira();
        
        //massa_util = p.getMassaUtilTotal();
        
        // Arredondando para casas decimais
        biomassa_util_folhas = (double)Math.round((biomassa_util_folhas)*100)/(double)100;
        biomassa_util_galhos = (double)Math.round((biomassa_util_galhos)*100)/(double)100;
        //massa_util = (double)Math.round((massa_util)*100)/(double)100;
        
        /*
        //percorre todos os galhos da planta:
        for (int s=0; s<p.getQtdeSuportes(); s++)
        {
            for (int g=0; g<p.getSuporte(s).getQtdeGalhos(); g++)
            {
                total_folhas = total_folhas + p.getSuporte(s).getGalho(g).getQtdeFolhasAbsoluta();
                qtde_entrenos = qtde_entrenos + p.getSuporte(s).getGalho(g).getQtdeEntrenosAbsoluta();                   
                area_foliar = area_foliar + p.getSuporte(s).getGalho(g).getAreaFoliarAbsoluta(); 
                total_rams = total_rams + p.getSuporte(s).getGalho(g).getQtdeRamAbsoluta();
                comp_total = comp_total + p.getSuporte(s).getGalho(g).getComprimentoAbsoluto(); 
            }
        } */           

        if (estagio == 0)
        {
            folhas_surgidas=0; folhas_caidas=0; ganho_area_fol=0; along_galhos=0; rams_surgidas=0; en_surgidos=0;
        }
        else
        {    
            p_precedente = Plantas.get(estagio-1);

            total_folhas_precedente = p_precedente.getQtdeFolhas();
            qtde_entrenos_precedente = p_precedente.getQtdeEntrenos();
            area_foliar_precedente = p_precedente.getTotalAreaFoliar();
            total_rams_precedente = p_precedente.getQtdeRams();
            comp_total_precedente = p_precedente.getCompTotalGalhos();  
            
            
            ganho_area_fol = area_foliar - area_foliar_precedente;
            along_galhos = comp_total - comp_total_precedente;
            rams_surgidas = total_rams - total_rams_precedente;
            en_surgidos = qtde_entrenos - qtde_entrenos_precedente;                 
           
        }

        gerarImpressaoAtributos(SEM_ESPACO);       
       
   }
   
   
   public void imprimirInformacoesDeUmGalho(int estagio, int sup, int gal, int galho_print)
   {
        limpaVariaveis();
                                                                         //"     #Galho "
        TextoRelatorio = TextoRelatorio + InterpolMateView.colecaomsgs.getString("GALHO") + galho_print + "\n";

        Galho g = Plantas.get(estagio).getSuporte(sup).getGalho(gal);
        Galho g_precedente;
        
        total_folhas = total_folhas + g.getQtdeFolhasAbsoluta();
        qtde_entrenos = qtde_entrenos + g.getQtdeEntrenosAbsoluta();                   
        area_foliar = area_foliar + g.getAreaFoliarAbsoluta(); 
        total_rams = total_rams + g.getQtdeRamAbsoluta();
        comp_total = comp_total + g.getComprimentoAbsoluto(); 
       

        if (estagio == 0)
        {
            folhas_surgidas=0; folhas_caidas=0; ganho_area_fol=0; along_galhos=0; rams_surgidas=0; en_surgidos=0;
        }
        else
        {    
            //pega o mesmo galho no estagio PRECEDENTE
            g_precedente = Plantas.get(estagio-1).getSuporte(sup).getGalho(gal);

            
            total_folhas_precedente = total_folhas_precedente + g_precedente.getQtdeFolhasAbsoluta();
            qtde_entrenos_precedente = qtde_entrenos_precedente + g_precedente.getQtdeEntrenosAbsoluta();                    
            area_foliar_precedente = area_foliar_precedente + g_precedente.getAreaFoliarAbsoluta(); 
            total_rams_precedente = total_rams_precedente + g_precedente.getQtdeRamAbsoluta();
            comp_total_precedente = comp_total_precedente + g_precedente.getComprimentoAbsoluto(); 


            ganho_area_fol = area_foliar - area_foliar_precedente;
            along_galhos = comp_total - comp_total_precedente;
            rams_surgidas = total_rams - total_rams_precedente;
            en_surgidos = qtde_entrenos - qtde_entrenos_precedente;                 

        }

        gerarImpressaoAtributos(COM_ESPACO); 
   }
           
}
