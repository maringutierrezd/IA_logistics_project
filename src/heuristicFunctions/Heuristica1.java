package heuristicFunctions;

import aima.search.framework.HeuristicFunction;
import main.Estado;

public class Heuristica1 implements HeuristicFunction {
    public Heuristica1(){}


    public double getHeuristicValue(Object state) {
          Estado e = (Estado) state;
          return e.getCostes();
    }
}