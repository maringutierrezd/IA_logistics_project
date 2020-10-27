package succesorFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import main.Estado;
import IA.Azamon.*;

public class SuccesorSA implements SuccessorFunction{
    private Random rand;
    private Paquetes P;
    private Transporte T;
    private boolean meterIntercambiar;

    public SuccesorSA(boolean meterInt, int seed) {meterIntercambiar=meterInt; rand = new Random(seed);}
    public List getSuccessors(Object state) {

        ArrayList<Successor> ret = new ArrayList<Successor>();
        Estado actual = (Estado) state;
        P = actual.getPaquetes();
        T = actual.getTransporte();

        ArrayList<Double> actualEspLibre = actual.getEspLibre();
        ArrayList<Integer> actualAsig = actual.getAsig();

        int actualFelicidad = actual.getFelicidad();
        double actualCostes = actual.getCostes();

        boolean corr = false;
        while(!corr) {
            if(rand.nextInt(2)==0 || !meterIntercambiar) {
                int i = rand.nextInt(P.size());
                int j = rand.nextInt(T.size());
                int ofetaActual = actualAsig.get(i);
                if (j != ofetaActual && actualEspLibre.get(j) >= P.get(i).getPeso() && cumplePrio(P.get(i).getPrioridad(), T.get(j).getDias())) {
                    ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>) actualEspLibre.clone());
                    ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>) actualAsig.clone());

                    int newFelicidad = actualFelicidad;
                    double newCostes = actualCostes;
                    Estado sucesor = new Estado(newFelicidad, newCostes, newEspLibre, newAsig);

                    set(sucesor, i, j); // aplicamos el operador set

                    // añadimos el sucesor a la lista
                    //System.out.println("Metemos el paquete " + i + " en la oferta" + j);
                    ret.add(new Successor("Metemos el paquete " + i + " en la oferta" + j, sucesor));
                    corr = true;
                }
            }
            else if(meterIntercambiar){
                int i = rand.nextInt(P.size());
                int j = rand.nextInt(P.size());
                //Condiciones de aplicabilidad: que ambos quepan y que se cumplan prioridades
                int ofertaI = actualAsig.get(i);
                int ofertaJ = actualAsig.get(j);
                boolean cabeI = (actualEspLibre.get(ofertaJ) + P.get(j).getPeso() >= P.get(i).getPeso());
                boolean cabeJ = (actualEspLibre.get(ofertaI) + P.get(i).getPeso() >= P.get(j).getPeso());
                boolean cumplePrioI = cumplePrio(P.get(i).getPrioridad(), T.get(ofertaJ).getDias());
                boolean cumplePrioJ = cumplePrio(P.get(j).getPrioridad(), T.get(ofertaI).getDias());
                if (cabeI && cabeJ && cumplePrioI && cumplePrioJ && ofertaI != ofertaJ) {
                    int newFelicidad = actualFelicidad;
                    double newCostes = actualCostes;
                    ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>) actualEspLibre.clone());
                    ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>) actualAsig.clone());
                    //Creamos el nuevo estado con estos datos
                    Estado sucesor = new Estado(newFelicidad, newCostes, newEspLibre, newAsig);
                    //Cambiamos los datos del sucesor
                    intecambiamosIconJ(sucesor, i, j);
                    Successor anadir = new Successor("intercambiamos " + i + " con " + j, sucesor);
                    ret.add(anadir);
                    corr = true;
                    //System.out.println("intercambiamos" + i + " con " + j);
                }
                //System.out.println("pasamos de iteracion");
            }
        }
        return ret;
    }

    private void intecambiamosIconJ(Estado e, int i, int j) {
        //Deshacemos felicidad, coste y ocupacion
        Paquetes p = e.getPaquetes();
        Transporte t = e.getTransporte();

        //Deshacemos felicidad
        int felicidad = e.getFelicidad() - calcular_felicidad(e,i);
        felicidad-= calcular_felicidad(e,j);

        //Deshacemos costes
        double costes = e.getCostes() - calcular_costes(e, i);
        costes-=calcular_costes(e, j);

        //Deshacemos espacio libre
        ArrayList<Double> espLibre = (ArrayList<Double>)e.getEspLibre().clone();
        int ofertaAnteriorI = e.getAsig().get(i);
        int ofertaAnteriorJ = e.getAsig().get(j);

        espLibre.set(ofertaAnteriorI, espLibre.get(ofertaAnteriorI)+p.get(i).getPeso()-p.get(j).getPeso());
        espLibre.set(ofertaAnteriorJ, espLibre.get(ofertaAnteriorJ)+p.get(j).getPeso()-p.get(i).getPeso());

        //Hacemos la nueva asignación
        e.setAsig(i,ofertaAnteriorJ);
        e.setAsig(j,ofertaAnteriorI);

        //Hacemos felicidad
        felicidad += calcular_felicidad(e, i) + calcular_felicidad(e,j);
        //Hacemos costes
        costes += calcular_costes(e,i) + calcular_costes(e,j);
        //Hacemos espacio libre
        espLibre.set(ofertaAnteriorJ,espLibre.get(ofertaAnteriorJ)+p.get(i).getPeso());
        espLibre.set(ofertaAnteriorI,espLibre.get(ofertaAnteriorI)+p.get(j).getPeso());

        //Seteamos felicidad costes y espacio libre
        e.setFelicidad(felicidad);
        e.setCostes(costes);
        e.setEspLibre(espLibre);
    }

    // pre: el Estado e es un estado inicializado. El paquete i esta asignado a alguna oferta en el estado e
    // post: devuelve el entero correspondiente a la felicidad que aporta i en el estado e
    private int calcular_felicidad (Estado e, int i){
        int ofertaAnterior = e.getAsig().get(i);
        int prioridad = P.get(i).getPrioridad();
        int dias = T.get(ofertaAnterior).getDias();
        if (prioridad != 0) {
            if (prioridad == 1) {if (dias==1) return 1; }
            else {
                if (dias< 4) return (4 - dias);
            }
        }
        return 0;
    }


    // pre: el Estado e es un estado inicializado. El paquete i esta asignado a alguna oferta en el estado e
    // post: devuelve el double correspondiente a los costes que supone i en el estado e.
    private double calcular_costes (Estado e, int i){
        int ofertaAnterior = e.getAsig().get(i);
        int noches_almacen;
        int dias_viaje = T.get(ofertaAnterior).getDias();
        if (dias_viaje == 1) noches_almacen = 0;
        else if (dias_viaje <= 3) noches_almacen = 1;
        else noches_almacen = 2;
        return P.get(i).getPeso()*T.get(ofertaAnterior).getPrecio() + 0.25 * P.get(i).getPeso() * noches_almacen;
    }



    // pre: el Estado e es un estado inicializado. El Paquete identificado por i cumple todas las condiciones para ser
    //      asignado a la oferta j
    // post: el Paquete identificado por i pasa a ser asignado a la oferta j, actualizando los valores del Estado e para ser
    //       coherentes con su representación
    private void set(Estado e, int i, int j) {
        Paquetes p = e.getPaquetes();
        Transporte t = e.getTransporte();

        //Deshacemos felicidad
        int felicidad = e.getFelicidad() - calcular_felicidad(e,i);
        //Deshacemos costes
        double costes = e.getCostes() - calcular_costes(e, i);
        //Deshacemos espacio libre
        ArrayList<Double> espLibre = (ArrayList<Double>)e.getEspLibre().clone();
        int ofertaAnterior = e.getAsig().get(i);
        espLibre.set(ofertaAnterior, espLibre.get(ofertaAnterior)+p.get(i).getPeso());

        //Hacemos la nueva asignación
        e.setAsig(i,j);

        //Hacemos felicidad
        felicidad += calcular_felicidad(e, i);
        //Hacemos costes
        costes += calcular_costes(e,i);
        //Hacemos espacio libre
        espLibre.set(j,espLibre.get(j)-p.get(i).getPeso());

        //Seteamos felicidad costes y espacio libre
        e.setFelicidad(felicidad);
        e.setCostes(costes);
        e.setEspLibre(espLibre);
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
