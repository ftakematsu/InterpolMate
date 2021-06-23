/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;
import interpolmates1.NeuralNetwork;





/**
 *
 * @author Fabio
 */
public class TesteSeries {
    
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
    public static double[] Normalizar(double v[])
    {
        double n[] = new double[v.length];
        for (int i=0; i<v.length; i++)
            n[i] = v[i]/Max(v);
        return n;
    };

    public static double[] DesNormalizar(double v[], double xmax, double xmin)
    {
        double n[] = new double[v.length];
        for (int i=0; i<v.length; i++)
            n[i] = v[i]*(xmax-xmin) + xmin;
        return v;
    };

    
    public static void main(String[] args){
                
                final int max_it = 10000;
                
                int qtd_comb = 6;
                int tamanho_entrada = 3;
                int tamanho_camada_oculta = 3;
                int tamanho_saida = 1;
                int qtd_camada_oculta = 1;
                double alfa = 0.5;
                
                double[][] dados_treinamento = new double[100][100]; // [quantidade de combinações utilizadas no treinamento][tamanho da entrada]
                
                double diaX[] = {0,30,60,90,105,120,135,150,165,180,210,225,240,270,285,300,315,330,360,390,420,450,480,495,510,525,540,570,585,600,615,630,645,660,675,690,705,720};
                double tmin[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,11.17,13.00,12.90,16.68,17.42,17.34,17.21,16.32,11.71,14.52};
                double tmax[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,22.05,23.82,22.97,27.26,28.02,28.42,27.90,25.00,20.16,23.29};
                double nl[] = {13.60,13.40,12.82,12.06,11.22,10.50,10.16,10.28,10.68,11.77,12.57,13.08,13.56,13.51,13.00,12.24,11.35,10.60,10.26,10.28,11.05,11.85,12.69,13.34,13.52};
                
                //double saida_param[] = {0,0,0,16,11.66666667,14.5,9.5,16.5,13.16666667,14,3.833333333,0,0,0,0,7,17.16666667,3.833333333,1,0,2.166666667,10.16666667,0,0.833333333,0};
                double saida_param[] = {0,0,0,4.87,23.85,27.09,7.34,21.33,12.49,29.99,8.91,0,0,0,0.47,2.271,14.149,19.94,0.7,0,5.58,4.5,2.28,2.16,0};

                // Valores normalizados
                double ntmin[] = Normalizar(tmin);
                double ntmax[] = Normalizar(tmax);
                double nnl[] = Normalizar(nl);
                double nsaida_param[] = Normalizar(saida_param);
                        
                // Setar dados treinamento
                for (int j=0; j<25; j++)
                {
                    dados_treinamento[j][0] = ntmin[j];
                    dados_treinamento[j][1] = ntmax[j];
                    dados_treinamento[j][2] = nnl[j];
                }
                
                qtd_comb = 25;
                tamanho_entrada = 3;
                tamanho_camada_oculta = 4;
                tamanho_saida = 1;
                qtd_camada_oculta = 1;
                alfa = 0.2;
                NeuralNetwork time_series = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, nsaida_param);
                
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
                
                dados_treinamento = time_series.GetDadoTreinamento();
                System.out.println("\n\n Saida da rede");
                for (int i = 0; i < time_series.QtdDadosTreinamento(); i++)
                {
                    double noutput[] = time_series.Simular(dados_treinamento[i], tamanho_entrada);
                    
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(", " + noutput[j]*Max(saida_param));
		}
                
                System.out.println("\n\n");
                
                
                double tmin_teste[] = {12.57,9.94,10.18,8.61,14.85,15.22,16.98,17.45,18.44,16.29,16.19,12.78,9.45,12.15,8.85,10,10,10,10,10,10,10,10,10,10};
                double tmax_teste[] = {19.57,18.87,21.57,20.17,24.84,24.43,27.70,28.29,28.30,27.02,27.73,20.67,16.82,20.05,17.54,15,15,15,15,15,15,15,15,15,15};

                
                double[][] dados_treinamento2 = new double[100][100];
                // Setar dados treinamento
                // Pode-se alterar aqui!
                tmin_teste = Normalizar(tmin_teste);
                tmax_teste = Normalizar(tmax_teste);
                
                for (int j=0; j<25; j++)
                {
                    dados_treinamento2[j][0] = tmin_teste[j];
                    dados_treinamento2[j][1] = tmax_teste[j];
                    dados_treinamento2[j][2] = nnl[j];
                }
                
                
                
                // Print de dados (teste)
                System.out.println("\n\n Print de dados (teste)");
                for (int i = 0; i < 25; i++)
                {   
                    double noutput[] = time_series.Simular(dados_treinamento2[i], tamanho_entrada);
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print("," + noutput[j]*Max(saida_param));
		}
                
                
//                System.out.println("\n\n Teste simulação");
//                double input[] = {0.1,0,1,0.5};
//                for (int j = 0; j < tamanho_saida; j++)
//                        System.out.print(" " + time_series.Simular(input, tamanho_entrada)[j]);
//                
                System.out.println();
                
	}
}
