package main;
import IA.Azamon.*;
import aima.search.framework.SearchAgent;
import aima.search.informed.*;
import heuristicFunctions.Heuristica1;
import succesorFunctions.*;
import aima.search.framework.Problem;
import aima.search.framework.Successor;

import static java.lang.System.exit;

public class Main {

    public static void main (String[] args) {
        //Aqui hemos de definir los paquetes, las ofertas.. En definitiva, el problema
        Paquetes paquetes = new Paquetes(15, 6532); //la seed 1234 es la del experimento que debemos hacer
        Transporte transporte = new Transporte(paquetes, 1.2, 1234);
        //Ahora creamos el estado inicial
        Estado e = new Estado(paquetes, transporte);
        // 'e' aún no es una solución, hay que generar una solucion inicial.
        System.out.println("Los paquetes son " + paquetes.toString());
        System.out.println("Los ofertas son " + transporte.toString());

        //Generamos el estado (solucion inicial)
        e.generador1();
        e.calculaCoste(); e.calculaFelicidad();
        System.out.println("La felicidad de la solución inicial es " + e.getFelicidad());
        System.out.println("El coste de la solución inicial es " + e.getCostes() + "\n");

        //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)
        SuccesorF succ = new SuccesorF();
        Problem problemHC = new Problem(e, succ, state->true, new Heuristica1());
        try{
            HillClimbingSearch HCS = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problemHC, HCS);
            System.out.println("SOLUCIÓN CON HILL CLIMBING ENCONTRADA:");

            Estado eFinal = (Estado) HCS.getGoalState();
            System.out.println("FELICIDAD DE LA SOLUCIÓN: " + eFinal.getFelicidad());
            System.out.println("COSTE DE LA SOLUCIÓN: " + eFinal.getCostes());
            System.out.println("ESTADO FINAL:" + "\n" + eFinal.getAsig());
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("Error en Hill Climbing");
            exit(1);
        }
    }
}   //TODO: problema1: funcion de calcular costes. problema2:  HC vs SimA. problema3: generadora inicial/operadores/heuristica