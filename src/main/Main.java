package main;
import IA.Azamon.*;
import aima.search.framework.Problem;

public class Main {

    public static void main (String[] args) {
        //Aqui hemos de definir los paquetes, las ofertas.. En definitiva, el problema
        Paquetes paquetes = new Paquetes(5, 1234); //la seed 1234 es la del experimento que debemos hacer
        Transporte transporte = new Transporte(paquetes, 1.2, 1234);
        //Ahora creamos el estado inicial
        Estado e = new Estado ();
        e.setInputs(transporte, paquetes);
        // 'e' aún no es una solución, hay que generar una solucion inicial.
        System.out.println("Los paquetes son " + paquetes);
        System.out.println("Los ofertas son " + transporte);

        //Generamos el estado (solucion inicial)
        e.generador1();
        System.out.println(e.getFelicidad());


        //Generamos el problema (solucion inicial, succesor function, goal test, heuristic function)

    }
}
