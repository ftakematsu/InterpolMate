package testpackage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class TesteEquacao extends JFrame{
  JPanel superior;

  public TesteEquacao(){
    super("Uso da classe JPanel");

    Container tela = getContentPane();

    BorderLayout layout = new BorderLayout();
    tela.setLayout(layout);

    Tratador trat = new Tratador();

    JRadioButton primeiro = new JRadioButton("Java");
    primeiro.setMnemonic(KeyEvent.VK_J);

    JRadioButton segundo = new JRadioButton("C++");
    segundo.setMnemonic(KeyEvent.VK_C);

    JRadioButton terceiro = new JRadioButton("Perl");
    terceiro.setMnemonic(KeyEvent.VK_P);

    JRadioButton quarto = new JRadioButton("Delphi");
    quarto.setMnemonic(KeyEvent.VK_D);

    /*
    ButtonGroup grupo = new ButtonGroup();
    grupo.add(primeiro);
    grupo.add(segundo);
    grupo.add(terceiro);
    grupo.add(quarto);*/

    primeiro.setSelected(true);

    JButton ok = new JButton("Responder");
    ok.addActionListener(trat);

    String titulo = "Qual sua linguagem favorita?";
    Border etched = BorderFactory.createEtchedBorder();
    Border border = BorderFactory.createTitledBorder(etched, titulo);

    superior = new JPanel();
    superior.setLayout(new FlowLayout(FlowLayout.LEFT));

    superior.setBorder(border);

    superior.add(primeiro);
    superior.add(segundo);
    superior.add(terceiro);
    superior.add(quarto);

    JPanel inferior = new JPanel();
    inferior.setLayout(new FlowLayout(FlowLayout.RIGHT));
    inferior.add(ok);

    tela.add(superior, BorderLayout.NORTH);
    tela.add(inferior, BorderLayout.SOUTH);

    pack();
    setVisible(true);
  }

  public static void main(String args[]){
    TesteEquacao app = new TesteEquacao();
    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private class Tratador implements ActionListener{
        public void actionPerformed(ActionEvent e){
      String escolha = "";

      for(int i = 0; i < superior.getComponentCount(); i++){
        Component comp = superior.getComponent(i);

        if(comp.getClass().getName().equals("javax.swing.JRadioButton"))
          if(((JRadioButton)(comp)).isSelected())
            escolha += ((JRadioButton)(comp)).getText() + "\n";
      }

      JOptionPane.showMessageDialog(null, escolha);
        }
  }
}

/*
package testpackage;

import java.lang.Math;
import interpolmate.*;

public class TesteEquacao {

    public static void main(String[] args)
    {
        OndasCrescimento ondas = new OndasCrescimento();
        int i;
        FuncaoMatematica funcao_aux = new FuncaoMatematica();
        FuncaoMatematica vet_funcao_aux[] = new FuncaoMatematica[9];

        funcao_aux.DefinirEquacao(-3.63E+03, 17.17, -3.00E-02, 3.38E+05, -1.15E+07,0,0);
        for (double x=90; x<=180; x=x+15)
        System.out.println(" y = " + funcao_aux.ValorY_Funcao2(x));



        /*
        funcao_aux.DefinirEquacao(1, 2, 3, 4, 5, 6, 7);

        for (i=0; i<9; i++)
        {
            vet_funcao_aux[i] = new FuncaoMatematica();
        }

        vet_funcao_aux[0].DefinirEquacao(1, 2, 3, 4, 5, 6, 7);
        vet_funcao_aux[1].DefineComoConstante();
        vet_funcao_aux[2].DefinirEquacao(1, 2, 3, 4, 5, 6, 7);
        vet_funcao_aux[3].DefineComoConstante();

        vet_funcao_aux[4].DefinirEquacao(10, 20, 30, 40, 50, 60, 70);
        vet_funcao_aux[5].DefineComoConstante();
        vet_funcao_aux[6].DefinirEquacao(10, 20, 30, 40, 50, 60, 70);
        vet_funcao_aux[7].DefineComoConstante();
        vet_funcao_aux[8].DefinirEquacao(100, 200, 300, 400, 500, 600, 700);

        ondas.SetConjuntoFuncao(vet_funcao_aux);

        
        for (i=0; i<9; i++)
        {
            if (ondas.RetornarOnda(i).Constante())
            {
                ondas.RetornarOnda(i).ImprimeFuncao();
                System.out.println(i + " => (FUNCAO CONSTANTE) \n\n");
            }
            else
            {
                ondas.RetornarOnda(i).ImprimeFuncao();
                System.out.println(i + " => (nao e constante) \n\n");
            }
        }
        
        
        
        FuncaoMatematica fx = new FuncaoMatematica();
        double numero = 10.25;
        final int MIN = 100;
        final int MAX = 150;
    
        System.out.println(" LN(X) = " + Math.log1p(numero));
        System.out.println(" Exp = " + Math.pow(numero, 6));

        fx.DefinirEquacao(-10.5789, -24.902424, -45.178303, -42.801455, 39.041246, -8.784309, 0.61875499);

        //fx.DefinirEquacao(-8862.1128,5318.532,-1060.7253,70.325147,0,0,0); //OK

        for (double i=MIN; i<MAX; i++)
        {
            System.out.println(" (x = " + i + "): " + fx.ValorY(i));
        }
    }

}
 */


