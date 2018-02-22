package tube;

/**
 * Created by SkaaRJ on 22.02.2018.
 */
public class CoastalConcentratedPipe extends BasePipe {



    public CoastalConcentratedPipe(double x, boolean coast, boolean turnedOn, double concentartion, double wastewaterConsumption ) {
        this.x = x;
        this.coast = coast;
        this.turnedOn = turnedOn;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }



    @Override
    public void mergeWaste(double[][] river) {
        river[columnLocation][rowLocation] = concentration;
    }

    @Override
    public void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns) {
        this.columnLocation = (int)(this.x / cellSizeX);
        /**
         * coast
         * false - правый берег;
         * true - левый берег;
         */
        if(this.coast == false) this.rowLocation = 0;
        else this.rowLocation = rows - 1;
    }
}
