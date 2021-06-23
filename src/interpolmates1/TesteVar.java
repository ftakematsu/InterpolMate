/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;

import interpolmate.*;
import java.io.File;
import java.io.IOException;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 *
 * @author Fabio
 */
public class TesteVar {
    MTGbase mtg_base = new MTGbase(true);
    
    WritableSheet sheet_fonte;
    
    public static void main(String[] args) throws IOException, WriteException  {
        Planta nova_planta = new Planta();
        Suporte suporte = new Suporte();
        Galho galho_atual = new Galho(45);
        File file = new File("TESTE.xls");
        
        
        System.out.println(" Teste de variabilidade");
        // Estrutura inicial 1 FUS_FE
        nova_planta.setAlfaTronco("70");
        nova_planta.setAlometriaFolha(0.5);
        nova_planta.setCompTronco("91.3");
        nova_planta.setAmbiente("SOMBRA");
        nova_planta.setSexo("F");
        nova_planta.setFolhasCaidasNesteEstagio(0);
        nova_planta.setFolhasSurgidasNesteEstagio(0);
        
        
        nova_planta.adicionarEntreno(1);
        nova_planta.adicionarEntreno(1);
        nova_planta.adicionarEntreno(1);
        nova_planta.adicionarEntreno(1);
        
        
        
        suporte = new Suporte();
        suporte.adicionarEntreno(39.7); // Seta o ComSRam
        galho_atual.adicionarEntreno(0, 0.001, 1, false);
        suporte.adicionarGalho(galho_atual);
        suporte.setCodPai(1);
        suporte.setAnguloAlfa(108);
        
        
        // Seta os dados do galho atual
        //galho_atual.adicionarEntreno(1, 5, 1, true, 10, 20);
        //galho_atual.adicionarEntreno(1, 5, 1, true, 10, 20);
        
        
        /*
        suporte.adicionarEntreno(5);
        suporte.adicionarGalho(galho_atual); // Seta o ComSRam
        suporte.setCodPai(1); // Indica em qual entreno do nova_planta.adicionarEntreno() vai ser adicionado o suporte
        suporte.setAnguloAlfa(40); // 
        nova_planta.adicionarSuporte(suporte); // Aqui adiciona o suporte na planta
        
        suporte = new Suporte();
        suporte.adicionarEntreno(7.3); // Seta o ComSRam
        suporte.adicionarGalho(galho_atual);
        suporte.adicionarEntreno(5);
        suporte.setCodPai(1);
        suporte.setAnguloAlfa(40);
         */
        
        nova_planta.adicionarSuporte(suporte);
        
        EscritaMTG mtg = new EscritaMTG(file, nova_planta);
    }
}
