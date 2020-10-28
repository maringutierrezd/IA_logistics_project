package main;
import IA.Azamon.*;
import aima.search.framework.SearchAgent;
import aima.search.informed.*;
import heuristicFunctions.*;
import succesorFunctions.*;
import aima.search.framework.Problem;

import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {

    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Indica el número de paquetes, seed, proporción, heurística y generador que quieras usar. ");
        System.out.println("Por ultimo escribe 'M' para usar el operador de mover unicamente o 'MI' para usar mover intercambiar. Por ejemplo: 100 1234 1.2 2 1 MI");
        int npaq = sc.nextInt();
        int seed = sc.nextInt();
        double prop = sc.nextDouble();
        int heu = sc.nextInt();
        int gen = sc.nextInt();
        String op = sc.nextLine();

        //Aqui hemos de definir los paquetes, las ofertas.. En definitiva, el problema
        Paquetes paquetes = new Paquetes(npaq, seed); //la seed 1234 es la del experimento que debemos hacer
        Transporte transporte = new Transporte(paquetes, prop, seed);
        //Ahora creamos el estado inicial
        Estado e = new Estado(paquetes, transporte);

        System.out.println("Pulsa 'H' para utilizar Hill Climbing. Pulsa 'S' para usar Simulated annealing. Pulsa enter al final.");
        String algorithm = sc.nextLine();

        double time = new Date().getTime();

        if (gen == 1) e.generador1();
        else e.generador2();


        System.out.println("La felicidad de la solución inicial es " + e.getFelicidad());
        System.out.println("El coste de la solución inicial es " + e.getCostes() + "\n");


        if (algorithm == "H"){
            //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)
            Sucesor0 succ0 = new Sucesor0(true); //Hill Climbing----------------------------
            Problem problemHC = new Problem(e, succ0, state->true, new Heuristica1());
        }


        //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)
        SuccesorSA succ0 = new SuccesorSA(true,1234); //Simulated Annealing--------------------
        Problem problemHC = new Problem(e, succ0, state->true, new Heuristica1());
        try{
            //HillClimbingSearch HCS = new HillClimbingSearch(); //Hill Climbing----------------
            SimulatedAnnealingSearch HCS = new SimulatedAnnealingSearch(50000000,100,10,0.001D); //Simulated Annealing-------------
            SearchAgent agent = new SearchAgent(problemHC, HCS);
            System.out.println(agent.getActions());

            System.out.println("SOLUCIÓN CON HILL CLIMBING ENCONTRADA:");

            Estado eFinal = (Estado) HCS.getGoalState();
            System.out.println("FELICIDAD DE LA SOLUCIÓN: " + eFinal.getFelicidad());
            System.out.println("COSTE DE LA SOLUCIÓN: " + eFinal.getCostes());
            System.out.println("ESTADO FINAL:" + "\n" + eFinal.getAsig());
            System.out.println("Ha tardado " + (new Date().getTime()-time)/1000.0 + " segundos. ");


        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("Error en Hill Climbing");
            exit(1);
        }
    }
}   //TODO: problema1: funcion de calcular costes. problema2:  HC vs SimA. problema3: generadora inicial/operadores/heuristica