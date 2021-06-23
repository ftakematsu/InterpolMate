/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author murilo
 * @author fabio
 */
public class ExibicaoConjunto3D {
    
    DefaultTableModel ModeloTabelaConjuntoPara3D;
    String caminho_dos_arquivos, cam_arq_inic, cam_arq_fin;
    PrintWriter prwriter;
            
    private boolean folhagem;
    private boolean mostrar_galhos;
    int total_dias;
        
    
    final int LINHA_INICIAL=43;
    int linha_atual;

    final static char ASPAS = (char)34;
    
    public ExibicaoConjunto3D (DefaultTableModel ModeloTabelaConjunto3D, String caminho_arquivos, int total_de_dias, String cam_arq_inicial, String cam_arq_final)
    {
        ModeloTabelaConjuntoPara3D = ModeloTabelaConjunto3D;
        caminho_dos_arquivos = caminho_arquivos;
 
        folhagem = true;
        total_dias = total_de_dias;
        cam_arq_inic = cam_arq_inicial;
        cam_arq_fin = cam_arq_final;
    }
    
    
    

    public boolean exibir() throws IOException, BiffException, WriteException
    {
        prwriter = new PrintWriter(new FileOutputStream("temp/conjunto.mtg"));
        
        WritableWorkbook workbook_escrita = Workbook.createWorkbook(new File("temp/temp_recorte.xls")); 
        WritableSheet sheet_escrita = workbook_escrita.createSheet("Recortando", 0); 
       
        linha_atual = LINHA_INICIAL;
        

        EscritaMTG.insereCabecario(sheet_escrita);
        
        //salva e fecha o workbook:
        workbook_escrita.write();
        workbook_escrita.close();
        
        ConversorXLSParaTexto conv = new ConversorXLSParaTexto("temp/temp_recorte.mtg", sheet_escrita);
        conv.converte();
        ColocarNoConjuntoMTG("temp/temp_recorte.mtg");
        
        
        // Gera um MTG com todas as plantas
        for (int indice_tabela=0; indice_tabela < ModeloTabelaConjuntoPara3D.getRowCount(); indice_tabela++)
        {
               
            String str_nome_do_arquivo = ModeloTabelaConjuntoPara3D.getValueAt(indice_tabela, 0).toString();
            Long dias  = Long.parseLong(ModeloTabelaConjuntoPara3D.getValueAt(indice_tabela, 1).toString());
            
            File arquivo;
            
            if (dias == 0) arquivo = new File(cam_arq_inic);
            else if (dias == (long)total_dias) arquivo = new File(cam_arq_fin);
            else arquivo = new File(caminho_dos_arquivos + "/" + str_nome_do_arquivo);
            

            if (arquivo.exists() == false) 
            {                                       //"Arquivo "                                            //" não existe em "
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO36") + str_nome_do_arquivo + InterpolMateView.colecaomsgs.getString("ERRO37") + caminho_dos_arquivos + ".", InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                return false;              
            }   
            else 
            {  

                if (arquivo.getName().endsWith("xls"))
                {
                    inserirPedacoDeXLSnoMTG(arquivo, (indice_tabela+1));
                }
                else
                {
                     if (arquivo.getName().endsWith("mtg"))
                     {
                         WritableWorkbook workbook_temprecorte = Workbook.createWorkbook(new File("temp/temprecorte2.xls")); 
                         WritableSheet sheet_temprecorte = workbook_temprecorte.createSheet("Recortando", 0); 
                         ConversorTextoParaXLS conversor = new ConversorTextoParaXLS(arquivo, sheet_temprecorte);  
                         if (conversor.converte())
                         {   
                              workbook_temprecorte.write();
                              workbook_temprecorte.close();
                              
                              arquivo = new File("temp/temprecorte2.xls");
                              
                              inserirPedacoDeXLSnoMTG(arquivo, (indice_tabela+1));
                         }
                         else
                         {                                      //"Não foi possível converter o arquivo "                       //" em xls p/ poder gerar o arquivo com todas as plantas."
                            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO40") + str_nome_do_arquivo + InterpolMateView.colecaomsgs.getString("ERRO41"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                            workbook_temprecorte.write();
                            workbook_temprecorte.close();
                            return false;
                         }
                         
                     }
                     else
                     {                                        //"Extensão do arquivo "                                                  //" não é válida! Necessita ser xls ou mtg."
                         JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO38") + str_nome_do_arquivo + InterpolMateView.colecaomsgs.getString("ERRO39"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                         return false;
                     }   
                }
            }    
       }

       prwriter.close();
       // Até aqui, o MTG com o conjunto de plantas já está criado
       // Agora, basta fazer o processamento


       // ***************************************
       // * Processamento do MTG
       // ***************************************
       File arquivo_conjuntomtg = new File("temp/conjunto.mtg"); //captura o arquivo "temp.mtg"
       if (arquivo_conjuntomtg.exists() == false)   //"Não foi possível visualizar o arquivo "
            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO29") + arquivo_conjuntomtg.getName() + InterpolMateView.colecaomsgs.getString("ERRO30"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
       else 
       {    
           String caminho_mtg = arquivo_conjuntomtg.getAbsolutePath(); //pega o caminho absoluto deste arquivo 

           BufferedReader br = new BufferedReader(new FileReader("config.ini"));
           String linha = br.readLine();

           // ****************************************
           // * Se o AMAPmod estiver integrado
           // ****************************************
           if (linha.startsWith("INTEGRARAMAPMOD"))
           {
                //cria um novo arquivo chamado amlparte1.aml, p/ escrever o caminho do MTG (que sera parte do codigo AML)
                PrintWriter prwriter_ = null;
                try {
                    prwriter_ = new PrintWriter(new FileOutputStream("amlparte1.aml"));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no arquivo amlparte1.aml:
                prwriter_.println("EchoOn()\n\ng=MTG(" + InterpolMateView.ASPAS + caminho_mtg.replaceAll((""+InterpolMateView.ASPAS), "") + InterpolMateView.ASPAS + ")\n");
                prwriter_.close(); //fecha o printwriter
            
                Runtime rt = Runtime.getRuntime();
                String caminho_amapmod = GeradorConfigIni.getCaminhoAMAPmod();

                String nome_aml_parte3;
                if (MostrarFolhagem()) nome_aml_parte3 = "amlparte3cf.aml";
                else nome_aml_parte3 = "amlparte3sf.aml";

            
                try
                {
                    /*caminho_amapmod recebe o caminho do executavel do amapmod com o ":" trocado por ":\"
                    Exemplo: de c:\Arquivos de Programas\aml.exe... para c:\\Arquivos de Programas\aml.exe... */
                    caminho_amapmod = InterpolMateView.ArrumarCaminhoPadraoWindows(caminho_amapmod);

                    Process proc = rt.exec("cmd.exe /c start " + caminho_amapmod + " +i amlparte1.aml amlparte2-win.aml " + nome_aml_parte3);
                }
                catch(Exception e)
                {
                    //Nao conseguiu o caminho do Windows
                    try
                    {
                        Process proc = rt.exec("xterm -e " + caminho_amapmod.replaceAll((""+InterpolMateView.ASPAS), "") + " +i amlparte1.aml amlparte2-lnx.aml " + nome_aml_parte3);
                    }
                    catch(Exception e2)
                    {   //Nao conseguiu o caminho do Linux
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO31"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                    }
                }
           }

           // ****************************************
           // * Se for o VPlants integrado (Python)
           // ****************************************
           else if (linha.startsWith("INTEGRAVPLANTS"))
           {
                //JOptionPane.showMessageDialog(null, "Integração do VPlants em construção...", "VPlants", JOptionPane.WARNING_MESSAGE);
                String caminho_python = GeradorConfigIni.getCaminhoAMAPmod();
                File arq_py = new File("python_files/modelo_ervamate_3D.py");
                String pasta_scripts_py = arq_py.getAbsolutePath().replace("modelo_ervamate_3D.py", "").replaceAll("\\\\", "/");

                PrintWriter script_python = new PrintWriter(new FileOutputStream(arq_py));
                

                // Escrevendo o cabeçalho no arquivo modelo_ervamate_3D.py
                script_python.println("from openalea.aml import *");
                script_python.println("from math import *");

                // Escrevendo o caminho do arquivo do MTG a ser processado
                script_python.println("\nmtg_ervamate=MTG(" + ASPAS + caminho_mtg.replaceAll("\\\\", "/") + ASPAS + ")\n");
                
                script_python.println("\n# Vetor com todas as plantas do MTG");
                script_python.println("plantas = VtxList(Scale=1)");
                script_python.println("\ndress = DressingData(" + ASPAS + pasta_scripts_py + "ervamate.drf" + ASPAS + ")\n");
                
                InputStream is = new FileInputStream("python_files/estrutura_3D.py");
                //InputStreamReader é uma classe para converter os bytes em char
                InputStreamReader isr = new InputStreamReader(is);
                //BufferedReader é uma classe para armazenar os chars em memoria
                BufferedReader buffer = new BufferedReader(isr);
                
                // Montando o script para processamento do MTG
                // Escrevendo as demais partes do arquivo
                AnexarNoArquivo(script_python, buffer);

                // Se estiver configurado para mostrar a folhagem
                // Anexa a parte do script para leitura dos atributos foliares e exibição 3D de folhas
                InputStream is_folhas = new FileInputStream("python_files/folhas_3D.py"); // Arquivo o qual deseja se acrescentar (append) no script_python
                InputStreamReader isr_folhas = new InputStreamReader(is_folhas);
                BufferedReader buffer2 = new BufferedReader(isr_folhas);
                // Parte do script que faz leitura dos atributos foliares
                AnexarNoArquivo(script_python, buffer2);

                // * Faz-se os filtros desejados

                // Se os galhos e a folhagem não estiverem habilitados simultaneamente
                // significa que será aplicado um filtro
                if (!(MostrarGalhos() && MostrarFolhagem()))
                {
                    InputStream is_filtro;
                    InputStreamReader isr_filtro;
                    BufferedReader buffer_filtro;
                    if (!MostrarFolhagem()) // Se não for mostrar folhagem, aplica o filtro de folhas
                    {
                        is_filtro = new FileInputStream("python_files/filtro_folhas.py"); // Arquivo o qual deseja se acrescentar (append) no script_python
                        isr_filtro = new InputStreamReader(is_filtro);
                        buffer_filtro = new BufferedReader(isr_filtro);
                        AnexarNoArquivo(script_python, buffer_filtro);
                        isr_filtro.close();
                    }
                    else if (!MostrarGalhos()) // Se não for mostrar galhos, aplica o filtro dos galhos
                    {
                        is_filtro = new FileInputStream("python_files/filtro_galhos.py"); // Arquivo o qual deseja se acrescentar (append) no script_python
                        isr_filtro = new InputStreamReader(is_filtro);
                        buffer_filtro = new BufferedReader(isr_filtro);
                        AnexarNoArquivo(script_python, buffer_filtro);
                        isr_filtro.close();
                    }
                }
                else // Se não, escreve no script a função para plotar sem filtro
                {
                    script_python.println("# Plota a planta da erva-mate em 3D");
                    script_python.println("Plot(plant_frame, VirtualLeaves=folha_virtual, DressingData=dress)");
                }

                script_python.append("raw_input(\"Pressione ENTER para continuar...\")");

                
                // Executa o script  em Python gerado
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("cmd.exe /c start " + caminho_python + " python_files/modelo_ervamate_3D.py");

                script_python.close();
                isr.close();
           }
       }
       return true;   
    }

    
    // Função para acrescentar o conteúdo de um arquivo
    // (anexar conteúdo no arquivo PrintWriter a partir de um BufferedReader)
    public void AnexarNoArquivo(PrintWriter script_python, BufferedReader buffer) throws IOException
    {
        String s;
        // A leitura está sendo feita por linha
        s = buffer.readLine(); //primeira linha
        script_python.println(s);
        while (s != null)
        {
            //System.out.println(s);
            s = buffer.readLine(); //primeira linha
            if (s!=null) script_python.println(s);
        }
    }

   
    public void setMostrarFolhagem(boolean flag_folhagem)
    {
        folhagem = flag_folhagem;
    }
    
    public boolean MostrarFolhagem()
    {
        return folhagem;
    }    

    public void setMostrarGalhos(boolean flag_galhos)
    {
        mostrar_galhos = flag_galhos;
    }

    public boolean MostrarGalhos()
    {
        return mostrar_galhos;
    }
    
    /*
     *  Insere a planta que esta em arquivoxls no mtg global sendo criado
     *  O parametro "num_planta" eh o codigo da planta que sera escrita.
     */
    private void inserirPedacoDeXLSnoMTG(File arquivoxls, int num_planta) throws IOException, BiffException, WriteException
    {
        //workbook_fonte recebe o conteudo do arquivo xls:
        Workbook workbook_temp_fonte = Workbook.getWorkbook(arquivoxls); //abre o arquivo xls contendo a planilha a ser lida
        WritableWorkbook workbook_fonte = Workbook.createWorkbook(new File ("temp/temp_recortando.xls"), workbook_temp_fonte);
        WritableSheet sheet_recortando = workbook_fonte.getSheet(0);

        while (sheet_recortando.getCell(0, 0).getContents().compareTo("/P1/T1/U1/E1")!=0) 
        {      
            if (sheet_recortando.getRows()>1)
            {
                sheet_recortando.removeRow(0);
            }
        }
        
        
        if (sheet_recortando.getCell(0, 0).getContents().compareTo("/P1/T1/U1/E1")==0) 
            sheet_recortando.addCell(new Label(0, 0, "/P" + num_planta + "/T1/U1/E1"));    

        
        workbook_fonte.write();

        ConversorXLSParaTexto conv = new ConversorXLSParaTexto("temp/temp_recortando.mtg", sheet_recortando);
        conv.converte();
        ColocarNoConjuntoMTG("temp/temp_recortando.mtg");
         
        workbook_fonte.close();
        
        
        File f1 = new File("temp/temp_recortando.xls");
        if (f1.exists()) f1.delete();
        File f2 = new File("temp/temp_recortando.mtg");
        if (f2.exists()) f2.delete();
    }
    
    

    public void ColocarNoConjuntoMTG(String nome_arquivo_fonte)
    {
        try 
        {

            FileReader fr = new FileReader(nome_arquivo_fonte); //le um arquivo
            BufferedReader br = new BufferedReader(fr);
            String tmp;

            while((tmp = br.readLine()) != null)
            {
                //le a linha ate o fim do arquivo
                prwriter.print(tmp);
                prwriter.print("\n");
            }

        br.close();

        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
}

