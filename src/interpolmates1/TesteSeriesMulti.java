/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;
import interpolmates1.NeuralNetwork;


public class TesteSeriesMulti {
    
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
    
    
    
    public static void main(String[] args){
                
                final int max_it = 10000;
                
                int qtd_comb = 25;
                int tamanho_entrada = 5;
                int tamanho_saida = 1;
                int tamanho_camada_oculta = tamanho_entrada+3;
                int qtd_camada_oculta = 1;
                double alfa = 0.4;
                
                
                double[][] dados_treinamento = new double[100][100]; // [quantidade de combinações utilizadas no treinamento][tamanho da entrada]
                
                
                double diaX[] = {0,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
                
                double tmin_[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
                double tmax_[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
                double nl_[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
                double rainfall_[] = {5.43,3.10,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.50,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,2.96,4.49,5.92,7.17,8.25};
                double gdd_[] = {254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};
                double uc[] = {0,0,0,0,0.2,0.2,0.2,0.2,0.4,0.4,0.4,0.0,0.0,0.0,0.0,0.6,0.6,0.6,0,0,0.8,0.8,0.8,0,0};
                double ano[] = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9};
                
                
                //// DADOS DAS 8 PLANTAS
                double acaule8[] = {0,0,0,5.733333333,22,26.2,12.01666667,27.26666667,16.43333333,19.55,1.683333333,0,0,0,0,0.216666667,8.683333333,6.616666667,0.433333333,0,0.166666667,0,0,0,0};
                double aen8[] = {0,0,0,16,11.66666667,14.5,9.5,16.5,13.16666667,14,3.833333333,0,0,0,0,7,17.16666667,3.833333333,1,0,2.166666667,10.16666667,0,0.833333333,0};
                double afolhas8[] = {0,0.5,0.666666667,4.833333333,6,4.666666667,0.166666667,0.666666667,1.666666667,2.5,0.333333333,0,-6,-1,0,-1.333333333,8.5,1.833333333,-0.666666667,-0.166666667,0.166666667,5.833333333,0.5,0.333333333,0};
                double aaf8[] = {0,0.368333333,0.355,80.34166667,350.5466667,614.6216667,37.425,17.11833333,51.29333333,136.45,21.66666667,0,-239.1566667,-15.76666667,0,-222.0716667,-16.58166667,363.1766667,-86.90166667,0.366666667,1.415,144.08,31.51166667,0,0};
                double qfolhas8[] = {0,0,2,7.166666667,4.166666667,2,0.166666667,3.333333333,0.166666667,2.333333333,0.166666667,0,6,1,0,7.166666667,1.833333333,2.833333333,0.833333333,0,0,7.666666667,0,0,0};
                
                
                //// TODAS
                double acaule[] = {0,0,0,4.87,23.85,27.09,7.34,21.33,12.49,29.99,8.91,0,0,0,0.47,2.271,14.149,19.94,0.7,0,5.58,4.5,2.28,2.16,0};
                double aen[] = {0,0,0,16,11.66666667,14.5,9.5,16.5,13.16666667,14,3.833333333,0,0,0,0,7,17.16666667,3.833333333,1,0,2.166666667,10.16666667,0,0.833333333,0};
                double afolhas[] = {0,0.5,0.666666667,4.833333333,6,4.666666667,0.166666667,0.666666667,1.666666667,2.5,0.333333333,0,-6,-1,0,-1.333333333,8.5,1.833333333,-0.666666667,-0.166666667,0.166666667,5.833333333,0.5,0.333333333,0};
                double aaf[] = {0,0.368333333,0.355,80.34166667,350.5466667,614.6216667,37.425,17.11833333,51.29333333,136.45,21.66666667,0,-239.1566667,-15.76666667,0,-222.0716667,-16.58166667,363.1766667,-86.90166667,0.366666667,1.415,144.08,31.51166667,0,0};
                double qfolhas[] = {0,0,2,7.166666667,4.166666667,2,0.166666667,3.333333333,0.166666667,2.333333333,0.166666667,0,6,1,0,7.166666667,1.833333333,2.833333333,0.833333333,0,0,7.666666667,0,0,0};
                
                double acaule8_MO_MA[] = {0,0,0,1.9,16.05,18.41666667,3.75,16.56666667,10.88333333,22.25,11.4,0,0,0,0,1,7.55,10.78333333,0.416666667,0,1.383333333,4.65,0.9,0.116666667,0};
                
                
                double matriz_param[] = acaule8; // Mudar aqui
                
                double saida_param[];

                // Valores normalizados
                double tmin[] = Normalizar(tmin_, Max(tmin_));
                double tmax[] = Normalizar(tmax_, Max(tmax_));
                double nl[] = Normalizar(nl_, Max(nl_));
                double rainfall[] = Normalizar(rainfall_, Max(rainfall_));
                double gdd[] = Normalizar(gdd_, Max(gdd_));
                double diaXn[] = Normalizar(diaX,Max(diaX));
                
                double aen_teste[] = Normalizar(aen,Max(aen));
                
                // Setar dados treinamento
                int t = 0;
                double dia_aux[] = new double[25];
                for (int j=0; j<qtd_comb; j++)
                {
                    //if (uc[j]!=0)
                    //{
                        dia_aux[t] = diaX[j];
                        dados_treinamento[t][0] = tmin[j];
                        dados_treinamento[t][1] = tmax[j];
                        dados_treinamento[t][2] = nl[j];
                        dados_treinamento[t][3] = rainfall[j];
                        dados_treinamento[t][4] = gdd[j];
                        dados_treinamento[t][5] = uc[j];
                        
                        if (uc[j]!=0)
                            matriz_param[t] = acaule8_MO_MA[j];
                        else matriz_param[t] = 0;
                        t++;
                    //}
                }
                
                saida_param = Normalizar(matriz_param, Max(matriz_param));
                
                //qtd_comb = t;
               
                NeuralNetwork time_series = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, saida_param);
                
                //System.out.println(">>>" + time_series.QtdDadosTreinamento());
                
                
                for (int i = 0; i <= max_it; i++)
                {
                    for (int j=0; j<time_series.QtdDadosTreinamento(); j++)
                    {
                        double[] input = time_series.GetDadoTreinamento(j);
                        //double[] target = new double[]{((int)input[0]+(int)input[1])%2};
                        double[] target = {time_series.GetSaidaEsperada(j)};
                        time_series.train(time_series.GetDadoTreinamento(j), target, tamanho_entrada);
                    }
		}
                System.out.println("Fim de treinamento");
                
                
                
                System.out.println("\n\n Dia X");
                //qtd_comb = 25;
                for (int i = 0; i < qtd_comb; i++)
                {
                        System.out.print(", " + diaX[i]);
		}
                
                dados_treinamento = time_series.GetDadoTreinamento();
                System.out.println("\n\n Saida parametro");
                for (int i = 0; i < qtd_comb; i++)
                {
                        System.out.print(", " + matriz_param[i]);
		}
                
                System.out.println("\n\n Saida da rede");
                for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
                {
                    double noutput[] = time_series.Simular(dados_treinamento[i], tamanho_entrada);
                    
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(", " + noutput[j]*Max(matriz_param));
		}
                
//                for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
//                {
//                    double input[] = time_series.GetDadoTreinamento(i);
//		      System.out.print("\nClassificando:");
//                    for (int j = 0; j < tamanho_entrada; j++)
//                        System.out.print("  " + input[j]);
//                    System.out.print("\tOutput:");
//                    
//                    double noutput[] = time_series.Simular(dados_treinamento[i], tamanho_entrada);
//                    
//                    for (int j = 0; j < tamanho_saida; j++)
//                        System.out.print(" " + noutput[j]*Max(saida_param));
//		}
                
                // Originais
//                double tmin_teste[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
//                double tmax_teste[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
//                double nl_teste[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
//                double rainfall_teste[] = {5.43,3.10,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.50,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,2.96,4.49,5.92,7.17,8.25};
//                double gdd_teste[] = {254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};

                
                double tmin_teste[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
                double tmax_teste[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
                double nl_teste[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
                double rainfall_teste[] = {5.43,3.10,1.52,1.76,3.48,5.73,11.57,4.85,4.28,3.73,3.50,5.73,2.49,3.48,1.52,4.07,4.69,5.33,0.63,2.61,2.96,4.49,5.92,7.17,8.25};
                double gdd_teste[] = {254.50,282.10,326.20,281.70,460.10,444.80,398.80,625.40,384.60,699.60,508.80,257.35,268.50,332.90,254.20,359.80,456.00,388.05,322.50,779.80,643.70,509.10,501.15,338.95,194.65};
                double aen_teste2[] = aen8;
                
                for (int i=0; i<25; i++)
                {
                    tmin_teste[i] += 5;
                    tmax_teste[i] += 5;
                    nl_teste[i] -= 1;
                    rainfall_teste[i] += 2;
                    gdd_teste[i] += 50;
                    //aen_teste2[i] -= 1;
                }
                
                
                // Setar dados treinamento
                
                // Pode-se alterar aqui!
                tmin_teste = Normalizar(tmin_teste,Max(tmin_));
                tmax_teste = Normalizar(tmax_teste,Max(tmax_));
                nl_teste= Normalizar(nl_teste,Max(nl_));
                rainfall_teste = Normalizar(rainfall_teste,Max(rainfall_));
                gdd_teste = Normalizar(gdd_teste,Max(gdd_));
                aen_teste2 = Normalizar(aen_teste2,Max(aen8));
                
                for (int j=0; j<25; j++)
                {
                    dados_treinamento[j][0] = tmin_teste[j];
                    dados_treinamento[j][1] = tmax_teste[j];
                    dados_treinamento[j][2] = nl_teste[j];
                    dados_treinamento[j][3] = rainfall_teste[j];
                    dados_treinamento[j][4] = gdd_teste[j];
                    dados_treinamento[j][5] = uc[j];
                 }
                
                
                
                // Print de dados (teste)
                System.out.println("\n\n Print de dados (teste)");
                for (int i = 0; i < qtd_comb; i++)
                {   
                    double noutput[] = time_series.Simular(dados_treinamento[i], tamanho_entrada);
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(", " + noutput[j]*Max(aen8));
		}
                
                // Benchmarking de um dado
                double tmin_t = 18.57;
                double tmax_t = 26.57;
                double nl_t = 13.60;
                double rain_t = 15.43;
                double gdd_t = 400.50;
                
                double teste_entrada[] = {tmin_t/Max(tmin_), tmax_t/Max(tmax_), nl_t/Max(nl_), rain_t/Max(rainfall_), gdd_t/Max(gdd_), 0.2};
                System.out.println("\n\nSAIDA: " + time_series.Simular(teste_entrada, tamanho_entrada)[0]*Max(matriz_param));
                
//                System.out.println("\n\n Teste simulação");
//                double input[] = {0.1,0,1,0.5};
//                for (int j = 0; j < tamanho_saida; j++)
//                        System.out.print(" " + time_series.Simular(input, tamanho_entrada)[j]);
//                
                System.out.println();
                
	}
}
