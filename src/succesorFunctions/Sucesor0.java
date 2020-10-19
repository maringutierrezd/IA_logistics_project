package succesorFunctions;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import main.Estado;
import IA.Azamon.*;

public class Sucesor0 implements SuccessorFunction {
    private boolean meterIntercambiar;
    private Paquetes P;
    private Transporte T;

    public Sucesor0(boolean metInt) {
        meterIntercambiar = metInt;
    }

    public List getSuccessors(Object state) {
        ArrayList<Successor> ret = new ArrayList<Successor>();
        Estado actual = (Estado) state;
        P = actual.getPaquetes();
        T = actual.getTransporte();

        ArrayList<Double> actualEspLibre = actual.getEspLibre();
        ArrayList<Integer> actualAsig = actual.getAsig();

        for(int i=0; i<P.size(); ++i) {
            int ofetaActual = actualAsig.get(i);
            for(int j=0; j<P.size(); ++j) {
                if(j!=ofetaActual && actualEspLibre.get(j) >= P.get(i).getPeso() && cumplePrio(P.get(i).getPrioridad(), T.get(j).getDias())) {
                    ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>)actualEspLibre.clone());
                    ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>)actualAsig.clone());
                    Estado sucesor = new Estado(newAsig, newEspLibre);

                    set(sucesor, i, j); // aplicamos el operador set

                    // añadimos el sucesor a la lista
                    ret.add(new Successor("Metemos el paquete " + i + " en la oferta" + j, sucesor));
                }
            }
        }
        return ret;
    }

    // pre: el Estado e es un estado inicializado. El Paquete identificado por i cumple todas las condiciones para ser
    //      asignado a la oferta j
    // post: el Paquete identificado por i pasa a ser asignado a la oferta j, actualizando los valores del Estado e para ser
    //       coherentes con su representación
    private void set(Estado e, int i, int j) {
        ArrayList<Double> espLibre = e.getEspLibre();
        int ofertaAnterior = e.getAsig().get(i);

        // liberamos el espacio ocupado por la oferta anterior del paquete i
        espLibre.set(ofertaAnterior, espLibre.get(ofertaAnterior) + P.get(i).getPeso());
        espLibre.set(j, espLibre.get(j) - P.get(i).getPeso());  // le quitamos espacio libre a la oferta j

        e.setAsig(i, j);            // asignamos al paquete i la oferta j
        e.setEspLibre(espLibre);    // actualizamos su espacio libre

        e.calculaCoste();           // llamamos a calculaCoste para actualizar los costes
        e.calculaFelicidad();       // lo mismo con felicidad
    }

    // pre: cierto
    // post: retorna cierto sii la prioridad prio es compatibe con el número de días nDias
    private boolean cumplePrio(int prio, int nDias) {
        if(prio==0 && nDias==1) return true;
        if(prio==1 && nDias<=3) return true;
        if(prio==2) return true;
        return false;
    }
}