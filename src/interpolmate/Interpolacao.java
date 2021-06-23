/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

import interpolmates1.Database1;
import interpolmates1.NeuralNetwork;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jxl.write.WriteException;

import metodos_numericos.Splines;



public class Interpolacao {

    MTGbase MTGbaseInicial;
    MTGbase MTGbaseFinal;
    private final Planta p_inicial;
    Planta p_final;
    Planta p;
    DefaultTableModel ModeloTabelaMTGsAGerar;
    Periodo periodo;
    String str_diretorio; //diretorio onde serao salvo os arquivos gerados na interpolacao 
    long dias_percorridos; //variavel p/ representar a qtdade de dias ja percorridos na interpolacao

    ArrayList<AtributosGrafico> ListaAtribGraf;
    
    final private ArrayList<Planta> Plantas_escrita_mtg;
    
    // *************************************s********* 
    // constqtdmaxram
    
    //ordens:
    final int EIXOPRINCIPAL = 0;
    final int PRIMEIRAORD = 1;
    final int SEGUNDAORD = 2;
    final int TERCEIRAORD = 3;
    
    final boolean TEM_RAMIF = true;
    final boolean NAO_TEM_RAMIF = false;
    
    //constante para representar o tempo de um dia em "long"
    final long DIA = 86400000; 
    
    EntrenoGalho EN_est_atu_temp, EN_est_fin_temp;
    

    int numero_de_metameros_disponivel;
    double tamanho_medio_folhas_atual;
    int qtde_folhas_disponivel_a_surgir;
    int qtde_folhas_disponivel_a_cair;
    double area_foliar_disponivel;
    double comp_ep_disponivel;

    boolean cresc_caule;
    
    int folhas_inseridas_atual;

    double Y_TF[] = new double[38];

    
    int ambiente; // 1-SOL; 2-SOMBRA
    
    // Vetores armazenando os valores dayly-step
    // São esses vetores que vão conter as variáveis 
    double aen_out[];
    double acaule_day, acaule_out[];
    double afolhas_out[];
    double qfolhas_out[];
    double aaf_day, aaf_out[];
    double tam_folhas_day[];
    
    // Parâmetros discretos
    int aen_day, afolhas_day, qfolhas_day, qtd_ram_day1[];
    
    // Parâmetros meteorológicos
    double temp_min[], temp_max[], par_geral[], rfr_geral[];
    double tempminmin_geral[], tempmaxmax_geral[];
    double gdd_[];
    double rainfall_[];
    double nl_[];
    double uc_amb[];
    
    
    // Supostas variáveis de saídas das redes
    double aen_min[], aen_max[], afolhas_min[], afolhas_max[], acaule_min[], acaule_max[], qfolhas_min[], qfolhas_max[], aaf_min[], aaf_max[];
    double aen_target[], afolhas_target[], acaule_target[], qfolhas_target[], aaf_target[];
    double prob_ramif1[];
    
    int aen_day_min[], aen_day_max[];
    int afolhas_day_min[], afolhas_day_max[];
    int qfolhas_day_min[], qfolhas_day_max[];
    double acaule_day_min[];
    double acaule_day_max[];
    double aaf_day_min[];
    double aaf_day_max[];
    
    
    
    
    // Matrizes de elementos de curvas de crescimento para cada galho
    int matriz_aen[][];
    double matriz_acaule[][];
    int matriz_afolhas[][];
    int matriz_qfolhas[][];
    double matriz_aaf[][];
    int matriz_cresc_ramif1[][];
    
    boolean month_scale;
    
    // Probabilidades de ocorrerem crescimento de eixo principal nessas UCs
    private double PROB_UC2;
    private double PROB_UC4;
    private double PROB_RAM1[], PROB_RAM2[];
    
    
    private int QTD_EN_UC1[];
    private int QTD_EN_UC2[];
    private int QTD_EN_UC3[];
    private int QTD_EN_UC4[];
    
    private double PROBF_EP_UC1[][];
    private double PROBF_EP_UC2[][];
    private double PROBF_EP_UC3[][];
    private double PROBF_EP_UC4[][];
    private double PROBF_Ram1_UC1[][];
    private double PROBF_Ram1_UC2[][];
    
    
    private int NUM_GALHOS_LONGOS, NUM_GALHOS_MEDIOS, NUM_GALHOS_CURTOS;
    
    private int MAX_UC1[] = new int[100], MAX_UC2[] = new int[100], MAX_UC3[] = new int[100], MAX_UC4[] = new int[100];
    private int MAX_UC11, MAX_UC21, MAX_UC31, MAX_UC41;
    
    
    double Y_AF[] = new double[38];
    double Y_ANF[] = new double[38];
    double Y_AC[] = new double[38];
    double Y_QF[] = new double[38];
    double Y_NM[] = new double[38];

    
    
    
    
   // Definte o tipo de teste na rede neural
   // 0 - Sem stress
   // 1 - Temp baixa UC3
   // 2 - Temp baixa UC4
   // 3 - Seca UC3
   // 4 - Seca UC4
   // 5 - Luz inversa
   private int TIPO_TESTE = 0;
   
   private boolean PROB_ATIVADA = true;
    
   public Interpolacao(DefaultTableModel ModeloTabMTGsAGerar, MTGbase MTGbaseInic, MTGbase MTGbaseFin, Periodo period, String diretorio, ArrayList<AtributosGrafico> ListaAtribGraficos) throws CloneNotSupportedException, IOException, ClassNotFoundException
   {
        MTGbaseInicial = MTGbaseInic;
        MTGbaseFinal = MTGbaseFin;

        p_inicial = MTGbaseInic.getPlanta(); //resgata a planta em seu estagio inicial
        //p = (Planta)MTGbaseInic.getPlanta().clone(); //resgata a planta em seu estagio inicial
    
        p = p_inicial.deepCopy();
        
        p_final = MTGbaseFin.getPlanta(); //resgata a planta em seu estagio final
        Plantas_escrita_mtg = new ArrayList<Planta>();
        Plantas_escrita_mtg.add(p_inicial);
        
        ModeloTabelaMTGsAGerar = ModeloTabMTGsAGerar;
        periodo = period;
        dias_percorridos = 0;
        
        str_diretorio = diretorio;
        cresc_caule = false;
        folhas_inseridas_atual=0;
        
        //p_inicial.setDadosProdutividade();
        //p_final.setDadosProdutividade();
        
        month_scale = true;
    }

    
    
    public double[] DayScale(int qtd_mes, int dias, double diaX[], double p[], boolean cresc)
    {
        double soma = 0;
        double a[] = new double[qtd_mes]; // Vetor que guardará os valores acumulados
        double v[] = new double[dias];
        Splines s = new Splines();
        
        a[0] = p[0];
        
        if (cresc)
            for (int i=1; i<qtd_mes; i++)
            {
                soma = soma + p[i];
                a[i] = soma;
            }
        else a = p;
        
        s.CalculaSplineCubico(diaX, a); // Calcula splines para os valores mensais acumulados
        
        for (int d=1; d<dias; d++)
        {
            v[d] = s.AvaliaFuncaoSpline(diaX, d); // Aqui gerou-se o vetor resultante da transformação na escala de dias
        }
        return v;
    };
    
    public int[] DayScaleDiscreto(int qtd_mes, int dias, double diaX[], double p[], boolean cresc)
    {
        double soma = 0;
        double a[] = new double[qtd_mes]; // Vetor que guardará os valores acumulados
        int v[] = new int[dias];
        Splines s = new Splines();
        
        a[0] = p[0];
        
        if (cresc)
            for (int i=1; i<qtd_mes; i++)
            {
                soma = soma + p[i];
                a[i] = soma;
            }
        else a = p;
        
        s.CalculaSplineCubico(diaX, a); // Calcula splines para os valores mensais acumulados
        
        for (int d=1; d<dias; d++)
        {
            v[d] = (int)Math.ceil(s.AvaliaFuncaoSpline(diaX, d)); // Aqui gerou-se o vetor resultante da transformação na escala de dias
        }
        return v;
    };
    
    public double[] CalculaDiferenca(double v[], int dias)
    {
        // Calcula diferenças para ser utilizado na simulação daily-step
        double dv[] = new double[v.length];
        for (int d=1; d<dias; d++)
        {
            dv[d] = v[d]-v[d-1];
            if (dv[d]<0) dv[d] = 0;
        }
        return dv;
    };
    
    public int[] CalculaDiferencaDisc(int v[], int dias)
    {
        // Calcula diferenças para ser utilizado na simulação daily-step
        int dv[] = new int[v.length];
        for (int d=1; d<dias; d++)
        {
            dv[d] = (int)Math.round(v[d]-v[d-1]); // Transforma em inteiro
            if (dv[d]<0) dv[d] = 0;
        }
        return dv;
    };
    
    private void DefineValorSaidaEImprime(double saida_rede[][], int qtd_comb, double acaule_out[], double aen_out[], double afolhas_out[], 
                               double qfolhas_out[], double aaf_out[], double acaule_out_max[], double aen_out_max[], double afolhas_out_max[], 
                               double qfolhas_out_max[], double aaf_out_max[], double ram1_out[], boolean impr)
    {
        if (impr) System.out.print("\nA.Caule min");
        for (int j=0; j<qtd_comb; j++)
        {
            acaule_out[j] = saida_rede[j][0]*Max(acaule_min);
            if (impr) System.out.print(" ," + acaule_out[j]);
        }

        if (impr) System.out.print("\nN.Metameros min");
        for (int j=0; j<qtd_comb; j++)
        {
            aen_out[j] = saida_rede[j][1]*Max(aen_min);
            if (impr) System.out.print(" ," + aen_out[j]);
        }

        if (impr) System.out.print("\nN.Folhas min");
        for (int j=0; j<qtd_comb; j++)
        {
            afolhas_out[j] = saida_rede[j][2]*Max(afolhas_min);
            if (impr) System.out.print(" ," + afolhas_out[j]);
        }


        if (impr) System.out.print("\nQ.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            qfolhas_out[j] = saida_rede[j][3]*Max(qfolhas_min);
            if (impr) System.out.print(" ," + qfolhas_out[j]);
        }


        if (impr) System.out.print("\nA.Foliar min");
        for (int j=0; j<qtd_comb; j++)
        {
            aaf_out_max[j] = saida_rede[j][4]*Max(aaf_min);
            if (impr) System.out.print(" ," + aaf_out[j]);
        }
        if (impr) System.out.println("\n");
        
        
        //
        if (impr) System.out.print("\nA.Caule max");
        for (int j=0; j<qtd_comb; j++)
        {
            acaule_out_max[j] = saida_rede[j][5]*Max(acaule_max);
            if (impr) System.out.print(" ," + acaule_out_max[j]);
        }

        if (impr) System.out.print("\nN.Metameros _max");
        for (int j=0; j<qtd_comb; j++)
        {
            aen_out_max[j] = saida_rede[j][6]*Max(aen_max);
            if (impr) System.out.print(" ," + aen_out_max[j]);
        }

        if (impr) System.out.print("\nN.Folhas _max");
        for (int j=0; j<qtd_comb; j++)
        {
            afolhas_out_max[j] = saida_rede[j][7]*Max(afolhas_max);
            if (impr) System.out.print(" ," + afolhas_out_max[j]);
        }


        if (impr) System.out.print("\nQ.Folhas_max");
        for (int j=0; j<qtd_comb; j++)
        {
            qfolhas_out_max[j] = saida_rede[j][8]*Max(qfolhas_max);
            if (impr) System.out.print(" ," + qfolhas_out_max[j]);
        }


        if (impr) System.out.print("\nA.Foliar _max");
        for (int j=0; j<qtd_comb; j++)
        {
            aaf_out_max[j] = saida_rede[j][9]*Max(aaf_max);
            if (impr) System.out.print(" ," + aaf_out_max[j]);
        }
        
        if (impr) System.out.print("\nRam1");
        for (int j=0; j<qtd_comb; j++)
        {
            ram1_out[j] = saida_rede[j][10];
            if (impr) System.out.print(" ," + ram1_out[j]);
        }
        
        if (impr) System.out.println("\n");
        
    };
    
    public double GeraRandomico(double min, double max)
    {
        return Math.round(min + (Math.random()*(max-min)));
    };

    public double GeraRandomicoReal(double min, double max)
    {
        return min + (Math.random()*(max-min));
    };

    public void PrintVetor(String s, double v[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v.length; i++)
                System.out.print(", " + v[i]);
    };
    public void PrintVetor(String s, int v[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v.length; i++)
                System.out.print(", " + v[i]);
    };
    
    public void PrintVertical(String s, int v[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v.length; i++)
                System.out.println("Dia: " + (i+1) + " Valor: " + v[i]);
    };
    
    public void PrintVertical2(String s, double v1[], double v2[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v1.length; i++)
                System.out.println("Dia " + (i+1) + "\t" + v1[i] + "\t" + v2[i]);
    };
    public void PrintVertical2(String s, int v1[], int v2[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v1.length; i++)
                System.out.println("Dia " + (i+1) + "\t" + v1[i] + "\t" + v2[i]);
    };
    
    
    public void PrintVertical(String s, double v[])
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < v.length; i++)
                System.out.println("Dia: " + (i+1) + " Valor: " + v[i]);
    };
    
    public int[] GerarDadosAleatorios1(int qtd, int tam)
    {
        int vet[] = new int[tam];
        for (int i=0; i<tam; i++) vet[i] = 0;
        
        int ins = 0, pos =0;
        while (ins<qtd)
        {
            pos = (int)GeraRandomico(5, tam-3);
            if ( vet[pos] == 0)
            {
                vet[pos] = 1;
                ins++;
            }
        }
        
        return vet;
    };
    
    // Função para definir as probabilidades de emissão de metâmeros por unidade de crescimento
    // Aqui são definidos funções randômicas para definir os valores de um determinado parâmetro
    public void DefineValorParametrosDia(int x, int g)
    {
        aen_day = matriz_aen[g][x];
        acaule_day = matriz_acaule[g][x];
        afolhas_day = matriz_afolhas[g][x]+matriz_qfolhas[g][x];
        aaf_day = matriz_aaf[g][x];
        qfolhas_day = matriz_qfolhas[g][x];
    };
    
    
    private void ImprimeDadosPorMes(double v[], int dias)
    {
            System.out.print("\n" + v.length + " --> ");
            for (int j=0; j<dias; j++)
            {
                System.out.print(v[j]+" ");
                if (((double)j)%30==0)
                {
                    System.out.println();
                }
            }
    };
    
    private void ImprimeDadosPorMes(int v[], int dias)
    {
            System.out.print("\n" + v.length + " --> ");
            for (int j=0; j<dias; j++)
            {
                System.out.print(v[j]+" ");
                if (((double)j)%30==0)
                {
                    System.out.println();
                }
            }
    };
    
    private double RetornaProbabilidade(int x, int ordem_ram, int reg_uc, int en)
    {
        double vetor_aux[];
        
        if (RetornaMesUC(x, reg_uc)<0) return 1;
        
        else if (ordem_ram==0)
        {
            switch (reg_uc)
            {
                case 1:
                    vetor_aux = PROBF_EP_UC1[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
                    
                case 2:
                    vetor_aux = PROBF_EP_UC2[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
                    
                case 3:
                    vetor_aux = PROBF_EP_UC3[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
                    
                case 4:
                    vetor_aux = PROBF_EP_UC4[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
            }
        }
        
        else if (ordem_ram>=1)
        {
            switch (reg_uc)
            {
                case 1:
                    vetor_aux = PROBF_Ram1_UC1[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
                default:
                    if (reg_uc>2) reg_uc=2;
                    vetor_aux = PROBF_Ram1_UC2[RetornaMesUC(x, reg_uc)];
                    if (en>vetor_aux.length-3) return 1;
                    else return vetor_aux[en];
            }
        }
        
        return 1;
    };
    
    
    int propriedade_ramo_vetor[] = new int[100];
    
    
    private double DefineValorLimite(double valor, double min[], double max[], int m)
    {
        double min_acc = 0;
        double max_acc = 0;
        
        if (NumeroUC_Mes(m)==1)
        {
            min_acc = SomaAcumuladaVetor(min, ObsFinalPorUC(NumeroUC_Mes(m)));
            max_acc = SomaAcumuladaVetor(max, ObsFinalPorUC(NumeroUC_Mes(m)));
        } else {
            min_acc = SomaAcumuladaVetor(min, ObsFinalPorUC(NumeroUC_Mes(m))) - SomaAcumuladaVetor(min, ObsFinalPorUC(NumeroUC_Mes(m)-1));
            max_acc = SomaAcumuladaVetor(max, ObsFinalPorUC(NumeroUC_Mes(m))) - SomaAcumuladaVetor(max, ObsFinalPorUC(NumeroUC_Mes(m)-1));
        }
        
        if (valor>(min_acc+max_acc)/2)
        {
            return min[m]; // Retorna limite máximo
        }
        else return max[m]; // Senão, retorna o limite mínimo
    };
    
    
    public void GeraAtributosCrescimento(int qtd_galhos, int dias, int meses)
    {
        double aen_aux[] = new double[meses], acaule_aux[] = new double[meses], afolhas_aux[] = new double[meses], 
                qfolhas_aux[] = new double[meses], aaf_aux[] = new double[meses];
        
        double rand_num = 0; // Variável de probabilidade 
        
        matriz_aen = new int[qtd_galhos+1][];
        matriz_acaule = new double[qtd_galhos+1][];
        matriz_afolhas = new int[qtd_galhos+1][];
        matriz_qfolhas = new int[qtd_galhos+1][];
        matriz_aaf = new double[qtd_galhos+1][];
        matriz_cresc_ramif1 = new int[qtd_galhos+1][];
        QTD_EN_UC1 = new int[qtd_galhos+1];
        QTD_EN_UC2 = new int[qtd_galhos+1];
        QTD_EN_UC3 = new int[qtd_galhos+1];
        QTD_EN_UC4 = new int[qtd_galhos+1];
        
        int ramo_atual = 2; // 0 - curto, 1 - médio, 2 - longo
        int gl = NUM_GALHOS_LONGOS, gm = NUM_GALHOS_MEDIOS, gc = NUM_GALHOS_CURTOS;

        int g = 0; // ìndice de galhos gerais
        int qtde_metameros_uc1 = 0;
        double comp_caule = 0;
        
        for (int s=0; s<p_final.getQtdeSuportes(); s++)
        {
            for (int gs=0; gs<p_final.getSuporte(s).getQtdeGalhos(); gs++, g++) // Para cada galho, gerar uma quantidade de vetores e gerar as probabilidades para UC2 e UC4
            {
                for (int m=0; m<meses; m++)
                {
                    int uc_mes = NumeroUC_Mes(m);
                    aen_aux[m] = DefineValorLimite(QtdeEntrenosUCn(p_final.getSuporte(s).getGalho(gs).recebeListaEntreno(), uc_mes), aen_min, aen_max, m); 
                    acaule_aux[m] = DefineValorLimite(CompCauleUCn(p_final.getSuporte(s).getGalho(gs).recebeListaEntreno(), uc_mes), acaule_min, acaule_max, m);
                    afolhas_aux[m] = DefineValorLimite(NumFolhasUCn(p_final.getSuporte(s).getGalho(gs).recebeListaEntreno(), uc_mes), afolhas_min, afolhas_max, m);
                    qfolhas_aux[m] = qfolhas_max[m];
                    aaf_aux[m] = DefineValorLimite(AreaFoliarUCn(p_final.getSuporte(s).getGalho(gs).recebeListaEntreno(), uc_mes), aaf_min, aaf_max, m);
                    
                    
                    aen_aux[m] += 3;
//                    acaule_aux[m] += 3;
//                    //afolhas_aux[m] += 3;
//                    //qfolhas_aux[m] += 3; 
//                    aaf_aux[m] += 10;
                    
                    
//                        aen_aux[m] = (aen_max[m]); //+aen_min[m])/2; // Gera o randômico do aumento do número de entrenós
//                        acaule_aux[m] = (acaule_max[m]); //+acaule_min[m])/2;
//                        afolhas_aux[m] = (afolhas_max[m]); //+afolhas_min[m])/2;
//                        qfolhas_aux[m] = qfolhas_max[m];
//                        aaf_aux[m] = (aaf_max[m]); //+aaf_min[m])/2; 
                }
                
                
                // Se der as probabilidades de que uma determinada UC não vai crescer
                if (PROB_ATIVADA)
                {
                    if (Math.random()>PROB_UC2)
                    {
                       for (int m=9; m<15; m++)
                       {
                            aen_aux[m] = 0;
                            afolhas_aux[m] = 0;
                            qfolhas_aux[m] = aen_aux[m]-afolhas_aux[m];
                       }
                    }
                    if (Math.random()>PROB_UC4)
                    {
                       for (int m=21; m<25; m++)
                       {
                            aen_aux[m] = 0;
                            afolhas_aux[m] = 0;
                            qfolhas_aux[m] = aen_aux[m]-afolhas_aux[m];
                       }
                    }
                }
                

                matriz_aen[g] = CalculaDiferencaDisc(DayScaleDiscreto(meses, dias, Database.diaX25, aen_aux, true), dias);
                matriz_acaule[g] = CalculaDiferenca(DayScale(meses, dias, Database.diaX25, acaule_aux, true), dias);
                matriz_afolhas[g] = CalculaDiferencaDisc(DayScaleDiscreto(meses, dias, Database.diaX25, afolhas_aux, true), dias);
                matriz_qfolhas[g] = CalculaDiferencaDisc(DayScaleDiscreto(meses, dias, Database.diaX25, qfolhas_aux, true), dias);
                matriz_aaf[g] = CalculaDiferenca(DayScale(meses, dias, Database.diaX25, aaf_aux, true), dias);

                // Define a quantidade prévia de Entrenós por UC
                for (int j=0; j<240; j++) QTD_EN_UC1[g] += matriz_aen[g][j];
                for (int j=241; j<420; j++) QTD_EN_UC2[g] += matriz_aen[g][j];
                for (int j=421; j<615; j++) QTD_EN_UC3[g] += matriz_aen[g][j];
                for (int j=616; j<720; j++) QTD_EN_UC4[g] += matriz_aen[g][j];
            }
        }

        
        // Gera atributos ramificações
        for (g=0; g<qtd_galhos; g++) // Para cada galho, gerar uma quantidade de vetores e gerar as probabilidades para UC2 e UC4
        {
            int d = 0;
            matriz_cresc_ramif1[g] = new int[dias];
            for (int m=0; m<meses-1; m++) // Aqui os meses representam observações
            {
                rand_num = Math.random();
                
                if (rand_num<PROB_RAM1[m+1]) // Se houve crescimento de ramos no próximo
                {
                    //System.arraycopy(matriz_aen[g], d, matriz_ramif[g], d, d+30 - d);
                    for (int i=d; i<d+30; i++) {
                        matriz_cresc_ramif1[g][i] = matriz_aen[g][i];
                    }
                }
                else {
                    for (int i=d; i<d+30; i++) {
                        matriz_cresc_ramif1[g][i] = 0;
                    }
                }
                d +=30;
                    
            }
        }
    };
    
    
    
    private double SomaAcumuladaVetor(double v[], int m)
    {
        double soma = 0;
        for (int i=0; i<m; i++)
        {
            soma += v[i];
        }
        return soma;
    };
    
    
    private void QuedaFolhas(ArrayList<EntrenoGalho> galho_atual, ArrayList<EntrenoGalho> galho_final, int x, int ordem)
    {
        for (int en=0; en<galho_atual.size(); en++)
        {
            if (!galho_final.get(en).temFolha()) // Se no mesmo entrenó do estágio final não tiver folha
            {
                int num_folhas_galhos = 0;
                for (int i=0; i<galho_atual.size(); i++)
                    if (galho_atual.get(i).temFolha()) num_folhas_galhos++;
                
                // Definir probabilidade dela cair ou não
                if (Math.random()>RetornaProbabilidade(x, ordem, galho_atual.get(en).getUN(), en)
                        && ( num_folhas_galhos > (SomaAcumuladaVetor(afolhas_max, x/30)+SomaAcumuladaVetor(afolhas_min, x/30))/2))
                {
                    galho_atual.get(en).setTemFolha(false); // Derruba a folha
                    galho_atual.get(en).setAreaFolha(0);
                }
            }
        }
    };
    
    private double ProbOrdemN(int ordem, int x)
    {
        switch (ordem)
        {
            case 1: return PROB_RAM1[x/30];
            default: return PROB_RAM2[x/30];
        }
    };
            
    

    private ArrayList<EntrenoGalho> CresceRamificacao(ArrayList<EntrenoGalho> galho_atual, ArrayList<EntrenoGalho> galho_final, int x, int ordem)
    {
        double comp_en = 0.1;
        int ind_en = 0; // índice do entrenó a inserir
        int uc_en = NumeroUC_Atual(x);
        
        for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
        {
            if (galho_final.get(en).temRamificacao()) // Se no mesmo entrenó tiver uma ramificação no estágio final
            {
                ind_en = galho_atual.get(en).recebeListaEntreno().size(); // Tamanho da ramificação atual (numero de entrenós)

                if (Math.random()<ProbOrdemN(ordem, x) && 
                        ind_en<galho_final.get(en).recebeListaEntreno().size()
                        && galho_final.get(en).recebeListaEntreno().get(ind_en).getUN()<=uc_en && p.MassaBrutaTotal()/1000 <11)   
                {
                    if (!galho_atual.get(en).temRamificacao()) // Se no entrenó atual não tiver ramificação, brotar uma
                    {
                       galho_atual.get(en).setTemRamificacao(TEM_RAMIF);
                    }
                    
                    // Seta o entrenó a ser inserido na ramificação
                    comp_en = galho_final.get(en).recebeListaEntreno().get(ind_en).getComp();
                    EN_est_atu_temp = new EntrenoGalho(1, ordem, comp_en, galho_final.get(en).recebeListaEntreno().get(ind_en).getUN(), NAO_TEM_RAMIF);
                    
                    if (galho_final.get(en).recebeListaEntreno().get(ind_en).temFolha())
                    {
                        EN_est_atu_temp.setTemFolha(true);
                        EN_est_atu_temp.setAreaFolha(galho_final.get(en).recebeListaEntreno().get(ind_en).getAreaFolha());
                        EN_est_atu_temp.setAlfaFolha(galho_final.get(en).recebeListaEntreno().get(ind_en).getAlfaFolha());
                    }
                    else if (TIPO_TESTE==0)
                    {
                        EN_est_atu_temp.setTemFolha(true);
                        EN_est_atu_temp.setAreaFolha(Arredonda2Casas(tam_folhas_day[x]));
                        EN_est_atu_temp.setAlfaFolha(Arredonda2Casas(GeraRandomicoReal(20, 80)));
                    }
                    
                    galho_atual.get(en).recebeListaEntreno().add(EN_est_atu_temp);
                }
            }
        }
        
        // Faz Queda de folhas se for o caso
        for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
        {
            boolean caiu = false;
            int miss = 0;
            if (galho_atual.get(en).temRamificacao() && qfolhas_day>0) // && galho_atual.get(en).recebeListaEntreno().size()>3
            {
                for (int j=0; j<galho_atual.get(en).recebeListaEntreno().size() && !caiu; j++)
                {   
                    if (Math.random()<RetornaProbabilidade(x, ordem, galho_atual.get(en).getUN(), en)
                            && (!galho_final.get(en).recebeListaEntreno().get(j).temFolha())) // E se nao tiver folha no mesmo entrenó
                    {
                        galho_atual.get(en).recebeListaEntreno().get(j).setTemFolha(false); // Derruba a folha
                        galho_atual.get(en).recebeListaEntreno().get(j).setAreaFolha(0);
                        caiu = true;
                    }
                }
                
                if (!caiu) //  Se não achou folha a cair, deve-se derrubar uma folha do galho
                {
                        for (int j=0; j<galho_atual.get(en).recebeListaEntreno().size() && !caiu; j++)
                        {
                            if ((!galho_final.get(en).recebeListaEntreno().get(j).temFolha())) // E se nao tiver folha no mesmo entrenó
                            {
                                galho_atual.get(en).recebeListaEntreno().get(j).setTemFolha(false); // Derruba a folha
                                galho_atual.get(en).recebeListaEntreno().get(j).setAreaFolha(0);
                                caiu = true;
                            }
                        }           
                }
                
                if (!caiu && TIPO_TESTE>0 && TIPO_TESTE!=5)
                {
                    //System.out.println("NAO_CAiU ");
                    for (int j=0; j<galho_atual.get(en).recebeListaEntreno().size(); j++)
                    {
                        if (galho_atual.get(en).recebeListaEntreno().get(j).getAreaFolha()>0) 
                        {
                            //System.out.println(" ---- CAIU");
                            galho_atual.get(en).recebeListaEntreno().get(j).setTemFolha(false); // Derruba a folha
                            galho_atual.get(en).recebeListaEntreno().get(j).setAreaFolha(0);
                            j = galho_atual.get(en).recebeListaEntreno().size()+1;
                        }
                    } 
                }
            }    
        }
        
        // Chamada recursiva para ramificações de ordem n
        for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
        {
            CresceRamificacao(galho_atual.get(en).recebeListaEntreno(), galho_final.get(en).recebeListaEntreno(), x, ordem);
        }
        
        return galho_atual;
    };
    
    private ArrayList<EntrenoGalho> CresceRamificacao2(Galho galho, ArrayList<EntrenoGalho> galho_atual, ArrayList<EntrenoGalho> galho_final, int x, int ordem, int mes_inicio_inv)
    {
        int uc_en = NumeroUC_Atual(x);
        
        if (p.getQtdeRams()<p_final.getQtdeRams())
        {
            for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
            {
                if (en<galho_final.size())
                {
                    if (galho_final.get(en).temRamificacao()) // Se no mesmo entrenó tiver uma ramificação no estágio final
                    {
                        int ind_en = galho_atual.get(en).recebeListaEntreno().size(); // Tamanho da ramificação atual (numero de entrenós)

                        if (ind_en<galho_final.get(en).recebeListaEntreno().size())   
                        {
                            if (!galho_atual.get(en).temRamificacao()) // Se no entrenó atual não tiver ramificação, brotar uma
                            {
                               galho_atual.get(en).setTemRamificacao(TEM_RAMIF);
                            }
                            
                            if (Math.random()<ProbOrdemN(ordem, x)) {
                                // Seta o entrenó a ser inserido na ramificação
                                double comp_en = galho_final.get(en).recebeListaEntreno().get(ind_en).getComp();
                                EN_est_atu_temp = new EntrenoGalho(1, ordem, comp_en, galho_final.get(en).recebeListaEntreno().get(ind_en).getUN(), NAO_TEM_RAMIF);

                                if (galho_final.get(en).recebeListaEntreno().get(ind_en).temFolha())
                                {
                                    EN_est_atu_temp.setTemFolha(true);
                                    EN_est_atu_temp.setAreaFolha(galho_final.get(en).recebeListaEntreno().get(ind_en).getAreaFolha());
                                    EN_est_atu_temp.setAlfaFolha(galho_final.get(en).recebeListaEntreno().get(ind_en).getAlfaFolha());
                                }
                                else 
                                {
                                    EN_est_atu_temp.setTemFolha(true);
                                    EN_est_atu_temp.setAreaFolha(Arredonda2Casas(tam_folhas_day[x]));
                                    EN_est_atu_temp.setAlfaFolha(Arredonda2Casas(GeraRandomicoReal(20, 80)));
                                }

                                galho_atual.get(en).recebeListaEntreno().add(EN_est_atu_temp);
                            }
                        }
                        
                        else
                        {
                            for (int i=0; i<galho_atual.size(); i++) // Percorre os entrenós do galho
                            {
                                if (GetInicioUC(3, galho_atual)<galho_atual.size())
                                {
                                    int en1 = (int)GeraRandomicoReal(GetInicioUC(3, galho_atual), galho_atual.size());
                                    
                                    if (Math.random()<ProbOrdemN(ordem, x) && !galho_atual.get(en1).temRamificacao()) //  && galho.getQtdeRamEixoPrincipal()<25 // Se no entrenó atual não tiver ramificação, brotar uma   && 
                                    {

                                           galho_atual.get(en1).setTemRamificacao(TEM_RAMIF);
                                           EN_est_atu_temp = new EntrenoGalho(1, ordem, 0.5, 1, NAO_TEM_RAMIF);
                                           galho_atual.get(en1).recebeListaEntreno().add(EN_est_atu_temp);
                                    }
                                }
                                if (galho_atual.get(i).temRamificacao()) // Se no mesmo entrenó tiver uma ramificação no estágio final
                                {
                                    if (Math.random()<ProbOrdemN(ordem, x))   
                                    {
                                        double en_ins = 0.1;
                                        // Seta o entrenó a ser inserido na ramificação
                                        if (acaule_day>1)  en_ins = acaule_day;
                                        else  en_ins = 1;
                                        EN_est_atu_temp = new EntrenoGalho(1, ordem, Arredonda2Casas(en_ins), uc_en, NAO_TEM_RAMIF);
                                        EN_est_atu_temp.setTemFolha(true);
                                        EN_est_atu_temp.setAreaFolha(Arredonda2Casas(tam_folhas_day[x]));
                                        EN_est_atu_temp.setAlfaFolha(Arredonda2Casas(GeraRandomicoReal(20, 80)));
                                        if (galho_atual.get(i).recebeListaEntreno().size()>0)
                                            if (QtdeEntrenosUCn(galho_atual.get(i).recebeListaEntreno(),uc_en)<=RetornaQtdeMaximaEnUC(uc_en, 1))
                                                galho_atual.get(i).recebeListaEntreno().add(EN_est_atu_temp);
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }    
        }
        else
        {
            for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
            {
                if (GetInicioUC(3, galho_atual)<galho_atual.size())
                {
                    int en1 = (int)GeraRandomicoReal(GetInicioUC(3, galho_atual), galho_atual.size());
                    if (Math.random()<ProbOrdemN(ordem, x) && !galho_atual.get(en1).temRamificacao()
                            && galho.getQtdeRamEixoPrincipal()<15 ) //  && galho.getQtdeRamEixoPrincipal()<25 // Se no entrenó atual não tiver ramificação, brotar uma   && 
                    {
                           galho_atual.get(en1).setTemRamificacao(TEM_RAMIF);
                           EN_est_atu_temp = new EntrenoGalho(1, ordem, 0.1, 1, NAO_TEM_RAMIF);
                           galho_atual.get(en1).recebeListaEntreno().add(EN_est_atu_temp);
                    }
                }
                if (galho_atual.get(en).temRamificacao()) // Se no mesmo entrenó tiver uma ramificação no estágio final
                {
                    if (Math.random()<ProbOrdemN(ordem, x))   
                    {
                        double en_ins = 0.1;
                        // Seta o entrenó a ser inserido na ramificação
                        if (acaule_day>1)  en_ins = acaule_day;
                        else  en_ins = 1;
                        // Seta o entrenó a ser inserido na ramificação
                        EN_est_atu_temp = new EntrenoGalho(1, ordem, Arredonda2Casas(en_ins), uc_en, NAO_TEM_RAMIF);
                        EN_est_atu_temp.setTemFolha(true);
                        EN_est_atu_temp.setAreaFolha(Arredonda2Casas(tam_folhas_day[x]));
                        EN_est_atu_temp.setAlfaFolha(Arredonda2Casas(GeraRandomicoReal(20, 80)));
                        // if (galho_atual.get(en).recebeListaEntreno().size()>0)
                            if (QtdeEntrenosUCn(galho_atual.get(en).recebeListaEntreno(),uc_en)<=RetornaQtdeMaximaEnUC(uc_en, 1))
                                galho_atual.get(en).recebeListaEntreno().add(EN_est_atu_temp);
                    }
                }
            }
        }
        
        
        // Faz queda de folhas o que for necessário
        for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
        {
            boolean caiu = false;
            if (galho_atual.get(en).temRamificacao() && qfolhas_day>0) // && galho_atual.get(en).recebeListaEntreno().size()>3
            {
                for (int j=0; j<galho_atual.get(en).recebeListaEntreno().size() && !caiu; j++)
                {   
                    if (Math.random()<RetornaProbabilidade(x, ordem, galho_atual.get(en).recebeListaEntreno().get(j).getUN(), en)) // E se nao tiver folha no mesmo entrenó
                    {
                        galho_atual.get(en).recebeListaEntreno().get(j).setTemFolha(false); // Derruba a folha
                        galho_atual.get(en).recebeListaEntreno().get(j).setAreaFolha(0);
                        caiu = true;
                    }
                }
                
            }    
        }
        
        // Chamada recursiva para ramificações de ordem n
        for (int en=0; en<galho_atual.size(); en++) // Percorre os entrenós do galho
        {
            if (galho_atual.get(en).temRamificacao() && galho_final.get(en).temRamificacao())
            {
                if (galho_atual.get(en).recebeListaEntreno().size()<galho_final.get(en).recebeListaEntreno().size())
                    CresceRamificacao(galho_atual.get(en).recebeListaEntreno(), galho_final.get(en).recebeListaEntreno(), x, ordem);
            }
        }
        
        return galho_atual;
    };
    
    private int RetornaQtdeMaximaEnUCEP(int uc, int g)
    {
            switch (uc)
            {
                case 1: return MAX_UC1[g];
                case 2: return MAX_UC2[g];
                case 3: return MAX_UC3[g];
                case 4: return MAX_UC4[g];
            }
            return 40;
    };
    
    
    private double Max(double v[])
    {
        double maior = v[0];
        for (int i=1; i<v.length; i++)
            if (v[i]>maior) maior = v[i];
        return maior;
    };
    
    private double Min(double v[])
    {
        double menor = v[0];
        for (int i=1; i<v.length; i++)
            if (v[i]<menor) menor = v[i];
        return menor;
    };
    
    // Normalização pelo método MinMax Equalizado
    private double[] Normalizar(double v[], double max)
    {
        double n[] = new double[v.length];
        for (int i=0; i<v.length; i++)
        {
            n[i] = v[i]/max;
            // if (n[i]>=1) n[i] = 0.999;
        }
        return n;
    };
    
    
    public void ValoresSimulacaoRede(int teste, double tmin_teste[], double tmax_teste[], double nl_teste[], 
            double rainfall_teste[], double gdd_teste[], double par_teste[], double rfr_teste[])
    {
        if (p_inicial.getAmbiente().equals("SOL"))
        {   
            switch (teste)
            {
                case 1:
                    tmin_teste = Database.TESTE1_TMIN_MO;
                    tmax_teste = Database.TESTE1_TMAX_MO;
                    nl_teste = nl_;
                    rainfall_teste = rainfall_; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= Database.TESTE1_GDD;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;

                 case 2:
                    tmin_teste = temp_min;
                    tmax_teste = temp_max;
                    nl_teste = nl_;
                    rainfall_teste = Database.TESTE2_RAINFALL; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= gdd_;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;

                 case 3:
                    tmin_teste = temp_min;
                    tmax_teste = temp_max;
                    nl_teste = nl_;
                    rainfall_teste = rainfall_; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= gdd_;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;
            }
        }

        // Ambiente SOMBRA
        else if (p_inicial.getAmbiente().equals("SOMBRA"))
        {
            switch (teste)
            {
                case 1:
                    tmin_teste = temp_min;
                    tmax_teste = temp_max;
                    nl_teste = nl_;
                    rainfall_teste = rainfall_; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= gdd_;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;

                 case 2:
                    tmin_teste = temp_min;
                    tmax_teste = temp_max;
                    nl_teste = nl_;
                    rainfall_teste = rainfall_; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= gdd_;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;

                 case 3:
                    tmin_teste = temp_min;
                    tmax_teste = temp_max;
                    nl_teste = nl_;
                    rainfall_teste = rainfall_; //{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
                    gdd_teste= gdd_;
                    par_teste= par_geral;
                    rfr_teste= rfr_geral;
                    break;
            }
        }
    };
    
    
    
    // redeneuralartificial
    public void ExecutaRedeNeural(int tipo_teste)
    {
        final int max_it = 10000;
        int qtd_comb = Database.diaX25.length;
        int tamanho_entrada = 9;
        int tamanho_saida = 11;
        int tamanho_camada_oculta = 2*tamanho_entrada + 1; // 2n-1 HECHT - NIELSEN
        int qtd_camada_oculta =1;
        double alfa = 0.2; 
        double momentum = 0.5;
        double saida_rede[][] = new double[100][100];
        double dados_treinamento[][] = new double[100][100];
        
        // Parâmetros que se diferenciam pelo ambiente
        double tmin_[] = temp_min;
        double tmax_[] = temp_max;
        double PAR_[] = par_geral;
        double RFR_[] = rfr_geral;
        double uc[] = uc_amb;
        double constminmin_[] = tempminmin_geral;
        double constmaxmax_[] = tempmaxmax_geral;
        
        double saida_param[] = new double[qtd_comb];
        
        double tmin[] = Normalizar(tmin_, Max(tmin_));
        double tmax[] = Normalizar(tmax_, Max(tmax_));
        double nl[] = Normalizar(nl_, Max(nl_));
        double rainfall[] = Normalizar(rainfall_, Max(rainfall_));
        double gdd[] = Normalizar(gdd_, Max(gdd_));
        double par[] = Normalizar(PAR_, Max(PAR_));
        double rfr[] = Normalizar(RFR_, Max(RFR_));
        double tminmin[] = Normalizar(constminmin_, Max(constminmin_));
        double tmaxmax[] = Normalizar(constmaxmax_, Max(constmaxmax_));
        
        int t = 0;
        for (int j=0; j<qtd_comb; j++)
        {
            dados_treinamento[t][0] = tmin[j];
            dados_treinamento[t][1] = tmax[j];
            dados_treinamento[t][2] = rainfall[j];
            dados_treinamento[t][3] = gdd[j];
            dados_treinamento[t][4] = nl[j];
            dados_treinamento[t][5] = par[j];
            dados_treinamento[t][6] = rfr[j];
            dados_treinamento[t][7] = tminmin[j];
            dados_treinamento[t][8] = tmaxmax[j];
            t++;
        }
        
        /* INICIALIZAÇÃO DE REDES NEURAIS */
        double acaule_out_min[] = new double[qtd_comb];
        double aen_out_min[] = new double[qtd_comb];
        double afolhas_out_min[] = new double[qtd_comb];
        double qfolhas_out_min[] = new double[qtd_comb];
        double aaf_out_min[] = new double[qtd_comb];
        double acaule_out_max[] = new double[qtd_comb];
        double aen_out_max[] = new double[qtd_comb];
        double afolhas_out_max[] = new double[qtd_comb];
        double qfolhas_out_max[] = new double[qtd_comb];
        double aaf_out_max[] = new double[qtd_comb];
        double ram1_out[] = new double[qtd_comb]; 

        NeuralNetwork time_series = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, saida_param);
        time_series.SetMomentum(momentum);
        
        double acaule8n_min[] = Normalizar(acaule_min, Max(acaule_min));
        double aen8n_min[] = Normalizar(aen_min, Max(aen_min));
        double afolhas8n_min[] = Normalizar(afolhas_min, Max(afolhas_min));
        double qfolhas8n_min[] = Normalizar(qfolhas_min, Max(qfolhas_min));
        double aaf8n_min[] = Normalizar(aaf_min, Max(aaf_min));
        double acaule8n_max[] = Normalizar(acaule_max, Max(acaule_max));
        double aen8n_max[] = Normalizar(aen_max, Max(aen_max));
        double afolhas8n_max[] = Normalizar(afolhas_max, Max(afolhas_max));
        double qfolhas8n_max[] = Normalizar(qfolhas_max, Max(qfolhas_max));
        double aaf8n_max[] = Normalizar(aaf_max, Max(aaf_max));
        double ram1n[] = PROB_RAM1; // Não necessita normalizar
        
        if (tipo_teste>0)
        {

            double target[][] = new double[100][100];
            for (int i=0; i<qtd_comb; i++)
            {
                target[i][0] = acaule8n_min[i];
                target[i][1] = aen8n_min[i];
                target[i][2] = afolhas8n_min[i];
                target[i][3] = qfolhas8n_min[i];
                target[i][4] = aaf8n_min[i];

                target[i][5] = acaule8n_max[i];
                target[i][6] = aen8n_max[i];
                target[i][7] = afolhas8n_max[i];
                target[i][8] = qfolhas8n_max[i];
                target[i][9] = aaf8n_max[i];

                target[i][10] = ram1n[i];
            }

            // Treinamento da rede
            for (int i = 0; i < max_it; i++)
            {
                for (int j=0; j<time_series.QtdDadosTreinamento(); j++)
                {
                    double[] input = time_series.GetDadoTreinamento(j);
                    time_series.train(time_series.GetDadoTreinamento(j), target[j], tamanho_entrada);
                }
            }


            // Simulação com a rede treinada

            // Dados originais
            double tmin_teste[] = temp_min;
            double tmax_teste[] = temp_max;
            double nl_teste[] = nl_;
            double rainfall_teste[] = rainfall_;
            double gdd_teste[] = gdd_;
            double par_teste[] = par_geral;
            double rfr_teste[] = rfr_geral;
            double tminmin_teste[] = tempminmin_geral;
            double tmaxmax_teste[] = tempmaxmax_geral;
            
            
            // Define tipo de stress
           if (tipo_teste==1)
           {
               if (p_inicial.getAmbiente().equals("SOMBRA"))
               {
                   tmin_teste = Database.TMIN_U3_FUS;
                   tmax_teste = Database.TMAX_U3_FUS;
                   gdd_teste = Database.GDD_U3_FUS;
                   tminmin_teste = Database.TMINMIN_U3_FUS;
                   tmaxmax_teste = Database.TMAXMAX_U3_FUS;
               } 
               else {
                   tmin_teste = Database.TMIN_U3_MO;
                   tmax_teste = Database.TMAX_U3_MO;
                   gdd_teste = Database.GDD_U3_MO;
                   tminmin_teste = Database.TMINMIN_U3_MO;
                   tmaxmax_teste = Database.TMAXMAX_U3_MO;
               } 
           }
           else if (tipo_teste==2)
           {
               if (p_inicial.getAmbiente().equals("SOMBRA"))
               {
                   tmin_teste = Database.TMIN_U4_FUS;
                   tmax_teste = Database.TMAX_U4_FUS;
                   gdd_teste = Database.GDD_U4_FUS;
                   tminmin_teste = Database.TMINMIN_U4_FUS;
                   tmaxmax_teste = Database.TMAXMAX_U4_FUS;
               } 
               else {
                   tmin_teste = Database.TMIN_U4_MO;
                   tmax_teste = Database.TMAX_U4_MO;
                   gdd_teste = Database.GDD_U4_MO;
                   tminmin_teste = Database.TMINMIN_U4_MO;
                   tmaxmax_teste = Database.TMAXMAX_U4_MO;
               } 
           }
           else if (tipo_teste==3)
           {
               if (p_inicial.getAmbiente().equals("SOMBRA"))
               {
                    rainfall_teste = Database.SECA_U3;
               }
               else rainfall_teste = Database.SECA2_U3;
           }
           else if (tipo_teste==4)
           {
               if (p_inicial.getAmbiente().equals("SOMBRA"))
               {
                    rainfall_teste = Database.SECA_U4;
               }
               else rainfall_teste = Database.SECA2_U4;
           }
           else if (tipo_teste==5)
           {
               if (p_inicial.getAmbiente().equals("SOMBRA"))
               {
                   tmin_teste = Database.LUZ_TMIN_FUS;
                   tmax_teste = Database.LUZ_TMAX_FUS;
                   par_teste = Database.LUZ_PAR_FUS;
                   rfr_teste = Database.LUZ_RFR_FUS;
                   tminmin_teste = Database.LUZ_CONSTMININ_FUS;
                   tmaxmax_teste = Database.LUZ_CONSTMAXMAX_FUS;
               } 
               else {
                   tmin_teste = Database.LUZ_TMIN_MO;
                   tmax_teste = Database.LUZ_TMAX_MO;
                   par_teste = Database.LUZ_PAR_MO;
                   rfr_teste = Database.LUZ_RFR_MO;
                   tminmin_teste = Database.LUZ_CONSTMININ_MO;
                   tmaxmax_teste = Database.LUZ_CONSTMAXMAX_MO;
               }
               
               // Se não for nenhum outro, sem stress
           }
            
            
            
            // Pode-se alterar aqui!
            tmin_teste = Normalizar(tmin_teste,Max(temp_min));
            tmax_teste = Normalizar(tmax_teste,Max(temp_max));
            nl_teste= Normalizar(nl_teste,Max(nl_));
            rainfall_teste = Normalizar(rainfall_teste,Max(rainfall_));
            gdd_teste = Normalizar(gdd_teste,Max(gdd_));
            par_teste = Normalizar(par_teste,Max(par_geral));
            rfr_teste = Normalizar(rfr_teste,Max(rfr_geral));
            tminmin_teste = Normalizar(tminmin_teste,Max(tempminmin_geral));
            tmaxmax_teste = Normalizar(tmaxmax_teste,Max(tempmaxmax_geral));
            
            int ind_maior = 0;
            double maior = 0;
            if (TIPO_TESTE==5)
            {
                System.out.println(" INVERSAO DE LUZ DETECTADA");
                PrintVetor("PAR_TESTE_NORMALIZADO: ", par_teste);
                                
                for (int i=0; i<25; i++)
                {
                    if (par_teste[i]>maior && par_teste[i]<1)
                    {
                        maior = par_teste[i];
                        ind_maior = i;
                    }
                }
                
                System.out.println("MAIOR DE TODOOS: " + maior + " indice " + ind_maior);
            
                for (int i=0; i<25; i++)
                {
                    if (par_teste[i]>1)
                    {
                        par_teste[i] = par_teste[ind_maior];
                        rfr_teste[i] = rfr_teste[ind_maior];
                    }
                }
            }
            
            
            double[][] dados_treinamento2 = new double[100][100]; 
            for (int j=0; j<25; j++)
            {
                dados_treinamento2[j][0] = tmin_teste[j];
                dados_treinamento2[j][1] = tmax_teste[j];
                dados_treinamento2[j][2] = rainfall_teste[j];
                dados_treinamento2[j][3] = gdd_teste[j];
                dados_treinamento2[j][4] = nl_teste[j];
                dados_treinamento2[j][5] = par_teste[j];
                dados_treinamento2[j][6] = rfr_teste[j];
                dados_treinamento2[j][7] = tminmin_teste[j];
                dados_treinamento2[j][8] = tmaxmax_teste[j];
             }


            for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
            {
                saida_rede[i] = time_series.Simular(dados_treinamento2[i], tamanho_entrada);
            }

            // Aqui vão sair os valores dos vetores de saída da rede (desnormalizados)
            DefineValorSaidaEImprime(saida_rede, qtd_comb, acaule_out_min, aen_out_min, afolhas_out_min, qfolhas_out_min, aaf_out_min, 
                                     acaule_out_max, aen_out_max, afolhas_out_max, qfolhas_out_max, aaf_out_max, ram1_out, true);
            
            aen_min = aen_out_min;
            aen_max = aen_out_max;
            acaule_min = acaule_out_min;
            acaule_max = acaule_out_max;
            afolhas_min = afolhas_out_min;
            afolhas_max = afolhas_out_max;
            qfolhas_min = qfolhas_out_min;
            qfolhas_max = qfolhas_out_max;
            aaf_min = aaf_out_min;
            aaf_max = aaf_out_max;
            PROB_RAM1 = ram1_out;
        }
        
        // Fazer para todos...
        aaf_max = AjustaMinMax(aaf_min, aaf_max, qtd_comb);
        
        
    };
    
    private double[] AjustaMinMax(double min[], double max[], int n)
    {
        
        for (int i=0; i<n; i++)
        {
            if (min[i]>max[i]) max[i] = min[i];
        }
        return max;
    };
    
    
    
    // Adiciona entrenó no galho atual
    private void AdicionaEntrenoGalho(ArrayList<EntrenoGalho> galho_atual , ArrayList<EntrenoGalho> galho_final, 
                                        double comp, double afolha, int uc_atual)
    {
        EN_est_atu_temp = new EntrenoGalho(1, 0, comp, uc_atual, false); //  afolha, GeraRandomico(20, 80)
        int en = galho_atual.size(); // Índice do entrenó a ser inserido
        int en_final = 1;
        double comp_restante = 0;
        
        
        int uc_aux = uc_atual-1;
        
        // Obtém a quantidade de entrenós do galho final das unidades subsequentes
        // Para definir o índice real do estágio final
        int en_atual = 1;

        // Obtém a quantidade de entrenós do galho final das unidades subsequentes
        // Para definir o índice real do estágio final
        while (uc_aux>0)
        {
            en_final += (QtdeEntrenosUCn(galho_final, uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
            en_atual += (QtdeEntrenosUCn(galho_atual, uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
            uc_aux--;
        }
        
        en_final = en_final + QtdeEntrenosUCn(galho_atual, uc_atual);
        
        //System.out.println(" - EN_atual: " + (en+1) + " EN_final: " + (en_final+1));
        
        if (en<galho_final.size() && en_final<galho_final.size() &&
            galho_final.get(en_final).getUN()==uc_atual) // Não insere se não tiver entrenós para inserir e se o estágio atual já estiver atingido seu máximo
        {
            double comp_final = galho_final.get(en_final).getComp();
            if (comp<comp_final)
            {
                EN_est_atu_temp.setComp(comp);
                comp_restante = 0;
            }
            else {
                EN_est_atu_temp.setComp(comp_final);
                comp_restante = comp-comp_final;
            }
            
            // Verifica a inserção das folhas
            double af_final = galho_final.get(en_final).getAreaFolha();
            double af_restante = 0;
            
            if (!galho_final.get(en_final).temFolha()) // Se não existir folha no mesmo entrenó do estágio final
            { // Insere incondicionalmente a área foliar
                EN_est_atu_temp.setTemFolha(true);
                EN_est_atu_temp.setAreaFolha(afolha);
                EN_est_atu_temp.setAlfaFolha(GeraRandomico(20, 80));
                af_restante = 0;
            }
            else {
                if (comp<comp_final)
                {
                    EN_est_atu_temp.setTemFolha(true);
                    EN_est_atu_temp.setAreaFolha(afolha);
                    EN_est_atu_temp.setAlfaFolha(galho_final.get(en_final).getAlfaFolha());
                    comp_restante = 0;
                }
                else {
                    EN_est_atu_temp.setTemFolha(true);
                    EN_est_atu_temp.setAreaFolha(af_final);
                    EN_est_atu_temp.setAlfaFolha(galho_final.get(en_final).getAlfaFolha());
                    af_restante = afolha-af_final;
                }
            }
            galho_atual.add(EN_est_atu_temp);
        }
        
        
        //System.out.println(" ==> SOBROU " + comp_restante);
        if (comp_restante>0) // Se sobrou comprimento, preencher os entrenós anteriores que ainda contém comprimento a serem crescidos 
        {
            CresceEntrenos(uc_atual, galho_atual , galho_final, comp_restante);
        }
    };
    
    
    private void AdicionaEntrenoGalho2(ArrayList<EntrenoGalho> galho_atual , ArrayList<EntrenoGalho> galho_final, 
                                        double comp, double afolha, int uc_atual)
    {
            EN_est_atu_temp = new EntrenoGalho(1, 0, comp, uc_atual, false); //  afolha, GeraRandomico(20, 80)
            
            double comp_aux = 0;
            int ind_en = galho_atual.size();
            if (ind_en<galho_final.size())
            {
                comp_aux = galho_final.get(ind_en).getComp();
            }
            else comp_aux = comp;
            
            EN_est_atu_temp.setComp(Arredonda2Casas(comp_aux));//(Arredonda2Casas(comp));
            
            
            EN_est_atu_temp.setTemFolha(true);
            EN_est_atu_temp.setAreaFolha(Arredonda2Casas(afolha));
            EN_est_atu_temp.setAlfaFolha(Arredonda2Casas(GeraRandomico(20, 80)));
            galho_atual.add(EN_est_atu_temp);
        
    };
    
    
    
    private void CresceEntrenos(int uc_atual, ArrayList<EntrenoGalho> galho_atual, ArrayList<EntrenoGalho> galho_final, double comp_restante)
    {
        int en_inicial = 0; //GetInicioUC(uc_atual, galho_atual);
        double comp_final = 0, comp_a_adicionar = 0;
        while (comp_restante>0 && en_inicial<galho_atual.size())
        {
            comp_final = galho_final.get(en_inicial).getComp();
            comp_a_adicionar = comp_restante + galho_atual.get(en_inicial).getComp();
            if (comp_a_adicionar<comp_final)
            {
                galho_atual.get(en_inicial).setComp(comp_a_adicionar); // comp_a_adicionar
                comp_restante = 0;
            }
            else {
                galho_atual.get(en_inicial).setComp(comp_final);
                comp_restante = comp_restante-(comp_final);
            }
            en_inicial++;
        }
    };
    
    
    private void CresceElementosExistentes(int x, int s, int g)
    {
        if (acaule_day>0 && NumeroUCPausa(x)>0)
        {
            //System.out.println(" ACAULE PERDIDO: " + acaule_day);
            int en_inicial = 0;
            double comp_restante = acaule_day;
            while (comp_restante>0 && en_inicial<p.getSuporte(s).getGalho(g).recebeListaEntreno().size()) // 
            {
                if (en_inicial<p.getSuporte(s).getGalho(g).recebeListaEntreno().size())
                    if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp() 
                                        < p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp())
                    {
                        double comp_a_inserir = Arredonda2Casas(p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp()+comp_restante);
                        if (comp_a_inserir>p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp())
                        {
                            p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).setComp(
                                    p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp() );
                            comp_restante -= (p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp() 
                                                - p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp());
                        }
                        else {
                            p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).setComp(p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getComp() );
                            comp_restante = 0;
                        }
                    }
                en_inicial++;
            }
        }


        if (aaf_day>0 && NumeroUCPausa(x)>0)
        {
            int en_inicial = 0;
            double af_restante = aaf_day;
            while (af_restante>0 && en_inicial<p.getSuporte(s).getGalho(g).recebeListaEntreno().size() && en_inicial<p_final.getSuporte(s).getGalho(g).recebeListaEntreno().size()) // 
            {
                if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha() 
                                    < p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha() &&
                    p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).temFolha() )
                {
                    double comp_a_inserir = Arredonda2Casas(p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha()+af_restante);
                    if (comp_a_inserir>p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha())
                    {
                        p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).setAreaFolha(
                                p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha() );
                        af_restante -= (p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha() 
                                            - p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha());
                    }
                    else {
                        p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).setAreaFolha(p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_inicial).getAreaFolha() );
                        af_restante = 0;
                    }
                }
                en_inicial++;
            }
        }
    };
    
    
    // queda de folhas
    private void QuedaDeFolhasEP(int x, int s, int g)
    {
        
        boolean caiu = false;
        int miss = 0;

        for (int en=0; en<p.getSuporte(s).getGalho(g).recebeListaEntreno().size(); en++) //  && qfolhas_day>0
        {
            int uc_aux = p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).getUN() - 1;
            int en_final = 0;
            int en_atual = 0;

            // Obtém a quantidade de entrenós do galho final das unidades subsequentes
            // Para definir o índice real do estágio final
            while (uc_aux>0)
            {
                en_final += (QtdeEntrenosUCn(p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
                en_atual += (QtdeEntrenosUCn(p.getSuporte(s).getGalho(g).recebeListaEntreno(), uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
                uc_aux--;
            }

            en_final += (en-en_atual);
            //System.out.println(" - EN_atual: " + (en+1) + " EN_final: " + (en_final+1));

            if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).temFolha() 
                    && !p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_final).temFolha()) // Se no mesmo entrenó do estágio final não tiver folha
            {
                int num_folhas_galhos = 0;
                for (int i=0; i<p.getSuporte(s).getGalho(g).recebeListaEntreno().size(); i++)
                    if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(i).temFolha()) num_folhas_galhos++;

                // Definir probabilidade dela cair ou não
                if (Math.random()>RetornaProbabilidade(x, 0, p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).getUN(), en))
                {
                    if (p_inicial.getAmbiente().equals("SOMBRA"))
                    {
                        if ( num_folhas_galhos > (SomaAcumuladaVetor(afolhas_max, x/30)+SomaAcumuladaVetor(afolhas_min, x/30))/2)
                        {
                            p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setTemFolha(false);
                            p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setAreaFolha(0);
                            //System.out.println(" --> FOLHA CAIU");
                            caiu = true;
                            qfolhas_day--;
                        }

                    }
                    else 
                        // Definir uma condição válida para isso ocorrer
                    ///if ( num_folhas_galhos > (SomaAcumuladaVetor(afolhas_max, x/30)+SomaAcumuladaVetor(afolhas_min, x/30))/2)
                    {
                        p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setTemFolha(false);
                        p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setAreaFolha(0);
                        //System.out.println(" --> FOLHA CAIU");
                        caiu = true;
                        qfolhas_day--;
                    }
                }
            }
        }



        if (!caiu && TIPO_TESTE>0 && TIPO_TESTE<5) //  Se não achou folha a cair, deve-se derrubar uma folha do galho
        {
            for (int en=0; en<p.getSuporte(s).getGalho(g).recebeListaEntreno().size() && !caiu; en++) //  && qfolhas_day>0
            {
                int uc_aux = p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).getUN() - 1;
                int en_final = 0;
                int en_atual = 0;

                // Obtém a quantidade de entrenós do galho final das unidades subsequentes
                // Para definir o índice real do estágio final
                while (uc_aux>0)
                {
                    en_final += (QtdeEntrenosUCn(p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
                    en_atual += (QtdeEntrenosUCn(p.getSuporte(s).getGalho(g).recebeListaEntreno(), uc_aux)); // Para índice de vetores, considera-se o índice de 0 a n-1
                    uc_aux--;
                }
                en_final += (en-en_atual);
                
                if (en_final<1) en_final = 1;
                
                // Achou folha
                if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).temFolha() 
                        && !p_final.getSuporte(s).getGalho(g).recebeListaEntreno().get(en_final-1).temFolha()) // Se no mesmo entrenó do estágio final não tiver folha
                {
                    p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setTemFolha(false); // Derruba a folha
                    p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setAreaFolha(0);
                    caiu = true;
                }
            }
        }
    };
    
    
    private void AdaptacaoMudancaAmbiente(int s, int g)
    {
        boolean caiu = false;
        for (int en=0; en<p.getSuporte(s).getGalho(g).recebeListaEntreno().size()-3  && !caiu; en++) //  && qfolhas_day>0
        {
            if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).temFolha())
            {
                p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setTemFolha(false);
                p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setAreaFolha(0);
                caiu = true;
            }
        }

        for (int en=0; en<p.getSuporte(s).getGalho(g).recebeListaEntreno().size(); en++)
        {
            if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).temRamificacao())
            {
                boolean caiu2 = false;
                for (int k=0; k<p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).recebeListaEntreno().size()-3; k++)
                {
                    p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).recebeListaEntreno().get(k).setTemFolha(false);
                    p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).recebeListaEntreno().get(k).setAreaFolha(0);
                    caiu2 = true;
                }
            }
        }
    };
    
    
    public void executar() throws IOException, WriteException, CloneNotSupportedException, ClassNotFoundException
    {
        double dia_inicial=0;
        int obs_final = 0;
        
        p = Plantas_escrita_mtg.get(Plantas_escrita_mtg.size()-1).deepCopy(); //copia a planta do estagio precedente

        int obs_inicial=0;
        // FuncaoMatematica funcao_atual = new FuncaoMatematica();

        System.out.println(" Datas i : " + MTGbaseInicial.getDataString() + "\tf: " + MTGbaseFinal.getDataString());

        // Setando os valores dos dias (apenas para os estágios conhecidos)
        if (MTGbaseInicial.getDataString().equals("24/06/2003") || MTGbaseInicial.getDataString().equals("24/06/03")) // Observação 1
        {
            dia_inicial=0;
            obs_inicial=0;
        }
        else if (MTGbaseInicial.getDataString().equals("23/10/2003")) // Observação 7 - meio da UC1
        {
            dia_inicial=135;
            obs_inicial=6;
        }
        else if (MTGbaseInicial.getDataString().equals("19/01/2004")) // Observação 12 - final da UC1
        {
            dia_inicial=180;
            obs_inicial=11;
        }
        
        System.out.println(" * DIA INICIAL: " + dia_inicial);



        // *******************************************************************
        // ** Definindo as funções Splines
        // *******************************************************************
        //System.out.println("\n obs_ini: "+obs_inicial +"    obs_f: "+obs_final);
        DefineParametrosConhecidos(p_inicial);
        int dias_absolutos = (Database.diaX25.length-1)*30; // Considerando-se uma escala mensal para escala diária
        
        
        
        // ********************************************************************
        ExecutaRedeNeural(TIPO_TESTE);        
        System.out.println(" --> Galhos: " + p.getQtdEP());
        
        
        // Gera atributos de crescimento        
        GeraAtributosCrescimento(p.getQtdEP(), dias_absolutos, Database.diaX25.length);
        
        
        if (TIPO_TESTE==5) 
            if (p_inicial.getSexo().equals(("F")))
                Y_TF = Database.TF_MO_F;
            else Y_TF = Database.TF_MO_M;
                
        
        tam_folhas_day = DayScale(Database.diaX25.length, dias_absolutos, Database.diaX25, Y_TF , false);
        qtd_ram_day1 = GerarDadosAleatorios1(35, dias_absolutos);

        System.out.println("\n\n\t *** Determinando os estágios intermediários ***");

        int uc_atual = 1;
        int uc_anterior = 1;
        
        ArrayList<EntrenoGalho> galho_atual = new ArrayList<EntrenoGalho>();
        ArrayList<EntrenoGalho> galho_final = new ArrayList<EntrenoGalho>();

        // O dia inicial é o que vai ser utilizado para definir a partir de onde será feito a leitura vetores convertidos na escala diária
        System.out.println(" DIA INICIAL: " + dia_inicial);
        
        // Início do processo de interpolação dos estágios intermediários
        // Começa o processo de encontrar os estágios intermediários
        for (int estagio_atual=0; estagio_atual<ModeloTabelaMTGsAGerar.getRowCount(); estagio_atual++) // é o índice dos estágios intermediários
        {        
            // Para cada estágio de crescimento intermediário desejado, calcula-se
            // Começa o processo de crescimento
            int d,x;
            Long dias_destino  = Long.parseLong(ModeloTabelaMTGsAGerar.getValueAt(estagio_atual, 1).toString()) + (long)dia_inicial;
            String str_nome_do_arquivo = ModeloTabelaMTGsAGerar.getValueAt(estagio_atual, 0).toString();

            //System.out.println(" >>>> INI: " + dia_inicial + " ATE: " + dias_destino);
            
            
            // Aqui é onde serão feitos os passos da interação. A cada dia serão feitos as inserções dos componentes necessárias
            for (x=(int)dia_inicial; x<dias_destino; x++) 
            {
                int num_galho = 0;
                //System.out.print("x");
                // Faz-se o crescimento dos eixos principais
                for (int s=0; s < p.getQtdeSuportes(); s++) // Percorre os suportes
                {
                    
                    // Para cada galho, será feito o crescimento necessário
                    for (int g=0; g < p.getSuporte(s).getQtdeGalhos(); g++, num_galho++) // Percorre os galhos
                    {
                        galho_atual = p.getSuporte(s).getGalho(g).recebeListaEntreno();
                        galho_final = p_final.getSuporte(s).getGalho(g).recebeListaEntreno();
                        
                        uc_atual = NumeroUC_Atual(x);
                        // Aqui definir os valores de crescimento de cada parâmetro
                        DefineValorParametrosDia(x, g+s);
                        
                        int cont_qtd_en = p.getSuporte(s).getGalho(g).recebeListaEntreno().size()-1;
                        int cont_qtd_fls = p.getSuporte(s).getGalho(g).getQtdeFolhasEixoPrincipal();
                        

                        /*
                         * Evento 1: Crescimento do eixo principal (caule)
                         */
                        // Se houver aumento do número de metâmeros, acrescenta entrenós na lista
                        if (TIPO_TESTE!=5)
                        {
                            if (aen_day>0 && NumeroUCPausa(x)>0)
                            {
                                if (acaule_day>=0.1)
                                {
                                    double comp_en = Arredonda2Casas(acaule_day/aen_day);
                                    double area_folha_atual = 0.1;
                                    if (aaf_day>0.1) area_folha_atual = Arredonda2Casas(aaf_day/aen_day);
                                    else area_folha_atual = 0.1;

                                    for (int en=0; en<aen_day; en++)
                                    {
                                        AdicionaEntrenoGalho(p.getSuporte(s).getGalho(g).recebeListaEntreno() , p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), comp_en, area_folha_atual, uc_atual);
                                        cont_qtd_en++; // Incrementa o contador de número de entrenós
                                    }
                                }

                                else 
                                {
                                    double area_folha_atual = 0.1;
                                    if (aaf_day>0.1) area_folha_atual = Arredonda2Casas(aaf_day/aen_day);
                                    else area_folha_atual = 0.1;

                                    for (int en=0; en<aen_day; en++)
                                    {                                     
                                        AdicionaEntrenoGalho(p.getSuporte(s).getGalho(g).recebeListaEntreno() , 
                                                p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), 0.1, area_folha_atual, uc_atual);
                                        cont_qtd_en++; // Incrementa o contador de número de entrenós
                                    }
                                }
                            }

                            // Se não houver número de entrenós, ver o que pode ser crescido
                            else
                            {
                                CresceElementosExistentes(x, s, g);
                            }

                            // Queda de folhas
                            // Só pode cair folhas se tiver folhas para cair
                            if (p.getSuporte(s).getGalho(g).getAreaFoliarAbsoluta()>0) // qfolhas_day>0 &&  Posteriormente, selecionar um algoritmo melhor para queda de folhas, utilizando a distribuição de folhas
                            {
                                QuedaDeFolhasEP(x, s, g);
                            }

                            // Ramificações de ordem 1
                            if (NumeroUCPausa(x)>0)
                            {
                                CresceRamificacao(p.getSuporte(s).getGalho(g).recebeListaEntreno(), p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), x, 1);
                            }
                        }
                        
                        else // TRATAMENTO AQUI
                        {
                            int mes_inicio_inv = 15;
                            int mes_inicio_adap = 18;
                            
                            if (aen_day>0 && NumeroUCPausa(x)>0)
                            {
                                if (acaule_day>=0.1)
                                {
                                    double comp_en = Arredonda2Casas(acaule_day/aen_day);
                                    double area_folha_atual = 0.1;
                                    if (aaf_day>0.1) area_folha_atual = Arredonda2Casas(aaf_day/aen_day);
                                    else area_folha_atual = 0.1;
                                    
                                    for (int en=0; en<aen_day; en++)
                                    {
                                        if (x/30>mes_inicio_adap)
                                        {
                                            ///if (QtdeEntrenosUCn(p.getSuporte(s).getGalho(g).recebeListaEntreno(),uc_atual)<=RetornaQtdeMaximaEnUCEP(uc_atual, num_galho))
                                            //{
                                                area_folha_atual = tam_folhas_day[x];
                                                AdicionaEntrenoGalho2(p.getSuporte(s).getGalho(g).recebeListaEntreno() , p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), acaule_day, area_folha_atual, uc_atual);
                                                cont_qtd_en++; // Incrementa o contador de número de entrenós
                                            //}
                                        }
                                        else
                                        {
                                            AdicionaEntrenoGalho(p.getSuporte(s).getGalho(g).recebeListaEntreno() , p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), comp_en, area_folha_atual, uc_atual);
                                            cont_qtd_en++; // Incrementa o contador de número de entrenós
                                        }
                                    }
                                }

                                else 
                                {
                                    double area_folha_atual = 0.1;
                                    if (aaf_day>0.1) area_folha_atual = Arredonda2Casas(aaf_day/aen_day);
                                    else area_folha_atual = 0.1;

                                    for (int en=0; en<aen_day; en++)
                                    {                                     
                                        AdicionaEntrenoGalho(p.getSuporte(s).getGalho(g).recebeListaEntreno() , 
                                                p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), 0.1, area_folha_atual, uc_atual);
                                        cont_qtd_en++; // Incrementa o contador de número de entrenós
                                    }
                                }
                            }
                            else if (p_final.getSuporte(s).getGalho(g).recebeListaEntreno().size() > p.getSuporte(s).getGalho(g).recebeListaEntreno().size()
                                     && (x/30>mes_inicio_adap))
                            {
                                CresceElementosExistentes(x, s, g);
                            }
                            
                            
                            // Queda de folhas para stress5
                            // Só pode cair folhas se tiver folhas para cair
                            if (x>=331 && x<=480) // Faz adaptação
                            {
                                AdaptacaoMudancaAmbiente(s, g);
                            }
                            else if (p.getSuporte(s).getGalho(g).getAreaFoliarAbsoluta()>0) // qfolhas_day>0 &&  Posteriormente, selecionar um algoritmo melhor para queda de folhas, utilizando a distribuição de folhas
                            {
                                boolean caiu = false;
                                
                                for (int en=0; en<p.getSuporte(s).getGalho(g).recebeListaEntreno().size(); en++) //  && qfolhas_day>0
                                {
                                    if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).temFolha()) // Se no mesmo entrenó do estágio final não tiver folha
                                    {
                                        int num_folhas_galhos = 0;
                                        for (int i=0; i<p.getSuporte(s).getGalho(g).recebeListaEntreno().size() && !caiu; i++)
                                            if (p.getSuporte(s).getGalho(g).recebeListaEntreno().get(i).temFolha()) num_folhas_galhos++;

                                        // Definir probabilidade dela cair ou não
                                        if (Math.random()>RetornaProbabilidade(x, 0, p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).getUN(), en))
                                        {
                                            // Definir uma condição válida para isso ocorrer
                                            if (num_folhas_galhos > (SomaAcumuladaVetor(afolhas_max, x/30)+SomaAcumuladaVetor(afolhas_min, x/30))/2)
                                            {
                                                p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setTemFolha(false);
                                                p.getSuporte(s).getGalho(g).recebeListaEntreno().get(en).setAreaFolha(0);
                                                //System.out.println(" --> FOLHA CAIU");
                                                caiu = true;
                                                qfolhas_day--;
                                            }
                                        }
                                    }
                                }
                            }

                            // Ramificações de ordem 1
                            if (NumeroUCPausa(x)>0)
                            {
                                if (x/30>mes_inicio_adap)
                                    CresceRamificacao2(p.getSuporte(s).getGalho(g), p.getSuporte(s).getGalho(g).recebeListaEntreno(), p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), x, 1, mes_inicio_inv);
                                else 
                                {
                                    if (TIPO_TESTE==3 || TIPO_TESTE==4)
                                    {
                                        if ((uc_atual!=3 && uc_atual!=4)) CresceRamificacao(p.getSuporte(s).getGalho(g).recebeListaEntreno(), p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), x, 1);
                                    }
                                    else {
                                        CresceRamificacao(p.getSuporte(s).getGalho(g).recebeListaEntreno(), p_final.getSuporte(s).getGalho(g).recebeListaEntreno(), x, 1);
                                    }
                                }
                            }
                            
                        } // Fim do laço para estresse de adaptação
                        
                        
                    } // Fim do laço de galhos
                    
                } // Fim do laço de suportes
                
            } // Fim do processo de interpolação do estágio de crescimento

            
            // Escreve o MTG correspondente ao estágio atual
            //dia_inicial = dias_destino;
            EscritaMTG EscritaMTG_ = new EscritaMTG(new File((str_diretorio + "/"+ str_nome_do_arquivo)), p);
            Plantas_escrita_mtg.add(p); // O negócio é setar a Planta p
            
            
            System.out.println("\n\n>>> Planta na posicao\t" + estagio_atual + " da lista:");
            //System.out.println("Quantidade de eixos principais\t" + p.getQtdEP());  
            System.out.println("Quantidade de folhas\t" + p.getQtdeFolhas());
            System.out.println("Quantidade de metâmeros\t" + p.getQtdeEntrenos());
            System.out.println("Área foliar total\t" + p.getTotalAreaFoliar());
            System.out.println("Quantidade de ramificações\t" + p.getQtdeRams());
            System.out.println("Comprimento total de galhos\t" + p.getCompTotalGalhos());    
            System.out.println("BIOMASSA OBTIDA\t" + p.MassaBrutaTotal()/1000);
            
            
            // Informações de cada galho
            //System.out.println("Num.en\tComp.ep\t\t\tNum.folhas\tAF");
            System.out.println("\nNum.en \tNum.fls \tComp.ep");
            for (int s=0; s<p.getQtdeSuportes(); s++)
            {
                for (int g=0; g<p.getSuporte(s).getQtdeGalhos(); g++)
                {
                    System.out.println(p.getSuporte(s).getGalho(g).getQtdeEntrenosRelativa() + "\t" + p.getSuporte(s).getGalho(g).getQtdeFolhasEixoPrincipal()
                                        +"\t"+ p.getSuporte(s).getGalho(g).getComprimentoEixoPrincipal()); //+"\t"+
                                  // p.getSuporte(s).getGalho(g).getQtdeFolhasEixoPrincipal() + "\t"+ p.getSuporte(s).getGalho(g).getAreaFoliarEixoPrincipal()
                                      //  );
                }
            }


        }
        

        
        for (int x=0; x<Plantas_escrita_mtg.size(); x++)
        {
//            System.out.println("\n\n>>> Planta na posicao " + x + " da lista:");
//            System.out.println("Plantas.get(x).getQtdeFolhas(): " + Plantas_escrita_mtg.get(x).getQtdeFolhas());
//            System.out.println("Plantas.get(x).getQtdeEntrenos(): " + Plantas_escrita_mtg.get(x).getQtdeEntrenos());
//            System.out.println("Plantas.get(x).getTotalAreaFoliar(): " + Plantas_escrita_mtg.get(x).getTotalAreaFoliar());
//            System.out.println("Plantas.get(x).getQtdeRams(): " + Plantas_escrita_mtg.get(x).getQtdeRams());
//            System.out.println("Plantas.get(x).getCompTotalGalhos(): " + Plantas_escrita_mtg.get(x).getCompTotalGalhos());
//            System.out.println("Plantas.get(x).getSexo: " + Plantas_escrita_mtg.get(x).getSexo());
//            System.out.println("Tamanho Medio de Folhas: " + Plantas_escrita_mtg.get(x).getTamanhoMedioFolhas());
//            
            //Plantas_escrita_mtg.get(x).setMassaBrutaUtil();
            //System.out.println("\n * MASSA BRUTA DE GALHOS OBTIDA: " + Plantas_escrita_mtg.get(x).BiomassaGalhos(0));
        }
        //Plantas_escrita_mtg.add(p_final);

        System.out.println("\n *** ESTÁGIOS INTERMEDIÁRIOS GERADOS COM SUCESSO! ***\n");
        JOptionPane.showMessageDialog(null,"Estágios intermediários gerados com sucesso!","InterpolMatePy",JOptionPane.INFORMATION_MESSAGE);
    };
    
    
    ArrayList<Planta> getListaplantas()
    {
        return Plantas_escrita_mtg;
    };

    

    public void DefineParametrosConhecidos(Planta planta)
    {
        nl_ = Database.NL;
        rainfall_ = Database.RAINFALL;
        gdd_ = Database.GDD;
                
        // Ambiente SOL
        if (planta.getAmbiente().equals("SOL"))
        {
            System.out.println(" AMBIENTE SOL");
            ambiente = 1;
            temp_min = Database.TMIN_MO;
            temp_max = Database.TMAX_MO;
            par_geral = Database.PAR_MO;
            rfr_geral = Database.RFR_MO;
            uc_amb = Database.uc_MO;   
            tempminmin_geral = Database.CONSTMININ_MO;
            tempmaxmax_geral = Database.CONSTMAXMAX_MO;
            
            if (planta.getSexo().equals("M"))
            {
                                Y_AC = Database.acaule_MO_MA;
                Y_NM = Database.aen_MO_MA;
                Y_AF = Database.aaf_MO_MA;
                Y_ANF = Database.afolhas_MO_MA;
                Y_QF = Database.qfolhas_MO_MA;
                
                acaule_min = Database.acaule_MO_MA_min;
                aen_min = Database.aen_MO_MA_min;
                aaf_min = Database.aaf_MO_MA_min;
                afolhas_min = Database.afolhas_MO_MA_min;
                qfolhas_min = Database.qfolhas_MO_MA_min;
                
                acaule_max = Database.acaule_MO_MA_max;
                aen_max = Database.aen_MO_MA_max;
                aaf_max = Database.aaf_MO_MA_max;
                afolhas_max = Database.afolhas_MO_MA_max;
                qfolhas_max = Database.qfolhas_MO_MA_max;
                
                PROB_UC2 = 1.0;
                PROB_UC4 = 0.533;
                PROB_RAM1 = Database.ram_SOL_MA;
                PROB_RAM2 = Database.ram2_SOL_MA;
                
                
                for (int g=0; g<p_inicial.getQtdEP(); g++)
                {
                    if (Math.random()<0.2) MAX_UC1[g] = 70; else MAX_UC1[g] = 40;
                    if (Math.random()<0.33) MAX_UC2[g] = 67; else MAX_UC2[g] = 40;
                    if (Math.random()<0.0) MAX_UC3[g] = 58; else MAX_UC3[g] = 40;
                    if (Math.random()<0.27) MAX_UC4[g] = 46; else MAX_UC4[g] = 40;
                }
                
                MAX_UC11=30;
                MAX_UC21=30; 
                MAX_UC31=20; 
                MAX_UC41=20;
            }

            else if (planta.getSexo().equals("F"))
            {
                                Y_AC = Database.acaule_MO_FE;
                Y_NM = Database.aen_MO_FE;
                Y_AF = Database.aaf_MO_FE;
                Y_ANF = Database.afolhas_MO_FE;
                Y_QF = Database.qfolhas_MO_FE;
                Y_TF = Database.TF_MO_F;
                
                acaule_min = Database.acaule_MO_FE_min;
                aen_min = Database.aen_MO_FE_min;
                aaf_min = Database.aaf_MO_FE_min;
                afolhas_min = Database.afolhas_MO_FE_min;
                qfolhas_min = Database.qfolhas_MO_FE_min;
                
                acaule_max = Database.acaule_MO_FE_max;
                aen_max = Database.aen_MO_FE_max;
                aaf_max = Database.aaf_MO_FE_max;
                afolhas_max = Database.afolhas_MO_FE_max;
                qfolhas_max = Database.qfolhas_MO_FE_max;
                
                PROB_UC2 = 1.0;
                PROB_UC4 = 0.433;
                PROB_RAM1 = Database.ram_SOL_FE;
                PROB_RAM2 = Database.ram2_SOL_FE;
                
                for (int g=0; g<p_inicial.getQtdEP(); g++)
                {
                    if (Math.random()<0.2) MAX_UC1[g] = 69; else MAX_UC1[g] = 40;
                    if (Math.random()<0.33) MAX_UC2[g] = 77; else MAX_UC2[g] = 40;
                    if (Math.random()<0.03) MAX_UC3[g] = 43; else MAX_UC3[g] = 40;
                    if (Math.random()<0.0) MAX_UC4[g] = 40; else MAX_UC4[g] = 38;
                }
                
                MAX_UC11=30;
                MAX_UC21=30; 
                MAX_UC31=20; 
                MAX_UC41=20;
            }
        }

        // Ambiente SOMBRA
        else if (planta.getAmbiente().equals("SOMBRA"))
        {
            System.out.println(" AMBIENTE SOMBRA");
            ambiente = 2;
            temp_min = Database.TMIN_FUS;
            temp_max = Database.TMAX_FUS;
            par_geral = Database.PAR_FUS;
            rfr_geral = Database.RFR_FUS;
            uc_amb = Database.uc_FUS;  
            tempminmin_geral = Database.CONSTMININ_FUS;
            tempmaxmax_geral = Database.CONSTMAXMAX_FUS;
            
            if (planta.getSexo().equals("M"))
            {
                
                                Y_AC = Database.acaule_FUS_MA;
                Y_NM = Database.aen_FUS_MA;
                Y_AF = Database.aaf_FUS_MA;
                Y_ANF = Database.afolhas_FUS_MA;
                Y_QF = Database.qfolhas_FUS_MA;
                Y_TF = Database.TF_FUS_M;
                
                acaule_min = Database.acaule_FUS_MA_min;
                aen_min = Database.aen_FUS_MA_min;
                aaf_min = Database.aaf_FUS_MA_min;
                afolhas_min = Database.afolhas_FUS_MA_min;
                qfolhas_min = Database.qfolhas_FUS_MA_min;
                
                acaule_max = Database.acaule_FUS_MA_max;
                aen_max = Database.aen_FUS_MA_max;
                aaf_max = Database.aaf_FUS_MA_max;
                afolhas_max = Database.afolhas_FUS_MA_max;
                qfolhas_max = Database.qfolhas_FUS_MA_max;
                
                PROB_UC2 = 0.939;
                PROB_UC4 = 0.719;
                PROB_RAM1 = Database.ram_SOMBRA_MA;
                PROB_RAM2 = Database.ram2_SOMBRA_MA;
                
                
                                
                for (int g=0; g<p_inicial.getQtdEP(); g++)
                {
                    if (Math.random()<0.2) MAX_UC1[g] = 70; else MAX_UC1[g] = 40;
                    if (Math.random()<0.33) MAX_UC2[g] = 67; else MAX_UC2[g] = 40;
                    if (Math.random()<0.0) MAX_UC3[g] = 58; else MAX_UC3[g] = 40;
                    if (Math.random()<0.27) MAX_UC4[g] = 46; else MAX_UC4[g] = 40;
                }
                
                MAX_UC11=15;
                MAX_UC21=15; 
                MAX_UC31=10; 
                MAX_UC41=10;
            }

            // Espécie e ambiente em teste F -Sombra
            else if (planta.getSexo().equals("F"))
            {
                                Y_AC = Database.acaule_FUS_FE;
                Y_NM = Database.aen_FUS_FE;
                Y_AF = Database.aaf_FUS_FE;
                Y_ANF = Database.afolhas_FUS_FE;
                Y_QF = Database.qfolhas_FUS_FE;
                Y_TF = Database.TF_FUS_F;
                
                
                acaule_min = Database.acaule_FUS_FE_min;
                aen_min = Database.aen_FUS_FE_min;
                aaf_min = Database.aaf_FUS_FE_min;
                afolhas_min = Database.afolhas_FUS_FE_min;
                qfolhas_min = Database.qfolhas_FUS_FE_min;
                
                acaule_max = Database.acaule_FUS_FE_max;
                aen_max = Database.aen_FUS_FE_max;
                aaf_max = Database.aaf_FUS_FE_max;
                afolhas_max = Database.afolhas_FUS_FE_max;
                qfolhas_max = Database.qfolhas_FUS_FE_max;
                
                PROB_UC2 = 0.75;
                PROB_UC4 = 0.583;
                PROB_RAM1 = Database.ram_SOMBRA_FE;
                PROB_RAM2 = Database.ram2_SOMBRA_FE;
                
                for (int g=0; g<p_inicial.getQtdEP(); g++)
                {
                    if (Math.random()<0.2) MAX_UC1[g] = 69; else MAX_UC1[g] = 40;
                    if (Math.random()<0.33) MAX_UC2[g] = 77; else MAX_UC2[g] = 40;
                    if (Math.random()<0.03) MAX_UC3[g] = 43; else MAX_UC3[g] = 40;
                    if (Math.random()<0.0) MAX_UC4[g] = 40; else MAX_UC4[g] = 38;
                }
                
                
                MAX_UC11=15;
                MAX_UC21=15; 
                MAX_UC31=10; 
                MAX_UC41=10;
            }
        }
        
        // Distribuição de folhas
        if (planta.getSexo().equals("F"))
        {
            PROBF_EP_UC1 = DatabaseProb.UC1FE;
            PROBF_EP_UC2 = DatabaseProb.UC2FE;
            PROBF_EP_UC3 = DatabaseProb.UC3FE;
            PROBF_EP_UC4 = DatabaseProb.UC4FE;
            PROBF_Ram1_UC1 = DatabaseProb.UC1Ram1FE;
            PROBF_Ram1_UC2 = DatabaseProb.UC2Ram1FE;
            
        } else {
            PROBF_EP_UC1 = DatabaseProb.UC1MA;
            PROBF_EP_UC2 = DatabaseProb.UC2MA;
            PROBF_EP_UC3 = DatabaseProb.UC3MA;
            PROBF_EP_UC4 = DatabaseProb.UC4MA;
            PROBF_Ram1_UC1 = DatabaseProb.UC1Ram1MA;
            PROBF_Ram1_UC2 = DatabaseProb.UC2Ram1MA;
        }
        
        
        
        System.out.println(" >>>>> Dados Y para treinamento de redes neurais definidas\n");
    };

    

    // *************************************************************************
    // **** Determina funções Splines
    // *************************************************************************
    public static double Maior(double[] a)
    {
        double max = a[0];
        for(int i = 1; i < a.length; i++)
          if(a[i] > max)
              max = a[i];
        return max;
    }


    public double CalculaSomaParametro(double d_inicial, double entrada_X, Splines spline)
    {
        double aux=0;
        

        for (double x_atual=d_inicial; x_atual<=entrada_X; x_atual++)
        {
            aux = aux + spline.AvaliaFuncaoSpline(Database.diaX, x_atual);
        }

        return aux;
    }

    public double Arredonda2Casas(double num)
    {
        return (double)Math.round(num*100)/(double)100;
    };

    private int NumeroUC_Atual(double entrada_X)
    {
        if (entrada_X>0 && entrada_X<=90) return 1;
        else if (entrada_X>=91 && entrada_X<=225) return 1;
        else if (entrada_X>=226 && entrada_X<=239) return 1;
        else if (entrada_X>=240 && entrada_X<=330) return 2;     
        else if (entrada_X>=331 && entrada_X<=420) return 2;
        else if (entrada_X>=421 && entrada_X<=540) return 3;
        else if (entrada_X>=541 && entrada_X<=615) return 3;
        else if (entrada_X>=616 && entrada_X<=705) return 4;
        return 4;
    };
    
    private  static int RetornaMesUC(int diaX, int reg_uc)
    {
        int dia_ref = 0;
        if (reg_uc==1) dia_ref = 0;
        else if (reg_uc==2) dia_ref = 7;
        else if (reg_uc==3) dia_ref = 14;
        else if (reg_uc==4) dia_ref = 20;
        
        return (diaX/30)-dia_ref;
    };
    

    private int NumeroUCPausa(double entrada_X)
    {
        if (entrada_X>0 && entrada_X<=90) return 0;
        else if (entrada_X>=91 && entrada_X<=225) return 1;
        else if (entrada_X>=226 && entrada_X<=239) return 1;
        else if (entrada_X>=240 && entrada_X<=330) return 2;     
        else if (entrada_X>=331 && entrada_X<=420) return 0;
        else if (entrada_X>=421 && entrada_X<=540) return 3;
        else if (entrada_X>=541 && entrada_X<=615) return 0;
        else if (entrada_X>=616 && entrada_X<=705) return 4;
        return 0;
    };
    
    private int NumeroUC_Mes(double mes_X)
    {
        if (mes_X>0 && mes_X<=3) return 1;
        else if (mes_X>=4 && mes_X<=8) return 1;
        else if (mes_X>=9 && mes_X<=12) return 2;
        else if (mes_X>=13 && mes_X<=15) return 2;     
        else if (mes_X>=16 && mes_X<=19) return 3;
        else if (mes_X==20) return 3;
        else if (mes_X>=21 && mes_X<=24) return 4;
        else if (mes_X==25) return 4;
        return 0;
    };
    
    private int ObsFinalPorUC(int uc)
    {
        switch (uc)
        {
            case 1: return 8;
            case 2: return 16;
            case 3: return 20;
            case 4: return 24;
        }
        return 0;
    };
    

    private int GetInicioUC(int uc, ArrayList<EntrenoGalho> EN)
    {
        int i=EN.size()-1;
        if (uc==1) return 0;
        else while (EN.get(i).getUN()==uc)
        {
            i--;
        }
        return i+1;
    }


    /**
     * Rotina para calcular o valor de um parâmetro em uma UC
     * @param X
     * @param inicio_uc
     * @param valor_desejado
     * @param S
     * @return 
     */
    private double ValorOndaUC(double X[], int inicio_uc, double valor_desejado, Splines S)
    {
        int i=inicio_uc; // Inicio da UC
        double out=0;
        while (X[i]<valor_desejado)
        {
            out = out + S.AvaliaFuncaoSpline(X, X[i]);
            i++;
        }
        return out + S.AvaliaFuncaoSpline(X, valor_desejado);
    };
    
    private int GetObs(double valor)
    {
        int i=0;
        while (Database.diaX[i]<valor) i++;
        return i;
    };
    
    private int RetornaQtdeMaximaEnUC(int uc, int ordem)
    {
        if (ordem==0)
        {
            switch (uc)
            {
                case 1: return 70;
                case 2: return 70;
                case 3: return 70;
                case 4: return 70;
            }
        }
        else
        {
            switch (uc)
            {
                case 1: return MAX_UC11;
                case 2: return MAX_UC21;
                case 3: return MAX_UC31;
                case 4: return MAX_UC41;
            }
        }
        return 40;
    };
    
    private int QtdeRamosUCn(ArrayList<EntrenoGalho> galho_atual, int uc)
    {
        int en = 0;
        int qtd_ramos = 0;
        
        // Primeiro, encontra a UC atual, a partir de início
        if (uc==1) en=0;
        else while (galho_atual.get(en).getUN()!=uc && en<galho_atual.size()-1) en++;
        
        // Depois conta os entrenós
        while (galho_atual.get(en).getUN()==uc && en<galho_atual.size()-1) 
        {
            en++;
            if (galho_atual.get(en).temRamificacao()) qtd_ramos++;
        }
        return qtd_ramos;
    };
    
    
    
    
    // Atributos para unidades
    
    private int QtdeEntrenosUCn(ArrayList<EntrenoGalho> galho_atual, int uc) {
        int en = 0;
        int qtd_en = 0;
        
        // Primeiro, encontra a UC atual, a partir de início
        if (uc==1) en=0;
        else while (galho_atual.get(en).getUN()!=uc && en<galho_atual.size()-1) en++;
        
        while (galho_atual.get(en).getUN()==uc && en<galho_atual.size()-1) 
        {
            en++;
            qtd_en++;
        }
        
        return qtd_en;
    };
    
    
    private double CompCauleUCn(ArrayList<EntrenoGalho> galho_atual, int uc) {
        int en = 0;
        double comp = 0;
        
        // Primeiro, encontra a UC atual, a partir de início
        if (uc==1) en=0;
        else while (galho_atual.get(en).getUN()!=uc && en<galho_atual.size()-1) en++;
        
        while (galho_atual.get(en).getUN()==uc && en<galho_atual.size()-1) 
        {
            en++;
            comp += galho_atual.get(en).getComp();
        }
        
        return comp;
    };
    
    private int NumFolhasUCn(ArrayList<EntrenoGalho> galho_atual, int uc) {
        int en = 0;
        int num_fls = 0;
        
        // Primeiro, encontra a UC atual, a partir de início
        if (uc==1) en=0;
        else while (galho_atual.get(en).getUN()!=uc && en<galho_atual.size()-1) en++;
        
        while (galho_atual.get(en).getUN()==uc && en<galho_atual.size()-1) 
        {
            en++;
            if (galho_atual.get(en).temFolha()) num_fls++;
                
        }
        
        return num_fls;
    };
    
    
    private double AreaFoliarUCn(ArrayList<EntrenoGalho> galho_atual, int uc) {
        int en = 0;
        double af = 0;
        
        // Primeiro, encontra a UC atual, a partir de início
        if (uc==1) en=0;
        else while (galho_atual.get(en).getUN()!=uc && en<galho_atual.size()-1) en++;
        
        while (galho_atual.get(en).getUN()==uc && en<galho_atual.size()-1) 
        {
            en++;
            af += galho_atual.get(en).getAreaFolha();
                
        }
        
        return af;
    };
    
    
}
