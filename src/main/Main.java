package main;
import IA.Azamon.*;
import aima.search.framework.Node;
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
        System.out.println("Por ultimo escribe 'M' para usar el operador de mover unicamente o 'MI' para usar mover intercambiar. Por ejemplo: 100 1234 1,2 1 1 MI");
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

        System.out.println("Pulsa 1 para utilizar Hill Climbing. Pulsa 2 para usar Simulated annealing. Pulsa enter al final.");
        int algorithm = sc.nextInt();
        System.out.println(algorithm);
        int steps=-1; int stiter=-1; int k=-1; double lamb=-1.0;
        if (algorithm == 2){
            System.out.println("Introduce los parámetros steps, stiter, k y lamb. Por ejemplo: 10000 100 5 0,01");
            steps = sc.nextInt();
            stiter = sc.nextInt();
            k = sc.nextInt();
            lamb = sc.nextDouble();
        }


        double time = new Date().getTime();

        if (gen == 1) e.generador1();
        else e.generador2();


        System.out.println("La felicidad de la solución inicial es " + e.getFelicidad());
        System.out.println("El coste de la solución inicial es " + e.getCostes() + "\n");


        if (algorithm == 1){
            //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)
            Sucesor0 succ0;
            if (op == "M") succ0 = new Sucesor0(false); //Hill Climbing----------------------------
            else succ0 = new Sucesor0(true);
            Problem problemHC;
            if (heu == 1) problemHC = new Problem(e, succ0, state->true, new Heuristica1());
            else problemHC = new Problem(e, succ0, state->true, new Heuristica2(5));
            try{
                HillClimbingSearch HCS = new HillClimbingSearch(); //Hill Climbing----------------
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

        if (algorithm == 2){
            //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)
            SuccesorSA succ0;
            if (op == "M") succ0 = new SuccesorSA(true, seed); //Simulated Annealing--------------------
            else succ0 = new SuccesorSA(false, seed);
            Problem problemSA;
            if (heu == 1) problemSA = new Problem(e, succ0, state->true, new Heuristica1());
            else problemSA = new Problem(e, succ0, state->true, new Heuristica2(5));
            try{
                SimulatedAnnealingSearch SAS = new SimulatedAnnealingSearch(steps,stiter,k,lamb); //Simulated Annealing-------------
                SearchAgent agent = new SearchAgent(problemSA, SAS);
                for (int i=0; i<SAS.getPathStates().size(); ++i){
                    Estado nodo = (Estado) SAS.getPathStates().get(i);
                    System.out.println(i + " " + nodo.getCostes());
                }

                System.out.println("SOLUCIÓN CON SIMULATED ANNEALING ENCONTRADA:");

                Estado eFinal = (Estado) SAS.getGoalState();
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
    }
}