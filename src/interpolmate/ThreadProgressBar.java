/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author murilo
 */
public class ThreadProgressBar extends Thread {

    
    private int valor_da_barra;
    
    JDialog jDialogBarraDeProgresso;
    JProgressBar jProgressBar1;
    JLabel jLabelProcessando;
    
    public ThreadProgressBar()
    {

/*
        jDialogBarraDeProgresso = new JDialog();
        jProgressBar1 = new JProgressBar();
        jLabelProcessando = new JLabel();
        
        
        
        jDialogBarraDeProgresso.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialogBarraDeProgresso.setTitle("Processando..."); 
        jDialogBarraDeProgresso.setMinimumSize(new java.awt.Dimension(200, 100));
        jDialogBarraDeProgresso.setResizable(false);

        
        jProgressBar1.setName("jProgressBar1"); 

        
        jLabelProcessando.setText("Procesando...");
 

        javax.swing.GroupLayout jDialogBarraDeProgressoLayout = new javax.swing.GroupLayout(jDialogBarraDeProgresso.getContentPane());
        jDialogBarraDeProgresso.getContentPane().setLayout(jDialogBarraDeProgressoLayout);
        jDialogBarraDeProgressoLayout.setHorizontalGroup(
            jDialogBarraDeProgressoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogBarraDeProgressoLayout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(jDialogBarraDeProgressoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelProcessando))
                .addGap(21, 21, 21))
        );
        jDialogBarraDeProgressoLayout.setVerticalGroup(
            jDialogBarraDeProgressoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogBarraDeProgressoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelProcessando)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        
        jDialogBarraDeProgresso.setLocation(300, 300);
        jDialogBarraDeProgresso.setVisible(true);
        
        */
  
    }
    

    
    @Override
    public void run ()
    {
        while (true)
        {
            //System.out.println("Classe ThreadProgressBar valor : " + valor_da_barra);

            try 
            {
                sleep(100); //dorme 100 milissegundos
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(ThreadProgressBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setValor(int valor)
    {
        valor_da_barra = valor;
    }
    
    public int getValor()
    {
        return valor_da_barra;
    }
    
    
    
    
    
}
