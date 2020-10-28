package heuristicFunctions;

import aima.search.framework.HeuristicFunction;
import main.Estado;

public class Heuristica2 implements HeuristicFunction {
    double k;
    public Heuristica2(double k) {
        this.k = k;
    }
    public double getHeuristicValue(Object state) {
        Estado e = (Estado) state;
        double coste = e.getCostes();
        int felicidad = e.getFelicidad();
        return coste - k*felicidad;
        //return coste/(k*felicidad);
    }
}