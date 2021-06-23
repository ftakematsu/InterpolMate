/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interpolmate;

/**
 *
 * @author murilo
 */
public class MTGaSerGerado {

    private int ordem; //ordem em que sera gerado
    
    private int dia; //data do MTG a ser gerado
    
    private String Nome_arquivo; //nome do arquivo a ser gerado para este estagio
    
    
    public MTGaSerGerado(int ord, int d, String nome_arq)
    {
        ordem = ord;
        dia = d;
        Nome_arquivo = nome_arq;
    }
    
    public void setOrdem(int o) { ordem = o; }
    public void setDia(int d) { dia = d; }
    public void setNomeArquivo(String nome) { Nome_arquivo  = nome; }
    
    public int getOrdem() { return ordem; }
    public int getDia() { return dia; }
    public String getNomeArquivo() { return Nome_arquivo; }
}
