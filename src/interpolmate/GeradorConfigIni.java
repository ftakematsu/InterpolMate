/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Murilo
 */
public class GeradorConfigIni {

    public GeradorConfigIni()
    {}
    
    public void executar() throws FileNotFoundException
    {             
        //exibe um JOptionPane questionando o usuario se deseja sobescrever o arquivo de nome ja existente, ou se deseja desistir:
        Object[] options = { InterpolMateView.colecaomsgs.getString("Sim"), InterpolMateView.colecaomsgs.getString("Nao") }; 
        int n = JOptionPane.showOptionDialog(null,    
                                            InterpolMateView.colecaomsgs.getString("PERGUNTA4"), //"Deseja localizar o AMAPmod para possível integração no decorrer da execução do programa?"  
                                            InterpolMateView.colecaomsgs.getString("Pergunta"), JOptionPane.YES_NO_OPTION,    
                                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);    
 
        if (n==0) //se o usuario escolheu sim
        {   
            buscarAMAPmod();
        }
        else
        {
            //cria um novo arquivo chamado config.ini p/ guardar configuracoes de integracao com AMAPMOD:
            PrintWriter prwriter = new PrintWriter(new FileOutputStream("config.ini")); 
            //imprime no arquivo config.ini:
            prwriter.println("INTEGRAVPLANTS=NAO");
            prwriter.close(); //fecha o printwriter   
        }
   
    }
    
    

    public void buscarAMAPmod() throws FileNotFoundException
    {
            //cria um novo arquivo chamado config.ini p/ guardar configuracoes de integracao com AMAPMOD:
            PrintWriter prwriter = new PrintWriter(new FileOutputStream("config.ini")); 
            
            //abrir janela de busca de arquivo
            JFileChooser fc = new JFileChooser(); //cria um "escolhedor de arquivos"
            //seta os textos presentes neste JFileChooser:
            fc.setDialogTitle(InterpolMateView.colecaomsgs.getString("TITULODIALOGO")); //"Procure o arquivo executável do AMAPmod..."
                      
            FileNameExtensionFilter filtro_grf = new FileNameExtensionFilter("aml.exe", "exe");  //cria um filtro de arquivo para a extensao "grf" (grafico). 

            fc.addChoosableFileFilter(filtro_grf); //adiciona este filtro para o "escolhedor de arquivos" exibir arquivos do tipo "exe".
    
            int status = fc.showOpenDialog(null); //este escolhedor de arquivos abrirá uma janela de "abertura de arquivos"
    
            if (status == JFileChooser.APPROVE_OPTION) //se um arquivo foi escolhido:
            {
                
                if (fc.getSelectedFile().getName().compareTo("aml.exe")==0 || fc.getSelectedFile().getName().compareTo("aml")==0)
                {
                    //mostra mensagem de sucesso:                   //"AMAPmod encontrado com sucesso!"
                    JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("AVISO3"), InterpolMateView.colecaomsgs.getString("Aviso"), JOptionPane.WARNING_MESSAGE);
                    //imprime no arquivo config.ini:
                    prwriter.println("INTEGRARAMAPMOD==SIM\nCAMINHO=" + fc.getSelectedFile().getAbsolutePath());
                }
                else 
                {
                    //mostra mensagem de erro:          //"Arquivo executável a ser encontrado deve se chamar aml.\nFalhou a tentativa de associar o AMAPmod ao programa.\nTente depois indo no menu ARQUIVO -> INTEGRAÇÃO AMAPMOD."
                    JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO42"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                    //imprime no arquivo config.ini:
                    prwriter.println("INTEGRAVPLANTS==NAO");
                }
            }
            else 
            {
                //mostra mensagem de erro:          //"Não conseguiu encontrar o AMAPmod, portanto falhou a tentativa de associá-lo ao programa.\nTente depois indo no menu ARQUIVO -> INTEGRAÇÃO AMAPMOD."
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO43"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                //imprime no arquivo config.ini:
                prwriter.println("INTEGRAVPLANTS==NAO");
            }  
            
            prwriter.close(); //fecha o printwriter   
    }
    
    
    
    
    public static boolean isAMAPmodAtivado()
    {
          BufferedReader br = null; //criar um leitor para percorrer o conteudo do arquivo
          String linha = null;
                try 
                {
                    br = new BufferedReader(new FileReader("config.ini"));
                } 
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
          
                try 
                {
                    linha = br.readLine(); //captura a primeira linha desta arquivo
                    
                    if (linha.length()>16)
                    {
                        if (linha.substring(16).compareTo("SIM")==0 || linha.substring(15).compareTo("SIM")==0)
                            return true;
                        else 
                            return false;
                    }
                     else
                        return false;
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
    }
        
    public static String getCaminhoAMAPmod()
    {
          BufferedReader br = null; //criar um leitor para percorrer o conteudo do arquivo
          String linha = null;
                try 
                {
                    br = new BufferedReader(new FileReader("config.ini"));
                } 
                catch (FileNotFoundException ex) 
                {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                    return "";
                }
          
                try 
                {
                    linha = br.readLine(); 
                    linha = br.readLine(); //captura a segunda linha desta arquivo
                    
                    if (linha.length()>8)
                        return linha.substring(8); 
                    else
                        return "";
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(InterpolMateView.class.getName()).log(Level.SEVERE, null, ex);
                    return "";
                }
    }
    
}









