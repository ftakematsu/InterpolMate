/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;
import interpolmate.Database;
import interpolmates1.NeuralNetwork;
import java.util.ArrayList;


public class TesteSeriesMultiSaidasVar2 {
    
    public static double acaule8[], acaule_saida[];
    public static double aen8[], aen_saida[];
    public static double afolhas8[], afolhas_saida[];
    public static double aaf8[], aaf_saida[];
    public static double qfolhas8[], qfolhas_saida[];
    
    public static double acaule_max[], acaule_min[];
    public static double aen_max[], aen_min[];
    public static double afolhas_max[], afolhas_min[];
    public static double aaf_max[], aaf_min[];
    public static double qfolhas_max[], qfolhas_min[];

    public static double temp_min[], temp_max[], par_geral[], rfr_geral[], tempminmin_geral[], tempmaxmax_geral[];
    public static double prob_ramif1[];
    
    private static double Max(double v[])
    {
        double maior = v[0];
        for (int i=1; i<v.length; i++)
            if (v[i]>maior) maior = v[i];
        return maior;
    };
    
    private static double Min(double v[])
    {
        double menor = v[0];
        for (int i=1; i<v.length; i++)
            if (v[i]<menor) menor = v[i];
        return menor;
    };
    
    // Normalização pelo método MinMax Equalizado
    public static double[] Normalizar(double v[], double max)
    {
        double n[] = new double[v.length];
        for (int i=0; i<v.length; i++)
        {
            n[i] = v[i]/max;
            //if (n[i]>=1) n[i] = 0.999;
        }
        return n;
    };
    
    public static double[] Normalizar2(double v[])
    {
        double n[] = new double[v.length];
        double min = Min(v);
        for (int i=0; i<v.length; i++)
            n[i] = v[i] - Min(v);
        return n;
    };
    
    
    public static double[] DesNormalizar(double v[], double max, double delta)
    {
        double n[] = new double[v.length];
        for (int i=0; i<v.length; i++)
            n[i] = v[i]*max + delta;
        return n;
    };
    
    
    public static void PrintVetor(String s, double v[], int len)
    {
        System.out.print("\n" + s + "\t");
        for (int i = 0; i < len; i++)
                System.out.print(", " + v[i]);
    }
    
    public static void PrintVetorVertical(String s, double v1[], double v2[], double v3[], double v4[], int len)
    {
        System.out.print("\n" + s + "\n");
        for (int i = 0; i < len; i++)
                System.out.println(v1[i] + "\t" + v2[i] +  "\t\t" + v3[i] + "\t" + v4[i]);
    }
    
    public static void PrintVetorVertical(String s, double v1[], double v2[], int len)
    {
        System.out.print("\n" + s + "\n");
        for (int i = 0; i < len; i++)
                System.out.println(v1[i] + "\t" + v2[i]);
    }
    
    public static void DefineDados(String amb, String gen)
    {
        if (amb.equals("SOL"))
        {
            temp_min = Database.TMIN_MO;
            temp_max = Database.TMAX_MO;
            par_geral = Database.PAR_MO;
            rfr_geral = Database.RFR_MO;
            tempminmin_geral = Database.CONSTMININ_MO;
            tempmaxmax_geral = Database.CONSTMAXMAX_MO;
            
            if (gen.equals("F"))
            {
                acaule8 = Database.acaule_MO_FE;
                aen8 = Database.aen_MO_FE;
                afolhas8 = Database.afolhas_MO_FE;
                aaf8 = Database.aaf_MO_FE;
                qfolhas8 = Database.qfolhas_MO_FE;
                
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
                
                prob_ramif1 = Database.ram_SOL_FE;
                
            }
            else if (gen.equals("M"))
            {
                acaule8 = Database.acaule_MO_MA;
                aen8 = Database.aen_MO_MA;
                afolhas8 = Database.afolhas_MO_MA;
                aaf8 = Database.aaf_MO_MA;
                qfolhas8 = Database.qfolhas_MO_MA;
                
                                
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
                
                prob_ramif1 = Database.ram_SOL_MA;
            }
        }
        else if (amb.equals("SOMBRA"))
        {
            temp_min = Database.TMIN_FUS;
            temp_max = Database.TMAX_FUS;
            par_geral = Database.PAR_FUS;
            rfr_geral = Database.RFR_FUS;
            tempminmin_geral = Database.CONSTMININ_FUS;
            tempmaxmax_geral = Database.CONSTMAXMAX_FUS;
            
            if (gen.equals("F"))
            {
                acaule8 = Database.acaule_FUS_FE;
                aen8 = Database.aen_FUS_FE;
                afolhas8 = Database.afolhas_FUS_FE;
                aaf8 = Database.aaf_FUS_FE;
                qfolhas8 = Database.qfolhas_FUS_FE;
                
                                
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
                
                prob_ramif1 = Database.ram_SOMBRA_FE;
            }
            else if (gen.equals("M"))
            {
                acaule8 = Database.acaule_FUS_MA;
                aen8 = Database.aen_FUS_MA;
                afolhas8 = Database.afolhas_FUS_MA;
                aaf8 = Database.aaf_FUS_MA;
                qfolhas8 = Database.qfolhas_FUS_MA;
                
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
                
                prob_ramif1 = Database.ram_SOMBRA_MA;
            }
        }
        
    }
    
    
    
    public static double ErroQuadrado(double v1[], double v2[], int n)
    {
        double e = 0;
        for (int i=0; i<n; i++)
        {
            e = e + (v1[i]-v2[i])*(v1[i]-v2[i]);
        }
        return Math.sqrt(e/n);
    }
    
    public static double Bias(double v1[], double v2[], int n)
    {
        double b = 0;
        for (int i=0; i<n; i++)
            b = b + (v1[i]-v2[i]);
        return b/n;
    }
    
    
    
    public static void SincronizarEntrada(int qtd_comb)
    {           
        double num_fls = 0;
        for (int m=0; m<qtd_comb; m++)
        {
//            if (aen8[m]==0) // Se não houver aumento do número de metâmeros
//            {
//                // Não-se deve ter alongamento de caule e aumento do número de folhas
//                acaule8[m] = 0;
//                afolhas8[m] = 0;
//            }
//            
            if (num_fls==0 && num_fls<qfolhas8[m]) 
            {
                qfolhas8[m] = 0;
                if (num_fls==0) aaf8[m]=0;
            } // Não tem folha para cair caso nenhuma folha foi emitida
            
            
            if (acaule8[m]==0 || afolhas8[m]==0)  // O crescimento destes parâmetros não pode oorrer caso não houver aumento do número de metâmeros
            {
                aen8[m] = 0;
            }
//            else if (afolhas8[m]>0) // Se forem emitidas novas folhas...
//            {
                // ... deve-se aumentar a área foliar
//                if (aaf8[m]==0) 
//                {
//                    afolhas8[m] = 0;
//                }
                // Não pode acontecer o aumento da área foliar caso a queda de folhas seja maior do que o aumento do número de folhas
//                else if (qfolhas8[m] > afolhas8[m])
//                {
//                    if (aaf8[m] > 0)
//                    {
//                        qfolhas8[m] = 0;
//                    }
//                }
//            }
            num_fls += afolhas8[m];
        }
    };
    
    
    public static void SincronizarParametrosMorfologicos(int qtd_comb, double uc[])
    {           
        double num_fls = 0;
        final double n = 0;
        boolean sinc2 = false;
        boolean pausa[] = {false,false,false,false,false,false,false,false,false,false,false,true,true
                            ,true,true,false, false,false,false,true,false,false, false, false,true};
            
        
        for (int m=0; m<qtd_comb; m++)
        {
            if (aen_saida[m]<0.001 || pausa[m]) aen_saida[m] = n;
            if (afolhas_saida[m]<0.001 || pausa[m]) afolhas_saida[m] = n;
            if (qfolhas_saida[m]<0.0001) qfolhas_saida[m] = n;
            if (aaf_saida[m]<0.0001) aaf_saida[m] = n; 
            if (acaule_saida[m]<0.0001) acaule_saida[m] = n; 
            
            
            if (acaule_saida[m]==n)
            {
                aen_saida[m] = 0;
            }
            
            if (aen_saida[m]==n && uc[m]==0) // Se não houver aumento do número de metâmeros (ocorrência nas pausas de crescimento)
            {
                // Não-se deve ter alongamento de caule e aumento do número de folhas
                acaule_saida[m] = n;
                afolhas_saida[m] = n;
                if (sinc2) {
                    acaule8[m] = n;
                    afolhas8[m] = n;
                }
            }
            
            if (num_fls==0 && num_fls<qfolhas_saida[m]) 
            {
                qfolhas_saida[m] = 0; // Não tem folha para cair caso nenhuma folha foi emitida

            }
            
            if (acaule_saida[m]==n || afolhas_saida[m]==n)  // O crescimento destes parâmetros não pode oorrer caso não houver aumento do número de metâmeros
            {
                aen_saida[m] = n;
                if (sinc2) aen8[m] = n;
            }
            else if (afolhas_saida[m]>n) // Se forem emitidas novas folhas...
            {
                // ... deve-se aumentar a área foliar
                if (aaf_saida[m]==n) 
                {
                    afolhas_saida[m] = n;
                    if (sinc2) afolhas8[m] = n;
                }
                // Não pode acontecer o aumento da área foliar caso a queda de folhas seja maior do que o aumento do número de folhas
                else if (qfolhas_saida[m] > afolhas_saida[m])
                {
                    if (aaf_saida[m] > n)
                    {
                        aaf_saida[m] = n;
                        //if (sinc2) aaf8[m] = n;
                    }
                }
            }
        }
    };
    
    
    static public void DefineValorSaidaEImprime(double saida_rede[][], int qtd_comb, double acaule_out[], double aen_out[], double afolhas_out[], 
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
    
    
    static public void PrintSaidaRede(double saida_rede[][], int qtd_comb)
    {
        System.out.print("\nA.Caule");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][0]*Max(acaule_min));
        }
        
        System.out.print("\nN.Metameros");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][1]*Max(aen_min));
        }

        System.out.print("\nN.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][2]*Max(afolhas_min));
        }


        System.out.print("\nQ.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][3]*Max(qfolhas_min));
        }


        System.out.print("\nA.Foliar");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][4]*Max(aaf_min));
        }
    };
    
    
    private static void ImprimeMatrizTabela(double T[][], int lin, int col)
    {
        for (int i=0; i<lin; i++)
        {
            System.out.println("\n");
            for (int j=0; j<col; j++)
            {
                System.out.print("\t" + T[i][j]);
            }
        }
    };
    
    
    public static void main(String[] args){
                
                final int max_it = 10000;
                
                int qtd_comb = 25;
                int qtd_meses = 25;
                int tamanho_entrada = 9;
                int tamanho_saida = 11;
                int tamanho_camada_oculta = 2*tamanho_entrada + 1; // HECHT - NIELSEN
                int qtd_camada_oculta =1;
                double alfa = 0.2; 
                double momentum = 0.5;
                double saida_rede[][] = new double[100][100];
                
                String ambiente = "SOMBRA", genero = "F";
                
                boolean impr_tab = true;
                DefineDados(ambiente, genero);
                //SincronizarEntrada(qtd_comb);
                

                double[][] dados_treinamento = new double[100][100]; // [quantidade de combinações utilizadas no treinamento][tamanho da entrada]
                
                
                double diaX[] = {0,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
                
                double tmin_[] = new double[100];
                double tmax_[] = new double[100];
                double nl_[] = new double[100];
                double rainfall_[] = new double[100];
                double gdd_[] = new double[100];
                double PAR_[] = new double[100];
                double RFR_[] = new double[100];
                double uc[] = new double[100];
                double constminmin_[] = new double[100];
                double constmaxmax_[] = new double[100];
                
                nl_ = Database.NL;
                rainfall_ = Database.RAINFALL; 
                gdd_ = Database.GDD;
                
                tmin_ = temp_min;
                tmax_ = temp_max;
                PAR_ = par_geral;
                RFR_ = rfr_geral;
                constminmin_ = tempminmin_geral;
                constmaxmax_ = tempmaxmax_geral;
                
                if (ambiente.equals("SOMBRA")) {
                    uc = Database.uc_FUS;
                } else { // se ambiente = "SOL"
                    uc = Database.uc_MO;
                }
                

                System.out.println(" Iniciando treinamento...");
                
                double saida_param[] = new double[qtd_comb];
                
                // Valores normalizados
                double tmin[] = Normalizar(tmin_, Max(tmin_));
                double tmax[] = Normalizar(tmax_, Max(tmax_));
                double nl[] = Normalizar(nl_, Max(nl_));
                double rainfall[] = Normalizar(rainfall_, Max(rainfall_));
                double gdd[] = Normalizar(gdd_, Max(gdd_));
                double par[] = Normalizar(PAR_, Max(PAR_));
                double rfr[] = Normalizar(RFR_, Max(RFR_));
                double tminmin[] = Normalizar(constminmin_, Max(constminmin_));
                double tmaxmax[] = Normalizar(constmaxmax_, Max(constmaxmax_));
                
                // Setar dados treinamento
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
                    //}
                }
                                
                //qtd_comb = t;
               
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
                boolean impr_it = true;
                double soma_err = 0;
                
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
                
                double ram1n[] = prob_ramif1; // Não necessita normalizar
                
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

                double menor_erro = 999999999;

                for (int i = 0; i < max_it; i++)
                {
                    // Aqui vai simular a rede para calcular o erro quadrático
                    for (int k = 0; k < time_series.QtdDadosTreinamento(); k++)
                    {
                        saida_rede[k] = time_series.Simular(dados_treinamento[k], tamanho_entrada);
                    }
                    
                    impr_it = false;
                    
                    if (i>=0)
                    {
                        if (impr_it) System.out.print("\n\n Iteracao " + i);

                        DefineValorSaidaEImprime(saida_rede, qtd_comb, acaule_out_min, aen_out_min, afolhas_out_min, qfolhas_out_min, aaf_out_min, 
                                 acaule_out_max, aen_out_max, afolhas_out_max, qfolhas_out_max, aaf_out_max, ram1_out, false);
                        //PrintVetorVertical("Aaf", aaf8, aaf_out, qtd_comb);

                        // Soma dos erros quadráticos
                        soma_err = 0;

                        soma_err += ErroQuadrado(acaule8,acaule_out_max,qtd_comb);
                        soma_err += ErroQuadrado(aen8,aen_out_max,qtd_comb);
                        soma_err += ErroQuadrado(afolhas8,afolhas_out_max,qtd_comb);
                        soma_err += ErroQuadrado(qfolhas8,qfolhas_out_max,qtd_comb);
                        soma_err += ErroQuadrado(aaf8,aaf_out_max,qtd_comb);

                        if (impr_it) System.out.print(" Soma de erros: " + soma_err);
                        if (soma_err<menor_erro) menor_erro = soma_err;
                    }
                    
                    for (int j=0; j<time_series.QtdDadosTreinamento(); j++)
                    {
                        double[] input = time_series.GetDadoTreinamento(j);
                        time_series.train(time_series.GetDadoTreinamento(j), target[j], tamanho_entrada);
                    }
                    
		}
                
                DefineValorSaidaEImprime(saida_rede, qtd_comb, acaule_out_min, aen_out_min, afolhas_out_min, qfolhas_out_min, aaf_out_min, 
                                 acaule_out_max, aen_out_max, afolhas_out_max, qfolhas_out_max, aaf_out_max, ram1_out, false);
                
                //rede_neural_otimizado.SetConfigNetwork(input_layer.get(id_menor_erro), hidden_layer.get(id_menor_erro), output_layer.get(id_menor_erro));
                System.out.println("\n MENOR ERRO >>> " + menor_erro);
                
                //time_series = ann_aux[0];
                
                
                
                
                System.out.println("\n\n Dia X");
                PrintVetor("Dia X", diaX, qtd_comb);
                
//                double tmin_teste[] = temp_min;
//                double tmax_teste[] = temp_max;
//                double nl_teste[] = nl_;
//                double rainfall_teste[] = rainfall_;
//                double gdd_teste[] = gdd_;
//                double par_teste[] = par_geral;
//                double rfr_teste[] = rfr_geral;

                
                // dados_treinamento = time_series.GetDadoTreinamento();
               double tmin_teste[] = temp_min;
               double tmax_teste[] = temp_max;
               double nl_teste[] = nl_;
               double rainfall_teste[] = rainfall_; //;{5.43,3.1,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.5,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,0.5,0.5,0.5,7.17,8.25};
               double gdd_teste[] = gdd_;
               double par_teste[] = par_geral; //{67.068,67.068,67.068,92.991,92.991,62.793,62.793,94.245,94.245,54.602,54.602,20.781,20.781,67.068,67.068,103.67,103.67,85.02,85.02,81.382,777.919,320.26,320.26,43.166,43.166};
               double rfr_teste[] = rfr_geral; //{1.1133,1.1133,1.1133,1.0962,1.0962,1.1168,1.1168,1.0763,1.0763,1.0779,1.0779,1.0978,1.0978,1.1133,1.1133,0.958,0.958,1.006,1.006,0.9346,0.6541,0.672,0.672,0.7366,0.7366};
               double tminmin_teste[] = tempminmin_geral;
               double tmaxmax_teste[] = tempmaxmax_geral;
                              
               
               // 0 - Sem stress
               // 1 - Temp baixa UC3
               // 2 - Temp baixa UC4
               // 3 - Seca UC3
               // 4 - Seca UC4
               // 5 - Luz inversa
               final int TIPO_STRESS = 5;
               
               // Definir aqui os dados de stress
               if (TIPO_STRESS==1)
               {
                   if (ambiente.equals("SOMBRA"))
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
               else if (TIPO_STRESS==2)
               {
                   if (ambiente.equals("SOMBRA"))
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
               else if (TIPO_STRESS==3)
               {
                   if (ambiente.equals("SOMBRA"))
                   {
                        rainfall_teste = Database.SECA_U3;
                   }
                   else rainfall_teste = Database.SECA2_U3;
               }
               else if (TIPO_STRESS==4)
               {
                   if (ambiente.equals("SOMBRA"))
                   {
                        rainfall_teste = Database.SECA_U4;
                   }
                   else rainfall_teste = Database.SECA2_U4;
               }
               else if (TIPO_STRESS==5)
               {
                   if (ambiente.equals("SOMBRA"))
                   {
                       tmin_teste = Database.LUZ_TMIN_FUS;
                       tmax_teste = Database.LUZ_TMAX_FUS;
                       par_teste = Database.LUZ_PAR_FUS;
                       rfr_teste = Database.LUZ_RFR_FUS;
                       //tminmin_teste = Database.LUZ_CONSTMININ_FUS;
                       //tmaxmax_teste = Database.LUZ_CONSTMAXMAX_FUS;
                   } 
                   else {
                       tmin_teste = Database.LUZ_TMIN_MO;
                       tmax_teste = Database.LUZ_TMAX_MO;
                       par_teste = Database.LUZ_PAR_MO;
                       rfr_teste = Database.LUZ_RFR_MO;
                       tminmin_teste = Database.LUZ_CONSTMININ_MO;
                       tmaxmax_teste = Database.LUZ_CONSTMAXMAX_MO;
                   }
               }
               
                
                // Setar dados treinamento
                
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
                
                
                PrintVetor("PAR_TESTE_NORMALIZADO: ", par_teste, 25);
                
                int ind_maior = 0;
                double maior = 0;
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
                
                System.out.println("\n\n Saida da rede");
                
                
                for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
                    saida_rede[i] = time_series.Simular(dados_treinamento2[i], tamanho_entrada);
                
                //ImprimeMatrizTabela(saida_rede,qtd_comb,tamanho_entrada);
                
                
                DefineValorSaidaEImprime(saida_rede, qtd_comb, acaule_out_min, aen_out_min, afolhas_out_min, qfolhas_out_min, aaf_out_min, 
                                 acaule_out_max, aen_out_max, afolhas_out_max, qfolhas_out_max, aaf_out_max, ram1_out, true);
                    
                
              System.out.println("\n\n Fim de treinamento\n");
              
              
              if (impr_tab)
              {
                  acaule_saida = acaule_out_min;
                  aen_saida = aen_out_min;
                  afolhas_saida = afolhas_out_min;
                  qfolhas_saida = qfolhas_out_min;
                  aaf_saida = aaf_out_min;
                  
                  double acaule_saida_max[] = acaule_out_max;
                  double aen_saida_max[] = aen_out_max;
                  double afolhas_saida_max[] = afolhas_out_max;
                  double qfolhas_saida_max[] = qfolhas_out_max;
                  double aaf_saida_max[] = aaf_out_max;
                  
                  SincronizarParametrosMorfologicos(qtd_comb, uc);
                  
                  PrintVetorVertical("Acaule", acaule_min, acaule_max, acaule_saida, acaule_saida_max, qtd_comb);
                  PrintVetorVertical("Aen", aen_min, aen_max, aen_saida, aen_saida_max, qtd_comb);
                  PrintVetorVertical("Afolhas", afolhas_min, afolhas_max, afolhas_saida, afolhas_saida_max, qtd_comb);
                  PrintVetorVertical("Qfolhas", qfolhas_min, qfolhas_max, qfolhas_saida, qfolhas_saida_max, qtd_comb);
                  PrintVetorVertical("Aaf", aaf_min, aaf_max, aaf_saida, aaf_saida_max, qtd_comb);
                  PrintVetorVertical("Ram", prob_ramif1, ram1_out, qtd_comb);
              }
              
//              System.out.println("\n\n Erro acaule:\t" + ErroQuadrado(acaule8,acaule_out,qtd_comb) + " Bias: " + Bias(acaule8,acaule_out,qtd_comb));
//              System.out.println(" Erro aen:\t" + ErroQuadrado(aen8,aen_out,qtd_comb) + " Bias: "  + Bias(aen8,aen_out,qtd_comb));
//              System.out.println(" Erro afolhas:\t" + ErroQuadrado(afolhas8,afolhas_out,qtd_comb) + " Bias: "  + Bias(afolhas8,afolhas_out,qtd_comb));
//              System.out.println(" Erro qfolhas:\t" + ErroQuadrado(qfolhas8,qfolhas_out,qtd_comb) + " Bias: "  + Bias(qfolhas8,qfolhas_out,qtd_comb));
//              System.out.println(" Erro aaf:\t" + ErroQuadrado(aaf8,aaf_out,qtd_comb) + " Bias: "  + Bias(aaf8,aaf_out,qtd_comb));
             
             // System.out.println("\n MENOR ERRO TOTAL DO TREINAMENTO>>> " + menor_erro);

              
              
              
              
              

                System.out.println();
                
	}
}
