/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 *
 * @author murilo
 */
public class Grafico extends Frame{

    String tipo_grafico=""; //tipo representando o eixo y do grafico (se serviva para Area Foliar, Comprimento de galhos, etc...)
    int qtde_dias; //periodo em dias (eixo x do grafico)
    double intensidade_min; //valor minino do eixo y do grafico
    double intensidade_max; //valor maximo do eixo y do grafico
    String unidade_de_y; //unidade de medida do eixo y do grafico
                  
    //especifica qual a lingua (referente a internacionalizacao) o grafico possui (0=PORTUGUES, 1=INGLES, 2=FRANCES)
    private int lingua; 
 
    private ArrayList<Point> ListaPontosFixos = new ArrayList<Point>();
    private ArrayList<Point> ListaTodosPontos = new ArrayList<Point>();        
    
    //Constantes representando as posicoes da imagem sendo criado (o background de fundo branco):
    private final int XINICIALBACKGROUND = 15;
    private final int YINICIALBACKGROUND = 15; 
    private final int XFINALBACKGROUND = 450;
    private final int YFINALBACKGROUND = 295; 
   
    //Constantes representando as posicoes e tamanhos da imagem do grafico:
    private final int XINICIALGRAFICO = XINICIALBACKGROUND+66; //limite esquerdo na coordenada x do grafico 
    private final int YINICIALGRAFICO = YINICIALBACKGROUND+36; //limite direito na coordenada x do grafico 
    private final int COMPRIMENTOGRAFICO = 320; //comprimento em pixels do grafico
    private final int ALTURAGRAFICO = 220; //altura em pixels do grafico //limite superior na coordenada y do grafico 
    private final int XFINALGRAFICO = XINICIALGRAFICO+COMPRIMENTOGRAFICO-1; //altura em pixels do grafico
    private final int YFINALGRAFICO = YINICIALGRAFICO+ALTURAGRAFICO-1; //comprimento em pixels do grafico
    
    //Constroi a classe, tendo como entrada o tipo de gráfico
    public Grafico(String tipo)
    {
         Point p_inicial = new Point(XINICIALGRAFICO,YINICIALGRAFICO+ALTURAGRAFICO-1); //ponto inicial do grafico
         Point p_final = new Point(XINICIALGRAFICO+COMPRIMENTOGRAFICO, YINICIALGRAFICO+1); //ponto final do grafico
               
         ListaPontosFixos.add(p_inicial); //adiciona o ponto inicial p/ a lista de pontos fixos do grafico
         ListaPontosFixos.add(p_final); //adiciona o ponto final p/ a lista de pontos fixos do grafico  
         
         tipo_grafico = tipo; //tipo do grafico indicando para qual parametro ele servira (Area Foliar, Comprimento de galhos, etc...)

         //dependendo do tipo do grafico passado por parametro, seta a unidade do eixo y:
         if      (tipo_grafico.equals("AREAFOLIAR"))    unidade_de_y = "cm²";
         else if (tipo_grafico.equals("COMPGALHOS"))    unidade_de_y = "cm";   
         else if (tipo_grafico.equals("QTDEFOLHAS"))    unidade_de_y = "folhas";
         else if (tipo_grafico.equals("EMISSAOFOLHAS")) unidade_de_y = "folhas";   
         else if (tipo_grafico.equals("QUEDAFOLHAS"))   unidade_de_y = "folhas";
         else if (tipo_grafico.equals("NUMEROMETAMEROS")) unidade_de_y = "entrenos";
         else if (tipo_grafico.equals("TAMANHOMEDIOFOLHAS")) unidade_de_y = "cm²";
         else                                           unidade_de_y = " ";
         //limpa estas variaveis: 
         qtde_dias=0;
         intensidade_min=0;
         intensidade_max=0;
    }
    
    public void setIntensidadeMinima(double intens_min)
    {
        intensidade_min = intens_min;
    }
    
    public void setIntensidadeMaxima(double intens_max)
    {
        intensidade_max = intens_max;
    }    

    public void setQtdeDias(int dias)
    {
        qtde_dias = dias;
    }
    
    
    //Desenha a imagem do Grafico em um determinado componente
    public void desenhaGraficoEm (Component componente, int internacionalizacao, ArrayList<MTGaSerGerado> ListaMTGsASeremGerados, boolean mostrar_dias_a_gerar) throws InterruptedException
    {
          /* ATENCAO: Nao confundir a variavel "graphics" com o Grafico ("chart") sendo criado.
             A variavel "graphics" é uma isntancia da classe Graphics, que eh uma "imagem" de um componente. */
        
          lingua = internacionalizacao;
        
          Graphics graphics = componente.getGraphics(); //graphics recebe a "imagem" atual daquele componente
          
          //Constante representando a cor verde claro: 
          final Color VERDECLARO = new Color(230,240,230);

          //Desenha Background c/ fundo branco:
          graphics.setColor(Color.WHITE);
          graphics.fillRect(XINICIALBACKGROUND, YINICIALBACKGROUND, XFINALBACKGROUND, YFINALBACKGROUND); 
          
          //Desenha espaco do grafico c/ verde claro:
          graphics.setColor(VERDECLARO);
          graphics.fillRect(XINICIALGRAFICO, YINICIALGRAFICO,  COMPRIMENTOGRAFICO, ALTURAGRAFICO); 
   

          if (mostrar_dias_a_gerar==true) //se checkbox "Mostrar dias requisitados" esta ativava:
            if (!ListaMTGsASeremGerados.isEmpty()) //e se existe algo na lista de mtgs a serem gerados:
                tracaDiasRequisitados(componente, ListaMTGsASeremGerados); //traca os dias requisitados no grafico (uma linha vermelha e um label vermelho indicando o dia)
          
          if (qtde_dias>0) //se periodo de dias do grafico ja foi setado:
            tracaMarcadoresDias(componente); //traca os marcadores de dias no grafico
           
          if (intensidade_max>0) //se intensidades do eixo y ja foram setadas:
            tracaValoresIntensidade(componente, intensidade_min, intensidade_max); //traca os valores da intensidade no grafico
          
          int i=YFINALGRAFICO+1; //i recebe a posicao y abaixo do inicio (parte de baixo) do grafico
          graphics.setColor(Color.LIGHT_GRAY); //seta cor como cinza claro
          //Desenhara 10 linhas finas divididas entre a altura do grafico:
          while (i>YINICIALGRAFICO) //enquanto a altura nao alcancar o topo do grafico:
          {   //caminha a posicao y (i):
              i = i - (ALTURAGRAFICO/10); 
              //desenha uma linha:
              graphics.drawLine(XINICIALGRAFICO, i , XFINALGRAFICO, i);
          }    
          
          graphics.setColor(Color.BLACK);
                    
          //Desenha eixos (linhas) x e y c/ preto:
          graphics.setColor(Color.BLACK);
          graphics.fillRect(XINICIALGRAFICO, YINICIALGRAFICO,  2, ALTURAGRAFICO); //desenha eixo x do Grafico
          graphics.fillRect(XINICIALGRAFICO, YFINALGRAFICO+1,  COMPRIMENTOGRAFICO, 2); //desenha eixo y do Grafico
            
          /* ******* deixa a fonte negrito ******* */
          graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));


          //"dia"
          graphics.drawString(InterpolMateView.colecaomsgs.getString("un_dia"), 424, 285);

          AffineTransform a_t = new AffineTransform();
          a_t.setToRotation(3.14*1.5, 0, 0);
          graphics.setFont(graphics.getFont().deriveFont(a_t));

          
          String str_eixo_y="";
         if      (tipo_grafico.equals("AREAFOLIAR")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_area_foliar");
         else if (tipo_grafico.equals("QTDEFOLHAS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_qtde_folhas");
         else if (tipo_grafico.equals("EMISSAOFOLHAS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_qtde_folhas");
         else if (tipo_grafico.equals("QUEDAFOLHAS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_qtde_folhas");
         else if (tipo_grafico.equals("COMPGALHOS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_comp_galhos");
         else if (tipo_grafico.equals("NUMEROMETAMEROS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_numero_metameros");
         else if (tipo_grafico.equals("TAMANHOMEDIOFOLHAS")) str_eixo_y = InterpolMateView.colecaomsgs.getString("un_tamanho_folhas");

          if (unidade_de_y.compareTo("folhas")==0)   
            graphics.drawString(str_eixo_y, XINICIALBACKGROUND+13, YINICIALBACKGROUND+250); 
          else   
            graphics.drawString(str_eixo_y + " (" + unidade_de_y + ")", XINICIALBACKGROUND+13, YINICIALBACKGROUND+250); 

          tracaRetas(componente);
             
    }

    
     //Tenta adicionar mais um ponto ao conjunto de ponto fixos (atraves desses pontos que as retas do grafico sao traÃ§adas
    public void setPontoFixo (int x, int y)
    {
         //se o ponto clicado pelo mouse tiver coordenada x dentro do limite do grafico:
         if (x >= XINICIALGRAFICO && x <=XFINALGRAFICO) 
         {
             //se o ponto clicado pelo mouse tiver coordenada y acima do grafico:
             if (y < YINICIALGRAFICO) y = YINICIALGRAFICO; //reposiciona a posicao para o ponto mais alto possivel (dentro do grafico)
             //se o ponto clicado pelo mouse tiver coordenada y aabaixo do grafico:
             if (y > YFINALGRAFICO) y = YFINALGRAFICO; //reposiciona a posicao para o ponto mais baixo possivel (dentro do grafico)            
             
             Point p = new Point(x,y); //cria um ponto que recebe as coordenadas x e y.
             
             //Percorre a lista de pontos fixos com o intuito de inserir este ponto na posicao correta.
             //(o criterio para ordenar os pontos serÃ¡ a partir do eixo x. Qto maior x, maior o indice deste ponto na lista).
             //obs: i comeÃ§a em '1' pois nao deve substituir o primeiro ponto do vetor (eh constante). obs: o ultimo ponto tambem nao pode ser substituido, mas ele ja nao esta acessivel p/ clique no grafico. 
             for (int i=1; i<ListaPontosFixos.size(); i++) 
             {
          
                if (ListaPontosFixos.get(i).x > x) //se encontrar um ponto na lista que tem x maior que do ponto a ser inserido
                {
                    ListaPontosFixos.add(i, p); //entao adiciona o ponto 'p' antes dele.
                    break;
                } 
          
                if (ListaPontosFixos.get(i).x == x) //se encontrar um ponto na lista que tem o mesmo 'x'
                {
                    ListaPontosFixos.remove(i); //substitui este ponto antigo pelo ponto 'p'
                    ListaPontosFixos.add(i, p);
                    break;
                }   
          
             }
        }
  }   


    
    public void tracaRetas(Component componente)
    {
    
        Point p_esq;
        Point p_dir;
        Point primeiro_p;
        Point ultimo_p;
        
        Graphics graphics = componente.getGraphics(); //graphics recebe a "imagem" atual daquele componente
        graphics.setColor(Color.BLUE);
                   
        
        /*System.out.println("CONJUNTOS DE PONTOS:");
        for (int i=0; i<ListaPontosFixos.size(); i++) //percorre a lista de pontos fixos
        {
            System.out.println("x: " + ListaPontosFixos.get(i).x + ", y: " + ListaPontosFixos.get(i).y);
        }*/
        
        
        for (int i=0; i<ListaPontosFixos.size()-1; i++) //percorre a lista de pontos fixos
        {
            p_esq = ListaPontosFixos.get(i);
            p_dir = ListaPontosFixos.get(i+1);
             
            graphics.drawLine(p_esq.x, p_esq.y, p_dir.x, p_dir.y);
            //reforço (desenha mais 2 linhas: uma acima e outra embaixo): (para a linha ficar com espessura de 3 pixels)
            graphics.drawLine(p_esq.x, p_esq.y+1, p_dir.x, p_dir.y+1);
            graphics.drawLine(p_esq.x, p_esq.y-1, p_dir.x, p_dir.y-1);
        
        }

        //primeiro_p e ultimo_p sao os pontos extremos da lista
        primeiro_p = ListaPontosFixos.get(0); //primeiro ponto da lista
        ultimo_p = ListaPontosFixos.get(ListaPontosFixos.size()-1); //ultimo ponto da lista

        //desenha 2 circulos vermelhos para o primeiro e ultimo ponto:
        graphics.setColor(Color.RED);
        graphics.fillOval(primeiro_p.x-2, primeiro_p.y-2, 5, 5);
        graphics.fillOval(ultimo_p.x-2, ultimo_p.y-2, 5, 5);
    }
    
    
    /*Limpa todos os pontos fixos (removendo consecutivamente as retas traçadas) deste grafico, 
      deixando apenas os dois pontos fixos iniciais. (funcao do botao "NOVO GRAFICO") 
      Retorna 'true' se conseguiu remover os pontos, ou 'falso' se nao houve remocao alguma. */
     public boolean limpaPontosFixos()
    {
        boolean flag=false; 
         
        while (ListaPontosFixos.size() != 2) //enquanto nao houveram apenas 2 pontos no ArrayList
        {
            ListaPontosFixos.remove(1); //remove sempre o segundo da lista.
            flag=true;
        }
        
        return flag;
    }
    
     
    //devolve uma string com todos os pontos fixos deste grafico
    public String getStringPontosFixos()
    { 
        String Saida = "";
        
        for (int i=0; i<ListaPontosFixos.size(); i++) //percorre a lista de pontos fixos
        {
            //Concatena todos os pontos na string:
            Saida = Saida + ListaPontosFixos.get(i).x;
            Saida = Saida + ",";  //separa o eixo x e y de cada ponto com uma virgula
            Saida = Saida + ListaPontosFixos.get(i).y;         
            Saida = Saida + "\n"; //insere cada ponto em uma linha
        }
        return Saida; //devolve esta string
    }
    
    
    //seta uma string com todos os pontos fixos deste grafico
    public void setStringPontosFixos(String strEntrada)
    { 
        ListaPontosFixos.clear(); //primeiramente limpa a estrutura p/ evitar que erros ocorram
        //divide a string de entradas em um vetor de strings (separadas por "\n"). Cada string do vetor deve conter: um numero, virgula e um numero
        String[] strPontos = strEntrada.split("\n"); 
        
       for (int i=0; i<strPontos.length; i++) //percorre este vetor de strings 
       {
       
            String[] strCoordenadas = strPontos[i].split(","); //divide cada string em duas strings (separadas agora pela "," obtendo dois numeros.
        
            
            Point ponto = new Point(Integer.parseInt(strCoordenadas[0]) , Integer.parseInt(strCoordenadas[1])); //cria o ponto (x,y) com as duas strings obtidas
     
            ListaPontosFixos.add(ponto); //adiciona este ponto na lista de pontos fixos do grafico.
        
       }
    }
    
    
    public void tracaMarcadoresDias(Component componente)
    {
        
          /* Numero de marcadores de dias que haverao no grafico. 
             O numero de marcadores ajuda o usuario a visualizar 
             melhor as divisoes dos dia ao longo da coordenada x  */
          int n_marcadores; 
          
          
          if (qtde_dias >= 10) //se existem mais de 10 dias no periodo de interpolacao  
              n_marcadores=10; //numero de marcadores eh 10
          else                  //caso existam menos de 10 dias
              n_marcadores = qtde_dias; //numero de marcadores eh o mesmo numero de dias (pois nao faria sentido colocar mais marcadores que o numero de dias)
        
          
          Graphics graphics = componente.getGraphics(); //graphics recebe a "imagem" atual daquele componente

          //arruma a fonte para imprimir na vertical:
          AffineTransform a_t = new AffineTransform();
          a_t.setToRotation(3.14*1.5, 0, 0);
          graphics.setFont(graphics.getFont().deriveFont(a_t));
          
          //diminui o tamanho da fonte:
          graphics.setFont(graphics.getFont().deriveFont((float)9)); 
          
          
          //criara 11 marcadores de dias no grafico:
          for (int i=0; i<=n_marcadores; i++)
          {
              //posicao indica o deslocamento do marcador no grafico na coordenada x (obs: deve-se converter os valores no calculo p/ float p/ obter um resultado mais preciso)
             int posicao = Math.round((float)i*((float)COMPRIMENTOGRAFICO/(float)n_marcadores));

             //label_numero eh o numero a ser impresso em determinada posicao (associado ao marcador) (obs: deve-se converter os valores no calculo p/ float p/ obter um resultado mais preciso)
             String label_dia = ""+ Math.round((float)i*((float)qtde_dias/(float)n_marcadores));             
               
             //desenha a linha (pequeno traco)
             graphics.drawLine(XINICIALGRAFICO+posicao, YFINALGRAFICO+1, XINICIALGRAFICO+posicao, YFINALGRAFICO+6);
          
             //ajuste_pixels eh um pequeno ajuste para que o texto "label_numero" fique "alinhado acima" com o grafico
             int ajuste_pixels = label_dia.length() * 6;
           
             //desenha o label_numero:
             graphics.drawString(label_dia, XINICIALGRAFICO+posicao+4, YFINALGRAFICO+12+ajuste_pixels);
          }

          
    }
         
    
    
    public void tracaValoresIntensidade(Component componente, double intens_min, double intens_max)
    {
        
          Graphics graphics = componente.getGraphics(); //graphics recebe a "imagem" atual daquele componente

          double variacao_intensidade = intens_max - intens_min;
          double temp;
                  
          //diminui o tamanho da fonte:
          graphics.setFont(graphics.getFont().deriveFont((float)9));
        

          //desenha as intensidades:
          for (int i=0; i<=10; i++)
          {
              temp = intens_min+(variacao_intensidade/10)*i; //temp recebe o valor calculado

              double valor = (((double)Math.round(temp*100))/(double)100);
              
              String valor_em_string;
              
              if (tipo_grafico.equals("QTDEFOLHAS") || tipo_grafico.equals("EMISSAOFOLHAS") || tipo_grafico.equals("QUEDAFOLHAS"))
                  valor_em_string = ""+Math.round(valor);
              else
                  valor_em_string = ""+valor;
              
              graphics.drawString(valor_em_string, XINICIALGRAFICO-48, YFINALGRAFICO+4-(22*i));
          }
          
    }
    

    public void tracaDiasRequisitados(Component componente, ArrayList<MTGaSerGerado> ListaMTGs)
    {
        
        
          Graphics graphics = componente.getGraphics(); //graphics recebe a "imagem" atual daquele componente     
        
          
          graphics.setColor(Color.GREEN); //seta a cor padrao de graphics como vermelho
          
          graphics.setColor(new Color(6, 119, 47));
          
          //diminui o tamanho da fonte:
          graphics.setFont(graphics.getFont().deriveFont((float)9)); 

          
          //arruma a fonte para imprimir na vertical:
          AffineTransform a_t = new AffineTransform();
          a_t.setToRotation(3.14*1.5, 0, 0);
          graphics.setFont(graphics.getFont().deriveFont(a_t));
          
          
          //percorre a lista de MTGs a serem gerados 
          for (int i=0; i<ListaMTGs.size(); i++)   
          {
              int dia = ListaMTGs.get(i).getDia(); //"dia" recebe o dia do MTGaSerGerado atual sendo percorrido

              //posicao indica o deslocamento do marcador no grafico na coordenada x (obs: deve-se converter os valores no calculo p/ float p/ obter um resultado mais preciso)              
              int posicao = Math.round((float)(COMPRIMENTOGRAFICO*dia)/(float)qtde_dias);
                     

              //desenha a linha (pequeno traco)
              graphics.drawLine(XINICIALGRAFICO+posicao, YINICIALGRAFICO, XINICIALGRAFICO+posicao, YFINALGRAFICO);
          
              //ajuste_pixels eh um pequeno ajuste para que o label do dia fique "alinhado acima" com o grafico
              int ajuste_pixels = (""+dia).length() * 6;
           
              //desenha o label daquele dia:
              graphics.drawString(""+dia, XINICIALGRAFICO+posicao+4, YINICIALGRAFICO-2);
          }
        
        
        
    }
            
            
     

    //devolve uma string com a unidade de medida da intensidade, de acordo com o tipo de grafico. Retorna "" se nenhum tipo foi reconhecido.
    public String getUnidadeIntensidade()
    {
        return unidade_de_y;
    }
    
    
    //metodo para verificar se tal ponto (x,y) de um componente esta no grafico:
    public boolean temPonto(int x_componente, int y_componente)
    {   
        //se x e y passados por parametro estao na regiao do grafico:
        if (x_componente > XINICIALGRAFICO && x_componente < XFINALGRAFICO &&
            y_componente > YINICIALGRAFICO && y_componente < YFINALGRAFICO)
             return true; //retorna verdadeiro
        
        else  //caso contrario
             return false; //retorna falso
    }
    
    
    public int descobrirDiaEmX(int x_componente)
    {

        int x_no_grafico = x_componente - XINICIALGRAFICO;

        //regra de 3 p/ descobrir o dia de tal posicao no grafico:
        int dia_desta_posicao = Math.round((float)((x_no_grafico*qtde_dias)/(float)COMPRIMENTOGRAFICO));  

        return dia_desta_posicao;
    }
    
    
    
    public double descobrirValorEmY(int y_componente)
    {

        int y_no_grafico = y_componente - YINICIALGRAFICO;
       
        double variacao_intensidade = intensidade_max - intensidade_min;
         
            
        //regra de 3 p/ descobrir o dia de tal posicao no grafico:
        double valor_desta_posicao = Math.round((double)((y_no_grafico*variacao_intensidade)/(double)ALTURAGRAFICO));       
         
         
        valor_desta_posicao = intensidade_max - valor_desta_posicao;

        //arredonda para duas casas decimais:
        long lon = Math.round(valor_desta_posicao*100); 
        valor_desta_posicao = ((double)lon)/(double)100;
        
        return valor_desta_posicao;
    }
                
    
    public void CriarListaTodosOsPontos()
    {
        ListaTodosPontos.clear();
        
        int x_inicial = ListaPontosFixos.get(0).x;
        int x_final = ListaPontosFixos.get(ListaPontosFixos.size()-1).x;    
        
        int x_atual;
        int y_atual;
        float ang_inclinacao;
        
        for (int i=1; i<ListaPontosFixos.size(); i++)
        {
       
            int x_i = ListaPontosFixos.get(i-1).x;
            int y_i = ListaPontosFixos.get(i-1).y;
            int x_f = ListaPontosFixos.get(i).x;
            int y_f = ListaPontosFixos.get(i).y;
            int dist_x = x_f - x_i;
            int dist_y = y_f - y_i;

            x_atual = x_i; 
            while (x_atual < x_f)
            {
                ang_inclinacao = ((float)dist_y)/((float)dist_x);

                y_atual = y_i + Math.round(((float)(x_atual - x_i)) * ang_inclinacao);
                
                ListaTodosPontos.add(new Point(x_atual, y_atual));
                
                x_atual++;
            }
            
        } 
        ListaTodosPontos.add(ListaPontosFixos.get(ListaPontosFixos.size()-1)); //adiciona tambem o ultimo ponto fixo
        
        /*
        System.out.println("LISTA DE TODOS OS PONTOS");
        for (int k=0; k < ListaTodosPontos.size(); k++)
            System.out.println("Ponto " + k + ": (" + ListaTodosPontos.get(k).x + ", " + ListaTodosPontos.get(k).y + ")");
        */

    }

    
    public int descobrirXEmDia(int dia)
    {
         //regra de 3 p/ descobrir o x no grafico para tal dia:       
         int x_no_grafico = Math.round((float)(dia*COMPRIMENTOGRAFICO)/(float)qtde_dias);

         int x_componente = x_no_grafico + XINICIALGRAFICO;

        
        return x_componente;
    }
    
    public double descobrirValorParaDia(int dia)
    {
         //regra de 3 p/ descobrir o x no grafico para tal dia:       
         int x_no_grafico = Math.round((float)(dia*COMPRIMENTOGRAFICO)/(float)qtde_dias);

         int x_componente = x_no_grafico + XINICIALGRAFICO;

        
         for (int i=0; i<ListaTodosPontos.size(); i++)
         {
             if (ListaTodosPontos.get(i).x == x_componente)
             {
                 int y_componente = ListaTodosPontos.get(i).y;
             
                 return descobrirValorEmY(y_componente);
             }
         }
          
        return 0;
    } 
    
    public String getTipoGrafico()
    {
        return tipo_grafico;
    }

    public int getQtdeDias()
    {
        return qtde_dias;
    }
    
    public double getValorMinimo()
    {
        return intensidade_min;
    }
    public double getValorMaximo()
    {
        return intensidade_max;
    }    
    
}


