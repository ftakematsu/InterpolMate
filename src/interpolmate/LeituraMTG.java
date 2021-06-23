/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jxl.DateCell;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 *
 * @author murilo
 */
public class LeituraMTG {

    //constantes representando as colunas dos atributos no MTG:
    static final int COLUNA_ALFARAMO=8;
    static final int COLUNA_SEXO=9;
    static final int COLUNA_LOCAL=10;
    static final int COLUNA_COMPENT=11;
    static final int COLUNA_AREAFOLHA=12;
    static final int COLUNA_ALFAFOLHA=13;
    static final int COLUNA_DAY=14;
    static final int COLUNA_COMPTRONCO=15;
    static final int COLUNA_ALFATRONCO=16;
    static final int COLUNA_ALTURASUP=17;
    static final int COLUNA_ALFASUP=18;
    static final int COLUNA_COMPSRAM=19;
    static final int PRIMEIRA_COLUNA_ATRIB=8;
    static final int ULTIMA_COLUNA_ATRIB=19;
    
    private WritableSheet sheet_mtg;
    
    private int linha_inicial;
    
    
    //Construtor:
    public LeituraMTG (WritableSheet wsheet) //recebe a planilha a executar a leitura por parametro 
    {
        sheet_mtg = wsheet; //armazena no atributo da classe
    }
    
    
    
    //metodo para verificar se a planilha do MTG a ser lido possui o numero de colunas minimo para a leitura
    public boolean verificarColunasValidas()
    {
         if (sheet_mtg.getColumns()<ULTIMA_COLUNA_ATRIB) //se MTG nao tiver o numero suficiente de colunas
             return false;
         else
             return true;
    }
    
    
    //metodo para reconhecer a linha inicial (dos dados da estrutura) do MTG:
    public int encontrarLinhaInicial() 
    {     

        
         for (linha_inicial=0; linha_inicial<sheet_mtg.getRows(); linha_inicial++)
         {   //quanto encontrar 'ENTITY-CODE' no codigo do MTG:
             if (sheet_mtg.getCell(0,linha_inicial).getContents().toString().equals("ENTITY-CODE"))
             {
                linha_inicial++; //a proxima linha sera a linha inicial
                return linha_inicial;
             }
         }
        
        return 0; //retorna 0 caso nao conseguir encontrar a linha inicial
    }
    

    

    //metodo para deixar todos os "numeros float" do MTG com ponto (.) ao inves de virgula (,):
    public void arrumarPadraoNumerosReais() throws WriteException
    {
         String str;

         //percorre todas as colunas de atributos:
         for (int coluna=PRIMEIRA_COLUNA_ATRIB; coluna<=ULTIMA_COLUNA_ATRIB; coluna++)
         {   //para todas as colunas que devem ser um numero:
             if (coluna==COLUNA_ALFARAMO || coluna==COLUNA_COMPENT || coluna == COLUNA_AREAFOLHA
              || coluna==COLUNA_ALFAFOLHA || coluna==COLUNA_COMPTRONCO || coluna == COLUNA_ALFATRONCO
              || coluna==COLUNA_ALTURASUP || coluna==COLUNA_ALFASUP || coluna == COLUNA_COMPSRAM)
             {   //percorre todas essas colunas linha por linha:
                 for (int linha =linha_inicial; linha<sheet_mtg.getRows(); linha++)
                 {   
                    str = sheet_mtg.getCell(coluna,linha).getContents(); //str recebe o conteudo de cada celula
                    str = str.replaceAll(" ",""); //se houver espaco em branco nesta celula, remova-o
                    str = str.replace(',','.'); //se houve virgula nesta string, troca a virgula por ponto
                    
                    sheet_mtg.addCell(new Label(coluna, linha, str)); //renova o conteudo da celula (sobescreve)

                 }     
             }
         }
    }
    

    /*metodo para encontrar Data do estagio no MTG e arruma-la p/ o padrao DD/MM/AAAA
      retorna:  1 se ok.
                0 se nao existe data declarada no MTG.
               -1 se houve erro de converter de String para data.
    */
    public int lerEArrumarData(MTGbase _mtgbase)
    {

         //se tipo da celula que deve representar uma data for realmente uma data:
         if (sheet_mtg.getCell(COLUNA_DAY, linha_inicial).getType().toString().equals("Date"))
         {

               //data recebe o dia da planilha do excel (topologia)
               Date data = ((DateCell)sheet_mtg.getCell(COLUNA_DAY, linha_inicial)).getDate();
               //ajuste p/ regular o dia certo:
               data.setTime(data.getTime()+(long)10000000); 
               //cria um dataformat para ler o "tempo do dia" no formato LONG:
               DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
               //cria um SimpleDateFormat p/ imprimir data no formato dd/MM/yyyy:
               SimpleDateFormat sdf = (SimpleDateFormat)dataformat;
               sdf.applyLocalizedPattern("dd/MM/yyyy");
               //str_data recebe a data no formato string:
               String str_dia = dataformat.format(data);
               System.out.println("\n Data => " + str_dia);

               try 
               {
                   Date d = new SimpleDateFormat("dd/MM/yyyy").parse(str_dia);
                   //seta as datas nos objetos MTGbase's:
                   _mtgbase.setData(d);
                   _mtgbase.setDataString(str_dia);
                   return 1; //ok

                } 
                catch (ParseException ex) 
                {
                   Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                   return -1; //nao conseguiu converter de String para data.
                }

          }
          else return 0; //senao: nao encontrou data declarada no MTG.
    }

    
    /*metodo para ler todos os dados iniciais da planta: (Sexo, Local, Comprimento de tronco e angulo alfa do tronco)
      retorna:  true se conseguiu ler todos os dados
                false se nao conseguiu ler todos os dados
    */
    public boolean lerDadosIniciasPlanta(Planta p, MTGbase _mtgbase)
    {
        
           //se atributo 'Sexo' existir
           if (sheet_mtg.getCell(COLUNA_SEXO, linha_inicial).getContents().equals("")==false)
           {   //armazena o sexo na Planta 'p'
               p.setSexo(sheet_mtg.getCell(COLUNA_SEXO, linha_inicial).getContents());
                   
               //se atributo 'Local' existir:
               if (sheet_mtg.getCell(COLUNA_LOCAL, linha_inicial).getContents().equals("")==false)
               {   //armazena o ambiente na Planta 'p'
                   p.setAmbiente(sheet_mtg.getCell(COLUNA_LOCAL, linha_inicial).getContents());
                       
                   //se atributo 'CompTronco' existir:
                   if (sheet_mtg.getCell(COLUNA_COMPTRONCO, linha_inicial).getContents().equals("")==false)
                   {
                        //armazena o comprimento do tronco na Planta 'p'
                        p.setCompTronco(sheet_mtg.getCell(COLUNA_COMPTRONCO, linha_inicial).getContents());
                            
                        //se atributo 'CompTronco' existir:
                        if (sheet_mtg.getCell(COLUNA_ALFATRONCO, linha_inicial).getContents().equals("")==false)
                        {                          
                            //armazena o angulo alfa do tronco na Planta 'p'
                            p.setAlfaTronco(sheet_mtg.getCell(COLUNA_ALFATRONCO, linha_inicial).getContents());    
                            return true;
                        }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o ANGULO ALFA DO TRONCO da planta no MTG."
                        else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO44"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            
                   }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o COMPRIMENTO DO TRONCO da planta no MTG."
                   else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO45"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                   
               }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o AMBIENTE da planta no MTG."
               else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO46"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                   
           }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o SEXO da planta no MTG."
           else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO47"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);


        return false; //retorna falso se nao conseguiu ler todos os itens
    }
    
    
    
    
    /*metodo para ler os entrenos do tronco na primeira coluna do MTG
      retorna:  true se conseguiu ler
                false se nao conseguiu ler
    */
    public boolean lerDadosTronco(Planta p, MTGbase _mtgbase)
    {
        boolean flag_adicionou=false;  
        
        for (int i=linha_inicial; i<=sheet_mtg.getRows(); i++)
        {
            if (sheet_mtg.getCell(0,i).getContents().equals("/P1/T1/U1/E1") //se a sintaxe esperada estiver correta:
            || sheet_mtg.getCell(0,i).getContents().startsWith("^<E"))
            {   
                
                if (sheet_mtg.getCell(COLUNA_ALTURASUP,i+1).getContents().length()>0) //se celula abaixo existir:
                {  
                    //adiciona o entreno
                    p.adicionarEntreno(Double.parseDouble(sheet_mtg.getCell(COLUNA_ALTURASUP,i+1).getContents()));
                 
                    flag_adicionou=true;
                }                                                                                                                          //" !\nNão encontrou o valor de 'AlturaSup' na coluna"                 //", linha "                                               //" na topologia do MTG."              
                else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() +  InterpolMateView.colecaomsgs.getString("ERRO48") + COLUNA_ALTURASUP + InterpolMateView.colecaomsgs.getString("ERRO49") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }            
        }        

        return flag_adicionou;
    }

    
    
    
    
    
    /*metodo para ler os entrenos do suporte na segunda coluna do MTG
      retorna:  true se conseguiu ler
                false se nao conseguiu ler
    */
    public boolean lerDadosSuporte(Planta p, MTGbase _mtgbase)
    {
        boolean flag_adicionou=false;  
        
        boolean primeira_vez=true;
        
        String str_pai="";
        int cod_pai=0;
        
        Suporte s = new Suporte(); 
        
        for (int i=linha_inicial; i<=sheet_mtg.getRows(); i++)
        {
            
            if (sheet_mtg.getCell(1,i).getContents().startsWith("+S")) //se a sintaxe esperada estiver correta:
            {   
                if (primeira_vez) //se for a primeira vez que encontra um suporte
                    primeira_vez=false; //desliga a flag;
                else              //se nao for a primeira vez (significa que tem algo guardado em "s":
                    p.adicionarSuporte(s); //adiciona este suporte à planta pai.
                
                
                s = new Suporte(); 
                
                if (sheet_mtg.getCell(COLUNA_COMPSRAM,i).getContents().length()>0) //se celula abaixo existir:
                {  
                    
                    if (sheet_mtg.getCell(0,i-1).getContents().startsWith("/P")) //se celula abaixo existir:
                    {             
                        if (sheet_mtg.getCell(0,i-1).getContents().substring(3, 11).compareTo("/T1/U1/E")==0)       
                            str_pai = sheet_mtg.getCell(0,i-1).getContents().substring(11);
                        else 
                        if (sheet_mtg.getCell(0,i-1).getContents().substring(4, 12).compareTo("/T1/U1/E")==0)
                            str_pai = sheet_mtg.getCell(0,i-1).getContents().substring(12);
                        else str_pai = "1";
                    }      
                    if (sheet_mtg.getCell(0,i-1).getContents().startsWith("^<E")) //se celula abaixo existir:  
                        str_pai = sheet_mtg.getCell(0,i-1).getContents().substring(3);
                   
                    cod_pai = Integer.parseInt(str_pai);
                    
                    s.setCodPai(cod_pai);
                    
                    s.setAnguloAlfa(Float.parseFloat(sheet_mtg.getCell(COLUNA_ALFASUP,i).getContents()));
                                        
                    //adiciona o entreno:
                    s.adicionarEntreno(Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPSRAM,i).getContents()));

                    flag_adicionou=true;
                    
                    
                }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o valor de 'CompSRam' na coluna "               //", linha "                                           //" na topologia do MTG."
                else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO51") + COLUNA_COMPSRAM + InterpolMateView.colecaomsgs.getString("ERRO49") + i + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }    
            
            
            
            if (sheet_mtg.getCell(1,i).getContents().startsWith("^<S")) //se a sintaxe esperada estiver correta:
            {   
                
                if (sheet_mtg.getCell(COLUNA_COMPSRAM,i).getContents().length()>0) //se celula abaixo existir:
                {  
                    //adiciona o entreno
                    s.adicionarEntreno(Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPSRAM,i).getContents()));
                 
                    flag_adicionou=true;
                }                                        //"Não foi possível reconhecer a topologia de MTG no arquivo "                      //" !\nNão encontrou o valor de 'CompSRam' na coluna "               //", linha "                                           //" na topologia do MTG."
                else JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO51") + COLUNA_COMPSRAM + InterpolMateView.colecaomsgs.getString("ERRO49") + i + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
            }         
            

        }        

        
        p.adicionarSuporte(s);

        return flag_adicionou;
    }
    
    

    /*metodo para ler os labels de galhos na terceira coluna do MTG
      retorna:  true se conseguiu ler
                false se nao conseguiu ler
    */
    public boolean lerDadosGalho(Planta p, MTGbase _mtgbase)
    {
        
        String str_suporte_pai;
        int cod_suporte_pai;
        
                
        for (int i=linha_inicial; i<=sheet_mtg.getRows(); i++)
        {
            str_suporte_pai=""; //limpa a variavel p/ utiliza-la novamente.
            
            if (sheet_mtg.getCell(2,i).getContents().startsWith("+G")) //se a sintaxe esperada estiver correta:
            {   
                
                if (sheet_mtg.getCell(1,i-1).getContents().length()>0) //se celula anterior existir:
                {  
                    
                    if (sheet_mtg.getCell(1,i-1).getContents().startsWith("+S")) //se celula abaixo existir:
                    {         
                        str_suporte_pai = sheet_mtg.getCell(1,i-1).getContents().substring(2,3);
                    }
                    else if (sheet_mtg.getCell(1,i-1).getContents().startsWith("^<S")) //se celula abaixo existir:
                    {         
                        str_suporte_pai = sheet_mtg.getCell(1,i-1).getContents().substring(3,4);
                    }
                    else 
                    {                                       //"Não foi possível reconhecer a topologia de MTG no arquivo "                                                                             //", linha "                                               //" na topologia do MTG."
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO53") + 1 + InterpolMateView.colecaomsgs.getString("ERRO49") + (i-1) + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    
                    
                    if (str_suporte_pai.length()>0) //se encontrou o indice de tal suporte:
                    {

                        cod_suporte_pai = Integer.parseInt(str_suporte_pai); //converte indice do suporte para inteiro
                    
                        
                        if (sheet_mtg.getCell(COLUNA_ALFARAMO,i).getContents().length()>0) //se celula abaixo existir:
                        {
                            Galho NovoGalho = new Galho (Float.parseFloat(sheet_mtg.getCell(COLUNA_ALFARAMO,i).getContents()));
                            
                            //le todas informacoes (entrenos) deste galho no MTG:
                            if (lerDadosEntrenos(NovoGalho, _mtgbase, i)==false) return false; //se der erro durante o procedimento retorna falso
                            
                            p.getSuporte(cod_suporte_pai-1).adicionarGalho(NovoGalho);
                        } 
                        else 
                        {                                                                                                                           //" !\nNão encontrou o valor de 'AlfaRamo' na coluna "
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO53") + COLUNA_ALFARAMO + InterpolMateView.colecaomsgs.getString("ERRO49") + i + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }

                    }
                    else 
                    {                                                                                                                           //" !\nNão encontrou a sintaxe correta do suporte na coluna "
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO52") + 1 + InterpolMateView.colecaomsgs.getString("ERRO49") + (i-1) + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
                else 
                {                                                                                                                           //" !\nNão encontrou a sintaxe correta do suporte na coluna "
                   JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO52")+ 1 + InterpolMateView.colecaomsgs.getString("ERRO49") + (i-1) + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                   return false;
                }

            }
                
        }
        
        return true;
    }
    
    
    
    /*metodo para ler os labels de galhos na terceira coluna do MTG
      retorna:  true se conseguiu ler
                false se nao conseguiu ler
    */
    public boolean lerDadosEntrenos(Galho Gal, MTGbase _mtgbase, int linha)
    { 
       
        
        //ESTADOS:
        final int COMECOU=-1;
        final int EIXPRINC=0;
        final int PRIMORD=1;
        final int SEGORD=2;
        final int TERORD=3;
        
        
        //posicoes das colunas:
        final int COLUNA_EIXO_PRINC = 3; 
        final int COLUNA_PRIM_ORD = 4;
        final int COLUNA_SEG_ORD = 5;
        final int COLUNA_TERC_ORD = 6;             
                 
        
        int en_esperado_eix_princ=0;
        int en_esperado_prim_ord=0;
        int en_esperado_seg_ord=0;
        int en_esperado_terc_ord=0;
        
        int un_cresc_eix_princ=0;
        int un_cresc_prim_ord=0;
        int un_cresc_seg_ord=0;
        int un_cresc_ter_ord=0;
        
        
        int coluna_atual = COLUNA_EIXO_PRINC;
        int ESTADO = COMECOU;
        
        int flag_atributos_entreno;
        
        linha++;
        
        //System.out.println("Comecou Leitura do MTG...");
        while (true)
        {
                //System.out.println("# ESTADO: " + ESTADO);
                /*************** SE ESTIVER NO EIXO PRINCIPAL ************* */
                //se comecou lendo este galho...
                if (ESTADO==COMECOU && coluna_atual==COLUNA_EIXO_PRINC && sheet_mtg.getCell(coluna_atual, linha).getContents().equals("/U1"))
                {
                    un_cresc_eix_princ=1;
                    linha++;
                    
                    if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1")) 
                    {
                        
                                                 
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {                                                                                                                           //" !\nNão encontrou a sintaxe correta dos atributos do entreno na coluna "
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                        else if (flag_atributos_entreno==1) //se tem folha
                            Gal.adicionarEntreno (0, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                        else
                            Gal.adicionarEntreno (0, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha));

                        
                        ESTADO = EIXPRINC;
                        en_esperado_eix_princ=2;   
                        linha++;             
                    }
                    else
                    {                                                                                                                           //" !\nNão encontrou a sintaxe correta do suporte na coluna "
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO52") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                }


                //se esta no eixo principal e encontrar um entreno...
                else if (ESTADO==EIXPRINC && coluna_atual==COLUNA_EIXO_PRINC && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<E"))
                {
                    
                    flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                    if (flag_atributos_entreno==-1)
                    {                                                                                                                           //" !\nNão encontrou a sintaxe correta dos atributos do entrenó na coluna "
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    else if (flag_atributos_entreno==1) //se tem folha
                        Gal.adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                    else
                        Gal.adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha));
                    
                    
                    en_esperado_eix_princ++;   
                    linha++;
                }

                //se esta no eixo principal e encontrar uma alteracao de unidade de crescimento...
                else if (ESTADO==EIXPRINC && coluna_atual==COLUNA_EIXO_PRINC && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<U"))
                {
                    un_cresc_eix_princ = Integer.parseInt(sheet_mtg.getCell(coluna_atual, linha).getContents().substring(3));
                    
                    linha++;

                    if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                    {
                        
                        
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {                                                                                                                           //" !\nNão encontrou a sintaxe correta dos atributos do entrenó na coluna "
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                        else if (flag_atributos_entreno==1) //se tem folha
                            Gal.adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                        else
                            Gal.adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_eix_princ, temRam(coluna_atual, linha));

                        
                        
                        ESTADO = EIXPRINC;
                        en_esperado_eix_princ=2;   
                        linha++;
                    }
                    else
                    {                                                                                                                           //"!\nNão encontrou a sintaxe correta na coluna "
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                }           

                //se esta no eixo principal e nao encontrar nada na celula:
                else if (ESTADO==EIXPRINC && coluna_atual==COLUNA_EIXO_PRINC && sheet_mtg.getCell(coluna_atual, linha).getContents().equals(""))
                {
                    if (sheet_mtg.getCell(COLUNA_PRIM_ORD, linha).getContents().equals("+U1")) //verifica se ha uma ramificacao (na coluna da frente)
                    {
                         //se houver, continua lendo o galho...
                         un_cresc_prim_ord=1;
                         linha++;
                         coluna_atual = COLUNA_PRIM_ORD;

                        if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                        {
                            ESTADO = PRIMORD;
                            
                            flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                            if (flag_atributos_entreno==-1)
                            {                                                                                                                           //"!\nNão encontrou a sintaxe correta do suporte na coluna "
                                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                            else if (flag_atributos_entreno==1) //se tem folha
                                Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                            else
                                Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha));

                            
                            en_esperado_prim_ord=2;   
                            linha++;
                        }
                        else
                        {                                                                                                                           //"!\nNão encontrou a sintaxe correta na coluna "
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }        

                    }
                    else return true; //se nao houver nada, significa que acabou o galho
                }    


                // ***************** SE ESTIVER NA PRIMEIRA ORDEM ************************ //
                //se estiver na coluna de primeira ordem e encontrar um entreno...
                else if (ESTADO==PRIMORD && coluna_atual==COLUNA_PRIM_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<E"))
                {
                    
                    flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                    if (flag_atributos_entreno==-1)
                    {                                                                                                                           //" !\nNão encontrou a sintaxe correta dos atributos do entrenó na coluna "
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    else if (flag_atributos_entreno==1) //se tem folha
                    {
                        

                        Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                    }
                    else
                        Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha));                    

                    
                    en_esperado_prim_ord++;   
                    linha++;
                }



                //se estiver na coluna de primeira ordem e encontrar uma alteracao de unidade de crescimento...
                else if (ESTADO==PRIMORD && coluna_atual==COLUNA_PRIM_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<U"))
                {
                    un_cresc_prim_ord = Integer.parseInt(sheet_mtg.getCell(coluna_atual, linha).getContents().substring(3));
                    linha++;

                    if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                    {
                        ESTADO = PRIMORD;
                        
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {                                                                                                                           //" !\nNão encontrou a sintaxe correta dos atributos do entrenó na coluna "
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                         }
                         else if (flag_atributos_entreno==1) //se tem folha
                            Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                         else
                            Gal.getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_prim_ord, temRam(coluna_atual, linha));
                        
                        
                        en_esperado_prim_ord=2;   
                        linha++;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                }   



                //se estiver na coluna de primeira ordem e nao encontrar nada na celula...
                else if (ESTADO==PRIMORD && coluna_atual==COLUNA_PRIM_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().equals(""))
                {


                    if (sheet_mtg.getCell(COLUNA_EIXO_PRINC, linha).getContents().length()>0) //verifica se esta voltando para o eixo principal...
                    {
                        ESTADO = EIXPRINC;
                        coluna_atual = COLUNA_EIXO_PRINC;
                    }
                    else if (sheet_mtg.getCell(COLUNA_SEG_ORD, linha).getContents().equals("+U1")) //se nao estiver, verifica se ha uma ramificacao (na coluna da frente)
                    {
                         //se houver, continua lendo o galho...
                         un_cresc_seg_ord=1;
                         linha++;
                         coluna_atual = COLUNA_SEG_ORD;

                        if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                        {
                            ESTADO = SEGORD;
                            
                            
                            flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                            if (flag_atributos_entreno==-1)
                            {
                                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                                return false;
                             }
                             else if (flag_atributos_entreno==1) //se tem folha
                                Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                             else
                                Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha));

                            
                            en_esperado_seg_ord=2;   
                            linha++;
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }        

                    }
                    else return true; //se nao houver nada, significa que acabou o galho...
                }            




                // ***************** SE ESTIVER NA SEGUNDA ORDEM ************************ //
                //se estiver na coluna de segunda ordem e encontrar um entreno...
                else if (ESTADO==SEGORD && coluna_atual==COLUNA_SEG_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<E"))
                {
                    
                    
                     flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                     if (flag_atributos_entreno==-1)
                     {
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                     }
                     else if (flag_atributos_entreno==1) //se tem folha
                        Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                     else
                        Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha));                    
                    
                    
                    en_esperado_seg_ord++;   
                    linha++;
                }

                //se estiver na coluna de segunda ordem e encontrar uma alteracao de unidade de crescimento...
                else if (ESTADO==SEGORD && coluna_atual==COLUNA_SEG_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<U"))
                {
                    un_cresc_seg_ord = Integer.parseInt(sheet_mtg.getCell(coluna_atual, linha).getContents().substring(3));
                    linha++;

                    if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                    {
                        ESTADO = SEGORD;
                        
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {
                           JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                           return false;
                        }
                        else if (flag_atributos_entreno==1) //se tem folha
                           Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                        else
                           Gal.getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_seg_ord, temRam(coluna_atual, linha));                        
                        
                        
                        en_esperado_seg_ord=2;   
                        linha++;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                }   

                //se estiver na coluna de segunda ordem e nao encontrar nada na celula...
                else if (ESTADO==SEGORD && coluna_atual==COLUNA_SEG_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().equals(""))
                {


                    if (sheet_mtg.getCell(COLUNA_PRIM_ORD, linha).getContents().length()>0)  //verifica se esta voltando para uma ramificacao de primeira ordem...
                    {
                        ESTADO = PRIMORD;
                        coluna_atual = COLUNA_PRIM_ORD;
                    }
                    else 
                    if (sheet_mtg.getCell(COLUNA_EIXO_PRINC, linha).getContents().length()>0)  //se nao estiver, verifica se esta voltando para o eixo principal...
                    {
                        ESTADO = EIXPRINC;
                        coluna_atual = COLUNA_EIXO_PRINC;             
                    }
                    else if (sheet_mtg.getCell(COLUNA_TERC_ORD, linha).getContents().equals("+U1")) //se nao estiver, verifica se ha uma ramificacao (na coluna da frente)
                    {
                         //se houver, continua lendo o galho...
                         un_cresc_ter_ord=1;
                         
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                         }
                         else if (flag_atributos_entreno==1) //se tem folha
                            Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                         else
                            Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha));                         
                         
                         linha++;
                         coluna_atual = COLUNA_TERC_ORD;

                        if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                        {
                            ESTADO = TERORD;
                            en_esperado_terc_ord=2;   
                            linha++;
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                        }        

                    }
                    else return true; //se nao houver nada, significa que acabou o galho...
                }             






                // ***************** SE ESTIVER NA TERCEIRA ORDEM ************************ //
                //se estiver na coluna de terceira ordem e encontrar um entreno...
                else if (ESTADO==TERORD && coluna_atual==COLUNA_TERC_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<E"))
                {
                    
                    flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                    if (flag_atributos_entreno==-1)
                    {
                       JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56")  + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                       return false;
                    }
                    else if (flag_atributos_entreno==1) //se tem folha
                       Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                    else
                       Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha));                      
                    
                    en_esperado_terc_ord++;   
                    linha++;
                }

                //se estiver na coluna de terceira ordem e encontrar uma alteracao de unidade de crescimento...
                else if (ESTADO==TERORD && coluna_atual==COLUNA_TERC_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().startsWith("^<U"))
                {
                    un_cresc_ter_ord = Integer.parseInt(sheet_mtg.getCell(coluna_atual, linha).getContents().substring(3));
                    linha++;

                    if (sheet_mtg.getCell(coluna_atual, linha).getContents().equals("^/E1"))
                    {
                        ESTADO = TERORD;
                        
                        flag_atributos_entreno = verificarAtributosEntreno(_mtgbase, linha);
                        if (flag_atributos_entreno==-1)
                        {
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO56") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            return false;
                         }
                         else if (flag_atributos_entreno==1) //se tem folha
                            Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha), Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents()), Double.parseDouble(sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents()));
                         else
                            Gal.getUltimoEntreno().getUltimoEntreno().getUltimoEntreno().adicionarEntreno (ESTADO, Double.parseDouble(sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents()), un_cresc_ter_ord, temRam(coluna_atual, linha));                          
                        
                        en_esperado_terc_ord=2;   
                        linha++;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO54") + coluna_atual + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                }   



                //se estiver na coluna de terceira ordem e nao encontrar nada na celula...
                else if (ESTADO==TERORD && coluna_atual==COLUNA_TERC_ORD && sheet_mtg.getCell(coluna_atual, linha).getContents().equals(""))
                {


                    if (sheet_mtg.getCell(COLUNA_SEG_ORD, linha).getContents().length()>0) //verifica se esta voltando para uma ramificacao de segunda ordem...
                    {
                        ESTADO = SEGORD;
                        coluna_atual = COLUNA_SEG_ORD;
                    }
                    else
                    if (sheet_mtg.getCell(COLUNA_PRIM_ORD, linha).getContents().length()>0) //se nao estiver, verifica se esta voltando para uma ramificacao de primeira ordem...
                    {
                        ESTADO = PRIMORD;
                        coluna_atual = COLUNA_PRIM_ORD;
                    }
                    else 
                    if (sheet_mtg.getCell(COLUNA_EIXO_PRINC, linha).getContents().length()>0) //se nao estiver, verifica se esta voltando para o eixo principal...
                    {
                        ESTADO = EIXPRINC;
                        coluna_atual = COLUNA_EIXO_PRINC;             
                    }
                    else return true; //se nao houver nada nas colunas anteriores, significa que acabou o galho...
                }   
                
                else return true; //se nao encontrar nenhum estado, acabou o galho.
            
            
           
        }
        
    }
        

    /* metodo para verificar se atributos do entreno nesta linha esta ok e se tem folha.
    Retorna: 1 se "tem folha"
             0 se "nao tem folha" 
            -1 se deu erro                                             */          
    public int verificarAtributosEntreno(MTGbase _mtgbase, int linha)
    { 
          String str_compent   = sheet_mtg.getCell(COLUNA_COMPENT,linha).getContents(); //resgata o valor do comprimento do entreno
          String str_areafolha = sheet_mtg.getCell(COLUNA_AREAFOLHA,linha).getContents(); //resgata o valor da area da folha 
          String str_alfafolha = sheet_mtg.getCell(COLUNA_ALFAFOLHA,linha).getContents(); //resgata o valor do angulo alfa da folha
            
          boolean tem_folha;
          
          //Lendo o comprimento do entreno:
          if (str_compent.length()==0) //se nao houver nenhum valor onde deveria ter o comprimento do entreno:
          {   //acusa erro:                                                                                                           //" !\nNão encontrou o valor do comprimento do entrenó na coluna "
              JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + InterpolMateView.colecaomsgs.getString("ERRO55") + COLUNA_COMPENT + InterpolMateView.colecaomsgs.getString("ERRO49") + linha + InterpolMateView.colecaomsgs.getString("ERRO50"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
              return -1;
          }    
                    
          //Lendo as folhas:
          if (str_areafolha.length()>0 && str_alfafolha.length()>0)  //se houverem valores nas duas colunas, entao EXISTE UMA FOLHA neste entreno
          {   
              tem_folha=true;
          }
          else if (str_areafolha.length()==0 && str_alfafolha.length()==0) //se nao houverem valores nas duas colunas, entao NAO EXISTE FOLHA neste entreno
          {
              tem_folha=false;
          }
          else //caso contrario acusa erro: (pois as 2 colunas devem ou estar preenchidas, ou estar vazias)
          {   
              JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO3") + _mtgbase.getArquivo().getName() + " !\nNa linha " + linha + " nas colunas " + COLUNA_AREAFOLHA + " e " + COLUNA_ALFAFOLHA + ", devem estar preenchidas as duas colunas caso exista folha neste entrenó.\nCaso não exista folha neste entrenó, as duas células referentes a estas colunas devem estar vazias.", InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
              return -1;
          }   
          
          if (tem_folha) return 1;
          else return 0;
    }
    
    
    
    
    /* metodo para verificar se um entreno tem ramificacao
    Retorna: 1 se tem ramificacao 
             0 se nao tem ramificacao                         */          
    public boolean temRam(int coluna, int linha)
    {     

        String celula_inf_dir = sheet_mtg.getCell(coluna+1,linha+1).getContents();
        
        if (celula_inf_dir.startsWith("+"))
            return true;
        else 
            return false;
    }

    public double lerAreaFoliarTotal()
    {
        double area_foliar_total=0;

        for (int i=linha_inicial; i<=sheet_mtg.getRows(); i++)
        {
             if (sheet_mtg.getCell(COLUNA_AREAFOLHA,i).getContents().length()>0) //se celula abaixo existir:
             {  
                 area_foliar_total = area_foliar_total + Double.parseDouble(sheet_mtg.getCell(COLUNA_AREAFOLHA,i).getContents()); 
             }
        }        

        return area_foliar_total;
    }
    
    public int lerQtdeFolhas()
    {
        int qtde_folhas=0;

        for (int i=linha_inicial; i<=sheet_mtg.getRows(); i++)
        {
             if (sheet_mtg.getCell(COLUNA_AREAFOLHA,i).getContents().length()>0) //se celula abaixo existir:
             {  
                 qtde_folhas++;
             }
        }        

        return qtde_folhas;
    }   
    
    

}
    
