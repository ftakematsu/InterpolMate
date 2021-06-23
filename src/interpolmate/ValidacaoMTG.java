/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author murilo
 */
public class ValidacaoMTG {
    
    
    public ValidacaoMTG()
    {}
    
    
    /* metodo para verificar se a planta no estagio inicial possui algum valor na topologia maior (que nao deveria ser) que em seu estagio final.
     * 
     *  Recebe por parametro: 
     *              - PlantaInicial:    a estrutura topologica da planta em seu estágio inicial.
     *              - PlantaFinal:      a estrutura topologica da planta em seu estágio final.
     * 
     *  Retorna: true se deu tudo certo, ou falso se encontrou algum erro.
     *                                                                           */
    public boolean VerificaTamanho(Planta PlantaInicial, Planta PlantaFinal)
    {
        
        //verifica se no estagio inicial possui mais entrenos no nivel da planta que no estagio final
        if (PlantaInicial.getQtdeEntreno()>PlantaFinal.getQtdeEntreno()) //se possuir exibe mensagem de erro e retorna falso:
        {                                       //"Quantidade de entrenós da planta (na coluna 0) no estágio inicial é maior \nque a quantidade de entrenós da planta no estágio final (na coluna 0).\nArrume o arquivos MTG e processe novamente."
            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO57"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
            return false;
        }
        

        //verifica se existe algum entreno (da coluna 0 da planta) que esta maior no estagio inicial que no estagio final
        for (int i=0; i<PlantaInicial.getQtdeEntreno(); i++)
        {
            if (Double.compare(PlantaInicial.getEntreno(i).getComp(), PlantaFinal.getEntreno(i).getComp())>0) //se existir exibe mensagem de erro e retorna falso:
            {                                       //"O entrenó "                                             //" da planta é maior no estágio inicial que no estágio final!\nO valor pode ser menor ou igual, mas nunca maior.\nArrume o arquivo MTG e processe novamente."
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO59"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                return false;
            }                  
        }

        //verifica se no estagio inicial existem mais suportes que no estagio final
        if (PlantaInicial.getQtdeSuportes()>PlantaFinal.getQtdeSuportes()) //se existir exibe mensagem de erro e retorna falso:
        {                                         //"Quantidade de suportes no estágio inicial é maior que no estágio final.\nArrume o arquivo MTG e processe novamente."
            JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO60"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
            return false;
        }
        
        //para cada suporte (obs: i=suporte)
        for (int i=0; i<PlantaInicial.getQtdeSuportes(); i++)
        {
             //verifica se no estagio inicial este suporte possui mais entrenos que no estagio final
            if  (PlantaInicial.getSuporte(i).getQtdeEntrenos() > PlantaFinal.getSuporte(i).getQtdeEntrenos()) //se possuir exibe mensagem de erro e retorna falso:
            {                                       //"Quantidade de entrenos do suporte "                   //" no estágio inicial é maior que no estágio final.\nArrume o arquivo MTG e processe novamente."
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO61") + (i+1) + " " + InterpolMateView.colecaomsgs.getString("ERRO62"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                return false;                 
            }
            
            
            //verifica se existe algum entreno (da coluna 1, do suporte) que esta maior no estagio inicial que no estagio final
            for (int j=0; j<PlantaInicial.getSuporte(i).getQtdeEntrenos(); j++)
            {
                if( Double.compare( PlantaInicial.getSuporte(i).getEntreno(j).getComp(), //se existir exibe mensagem de erro e retorna falso:
                                      PlantaFinal.getSuporte(i).getEntreno(j).getComp()) > 0 )
                {                                       //"O entrenó "                                             //" do suporte "                                            //" é maior no estágio inicial que no estágio final!\nO valor pode ser menor ou igual, mas nunca maior.\nArrume o arquivo MTG e processe novamente."
                    JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (j+1) + InterpolMateView.colecaomsgs.getString("ERRO63") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO64"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                    return false;
                }
            }
            
            
             //verifica se no estagio inicial este suporte possui mais galhos que no estagio final
            if  (PlantaInicial.getSuporte(i).getQtdeGalhos() > PlantaFinal.getSuporte(i).getQtdeGalhos()) //se possuir exibe mensagem de erro e retorna falso:
            {                                      //"Quantidade de galhos do suporte "                        //" no estágio inicial é maior que no estágio final.\nArrume o arquivo MTG e processe novamente."
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO65") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO62"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                return false;                 
            }            
            
            
            //verifica se a quantidade de entrenos no estagio inicial eh maior que no estagio final:
            for (int k=0; k<PlantaInicial.getSuporte(i).getQtdeGalhos(); k++)
            { 
                if ( PlantaInicial.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa() >
                       PlantaFinal.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa())
                {                                       //"Quantidade de entrenos relativos (do eixo principal) do galho "  //"\ndo suporte "                                 //" no estágio inicial é maior que no estágio final.\nArrume o arquivo MTG e processe novamente."
                    JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO66") + (k+1) + InterpolMateView.colecaomsgs.getString("ERRO67") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO62"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 

                    System.out.println("PlantaInicial.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa() : " + PlantaInicial.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa());
                    System.out.println("PlantaInicial.getSuporte(i).getGalho(k).getQtdeEntrenosAbsoluta() : " + PlantaInicial.getSuporte(i).getGalho(k).getQtdeEntrenosAbsoluta());   
                    System.out.println("PlantaFinal.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa() : " + PlantaFinal.getSuporte(i).getGalho(k).getQtdeEntrenosRelativa());
                    System.out.println("PlantaFinal.getSuporte(i).getGalho(k).getQtdeEntrenosAbsoluta() :" + PlantaFinal.getSuporte(i).getGalho(k).getQtdeEntrenosAbsoluta());                             

                    return false;                 
                }  
                
                // Verifica se os eixos deste galho possuem valores OK (OBS: funcao recursiva. Ira processar para todas as ordens de ramificacao).
                if (!VerificaEixos(PlantaInicial.getSuporte(i).getGalho(k).recebeListaEntreno(), 
                                    PlantaFinal.getSuporte(i).getGalho(k).recebeListaEntreno(), i+1, k+1, 0))
                    return false; //caso encontrou algum erro retorna falso.
            }
        }
        return true;
    }
    

    
     /* metodo para verificar se um eixo (de qualquer ordem de ramificacao) possui algum valor na topologia 
     * maior (que nao deveria ser) que em seu estagio final.
     * 
     *  Recebe por parametro: 
     *              - Eixo1:            um determinado eixo no estágio inicial da planta.
     *              - Eixo2:            o eixo (o mesmo do de cima) no estágio final da planta.
     *              - indice_galho:     o indice do galho onde esta contido o eixo passado por parametro
     *              - indice_suporte:   o indice do suporte onde esta contido o galho passado por parametro (valor acima) 
     *              - ordem_de_ram:     a ordem de ramificacao do eixo passado por parametro (0 = eixo principal, 1 = ramificacao de primeira ordem, etc...)
     * 
     *  Retorna: true se deu tudo certo, ou falso se encontrou algum erro.
     *                                                                           */
    public boolean VerificaEixos(ArrayList<EntrenoGalho> Eixo1, ArrayList<EntrenoGalho> Eixo2, int indice_suporte, int indice_galho, int ordem_de_ram)
    {
        
        //para cada entreno deste eixo:
        for (int i=0; i<Eixo1.size(); i++)
        {   
            //se o comprimento do entreno no estagio inicial eh maior que no estagio final:
            if (  Double.compare(  Eixo1.get(i).getComp()  ,   Eixo2.get(i).getComp() ) > 0 ) 
            {   //exibe erro:
                
                
                
                System.out.println("no Eixo1: " + Eixo1.get(i).getComp() + ", no Eixo2: " + Eixo2.get(i).getComp());
                                                    ////"O entrenó "                                           //" (de ordem "                                                  //") do galho "                                                    //" do suporte "                                                     " tem comprimento maior no estágio inicial que no estágio final.\nArrume o arquivo MTG e processe novamente. "
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO68") + ordem_de_ram + InterpolMateView.colecaomsgs.getString("ERRO69") + indice_galho + InterpolMateView.colecaomsgs.getString("ERRO63") + indice_suporte + InterpolMateView.colecaomsgs.getString("ERRO70"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                return false;
            }
            
            //se a unidade de crescimento do entreno no estagio inicial eh maior que a do estagio final:
            if (  Eixo1.get(i).getUN()  >  Eixo2.get(i).getUN() )
            {   //exibe erro:                       ////"O entrenó "                                            //" (de ordem "                                                 //") do galho "                                                    //" do suporte "                                                    //" tem unidade de crescimento maior no estágio inicial que no estágio final.\nArrume o arquivo MTG e processe novamente."
                System.out.println("no Eixo1: " + Eixo1.get(i).getUN() + ", no Eixo2: " + Eixo2.get(i).getUN());
                JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO68") + ordem_de_ram + InterpolMateView.colecaomsgs.getString("ERRO69") + indice_galho + InterpolMateView.colecaomsgs.getString("ERRO63") + indice_suporte + InterpolMateView.colecaomsgs.getString("ERRO71"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE);
                return false;
            }            
            
            //se este entreno, no estagio inicial, possui ramificacao:
            if (Eixo1.get(i).temRamificacao())
            {
                if (Eixo2.get(i).temRamificacao()) //entao precisara ter ramificacao tambem no estagio final: (verifica esta condicao primeiro)
                {
                    //verifica entao a quantidade de entrenos ramificacao (deste entreno):
                    if ( Eixo1.get(i).getQtdeEntrenosRam() > Eixo2.get(i).getQtdeEntrenosRam()) //se a qtde de entrenos for maior no estagio inicial que no estagio final:
                    {   //entao exibe erro:                 ////"O entrenó "                                            //" (de ordem "                                                 //") do galho "                                                    //" do suporte "                                                    //" tem uma ramificação no estágio inicial maior que no estágio final.\nArrume o arquivo MTG e processe novamente."
                        JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO68") + ordem_de_ram + InterpolMateView.colecaomsgs.getString("ERRO69") + indice_galho + InterpolMateView.colecaomsgs.getString("ERRO63") + indice_suporte + InterpolMateView.colecaomsgs.getString("ERRO72") , InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                        return false;                                
                    }
                    
                    
                    //se encontrou ramificacao e esta tudo ok, entao verificara os valores do eixo desta ramificacao (funcao recursiva). Passa por parametro os 2 eixos (no estagio inicial e final) da ramificacao, e o mesmo indice de suporte e galho. 
                    if (!VerificaEixos(Eixo1.get(i).recebeListaEntreno(), Eixo2.get(i).recebeListaEntreno(), indice_suporte, indice_galho, (ordem_de_ram+1)))
                        return false; //se encontrou algum erro entao retorna falso.
                    
                }
                else //se nao houver ramificacao no estagio final tambem, entao
                {    //exibe erro:                      //"O entrenó "                                             //" (de ordem "                                                  //") do galho "                                                    //" do suporte "                                                    //" tem ramificação no estágio inicial, mas não\nno estágio final. O entrenó deve ter a ramificação também no estágio final, ou então não deve ter\na ramificação no estágio inicial. Arrume o arquivo MTG e processe novamente."
                    JOptionPane.showMessageDialog(null, InterpolMateView.colecaomsgs.getString("ERRO58") + (i+1) + InterpolMateView.colecaomsgs.getString("ERRO68") + ordem_de_ram + InterpolMateView.colecaomsgs.getString("ERRO69") + indice_galho + InterpolMateView.colecaomsgs.getString("ERRO63") + indice_suporte + InterpolMateView.colecaomsgs.getString("ERRO73"), InterpolMateView.colecaomsgs.getString("Erro"), JOptionPane.WARNING_MESSAGE); 
                    return false;                
                }
            }
            
            
     
        }
        
        
        return true; //retorna OK se nao encontrou nenhum erro.
    }
    
    
 
}

