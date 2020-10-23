package main;
import IA.Azamon.*;
import aima.search.framework.SearchAgent;
import aima.search.informed.*;
import heuristicFunctions.*;
import succesorFunctions.*;
import aima.search.framework.Problem;
import aima.search.framework.Successor;

import java.util.Date;
import java.util.Properties;

import static java.lang.System.exit;

public class Main {

    public static void main (String[] args) {
        //Aqui hemos de definir los paquetes, las ofertas.. En definitiva, el problema
        Paquetes paquetes = new Paquetes(4, 1234 ); //la seed 1234 es la del experimento que debemos hacer

        Transporte transporte = new Transporte(paquetes, 1.2, 1234);
        //Ahora creamos el estado inicial
        Estado e = new Estado(paquetes, transporte);
        // 'e' aún no es una solución, hay que generar una solucion inicial.
        //System.out.println("Los paquetes son " + paquetes.toString());
        //System.out.println("Los ofertas son " + transporte.toString());


        double time = new Date().getTime();
        //Generamos el estado (solucion inicial)
        e.generador2();
        System.out.println(paquetes);
        System.out.println(transporte);
        System.out.println(e.getAsig());
        System.out.println("La felicidad de la solución inicial es " + e.getFelicidad());
        System.out.println("El coste de la solución inicial es " + e.getCostes() + "\n");

        //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)

        Sucesor0 succ0 = new Sucesor0(true);
        Problem problemHC = new Problem(e, succ0, state->true, new Heuristica2());
        try{
            HillClimbingSearch HCS = new HillClimbingSearch();
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