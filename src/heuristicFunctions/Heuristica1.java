package heuristicFunctions;

import aima.search.framework.HeuristicFunction;
import main.Estado;

public class Heuristica1 implements HeuristicFunction {
    public double getHeuristicValue(Object state) {
          Estado e = (Estado) state;
        System.out.println(e.getCostes());
          return e.getCostes();
    }
}