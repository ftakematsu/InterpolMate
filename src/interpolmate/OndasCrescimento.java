/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

/**
 *
 * @author Fabio
 */
public class OndasCrescimento {

    // Talvez não necessário
    public Planta planta;
    public String ambiente, sexo;

    public int parametro;
    public FuncaoMatematica[] conjunto_funcoes = new FuncaoMatematica[9];

    public OndasCrescimento()
    {
        for (int i=0; i<9; i++)
        {
            conjunto_funcoes[i] = new FuncaoMatematica();
        }
    }

    public OndasCrescimento(Planta p, String amb, String sex)
    {
        planta = p;
        ambiente = amb;
        sexo = sex;
        for (int i=0; i<9; i=i+2)
        {
            conjunto_funcoes[i].DefineComoConstante();
        }
    }

     /*
     Pausas: Dias=> (1,90), (195,225), (345,405), (555,600), (705,720)
     */
     public FuncaoMatematica RetornarOnda(int onda)
     {
         return conjunto_funcoes[onda];
     }

     public void SetFuncao(int onda, FuncaoMatematica f)
     {
         conjunto_funcoes[onda] = f;
     }

     public void SetConjuntoFuncao(FuncaoMatematica f[])
     {
         conjunto_funcoes = f;
     }

     public void ImprimeEquacao(int onda)
     {
         conjunto_funcoes[onda].ImprimeFuncao();
     }

}
