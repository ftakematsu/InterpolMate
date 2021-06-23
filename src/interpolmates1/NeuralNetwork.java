package interpolmates1;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class NeuralNetwork{
	private double learningrate; // Taxa de aprendizagem
        private double momentum;
	private ArrayList<ArrayList<Neuron>> hidden; // Camada(s) oculta(s)
	private ArrayList<Neuron> input; // Camada de entrada
	private ArrayList<Neuron> output; // Camada de saída
        
        // Índices para mapear os neurônios das camadas 
	private HashMap<Neuron,Integer> inputIndex, outputIndex;
	private HashMap<Integer,HashMap<Neuron,Integer>> hiddenIndex;
	
        private int qtd_dados_treinamento;
        private double dados_treinamento[][];
        private double saida_esperada[];
        
        public ArrayList<Neuron> GetInputLayer()
        {
            return input;
        };
        
        public ArrayList<ArrayList<Neuron>> GetHiddenLayer()
        {
            return hidden;
        };
        
        public ArrayList<Neuron> GetOutputLayer()
        {
            return output;
        };
        
        public void SetConfigNetwork(ArrayList<Neuron> in, ArrayList<ArrayList<Neuron>> hid, ArrayList<Neuron> out)
        {
            input = in;
            hidden = hid;
            output = out;
        };
        
        public void SetMomentum(double m)
        {
            this.momentum = m;
        };
        
        
        /**
	 * @param Tamanho da camada de entrada
	 * @param Tamanho das camadas ocultas
	 * @param Tamanho da camada de saída
	 * @param Número de camadas ocultas
	 * @param Taxa de aprendizagem (alfa)
	 */
	public NeuralNetwork(int input, int hidden, int output, int numberOfHiddenLayers, double learningrate, int qtd_dados_trein, double dados_trein[][], double saida_esp[]){
		this.hiddenIndex = new HashMap<Integer,HashMap<Neuron,Integer>>();
		this.inputIndex = new HashMap<Neuron,Integer>();
		this.outputIndex = new HashMap<Neuron,Integer>();
		
		this.hidden = new ArrayList<ArrayList<Neuron>>();
		this.input = new ArrayList<Neuron>();
 		this.output = new ArrayList<Neuron>();
 		this.learningrate = learningrate;
                
                dados_treinamento = new double[100][100];
                saida_esperada = new double[100];
                
                this.qtd_dados_treinamento = qtd_dados_trein;
                
                this.dados_treinamento = dados_trein;
                this.saida_esperada = saida_esp;
                
 		//Input
 		for(int i = 1; i <= input; i++){
 			this.input.add(new Neuron(false));
 		}
 		for(Neuron i : this.input){
 			this.inputIndex.put(i, this.input.indexOf(i));
 		}
 		
 		//Hidden
 		for(int i = 1; i <= numberOfHiddenLayers; i++){
 			ArrayList<Neuron> a = new ArrayList<Neuron>();
 			for(int j = 1; j <= hidden; j++){
 				a.add(new Neuron(true));
 			}
 			this.hidden.add(a);
 		}
 		for(ArrayList<Neuron> a : this.hidden){
 			HashMap<Neuron,Integer> put = new HashMap<Neuron,Integer>();
 			for(Neuron h : a){
 				put.put(h, a.indexOf(h));
 			}
 			this.hiddenIndex.put(this.hidden.indexOf(a), put);
 		}
 		
 		//Output
 		for(int i = 1; i <= output; i++){
 			this.output.add(new Neuron(true));
 		}
 		for(Neuron o : this.output){
 			this.outputIndex.put(o, this.output.indexOf(o));
 		}
 		
 		
 		for(Neuron i : this.input){
 			for(Neuron h : this.hidden.get(0)){
 				i.SetDadosNeuron(h, Math.random()*(Math.random() > 0.5 ? 1 : -1));
 			}
 		}
 		for(int i = 1; i < this.hidden.size(); i++){
 			for(Neuron h : this.hidden.get(i-1)){
 				for(Neuron hto : this.hidden.get(i)){
 					h.SetDadosNeuron(hto, Math.random()*(Math.random() > 0.5 ? 1 : -1));
 				}
 			}
 		}
 		for(Neuron h : this.hidden.get(this.hidden.size()-1)){
 			for(Neuron o : this.output){
 				h.SetDadosNeuron(o, Math.random()*(Math.random() > 0.5 ? 1 : -1));
 			}
 		}
 	}
        
        public int QtdDadosTreinamento()
        {
            return qtd_dados_treinamento;
        }
        
        public void SetDadosTreinamento(int t)
        {
            this.qtd_dados_treinamento = t;
        }
	
        public double[][] GetDadoTreinamento()
        {
            return this.dados_treinamento;
        }
        
        public double[] GetDadoTreinamento(int j)
        {
            return this.dados_treinamento[j];
        }
        
        public double[] GetSaidaEsperada()
        {
            return this.saida_esperada;
        }
        
        public double GetSaidaEsperada(int j)
        {
            return this.saida_esperada[j];
        }
        
        
        public void AddDadoTreinamento(double dado[], double saida)
        {
            this.dados_treinamento[qtd_dados_treinamento] = dado;
            this.saida_esperada[qtd_dados_treinamento] = saida;
            this.qtd_dados_treinamento++;
        }
        
        public void PrintDadosSaida()
        {
            for (int i=0; i<qtd_dados_treinamento; i++)
                System.out.println(saida_esperada[i]);
        }
        
        
        public class Neuron {
		private boolean ativado;
		private int antalTriggered = 0, qtd_neuronios = 0;
		private double neuron_input = 0, neuron_output = 0, sum;
		private ArrayList<Synaps> lista_sinapses;

                
		public Neuron(boolean act){
			this.ativado = act;
			this.lista_sinapses = new ArrayList<Synaps>();
		}
		
		public void SetDadosNeuron(Neuron e, double peso){
			Synaps n = new Synaps(e, peso);
			this.lista_sinapses.add(n);
			e.LigaNeuronios();
		}
		
                
		public double getNeuronInput(){
			return this.neuron_input;
		}
		
		public double getNeuronOutput(){
			return this.neuron_output;
		}
		
		public void setInputNeuron(double input){
			this.antalTriggered++;
			this.sum = sum+input;
			if(this.antalTriggered >= this.qtd_neuronios)
                        {
				this.neuron_input = sum;
				ExecutaNeuronio();
			}
		}
		
                // Testa (executa) Neurônio 
		public void ExecutaNeuronio()
                {
			for(Synaps n : this.lista_sinapses)
                        {
				if(this.ativado) // Se o neurônio for indicado como ativado, multiplica a função de ativação
                                {
                                    // Entrada do neuronio atual = FuncaoAtivacao(soma_pesos_atual)*Peso(neuronio_atual)
                                    n.getNeuronio().setInputNeuron(NeuralNetwork.FuncaoAtivacao(this.sum)*n.getPesoSynaps());
				}
                                else // Caso contrário, não aplica a função de ativação
                                { 
					n.getNeuronio().setInputNeuron(this.sum*n.getPesoSynaps());
				}
			}
			if(this.ativado)
                        {
				this.neuron_output = NeuralNetwork.FuncaoAtivacao(this.sum);	
			}
			else{
				this.neuron_output = this.sum;
			}
			this.sum = 0.0;
			this.antalTriggered = 0;
		}
		
		public void LigaNeuronios(){
			this.qtd_neuronios++;
		}
		
		public ArrayList<Synaps> getListaSinapses(){
			return this.lista_sinapses;
		}
		
		public String toString(){
			String retur = this.hashCode()+" med "+this.lista_sinapses.size()+" forbindelser.";
			return retur;
		}
	}
	
	
	/**
	 * 
	 * Private class to connect neurons
	 *
	 */
	private class Synaps {
		private Neuron neuronio;
		private double peso;
                private double peso_ant;
                private double peso_ant_ant;
		public Synaps(Neuron til, double synaps){
			this.neuronio = til;
			this.peso = synaps;
                        this.peso_ant_ant = 0;
                        this.peso_ant = 0;
		}
		
		public double getPesoSynaps(){
			return this.peso;
		}
                public double getPesoAnt(){
			return this.peso_ant;
		}
                public double getPesoAntAnt(){
			return this.peso_ant_ant;
		}
		
		public void setPesoSynaps(double v){
			this.peso = v;
		}
                public void setPesoAnt(double v){
			this.peso_ant = v;
		}
                public void setPesoAntAnt(double v){
			this.peso_ant_ant = v;
		}
                
                public double DeltaPeso()
                {
                    return (peso_ant-peso_ant_ant);
                };
		
		public Neuron getNeuronio(){
			return this.neuronio;
		}
		
		public String toString(){
			return peso+"";
		}
	}
        
        
       
        
	/**
	 * @param exp - saída esperada
	 */
	private void Backpropagate(double[] exp)
        {
		double[] error = new double[this.output.size()];
		//Hidden->Output
		int c = 0;
                
                // Calcula o erro para cada saída
		for(Neuron o : this.output)
                {
                    // Erro = saida_rede * (1 - saida_rede) * (saida_esperada - saida_rede)
                    error[c] = o.getNeuronOutput()*(1.0 - o.getNeuronOutput())*(exp[this.outputIndex.get(o)]-o.getNeuronOutput());
                    c++;
		}
                
                // Uma vez o erro de cada saída calculada, retropropagar o erro para as camadas anteriores
		for(Neuron h : this.hidden.get(this.hidden.size()-1)) // Para camadas intermediárias
                {
                    Synaps s_ant = h.getListaSinapses().get(0);
                    // Percorre cada neurônio (e suas respectivas sinapses)
                    for (Synaps s : h.getListaSinapses())
                    {
                        s.setPesoAntAnt(s.getPesoAnt());
			double v = s.getPesoSynaps();
                        s.setPesoAnt(v);
                        // s = peso_sinapt_atual + alfa*saida_h*erro;
			//s.setPesoSynaps(v + this.learningrate*h.getNeuronOutput()*error[this.outputIndex.get(s.getNeuronio())]);
                        s.setPesoSynaps(v + this.learningrate*h.getNeuronOutput()*error[this.outputIndex.get(s.getNeuronio())] + this.momentum*s.DeltaPeso());
                    }
		}
                
		double[] oerror = error.clone();
                
		error = new double[this.hidden.get(0).size()];
		
                //Hidden->Hidden
		for(int i = this.hidden.size()-1; i > 0; i--){
			c = 0;
			for(Neuron h : this.hidden.get(i))
                        {
                            // 
				double p = h.getNeuronOutput()*(1-h.getNeuronOutput());
				double k = 0;
				for(Synaps s : h.getListaSinapses()){
					if(i == this.hidden.size()-1){
						k = k+oerror[this.outputIndex.get(s.getNeuronio())]*s.getPesoSynaps();
					}
					else{
						k = k+error[this.hiddenIndex.get(i+1).get(s.getNeuronio())]*s.getPesoSynaps();
					}
				}
				error[c] = p*k;
				c++;
			}
			for(Neuron h : this.hidden.get(i-1))
                        {
				for(Synaps s : h.getListaSinapses())
                                {
                                    s.setPesoAntAnt(s.getPesoAnt());
                                    double v = s.getPesoSynaps();
                                    s.setPesoAnt(v);
                                    int index = this.hiddenIndex.get(i).get(s.getNeuronio());
                                    s.setPesoSynaps(v + this.learningrate*error[index]*h.getNeuronInput() + this.momentum*s.DeltaPeso());
				}
			}
		}
		
                //Input->Hidden
		c = 0;
		double[] t = error.clone();
		for(Neuron h : this.hidden.get(0)) // Para camada de entrada - intermediária
                { 
			double p = h.getNeuronOutput()*(1.0-h.getNeuronOutput());
			double k = 0;
			for(Synaps s : h.getListaSinapses()){
				if(this.hidden.size() == 1){
					k = k+s.getPesoSynaps()*oerror[this.outputIndex.get(s.getNeuronio())];
				}
				else{
					k = k+s.getPesoSynaps()*error[this.hiddenIndex.get(1).get(s.getNeuronio())];
				}
			}
			t[c] = k*p;
			c++;
		}
                
		for(Neuron i : this.input) // Para camada de entrada
                {
			for(Synaps s : i.getListaSinapses())
                        {
                            s.setPesoAntAnt(s.getPesoAnt());
                            double v = s.getPesoSynaps();
                            s.setPesoAnt(v);
                            s.setPesoSynaps(v + this.learningrate*t[this.hiddenIndex.get(0).get(s.getNeuronio())]*i.getNeuronInput()  + this.momentum*s.DeltaPeso());
			}
		}
	};
        
        
	
        // Ativação através da função Sigmóide
	private static double FuncaoAtivacao(double x){
		return 1.0/(1+Math.pow(Math.E, -x));
                //return (Math.pow(Math.E, x) - Math.pow(Math.E, -x))/(Math.pow(Math.E, x) + Math.pow(Math.E, -x));
	}
	
	/**
	 * 
	 * @param input Input to be classified
	 * @return The classification of the input
	 */
	public double[] Simular(double[] input, int len) {
		for(int i = 0; i < len; i++){
			this.input.get(i).setInputNeuron(input[i]);
		}
		double[] r = new double[this.output.size()];
		for(int i = 0; i < r.length; i++){
			r[i] = this.output.get(i).getNeuronOutput();
		}
		return r;
	}
	
	public double[] map(double[] input, int len)
        {
		for(int i = 0; i < len; i++){
			this.input.get(i).setInputNeuron(input[i]);
		}
		double[] retur = new double[this.output.size()];
		for(int i = 0; i < retur.length; i++){
			retur[i] = this.output.get(i).getNeuronOutput();
		}
		return retur;
	}	
	
	public void train(double[] input, double[] target, int len){
		for(int i = 0; i < len; i++){
			this.input.get(i).setInputNeuron(input[i]);
		}
		Backpropagate(target);
	}
        
        
        private static void PrintVetor(double[] input)
        {
            for (int i=0; i<input.length; i++)
            {
                System.out.print(input[i] +" ");
            }
            System.out.println();
        }
        
        
        
       
        
        
        /**
	 * Example of the network solving the XOR-problem
	 */
	public static void main(String[] args){
                
                final int max_it = 1000;
                
                int qtd_comb = 4;
                final int tamanho_entrada = 2;
                final int tamanho_camada_oculta = 4;
                final int tamanho_saida = 3;
                final int qtd_camada_oculta = 2;
                final double alfa = 0.2;
                final double momentum = 0.5;
                
                double[][] dados_treinamento = new double[100][100]; // [quantidade de combinações utilizadas no treinamento][tamanho da entrada]
                
                // Definindo o conjunto de dados para treinamento
                dados_treinamento[0][0] = 0; dados_treinamento[0][1] = 0;
		dados_treinamento[1][0] = 1; dados_treinamento[1][1] = 0;
		dados_treinamento[2][0] = 0; dados_treinamento[2][1] = 1;
		dados_treinamento[3][0] = 1; dados_treinamento[3][1] = 1;
                
                double[] saida_esperada = new double[100];
                
                saida_esperada[0] = 0;
                saida_esperada[1] = 1;
                saida_esperada[2] = 1;
                saida_esperada[3] = 0;
                
                
		NeuralNetwork mlp = new NeuralNetwork(tamanho_entrada, tamanho_camada_oculta, tamanho_saida, qtd_camada_oculta, alfa, qtd_comb, dados_treinamento, saida_esperada);
                mlp.SetMomentum(momentum);
                
		Random r = new Random();
                
                double target[][] = new double[100][100];
                target[0][0] = 0; target[0][1] = 0.4; target[0][2] = 0.4;
                target[1][0] = 1; target[1][1] = 0.9; target[1][2] = 0.6; 
                target[2][0] = 1; target[2][1] = 0.1; target[2][2] = 0.1;
                target[3][0] = 0; target[3][1] = 0.3; target[3][2] = 0.3;
                
                for (int i = 0; i <= max_it; i++)
                {
                    for (int j=0; j<mlp.QtdDadosTreinamento(); j++)
                    {
                        double[] input = mlp.GetDadoTreinamento(j);
                        //double[] target = new double[]{((int)input[0]+(int)input[1])%2};
                        //double[] target = {mlp.GetSaidaEsperada(j)};
                        mlp.train(mlp.GetDadoTreinamento(j), target[j], tamanho_entrada);
                    }
                    
                    
                    
		}
                System.out.println("Fim de treinamento");
                
                
                
                // Novo conjunto de dados              
                
                //dados_treinamento = new double[qtd_comb+1][tamanho_entrada]; 
                //for (int i = 0; i < qtd_comb+1; i++)
                //{
                //    System.out.println("Classifying "+dados_treinamento[i][0]+","+dados_treinamento[i][1]);
		//}
                
                dados_treinamento = mlp.GetDadoTreinamento();
		for (int i = 0; i < mlp.QtdDadosTreinamento(); i++)
                {
                    double input[] = mlp.GetDadoTreinamento(i); 
		    System.out.print("\nClassificando: "+ input[0] + " " + input[1] + "\tOutput:");
                        
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(" " + mlp.Simular(dados_treinamento[i], tamanho_entrada)[j]);
		}
                
                
//                double vetor_aux[] = {0.5, 0.5};
//                mlp.AddDadoTreinamento(vetor_aux, 0.5);
//                
//                //System.out.println("\n Para saidas " + mlp.QtdDadosTreinamento());
//                //mlp.PrintDadosSaida();
//                
//                for (int i = 0; i <= max_it; i++)
//                {
//                    for (int j=0; j<mlp.QtdDadosTreinamento(); j++)
//                    {
//                        //double[] target = new double[]{((int)input[0]+(int)input[1])%2};
//                        double[] target = {mlp.GetSaidaEsperada(j)};
//                        mlp.train(mlp.GetDadoTreinamento(j), target, tamanho_entrada);
//                    }
//		}
                
                System.out.println("\n\n");
                for (int i = 0; i < mlp.QtdDadosTreinamento(); i++)
                {
                    double input[] = mlp.GetDadoTreinamento(i); 
		    System.out.print("\nClassificando: "+ input[0] + " " + input[1] + "\tOutput:");
                        
                    for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(" " + mlp.Simular(dados_treinamento[i], tamanho_entrada)[j]);
		}
                
                
                System.out.println("\n\n Teste simulação");
                double input[] = {0.1,0,1};
                for (int j = 0; j < tamanho_saida; j++)
                        System.out.print(" " + mlp.Simular(input, tamanho_entrada)[j]);
                
                System.out.println();
                
	}
        
}
