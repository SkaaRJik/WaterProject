package tube;

/**
 * Береговой сосредоточенный выпуск.
 *
 */



public abstract class BasePipe implements IPipe {
    /**
     * false - правый берег;
     * true - левый берег;
     */
    boolean coast;
    double x;
    int rowLocation;
    int columnLocation;
    boolean turnedOn;
    double concentration;
    double wastewaterConsumption;



    @Override
    public abstract void mergeWaste(double[][] river);

    @Override
    public abstract void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns);

    public void setX(double x) {
        this.x = x;
    }


    public boolean isTurnedOn() {
        return turnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        this.turnedOn = turnedOn;
    }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public double getWastewaterConsumption() {
        return wastewaterConsumption;
    }

    public void setWastewaterConsumption(double wastewaterConsumption) {
        this.wastewaterConsumption = wastewaterConsumption;
    }
}
