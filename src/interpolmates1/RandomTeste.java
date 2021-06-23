/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolmates1;

/**
 *
 * @author Fabio
 */



public class RandomTeste {
    
    public static double GeraRandomico(double min, double max)
    {
        return Math.round(min + (Math.random()*(max-min)));
    }


        public static void main(String[] args){
            int random = 0;
            
            int min = 50;
            int max = 200;
            int m[] = new int[100];
            int i =0;
            while (true)
            {
                System.out.println( " " + Math.random());
            }
            
//            while (random!=40)
//            {
//                random = (int)GeraRandomico(min,max);
//                System.out.println(random);
//            }
        }
}
