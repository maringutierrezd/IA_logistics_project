package main;
import IA.Azamon.*;
import java.util.*;

//TODO: No hace falta tener la lista de paquetes y ofertas en el estado. Es info fija.
public class Estado {
    // Atributos privados:
    static Paquetes P;		// Inputs
    static Transporte T;

    private ArrayList<Integer> asig = new ArrayList<>();  //Posicion significa paquete, contenido significa oferta
    private ArrayList<Double> espLibre = new ArrayList<>(); //Peso libre de cada oferta

    private int felicidad;
    private double costes;	// Coste total = € almacenaje + € transporte

    // Atributos públicos:
    // Constructora(s):

    //Constructora del estado inicial
    public Estado (Paquetes p, Transporte t) {
        this.felicidad = 0;
        this.costes = 0;
        P = p;
        T = t;
        for (int i=0; i<P.size(); ++i) asig.add(i, -1);
        for (int i=0; i<T.size(); ++i) {
            espLibre.add(T.get(i).getPesomax());
        }
    }

    //Constructora para nuevos sucesores
    public Estado(int newFelicidad, double newCostes, ArrayList<Double> newEspLibre, ArrayList<Integer> newAsig) {
        felicidad = newFelicidad;
        costes = newCostes;
        espLibre = newEspLibre;
        asig = newAsig;
    }

    //Setters:
    public void setFelicidad (int f){
        felicidad = f;
    }

    public void  setAsig (int i, int j){
        asig.set(i, j);
    }

    public void setCostes (Double c){
        costes = c;
    }
    public void setEspLibre (ArrayList<Double> e){
        espLibre = e;
    }

    // Getters:
    public double getCostes() {
        return costes;
    }

    public int getFelicidad() {
        return felicidad;
    }

    public Paquetes getPaquetes () {
        return P;
    }

    public Transporte getTransporte(){
        return T;
    }

    public ArrayList<Integer> getAsig(){
        return asig;
    }

    public ArrayList<Double> getEspLibre(){
        return espLibre;
    }


    // Calculadoras:
    public void calculaFelicidad() {
        for(int i = 0; i < asig.size(); ++i) {
            Paquete p = P.get(i);
            Oferta o = T.get(asig.get(i));

            if(p.getPrioridad() == 1) {
                int d = o.getDias();
                if(d < 2) ++felicidad;
            }
            else if(p.getPrioridad() == 2) {
                int d = o.getDias();
                if(d < 4) felicidad += (4-d);
            }
        }
    }

    public void calculaCoste() {
        for(int i = 0; i < espLibre.size(); ++i) {
            double ocupado = T.get(i).getPesomax() - espLibre.get(i);
            costes += ocupado*T.get(i).getPrecio();
        }
    }

    //TODO: Asegurarse de que no se asignan a ofertas de prioridad mayor
    private int auxGenerador1 (int currentOfert, ArrayList <Integer> paq) {
        //Vamos asignando a cada paquete una oferta sin tener en cuenta el precio (pero calculandolo)
        int noches_almacen;
        int dias_viaje = T.get(currentOfert).getDias();
        if (dias_viaje == 1) noches_almacen = 0;
        else if (dias_viaje <= 3) noches_almacen = 1;
        else noches_almacen = 2;
        //Asignamos los de prioridad1
        for (int i=0; i<paq.size(); ++i) {
            int paqueteActual = paq.get(i);
            if (currentOfert < T.size() && espLibre.get(currentOfert)>=P.get(paqueteActual).getPeso()) {
                //asignamos paquete a oferta
                asig.set(paqueteActual, currentOfert);
                // restamos el peso
                espLibre.set(currentOfert, espLibre.get(currentOfert)-P.get(paqueteActual).getPeso());
                // aumentamos coste
                costes += P.get(paqueteActual).getPeso()*T.get(currentOfert).getPrecio() + 0.25 * P.get(paqueteActual).getPeso() * noches_almacen;
                //calculamos felicidad
                int prioridad = P.get(paqueteActual).getPrioridad();
                if (prioridad != 0) {
                    if (prioridad == 1) {if (T.get(currentOfert).getDias()==1) ++felicidad; }
                    else {
                        if (T.get(currentOfert).getDias() < 4) felicidad += (4 - T.get(currentOfert).getDias());
                    }
                }

            }

            //si ya hemos llegado al final de las ofertas (poco probable)
            else if (currentOfert >= T.size()) {
                //miramos si cabe en alguna de las ofertas anteriores
                for (int j=0; j<T.size(); ++i) {
                    if (espLibre.get(j)>=P.get(paqueteActual).getPeso()) {
                        //asignamos paquete a oferta
                        asig.set(paqueteActual, j);
                        // restamos el peso
                        espLibre.set(j, espLibre.get(j)-P.get(paqueteActual).getPeso());
                        // aumentamos coste
                        costes += P.get(paqueteActual).getPeso()*T.get(j).getPrecio() + 0.25 * P.get(paqueteActual).getPeso() * noches_almacen;
                        //calculamos felicidad
                        int prioridad = P.get(paqueteActual).getPrioridad();
                        if (prioridad != 0) {
                            if (prioridad == 1) {if (T.get(j).getDias()==1) ++felicidad; }
                            else {
                                if (T.get(j).getDias() < 4) felicidad += (4 - T.get(j).getDias());
                            }
                        }
                    }
                    else System.out.println("Error! Paquetes no caben en ofertas");
                }
            }
            //si el paquete no cabe en esta oferta
            else {
                --i;
                ++currentOfert;
            }
        }
        return currentOfert;
    }

    //Generadores Iniciales
    public void generador1 () {
        //dividimos paquetes en 3 vectores segun prioridad. Esto tiene coste O(n), ordenarlos sería O(n*logn)
        ArrayList <Integer> paq1 = new ArrayList <Integer> ();
        ArrayList <Integer> paq2 = new ArrayList <Integer> ();
        ArrayList <Integer> paq3 = new ArrayList <Integer> ();
        int currentOfert = 0;

        //Dividimos paquetes
        for (int i=0; i<P.size(); ++i) {
            int x = P.get(i).getPrioridad();
            if (x == 0) paq1.add(i);
            else if (x == 1) paq2.add(i);
            else paq3.add(i);
        }

        currentOfert = auxGenerador1 (currentOfert, paq1);
        currentOfert = auxGenerador1 (currentOfert, paq2);
        currentOfert = auxGenerador1 (currentOfert, paq3);

        System.out.println("Las asignaciones son: ");
        System.out.println(asig);
    }

    // Este generador va a meter paquetes en ofertas siempre que pueda, cumpliendo las condiciones mínimas
    public void generadorTonto() {
        //TODO Marín hazlo mañana me da palo hacerlo ahora
    }
}