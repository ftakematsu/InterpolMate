/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author murilo
 */
public class EscritaMTG {

    
    final int LINHA_INICIAL=43;
    
    
    //constantes representando as colunas da estrutura topologica no MTG:
    final int COLUNA_PLANTA=0;
    final int COLUNA_SUPORTES=1;
    final int COLUNA_GALHOS=2;  
    final int COLUNA_DO_EIXOPRINCIPAL=3;
    final int COLUNA_DA_PRIMEIRARAMIFICACAO=4;
    final int COLUNA_DA_SEGUNDARAMIFICACAO=5;
    final int COLUNA_DA_TERCEIRARAMIFICACAO=6;
    
    //constantes representando as colunas dos atributos no MTG:
    final int COLUNA_ALFARAMO;
    final int COLUNA_SEXO;
    final int COLUNA_LOCAL;
    final int COLUNA_COMPENT;
    final int COLUNA_AREAFOLHA;
    final int COLUNA_ALFAFOLHA;
    final int COLUNA_DAY;
    final int COLUNA_COMPTRONCO;
    final int COLUNA_ALFATRONCO;
    final int COLUNA_ALTURASUP;
    final int COLUNA_ALFASUP;
    final int COLUNA_COMPSRAM;
    final int PRIMEIRA_COLUNA_ATRIB;
    final int ULTIMA_COLUNA_ATRIB;
    
    
    private WritableWorkbook workbook_escrita;
    private WritableSheet sheet_escrita;
    
    private int linha_atual;    
    
    
    
    //Construtor:
    public EscritaMTG (File arquivo, Planta p) throws IOException, WriteException //recebe a planilha a executar a leitura por parametro 
    {
        
        COLUNA_ALFARAMO = LeituraMTG.COLUNA_ALFARAMO;
        COLUNA_SEXO = LeituraMTG.COLUNA_SEXO;
        COLUNA_LOCAL = LeituraMTG.COLUNA_LOCAL;
        COLUNA_COMPENT = LeituraMTG.COLUNA_COMPENT;
        COLUNA_AREAFOLHA = LeituraMTG.COLUNA_AREAFOLHA;
        COLUNA_ALFAFOLHA = LeituraMTG.COLUNA_ALFAFOLHA;
        COLUNA_DAY = LeituraMTG.COLUNA_DAY;
        COLUNA_COMPTRONCO = LeituraMTG.COLUNA_COMPTRONCO;
        COLUNA_ALFATRONCO = LeituraMTG.COLUNA_ALFATRONCO;
        COLUNA_ALTURASUP = LeituraMTG.COLUNA_ALTURASUP;
        COLUNA_ALFASUP = LeituraMTG.COLUNA_ALFASUP;
        COLUNA_COMPSRAM = LeituraMTG.COLUNA_COMPSRAM;
        PRIMEIRA_COLUNA_ATRIB = LeituraMTG.PRIMEIRA_COLUNA_ATRIB;
        ULTIMA_COLUNA_ATRIB = LeituraMTG.ULTIMA_COLUNA_ATRIB;
        

        workbook_escrita = Workbook.createWorkbook(arquivo);
        sheet_escrita = workbook_escrita.createSheet(arquivo.getName(), 0); 


        linha_atual = LINHA_INICIAL;
        
        this.insereCabecario(sheet_escrita);
        this.insereDadosIniciaisPlanta(sheet_escrita, p);

        
        ArrayList<EntrenoPlanta> ListaEN =  p.recebeListaEntreno();

        System.out.println(" QTD EN>>>> " + p.getQtdeEntreno());
        //percorre todos os "entrenos da planta"
        for (int i=0; i<p.getQtdeEntreno(); i++)
        {
            
            //resgata o codigo e comprimento (cm) de tal entreno: 
            int cod = p.getEntreno(i).getCod();
            double comp = p.getEntreno(i).getComp();
            System.out.println(" COD >>>> " + cod);
            //se codigo for 1
            if (cod==1)
                 sheet_escrita.addCell(new Label(0, linha_atual, "/P1/T1/U1/E1")); //escreve "/P1/T1/U1/E1" no MTG
            else //caso contrario
                 sheet_escrita.addCell(new Label(0, linha_atual, "^<E" + cod));  //escreve ^<E(i)              

            
            sheet_escrita.addCell(new Label(COLUNA_ALTURASUP, linha_atual+1, ""+comp)); //escreve o comprimento deste entreno no MTG 
            
            linha_atual++; //avanca p/ a proxima linha
           

            //    ProcEscreverGalho(Sup.getGalho(i), codsup);
            //Procedimento p/ escrever o suporte (passa o suporte e o codigo do pai por parametro):
            if (i<p.getQtdeSuportes())
                ProcEscreverSuporte(p.getSuporte(i), cod); 
        }
     
        //salva e fecha o workbook:
        workbook_escrita.write();
        workbook_escrita.close();


    
    }
    
    
    

    public void ProcEscreverSuporte(Suporte Sup, int codpai) throws WriteException
    {
       if (Sup.getCodPai() == codpai)
       {          
            //percorre todos os "entrenos do suporte"
            for (int i=0; i<Sup.getQtdeEntrenos(); i++)
            {
                //resgata o codigo e comprimento (cm) de tal entreno de suporte:
                int codsup = Sup.getEntreno(i).getCod();
                double compsup = Sup.getEntreno(i).getComp();
                    
                if (codsup==1) //se entreno for == 1:
                    sheet_escrita.addCell(new Label(1, linha_atual, "+S"+codpai+"/U1/E1")); //escreve "S(codpai)/U1/E1 no MTG
                else 
                    sheet_escrita.addCell(new Label(1, linha_atual, "^<S"+codpai+"/U1/E"+codsup)); //escreve ^<S(codpai)/U1/E(codsup)                  

                    
                sheet_escrita.addCell(new Label(COLUNA_COMPSRAM, linha_atual, ""+compsup));    
                sheet_escrita.addCell(new Label(COLUNA_ALFASUP, linha_atual, ""+Sup.getAnguloAlfa()));    
                                
                linha_atual++;
                
                
                //Procedimento p/ escrever o galho deste suporte:
                if (i<Sup.getQtdeGalhos())
                    ProcEscreverGalho(Sup.getGalho(i), codsup);
            }
  
        } 
    }
    
    
    
    public void ProcEscreverGalho(Galho Gal, int codpai) throws WriteException
    {
        
        sheet_escrita.addCell(new Label(2, linha_atual, "+G1"));
                       
        sheet_escrita.addCell(new Label(COLUNA_ALFARAMO, linha_atual, ""+Gal.getAnguloAlfa()));
        
        linha_atual++;
        

        int coluna_escrita = COLUNA_DO_EIXOPRINCIPAL;
        
        ArrayList<EntrenoGalho> EixoPrincipal = Gal.recebeListaEntreno();

        
        /* Escreve o eixo principal (e tambem suas ramificacoes) no MTG: 
        (OBS: Funcao recursiva. Chamara a mesma funcao sempre que houver uma ramificacao) */
        EscreverEixo(EixoPrincipal, coluna_escrita); 
        
 
    }
    
    
    
   /*   Imprime a unidade de crescimento de um Entreno com a sintaxe correta.
    * 
    *   Recebe por parametro:
            EntrenoGalho Entreno -      o Entreno que possui a unidade de crescimento a ser impressa.
    *       int coluna_de_escrita -     a coluna que devera ser impressa a unidade_de_crescimento
    *     
    *   Retorna a nova unidade de crescimento sendo impressa.
    */
    public int imprimeUnidCresc(EntrenoGalho Entreno, int coluna_de_escrita) throws WriteException 
    {
        
            String str_conector; //string p/ armazernar o tipo de conector (/ ou +, dependendo da ordem de ramificacao)
        
            int unid_cresc_atual = Entreno.getUN(); //unid_cresc_atual eh a unidade a ser impressa deste entreno.
                 
            if (unid_cresc_atual==1) //se unidade = 1
            {
                if (Entreno.getOrdem()==0) str_conector = "/";  //se este entreno pertence a um eixo principal, o conector sera "/"  
                else                       str_conector = "+";  //se este entreno pertence a alguma ramificacao, o conector sera "+"
                    
                sheet_escrita.addCell(new Label(coluna_de_escrita, linha_atual, str_conector+"U1")); //imprime conector + U1
            }
            else //se unidade > 1
                sheet_escrita.addCell(new Label(coluna_de_escrita, linha_atual, "^<U"+unid_cresc_atual)); //imprime ^<U + unid_cresc_atual
                
            
            linha_atual++; //pula uma linha no MTG sendo escrito

            return unid_cresc_atual; //retorna a UC atual
    }
    
    
    
    /*   Imprime um eixo de entrenos no MTG.
    * 
    *   Recebe por parametro:
            ArrayList<EntrenoGalho> EixoDeEntreno -      A lista de entrenos a ser impressa.
    *       int coluna_de_escrita -                      A coluna onde sera escrita a lista de entrenos
    *     
    *   OBS: Funcao recursiva. Quando for encontrado um entreno que possui ramificacao, 
    *   sera chamada a propria funcao p/ imprimir esta ramificacao.
    */
    public void EscreverEixo(ArrayList<EntrenoGalho> EixoDeEntreno, int coluna_de_escrita) throws WriteException
    {

        /* obs: coluna da escrita deve ser atribuida em outra variavel, para que o valor passado por parametro seja mantido 
           (pois eh a posicao da ramificacao anterior) */        
        int coluna_escrita = coluna_de_escrita; 
        
        int uc_ram = -1; //uc_ram = unidade de crescimento da ramificacao. Inicializa-a com qualquer valor p/ que depois nao seja comparada com algum valor por engano.


        //percore toda a lista de entrenos:
        for (int i=0, i_impressao=0; i<EixoDeEntreno.size(); i++, i_impressao++)
        {
            if (uc_ram != EixoDeEntreno.get(i).getUN()) //se comecou uma nova unidade de crescimento...
            {
                uc_ram = imprimeUnidCresc(EixoDeEntreno.get(i), coluna_escrita); //imprime esta unidade, e unidade_atual recebe o novo valor.
                i_impressao=0; //zera o indice de entreno (apos começar uma nova unidade de crescimento)
            }
            
            //imprime o entreno
            if (i_impressao==0) sheet_escrita.addCell(new Label(coluna_escrita, linha_atual, "^/E"+(i_impressao+1)));
            else                sheet_escrita.addCell(new Label(coluna_escrita, linha_atual, "^<E"+(i_impressao+1)));

            
            sheet_escrita.addCell(new Label(COLUNA_COMPENT, linha_atual, ""+EixoDeEntreno.get(i).getComp())); //imprime o comprimento deste entreno

            if (EixoDeEntreno.get(i).temFolha()) //se este entreno possuir uma folha:
            {
                String str_af = ""+EixoDeEntreno.get(i).getAreaFolha();
                //str_af.substring(0, 13);
                if (str_af.length()>13)
                {
                    sheet_escrita.addCell(new Label(COLUNA_AREAFOLHA, linha_atual, str_af.substring(0, 12))); //imprime a area da folha
                    sheet_escrita.addCell(new Label(COLUNA_ALFAFOLHA, linha_atual, ""+EixoDeEntreno.get(i).getAlfaFolha())); //imprime o angulo alfa da folha
                }
                else
                {
                    sheet_escrita.addCell(new Label(COLUNA_AREAFOLHA, linha_atual, ""+EixoDeEntreno.get(i).getAreaFolha())); //imprime a area da folha
                    sheet_escrita.addCell(new Label(COLUNA_ALFAFOLHA, linha_atual, ""+EixoDeEntreno.get(i).getAlfaFolha())); //imprime o angulo alfa da folha
                }
            }         
            
            linha_atual++; //cada vez que eh percorrido um entreno aqui, pula uma linha
           
            //se este entreno possuir uma ramificacao, eh chamada a mesma funcao recursivamente (p/ imprimir os entrenos das ramificacoes):
            if (EixoDeEntreno.get(i).temRamificacao()) 
            {
                ArrayList<EntrenoGalho> EixoDaRamificacao = EixoDeEntreno.get(i).recebeListaEntreno();
                
                EscreverEixo(EixoDaRamificacao, coluna_escrita+1);
            }
            
        }
   }
    
    
    
    
    

    //procedimento p/ escrever o cabecario do MTG na planilha de escrita:
    public static void insereCabecario(WritableSheet sheet_escrita) throws WriteException
    {
        //escreve o cabecalho do MTG na planilha:
                                      //COL LIN
        sheet_escrita.addCell(new Label(0, 0, "CODE:"));
        sheet_escrita.addCell(new Label(1, 0, "FORM-A"));
        sheet_escrita.addCell(new Label(0, 2, "CLASSES:"));
        sheet_escrita.addCell(new Label(0, 3, "SYMBOL"));
        sheet_escrita.addCell(new Label(1, 3, "SCALE"));
        sheet_escrita.addCell(new Label(2, 3, "DECOMPOSITION"));   
        sheet_escrita.addCell(new Label(3, 3, "INDEXATION"));
        sheet_escrita.addCell(new Label(4, 3, "DEFINITION"));
        sheet_escrita.addCell(new Label(0, 4, "$"));
        sheet_escrita.addCell(new Label(1, 4, "0"));
        sheet_escrita.addCell(new Label(2, 4, "FREE"));
        sheet_escrita.addCell(new Label(3, 4, "FREE"));
        sheet_escrita.addCell(new Label(4, 4, "IMPLICIT"));
        sheet_escrita.addCell(new Label(0, 5, "P"));
        sheet_escrita.addCell(new Label(1, 5, "1"));
        sheet_escrita.addCell(new Label(2, 5, "FREE"));
        sheet_escrita.addCell(new Label(3, 5, "FREE"));
        sheet_escrita.addCell(new Label(4, 5, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 6, "T"));
        sheet_escrita.addCell(new Label(1, 6, "2"));
        sheet_escrita.addCell(new Label(2, 6, "FREE"));
        sheet_escrita.addCell(new Label(3, 6, "FREE"));
        sheet_escrita.addCell(new Label(4, 6, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 7, "S"));
        sheet_escrita.addCell(new Label(1, 7, "2"));
        sheet_escrita.addCell(new Label(2, 7, "FREE"));
        sheet_escrita.addCell(new Label(3, 7, "FREE"));
        sheet_escrita.addCell(new Label(4, 7, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 8, "G"));
        sheet_escrita.addCell(new Label(1, 8, "2"));
        sheet_escrita.addCell(new Label(2, 8, "FREE"));
        sheet_escrita.addCell(new Label(3, 8, "FREE"));
        sheet_escrita.addCell(new Label(4, 8, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 9, "U"));
        sheet_escrita.addCell(new Label(1, 9, "3"));
        sheet_escrita.addCell(new Label(2, 9, "FREE"));
        sheet_escrita.addCell(new Label(3, 9, "FREE"));
        sheet_escrita.addCell(new Label(4, 9, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 10, "E"));
        sheet_escrita.addCell(new Label(1, 10, "4"));
        sheet_escrita.addCell(new Label(2, 10, "FREE"));
        sheet_escrita.addCell(new Label(3, 10, "FREE"));
        sheet_escrita.addCell(new Label(4, 10, "EXPLICIT"));
        sheet_escrita.addCell(new Label(0, 13, "DESCRIPTION:"));  
        sheet_escrita.addCell(new Label(0, 14, "LEFT"));
        sheet_escrita.addCell(new Label(1, 14, "RIGHT"));
        sheet_escrita.addCell(new Label(2, 14, "RELTYPE"));
        sheet_escrita.addCell(new Label(3, 14, "MAX"));
        sheet_escrita.addCell(new Label(0, 15, "T"));
        sheet_escrita.addCell(new Label(1, 15, "T"));
        sheet_escrita.addCell(new Label(2, 15, "+"));
        sheet_escrita.addCell(new Label(3, 15, "?"));
        sheet_escrita.addCell(new Label(0, 16, "T"));
        sheet_escrita.addCell(new Label(1, 16, "S"));
        sheet_escrita.addCell(new Label(2, 16, "+"));
        sheet_escrita.addCell(new Label(3, 16, "?"));
        sheet_escrita.addCell(new Label(0, 17, "S"));
        sheet_escrita.addCell(new Label(1, 17, "S"));
        sheet_escrita.addCell(new Label(2, 17, "<"));
        sheet_escrita.addCell(new Label(3, 17, "1"));
        sheet_escrita.addCell(new Label(0, 18, "S"));
        sheet_escrita.addCell(new Label(1, 18, "S"));
        sheet_escrita.addCell(new Label(2, 18, "+"));
        sheet_escrita.addCell(new Label(3, 18, "?"));
        sheet_escrita.addCell(new Label(0, 19, "S"));
        sheet_escrita.addCell(new Label(1, 19, "G"));
        sheet_escrita.addCell(new Label(2, 19, "+"));
        sheet_escrita.addCell(new Label(3, 19, "?"));
        sheet_escrita.addCell(new Label(0, 20, "U"));
        sheet_escrita.addCell(new Label(1, 20, "U"));
        sheet_escrita.addCell(new Label(2, 20, "<"));
        sheet_escrita.addCell(new Label(3, 20, "1"));
        sheet_escrita.addCell(new Label(0, 21, "U"));
        sheet_escrita.addCell(new Label(1, 21, "U"));
        sheet_escrita.addCell(new Label(2, 21, "+"));
        sheet_escrita.addCell(new Label(3, 21, "?"));
        sheet_escrita.addCell(new Label(0, 22, "E"));
        sheet_escrita.addCell(new Label(1, 22, "E"));
        sheet_escrita.addCell(new Label(2, 22, "<"));
        sheet_escrita.addCell(new Label(3, 22, "1"));
        sheet_escrita.addCell(new Label(0, 23, "E"));
        sheet_escrita.addCell(new Label(1, 23, "E"));
        sheet_escrita.addCell(new Label(2, 23, "+"));
        sheet_escrita.addCell(new Label(3, 23, "?"));
        sheet_escrita.addCell(new Label(0, 25, "FEATURES:"));
        sheet_escrita.addCell(new Label(0, 26, "NAME"));
        sheet_escrita.addCell(new Label(1, 26, "TYPE"));   
        sheet_escrita.addCell(new Label(0, 28, "AlfaRamo"));
        sheet_escrita.addCell(new Label(1, 28, "REAL")); 
        sheet_escrita.addCell(new Label(0, 29, "Sexo"));
        sheet_escrita.addCell(new Label(1, 29, "STRING")); 
        sheet_escrita.addCell(new Label(0, 30, "Local"));
        sheet_escrita.addCell(new Label(1, 30, "STRING")); 
        sheet_escrita.addCell(new Label(0, 31, "CompEnt"));
        sheet_escrita.addCell(new Label(1, 31, "REAL")); 
        sheet_escrita.addCell(new Label(0, 32, "AreaFolha"));
        sheet_escrita.addCell(new Label(1, 32, "REAL")); 
        sheet_escrita.addCell(new Label(0, 33, "AlfaFolha"));
        sheet_escrita.addCell(new Label(1, 33, "REAL")); 
        sheet_escrita.addCell(new Label(0, 34, "Day"));
        sheet_escrita.addCell(new Label(1, 34, "DD/MM/YY")); 
        sheet_escrita.addCell(new Label(0, 35, "CompTronco"));
        sheet_escrita.addCell(new Label(1, 35, "REAL"));
        sheet_escrita.addCell(new Label(0, 36, "AlfaTronco"));
        sheet_escrita.addCell(new Label(1, 36, "REAL")); 
        sheet_escrita.addCell(new Label(0, 37, "AlturaSup"));
        sheet_escrita.addCell(new Label(1, 37, "REAL"));
        sheet_escrita.addCell(new Label(0, 38, "AlfaSup"));
        sheet_escrita.addCell(new Label(1, 38, "REAL")); 
        sheet_escrita.addCell(new Label(0, 39, "CompSRam"));
        sheet_escrita.addCell(new Label(1, 39, "REAL")); 
        sheet_escrita.addCell(new Label(0, 41, "MTG:"));
        sheet_escrita.addCell(new Label(0, 42, "ENTITY-CODE"));
        sheet_escrita.addCell(new Label(8, 42, "AlfaRamo"));
        sheet_escrita.addCell(new Label(9, 42, "Sexo"));   
        sheet_escrita.addCell(new Label(10, 42, "Local"));
        sheet_escrita.addCell(new Label(11, 42, "CompEnt"));
        sheet_escrita.addCell(new Label(12, 42, "AreaFolha"));
        sheet_escrita.addCell(new Label(13, 42, "AlfaFolha"));   
        sheet_escrita.addCell(new Label(14, 42, "Day"));
        sheet_escrita.addCell(new Label(15, 42, "CompTronco"));
        sheet_escrita.addCell(new Label(16, 42, "AlfaTronco"));
        sheet_escrita.addCell(new Label(17, 42, "AlturaSup"));   
        sheet_escrita.addCell(new Label(18, 42, "AlfaSup"));
        sheet_escrita.addCell(new Label(19, 42, "CompSRam"));
    }
    
    //procedimento p/ escrever os dados inicias da planta no MTG: (Sexo, Ambiente, Comprimento do tronco a ângulo alfa)
    public void insereDadosIniciaisPlanta(WritableSheet sheet_escrita, Planta p) throws WriteException
    {
        sheet_escrita.addCell(new Label(COLUNA_SEXO, LINHA_INICIAL, p.getSexo()));
        sheet_escrita.addCell(new Label(COLUNA_LOCAL, LINHA_INICIAL, p.getAmbiente()));
        sheet_escrita.addCell(new Label(COLUNA_COMPTRONCO, LINHA_INICIAL, ""+p.getCompTronco()));
        sheet_escrita.addCell(new Label(COLUNA_ALFATRONCO, LINHA_INICIAL, ""+p.getAlfaTronco()));  
    }
    
    

    
    
}
