/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;
import interpolmate.Database;
import interpolmates1.NeuralNetwork;
import java.util.ArrayList;


public class TesteSeriesMultiSaidas {
    
    public static double acaule8[], acaule_saida[];
    public static double aen8[], aen_saida[];
    public static double afolhas8[], afolhas_saida[];
    public static double aaf8[], aaf_saida[];
    public static double qfolhas8[], qfolhas_saida[];

    
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
            n[i] = v[i]/max;
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
            if (gen.equals("F"))
            {
                acaule8 = Database.acaule_MO_FE;
                aen8 = Database.aen_MO_FE;
                afolhas8 = Database.afolhas_MO_FE;
                aaf8 = Database.aaf_MO_FE;
                qfolhas8 = Database.qfolhas_MO_FE;
            }
            else if (gen.equals("M"))
            {
                acaule8 = Database.acaule_MO_MA;
                aen8 = Database.aen_MO_MA;
                afolhas8 = Database.afolhas_MO_MA;
                aaf8 = Database.aaf_MO_MA;
                qfolhas8 = Database.qfolhas_MO_MA;

            }
        }
        else if (amb.equals("SOMBRA"))
        {
            if (gen.equals("F"))
            {
                acaule8 = Database.acaule_FUS_FE;
                aen8 = Database.aen_FUS_FE;
                afolhas8 = Database.afolhas_FUS_FE;
                aaf8 = Database.aaf_FUS_FE;
                qfolhas8 = Database.qfolhas_FUS_FE;
            }
            else if (gen.equals("M"))
            {
                acaule8 = Database.acaule_FUS_MA;
                aen8 = Database.aen_FUS_MA;
                afolhas8 = Database.afolhas_FUS_MA;
                aaf8 = Database.aaf_FUS_MA;
                qfolhas8 = Database.qfolhas_FUS_MA;
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
                        //aaf_saida[m] = n;
                        //if (sinc2) aaf8[m] = n;
                    }
                }
            }
                        
            num_fls += afolhas_saida[m];
            if (num_fls==0) aaf_saida[m]=0;
        }
    };
    
    
    static public void PrintSaidaRede(double saida_rede[][], int qtd_comb, double acaule_out[], double aen_out[], double afolhas_out[], 
                               double qfolhas_out[], double aaf_out[], boolean impr)
    {
        if (impr) System.out.print("\nA.Caule");
        for (int j=0; j<qtd_comb; j++)
        {
            acaule_out[j] = saida_rede[j][0]*Max(acaule8);
            if (impr) System.out.print(" ," + acaule_out[j]);
        }

        if (impr) System.out.print("\nN.Metameros");
        for (int j=0; j<qtd_comb; j++)
        {
            aen_out[j] = saida_rede[j][1]*Max(aen8);
            if (impr) System.out.print(" ," + aen_out[j]);
        }

        if (impr) System.out.print("\nN.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            afolhas_out[j] = saida_rede[j][2]*Max(afolhas8);
            if (impr) System.out.print(" ," + afolhas_out[j]);
        }


        if (impr) System.out.print("\nQ.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            qfolhas_out[j] = saida_rede[j][3]*Max(qfolhas8);
            if (impr) System.out.print(" ," + qfolhas_out[j]);
        }


        if (impr) System.out.print("\nA.Foliar");
        for (int j=0; j<qtd_comb; j++)
        {
            aaf_out[j] = saida_rede[j][4]*Max(aaf8);
            if (impr) System.out.print(" ," + aaf_out[j]);
        }
        if (impr) System.out.println("\n");
    };
    
    
    static public void PrintSaidaRede(double saida_rede[][], int qtd_comb)
    {
        System.out.print("\nA.Caule");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][0]*Max(acaule8));
        }

        System.out.print("\nN.Metameros");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][1]*Max(aen8));
        }

        System.out.print("\nN.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][2]*Max(afolhas8));
        }


        System.out.print("\nQ.Folhas");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][3]*Max(qfolhas8));
        }


        System.out.print("\nA.Foliar");
        for (int j=0; j<qtd_comb; j++)
        {
            System.out.print(" ," + saida_rede[j][4]*Max(aaf8));
        }
    };
    
    
    
    
    public static void main(String[] args){
                
                final int max_it = 10000;
                
                int qtd_comb = 25;
                int tamanho_entrada = 6;
                int tamanho_saida = 5;
                int tamanho_camada_oculta = 2*tamanho_entrada + 1; // HECHT - NIELSEN
                int qtd_camada_oculta = 1;
                double alfa = 0.2; 
                
                double saida_rede[][] = new double[100][100];
                
                String ambiente = "SOMBRA", genero = "F";
                
                boolean impr_tab = true;
                DefineDados(ambiente, genero);
                SincronizarEntrada(qtd_comb);
                
                //double uc_mo[] = {0,0,0,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.0,0.0,0.9,0.9,0.9,0.9,0.9,0,0.9,0.9,0.9,0.9,0.9,0};
                //double uc_fus[] = {0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.9,0.0,0.0,0.0,0.9,0.9,0.9,0.9,0,0,0.9,0.9,0.9,0.9,0};

                if (ambiente.equals("SOMBRA"))
                {
                    for (int j=0; j<3; j++) 
                    { 
                        //acaule8[j] = 0; aen8[j] = 0; afolhas8[j] = 0; qfolhas8[j] = 0; aaf8[j] = 0;
                        //uc_fus[j] = 0.5;
                    }
                }
                
                
                double[][] dados_treinamento = new double[100][100]; // [quantidade de combinações utilizadas no treinamento][tamanho da entrada]
                
                
                double diaX[] = {0,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
                
                double tmin_[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
                double tmax_[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
                double nl_[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
                double rainfall_[] = {5.43,3.10,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.50,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,2.96,4.49,5.92,7.17,8.25};
                double gdd_[] = {254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};
                
                // Outros parâmetros de entrada
                double uc_MO[] = {0,0,0,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.0,0.0,0.8,0.8,0.8,0.8,0.8,0,0.8,0.8,0.8,0.8,0};
//                double uc_MO[] = {0,0,0,0,0.2,0.2,0.2,0.2,0.4,0.4,0.4,0.4,0.0,0.0,0.6,0.6,0.6,0.6,0.6,0,0.8,0.8,0.8,0.8,0};
                //double uc_FUS[] = {0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0,0,0,0,0.8,0.8,0.8,0.8,0,0.8,0.8,0.8,0.8,0};
                double uc_FUS[] = {0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0,0,0,0,0,0.8,0.8,0.8,0,0.8,0.8,0.8,0.8,0};
                // 
                double uc[] = uc_MO;
                
                if (ambiente.equals("SOMBRA"))
                {
                    uc = uc_FUS;
                } else { // se ambiente = "SOL"
                    uc = uc_MO;
                }
                
                double ano[] = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9};
                
                
                
                double matriz_param[] = acaule8; // Mudar aqui
                
                //aaf8[1] = 57.78; aaf8[2] = 41.59; aaf8[8] = 42.86; aaf8[10] = 160.52;  aaf8[15] = 69.9; aaf8[18] = 0.45; aaf8[20] = 2.52; aaf8[23] = 38.52; 
                
                double saida_param[];
                
                // Valores normalizados
                double tmin[] = Normalizar(tmin_, Max(tmin_));
                double tmax[] = Normalizar(tmax_, Max(tmax_));
                double nl[] = Normalizar(nl_, Max(nl_));
                double rainfall[] = Normalizar(rainfall_, Max(rainfall_));
                double gdd[] = Normalizar(gdd_, Max(gdd_));
                
                
                // Setar dados treinamento
                int t = 0;
                double dia_aux[] = new double[25];
                for (int j=0; j<qtd_comb; j++)
                {
                    //if (uc[j]!=0)
                    //{
                        dia_aux[t] = diaX[j];
                        dados_treinamento[t][0] = uc[j];
                        dados_treinamento[t][1] = tmin[j];
                        dados_treinamento[t][2] = tmax[j];
                        dados_treinamento[t][3] = rainfall[j];
                        dados_treinamento[t][4] = gdd[j];
                        dados_treinamento[t][5] = nl[j];
                        
                        t++;
                    //}
                }
                
                saida_param = Normalizar(matriz_param, Max(matriz_param));
                
                //qtd_comb = t;
               
                /* INICIALIZAÇÃO DE REDES NEURAIS */
                double acaule_out[] = new double[qtd_comb];
                double aen_out[] = new double[qtd_comb];
                double afolhas_out[] = new double[qtd_comb];
                double qfolhas_out[] = new double[qtd_comb];
                double aaf_out[] = new double[qtd_comb];
                
                NeuralNetwork time_series = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, saida_param);
                boolean impr_it = true;
                double soma_err = 0;
                
                //for (int q=0; q<10; q++)
                //{
                    time_series = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, saida_param);

                    // Aqui vão ser verificados os pesos inciais de treinamento e qual o erro de rede
                    for (int k = 0; k < time_series.QtdDadosTreinamento(); k++) {
                            saida_rede[k] = time_series.Simular(dados_treinamento[k], tamanho_entrada);
                    }
                    PrintSaidaRede(saida_rede, qtd_comb, acaule_out, aen_out, afolhas_out, qfolhas_out, aaf_out, false);
                    
                    soma_err = 0;
                    soma_err += ErroQuadrado(acaule8,acaule_out,qtd_comb);
                    soma_err += ErroQuadrado(aen8,aen_out,qtd_comb);
                    soma_err += ErroQuadrado(afolhas8,afolhas_out,qtd_comb);
                    soma_err += ErroQuadrado(qfolhas8,qfolhas_out,qtd_comb);
                    soma_err += ErroQuadrado(aaf8,aaf_out,qtd_comb);
                    
                    if (impr_it) System.out.println(" Soma de erros: " + soma_err);
               // }
                
                
//                if (ambiente.equals("SOL"))
//                    for (int i=0; i<qtd_comb; i++)
//                    {
//                        if (uc[i]==0)
//                        {
//                            acaule8[i] = 0;
//                            aen8[i] = 0;
//                            afolhas8[i] = 0;
//                            aaf8[i] = 0;
//                        }
//
//                    }
                
                
                double acaule8n[] = Normalizar(acaule8, Max(acaule8));
                double aen8n[] = Normalizar(aen8, Max(aen8));
                double afolhas8n[] = Normalizar(afolhas8, Max(afolhas8));
                double qfolhas8n[] = Normalizar(qfolhas8, Max(qfolhas8));
                double aaf8n[] = Normalizar(aaf8, Max(aaf8));
                
                double target[][] = new double[100][100];
                for (int i=0; i<qtd_comb; i++)
                {
                    target[i][0] = acaule8n[i];
                    target[i][1] = aen8n[i];
                    target[i][2] = afolhas8n[i];
                    target[i][3] = qfolhas8n[i];
                    target[i][4] = aaf8n[i];
                }
                
                
                
                //double vetor_saidas[][][] = new double[100][100][max_it];
                int id_menor_erro = 0;
                double menor_erro = 999999999;
                
                //NeuralNetwork rede_neural_otimizado = time_series;
//                ArrayList< ArrayList<ArrayList<NeuralNetwork.Neuron>> > hidden_layer = new ArrayList< ArrayList<ArrayList<NeuralNetwork.Neuron>> > (); // Camada(s) oculta(s)
//                ArrayList< ArrayList<NeuralNetwork.Neuron> > input_layer = new ArrayList< ArrayList<NeuralNetwork.Neuron> > (); // Camada de entrada
//                ArrayList< ArrayList<NeuralNetwork.Neuron> > output_layer = new ArrayList< ArrayList<NeuralNetwork.Neuron> > (); // Camada de saída
                
                ///dados_treinamento = time_series.GetDadoTreinamento();
                
                for (int i = 0; i < max_it; i++)
                {
                    // Aqui vai simular a rede para calcular o erro quadrático
                    for (int k = 0; k < time_series.QtdDadosTreinamento(); k++)
                    {
                        saida_rede[k] = time_series.Simular(dados_treinamento[k], tamanho_entrada);
                        
//                        hidden_layer.add(time_series.GetHiddenLayer());
//                        input_layer.add(time_series.GetInputLayer());
//                        output_layer.add(time_series.GetOutputLayer());
                    }
                    
                    impr_it = false;
                    
                    if (i>=0)
                    {
                    
                        if (impr_it) System.out.print("\n\n Iteracao " + i);

                        PrintSaidaRede(saida_rede, qtd_comb, acaule_out, aen_out, afolhas_out, qfolhas_out, aaf_out, false);
                        //PrintVetorVertical("Aaf", aaf8, aaf_out, qtd_comb);

                        // Soma dos erros quadráticos
                        soma_err = 0;

                        soma_err += ErroQuadrado(acaule8,acaule_out,qtd_comb);
                        soma_err += ErroQuadrado(aen8,aen_out,qtd_comb);
                        soma_err += ErroQuadrado(afolhas8,afolhas_out,qtd_comb);
                        soma_err += ErroQuadrado(qfolhas8,qfolhas_out,qtd_comb);
                        soma_err += ErroQuadrado(aaf8,aaf_out,qtd_comb);

                        if (impr_it) System.out.print(" Soma de erros: " + soma_err);
                        
                        if (soma_err<menor_erro) menor_erro = soma_err;
                    }
                    
                    for (int j=0; j<time_series.QtdDadosTreinamento(); j++)
                    {
                        double[] input = time_series.GetDadoTreinamento(j);
                        time_series.train(time_series.GetDadoTreinamento(j), target[j], tamanho_entrada);
                    }
                    
		}
                
                PrintSaidaRede(saida_rede, qtd_comb, acaule_out, aen_out, afolhas_out, qfolhas_out, aaf_out, true);
                
                //rede_neural_otimizado.SetConfigNetwork(input_layer.get(id_menor_erro), hidden_layer.get(id_menor_erro), output_layer.get(id_menor_erro));
                System.out.println("\n MENOR ERRO >>> " + menor_erro);
                
                //time_series = ann_aux[0];
                
                System.out.println("\n\n Dia X");
                PrintVetor("Dia X", diaX, qtd_comb);
                
                
                PrintVetor("\nA.Caule", DesNormalizar(acaule8n,Max(acaule8),0), qtd_comb);
                PrintVetor("N.Metameros", DesNormalizar(aen8n,Max(aen8),0), qtd_comb);
                PrintVetor("N.Folhas", DesNormalizar(afolhas8n,Max(afolhas8),0), qtd_comb);
                PrintVetor("Q.Folhas", DesNormalizar(qfolhas8n,Max(qfolhas8),0), qtd_comb);
                PrintVetor("A.Foliar", DesNormalizar(aaf8n,Max(aaf8),0), qtd_comb);
                
                
               // dados_treinamento = time_series.GetDadoTreinamento();
                
                
                
                System.out.println("\n\n Saida da rede");
                
                
                for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
                    saida_rede[i] = time_series.Simular(dados_treinamento[i], tamanho_entrada);
                
                PrintSaidaRede(saida_rede, qtd_comb, acaule_out, aen_out, afolhas_out, qfolhas_out, aaf_out, false);
                    
                
              System.out.println("\n\n Fim de treinamento\n");
              
              
              if (impr_tab)
              {
                  acaule_saida = acaule_out;
                  aen_saida = aen_out;
                  afolhas_saida = afolhas_out;
                  qfolhas_saida = qfolhas_out;
                  aaf_saida = aaf_out;
                  
                  double uc2[] = {};
                  
                  SincronizarParametrosMorfologicos(qtd_comb, uc); // Brógui duh capim-naremus, kogumelu-loko, áneg
                  
                  PrintVetorVertical("Acaule", acaule8, acaule_saida, qtd_comb);
                  PrintVetorVertical("Aen", aen8, aen_saida, qtd_comb);
                  PrintVetorVertical("Afolhas", afolhas8, afolhas_saida, qtd_comb);
                  PrintVetorVertical("Qfolhas", qfolhas8,qfolhas_saida, qtd_comb);
                  PrintVetorVertical("Aaf", aaf8, aaf_saida, qtd_comb);
              }
              
              System.out.println("\n\n Erro acaule:\t" + ErroQuadrado(acaule8,acaule_out,qtd_comb) + " Bias: " + Bias(acaule8,acaule_out,qtd_comb));
              System.out.println(" Erro aen:\t" + ErroQuadrado(aen8,aen_out,qtd_comb) + " Bias: "  + Bias(aen8,aen_out,qtd_comb));
              System.out.println(" Erro afolhas:\t" + ErroQuadrado(afolhas8,afolhas_out,qtd_comb) + " Bias: "  + Bias(afolhas8,afolhas_out,qtd_comb));
              System.out.println(" Erro qfolhas:\t" + ErroQuadrado(qfolhas8,qfolhas_out,qtd_comb) + " Bias: "  + Bias(qfolhas8,qfolhas_out,qtd_comb));
              System.out.println(" Erro aaf:\t" + ErroQuadrado(aaf8,aaf_out,qtd_comb) + " Bias: "  + Bias(aaf8,aaf_out,qtd_comb));
              
              System.out.println("\n MENOR ERRO TOTAL DO TREINAMENTO>>> " + menor_erro);
              
              // 
              
              System.out.println("\n\n INICIO DE SIMULAÇÃO");
              
                double tmin_teste[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
                double tmax_teste[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
                double nl_teste[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
                double rainfall_teste[] = {5.43,3.10,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.50,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,2.96,4.49,5.92,7.17,8.25};
                double gdd_teste[] = {254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};
                
                for (int i=0; i<25; i++)
                {
                    //tmin_teste[i] -= 0.1;
                    //tmax_teste[i] -= 0.1;
                    //nl_teste[i] += 0.1;
                    //rainfall_teste[i] -= 0.1;
                    //gdd_teste[i] -= 0.1;
                }
                
                
                // Setar dados treinamento
                
                // Pode-se alterar aqui!
                tmin_teste = Normalizar(tmin_teste,Max(tmin_));
                tmax_teste = Normalizar(tmax_teste,Max(tmax_));
                nl_teste= Normalizar(nl_teste,Max(nl_));
                rainfall_teste = Normalizar(rainfall_teste,Max(rainfall_));
                gdd_teste = Normalizar(gdd_teste,Max(gdd_));
                
                for (int j=0; j<25; j++)
                {
                    dados_treinamento[j][0] = tmin_teste[j];
                    dados_treinamento[j][1] = tmax_teste[j];
                    dados_treinamento[j][2] = nl_teste[j];
                    dados_treinamento[j][3] = rainfall_teste[j];
                    dados_treinamento[j][4] = gdd_teste[j];
                    
                    dados_treinamento[j][5] = uc[j];
                 }
                
//                System.out.println("\n\n Print de dados (teste)");
//                for (int i = 0; i < rede_neural_otimizado.QtdDadosTreinamento(); i++)
//                    saida_rede[i] = rede_neural_otimizado.Simular(dados_treinamento[i], tamanho_entrada);
//                
//                System.out.print("\nA.Caule");
//                for (int j=0; j<qtd_comb; j++)
//                    System.out.print(" ," + saida_rede[j][0]*Max(acaule8));
//                
//                System.out.print("\nN.Metameros");
//                for (int j=0; j<qtd_comb; j++)
//                    System.out.print(" ," + saida_rede[j][1]*Max(aen8));
//                
//                System.out.print("\nN.Folhas");
//                for (int j=0; j<qtd_comb; j++)
//                    System.out.print(" ," + saida_rede[j][2]*Max(afolhas8));
//              
//                System.out.print("\nQ.Folhas");
//                for (int j=0; j<qtd_comb; j++)
//                    System.out.print(" ," + saida_rede[j][3]*Max(qfolhas8));
//                
//                System.out.print("\nA.Foliar");
//                for (int j=0; j<qtd_comb; j++)
//                    System.out.print(" ," + saida_rede[j][4]*Max(aaf8));
//                
                
                
                System.out.println();
                
	}
    
    
    
    
    
    
    
    
    
    
}
