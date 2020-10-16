package succesorFunctions;

import java.util.ArrayList;
import java.util.List;

import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import main.Estado;

import javax.swing.*;

public class SuccesorF implements SuccessorFunction {

    private boolean meterIntercambiar;

    public SuccesorF (boolean meterIntercambiar){
        this.meterIntercambiar = meterIntercambiar;
    }

    Paquetes p;
    Transporte t;
    public List getSuccessors (Object state) {
        ArrayList<Successor> retval = new ArrayList<Successor>();
        Estado estado_original = (Estado) state;

        p = estado_original.getPaquetes();
        t = estado_original.getTransporte();

        ArrayList <Double> espLibre = estado_original.getEspLibre();
        ArrayList <Integer> asig = estado_original.getAsig();
        int felicidad = estado_original.getFelicidad();
        double costes = estado_original.getCostes();


        //recorremos todos los paquetes
        for (int i=0; i<p.size(); ++i){
            int ofeta_actual = estado_original.getAsig().get(i);
            for (int j=0; j<t.size(); ++j){
                //Comprobamos que quepa en la nueva oferta, que no sea la oferta que ya tienes y que cumpla prioridades
                if (j!=ofeta_actual && espLibre.get(j)>=p.get(i).getPeso() && cumplePrioridad(p.get(i).getPrioridad(),t.get(j).getDias())){
                    //Creamos los datos para el nuevo sucesor


                    int newFelicidad = felicidad;
                    double newCostes = costes;
                    ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>)espLibre.clone());
                    ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>)asig.clone());
                    //Creamos el nuevo estado con estos datos
                    Estado sucesor = new Estado(newFelicidad, newCostes, newEspLibre, newAsig);
                    //Cambiamos los datos del sucesor
                    meterIenJ(sucesor, i, j);
                    retval.add(new Successor("metemos " + i + " en " + j, sucesor));
                }
            }
        }


        if (meterIntercambiar){
            for (int i=0; i<p.size(); ++i){
                for (int j=i+1; j<p.size(); ++j){
                    //Condiciones de aplicabilidad: que ambos quepan y que se cumplan prioridades
                    int ofertaI = asig.get(i);
                    int ofertaJ = asig.get(j);
                    boolean cabeI = (espLibre.get(ofertaJ)+p.get(j).getPeso()>=p.get(i).getPeso());
                    boolean cabeJ = (espLibre.get(ofertaI)+p.get(i).getPeso()>=p.get(j).getPeso());
                    boolean cumplePrioI = cumplePrioridad(p.get(i).getPrioridad(),t.get(ofertaJ).getDias());
                    boolean cumplePrioJ = cumplePrioridad(p.get(j).getPrioridad(),t.get(ofertaI).getDias());
                    if (cabeI && cabeJ && cumplePrioI && cumplePrioJ){
                        int newFelicidad = felicidad;
                        double newCostes = costes;
                        ArrayList<Double> newEspLibre = new ArrayList<Double>((ArrayList<Double>)espLibre.clone());
                        ArrayList<Integer> newAsig = new ArrayList<Integer>((ArrayList<Integer>)asig.clone());
                        //Creamos el nuevo estado con estos datos
                        Estado sucesor = new Estado(newFelicidad, newCostes, newEspLibre, newAsig);
                        //Cambiamos los datos del sucesor
                        intecambiamosIconJ(sucesor, i, j);
                        retval.add(new Successor("intercambiamos " + i + " con " + j, sucesor));
                        System.out.println("intercambiamos" + i + " con " + j);
                    }
                }
            }
        }

        return retval;
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
        espLibre.set(ofertaAnteriorI, espLibre.get(ofertaAnteriorI)+p.get(i).getPeso());
        espLibre.set(ofertaAnteriorJ, espLibre.get(ofertaAnteriorJ)+p.get(j).getPeso());

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

    private boolean cumplePrioridad (int prioridad, int dias){
        if (prioridad==0 && dias==1) return true;
        else if (prioridad==1 && dias<=3) return true;
        else if (prioridad == 2) return true;
        return false;
    }

    private int calcular_felicidad (Estado e, int i){
        int ofertaAnterior = e.getAsig().get(i);
        int prioridad = p.get(i).getPrioridad();
        int dias = t.get(ofertaAnterior).getDias();
        if (prioridad != 0) {
            if (prioridad == 1) {if (dias==1) return 1; }
            else {
                if (dias< 4) return (4 - dias);
            }
        }
        return 0;
    }

    private double calcular_costes (Estado e, int i){
        int ofertaAnterior = e.getAsig().get(i);
        int noches_almacen;
        int dias_viaje = t.get(ofertaAnterior).getDias();
        if (dias_viaje == 1) noches_almacen = 0;
        else if (dias_viaje <= 3) noches_almacen = 1;
        else noches_almacen = 2;
        return p.get(i).getPeso()*t.get(ofertaAnterior).getPrecio() + 0.25 * p.get(i).getPeso() * noches_almacen;
    }

    private void meterIenJ(Estado e, int i, int j){
        //Que felicidad aportaba i en la conf anterior? Cuanto costaba i en la conf anterior? Cuanto ocupaba i en la oferta anterior?
        //Deshacemos esta felicidad, coste y ocupacion
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
        espLibre.set(j,espLibre.get(j)+p.get(i).getPeso());

        //Seteamos felicidad costes y espacio libre
        e.setFelicidad(felicidad);
        e.setCostes(costes);
        e.setEspLibre(espLibre);
    }

}
