package model.substance;

/**
 * Created by SkaaRJ on 05.04.2018.
 */
public class Substance {
    String name;
    double proportion;
    double LAC;

    public Substance(String name, double proportion, double LAC) {
        this.name = name;
        this.proportion = proportion;
        this.LAC = LAC;
    }

    public String getName() {
        return name;
    }

    public double getProportion() {
        return proportion;
    }

    public double getLAC() {
        return LAC;
    }

}
