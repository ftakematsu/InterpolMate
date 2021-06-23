/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testpackage;

import interpolmate.*;

/**
 *
 * @author Administrador
 */
public class SaidaFuncoes {
    public static void main(String[] args)
    {
        int conj1[] = new int[10];
        int conj2[] = new int[10];
        int conj3[] = new int[10];
        int conj4[] = new int[10];
        int x=1;
        
        FuncaoMatematica funcao1 = new FuncaoMatematica();
        FuncaoMatematica funcao2 = new FuncaoMatematica();
        FuncaoMatematica funcao3 = new FuncaoMatematica();
        FuncaoMatematica funcao4 = new FuncaoMatematica();


//         AF_EP

        //SOMBRA_M
        funcao1.DefinirEquacao(-13489.013,92.214101,-0.22192428,880697.62,-22612929, 0, 0);
        funcao2.DefinirEquacao(208060.44,-523.00734,0.48234474,-35903439,2.2662881E+09, 0, 0);
        funcao3.DefinirEquacao(-637068.45,1064.957,-0.64820782,1.6314371E+08,-1.4871605E+10, 0, 0);
        funcao4.DefinirEquacao(17607053,-18121.467,6.9813363,-7.5893065E+09,1.2245E+12, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/

        /*
        //SOL_F
        funcao1.DefinirEquacao(47985.766,-195.80238,0.26977728,-4757502.6,1.6454417E+08, 0, 0);
        funcao2.DefinirEquacao(183251.6,-468.80786,0.43739919,-30825263,1.8776795E+09, 0, 0);
        funcao3.DefinirEquacao(-557830.99,851.37238,-0.48246317,1.6086401E+08,-1.7225056E+10, 0, 0);
        funcao4.DefinirEquacao(-2992835.2,3032.0314,-1.1510792,1.3119504E+09,-2.1548798E+11, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        


        /*
        //SOL_M
        funcao1.DefinirEquacao(56757.032,-231.59383,0.31909339,-5627111.7,1.9462058E+08, 0, 0);
        funcao2.DefinirEquacao(216785.34,-554.59003,0.51743037,-36466692,2.2213747E+09, 0, 0);
        funcao3.DefinirEquacao(-659622.44,1006.7459,-0.57052029,1.9021428E+08,-2.0367374E+10, 0, 0);
        funcao4.DefinirEquacao(-3539201.08,3585.5499,-1.3612153,1.5514593E+09,-2.5482761E+11, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        

        /*
        //SOMBRA_F
        funcao1.DefinirEquacao(-11406.891,77.977683,-0.18765802,744777.69,-19123294, 0, 0);
        funcao2.DefinirEquacao(165707.37,-420.93274,0.391507,-28218944,1.7514807E+09, 0, 0);
        funcao3.DefinirEquacao(-674041.21,1045.1046,-0.60185761,1.9155882E+08,-2.0255812E+10, 0, 0);
        funcao4.DefinirEquacao(14887783,-15322.764,5.9031356,-6.4171925E+09,1.0353839E+12, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        




        // Queda. folhas

        //SOL_F
        /*
        funcao1.DefinirEquacao(-3751.4223,18.300979,-0.032696538,333522.45,-10825625, 0, 0);
        funcao2.DefinirEquacao(-35398.72,84.655787,-0.075291775,6526486.5,-4.4773597E+08, 0, 0);
        funcao3.DefinirEquacao(-54086.372,73.099214,-0.036963157,17741462,-2.1761654E+09, 0, 0);
        funcao4.DefinirEquacao(-252095.56,252.08583,-0.094455326,1.1195143E+08,-1.8625718E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        //SOL_M
        funcao1.DefinirEquacao(-3447.9904,16.82117,-0.030053387,306536.63,-9949426.3, 0, 0);
        funcao2.DefinirEquacao(-32551.783,77.845789,-0.069233631,6001711.4,-4.1174244E+08, 0, 0);
        funcao3.DefinirEquacao(-49698.676,67.161091,-0.033956396,16304111,-2.0000861E+09, 0, 0);
        funcao4.DefinirEquacao(-232203.25,232.21895,-0.087020546,1.031067E+08,-1.7152401E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        //SOMBRA_F
        funcao1.DefinirEquacao(-1541.4845,7.1950412,-0.012342477,143449.36,-4860076.4, 0, 0);
        funcao2.DefinirEquacao(5706.3688,-14.078819,0.01283254,-1012104.1,66315715, 0, 0);
        funcao3.DefinirEquacao(-149339.12,200.39239,-0.10052932,49300110,-6.0811277E+09, 0, 0);
        funcao4.DefinirEquacao(1675018.6,-1721.4101,0.66244885,-7.2334329E+08,1.1697047E+11, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        //SOMBRA_M
        funcao1.DefinirEquacao(-1420.0731,6.6302203,-0.011376819,132115.71,-4475062.2, 0, 0);
        funcao2.DefinirEquacao(5214.9957,-12.870994,0.011735118,-924572.91,60551717, 0, 0);
        funcao3.DefinirEquacao(-137447.95,184.43593,-0.092524471,45374612,-5.5969247E+09, 0, 0);
        funcao4.DefinirEquacao(1538588.9,-1581.1928,0.60848558,-6.6443092E+08,1.0744445E+11, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        // Emiss. folhas
        //SOMBRA_M
        funcao1.DefinirEquacao(1284.2695,-6.4265134,0.011558527,-108730.59,3309735.1, 0, 0);
        funcao2.DefinirEquacao(4928.5492,-12.092745,0.010926731,-875601.42,57244121, 0, 0);
        funcao3.DefinirEquacao(114794.36,-159.18869,0.082369406,-36606374,4.3558347E+09, 0, 0);
        funcao4.DefinirEquacao(-597785.52,600.78812,-0.22618695,2.6405808E+08,-4.3688479E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        // SOL_M
        funcao1.DefinirEquacao(-197.8403,1.0423016,-0.0023109065,20398.607,-841970.93, 0, 0);
        funcao2.DefinirEquacao(4869.0076,-13.031643,0.012542016,-765812.31,42116376, 0, 0);
        funcao3.DefinirEquacao(117687.37,-164.38822,0.085618717,-37230680,4.3918269E+09, 0, 0);
        funcao4.DefinirEquacao(-126299.34,120.47294,-0.0429374,58634602,-1.0170495E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/
        
        /*
        // SOL_F
        funcao1.DefinirEquacao(-194.42436,1.0235483,-0.0022632153,19991.584,-822450.57, 0, 0);
        funcao2.DefinirEquacao(4739.4712,-12.67659,0.012195164,-746231.43,41112915, 0, 0);
        funcao3.DefinirEquacao(113249.37,-158.19891,0.082399305,-35824066,4.2255375E+09, 0, 0);
        funcao4.DefinirEquacao(-121049.8,115.40865,-0.041110329,56222947,-9.7562346E+09, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }

        /**/



        /*
        //SOMBRA_F
        funcao1.DefinirEquacao(1237.6309, -6.1936723, 0.011140013, -104764.16, 3188245, 0, 0);
        funcao2.DefinirEquacao(4709.1376,-11.558773,0.010447103,-836196.22,54632579, 0, 0);
        funcao3.DefinirEquacao(66822.084,-94.214443,0.049485566,-20923211,2.4407156E+09, 0, 0);
        funcao4.DefinirEquacao(-579633.19, 582.5772, -0.2193433, 2.5602567E+08, -4.2357246E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }

        /**/



        /*
        // *********** ALong. caule
        // sol
        funcao1.DefinirEquacao(2196.8665,-9.9498226,0.015848686,-200884.34,6498448.3, 0, 0);
        funcao2.DefinirEquacao(3658.5233,-11.964156,0.013012049,-385163.35,4387381.3, 0, 0);
        funcao3.DefinirEquacao(92623.303,-123.37098,0.061170657,-30678986,3.7832954E+09, 0, 0);
        funcao4.DefinirEquacao(-489154.51,494.67246,-0.18740214,2.1474681E+08,-3.5314378E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        
        
        
        // sombra
        /*
        funcao1.DefinirEquacao(-529.7133,2.2482491,-0.0038608988,58466.771,-2355759.1, 0, 0);
        funcao2.DefinirEquacao(5000.0334,-13.325082,0.012834315,-795908.74,44839929, 0, 0);
        funcao3.DefinirEquacao(231266.32,-315.1935,0.16022193,-75001391,9.0716515E+09, 0, 0);
        funcao4.DefinirEquacao(-640663.31,634.18308,-0.23505736,2.8718952E+08,-4.8195142E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/


        //************ Numero de met
        // SOL
        /*
        funcao1.DefinirEquacao(-3633.3537, 17.169201, -0.030069543, 337651.26, -11503210, 0, 0);
        funcao2.DefinirEquacao(-5411.6878,10.969573,-0.0083542338,1179888,-95038959, 0, 0);
        funcao3.DefinirEquacao(37367.689,-57.754466,0.032693419,-10434192,1.0522881E+09, 0, 0);
        funcao4.DefinirEquacao(561106.53,-583.98653,0.22738199,-2.3903321E+08,3.8093926E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }
        /**/

        // Sombra
        /*funcao1.DefinirEquacao(33.581638, -0.63123177, 0.0016889466, 8470.5652, -685355.98, 0, 0);
        funcao2.DefinirEquacao(9953.5083, -24.525527, 0.022286372, -1763872.6, 1.1521901E+08, 0, 0);
        funcao3.DefinirEquacao(49627.167, -72.706483, 0.039465578, -14857552, 1.6445653E+09, 0, 0);
        funcao4.DefinirEquacao(985386.9, -1026.127, 0.39985939, -4.1966883E+08, 6.6883641E+10, 0, 0);

        System.out.println(" UC1");
        for (x=105; x<=165; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao1.ValorY_Funcao2(x));
        }

        System.out.println(" UC2");
        for (x=240; x<=315; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao2.ValorY_Funcao2(x));
        }

        System.out.println(" UC3");
        for (x=405; x<=525; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao3.ValorY_Funcao2(x));
        }

        System.out.println(" UC4");
        for (x=615; x<=675; x=x+15)
        {
            System.out.println(" x = " + x + "\t y = " + funcao4.ValorY_Funcao2(x));
        }

        
        

        /**/
    }
}
