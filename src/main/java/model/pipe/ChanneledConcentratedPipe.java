package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;
import utils.RiverMath;

/**
 * Русловой рассеивающий выпуск
 */
public class ChanneledConcentratedPipe extends BasePipe {

    int rowLocation;
    int columnLocation;
    double length;

    public ChanneledConcentratedPipe(double x, boolean coast, Mode mode, double length, double concentartion, double wastewaterConsumption){
        this.x = x * 1000;
        this.endX = this.x;
        this.coast = coast;
        this.mode = mode;
        this.length = length;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }

    @Override
    public void mergeWaste(double[][] river) {
        river[rowLocation][columnLocation] = this.initialDilution;
    }

    @Override
    public void calculateInitialDilution(River riverInfo, int rows) {
        double Ccp = RiverMath.averageImpurityConcentration(this.concentration,
                riverInfo.backgroundConcentration, this.wastewaterConsumption, riverInfo.riverWidth,
                riverInfo.riverDepth,riverInfo.flowSpeed);
        this.initialDilution = Ccp * rows - riverInfo.backgroundConcentration * rows - 1;
    }

    @Override
    public void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columns) {
        this.columnLocation = (int)(this.x / cellSizeX);
         /* coast  false - правый берег; true - левый берег;  */
        if(this.coast == false) this.rowLocation = (int)(this.length / cellSizeY);
        else this.rowLocation = rows - (int)(this.length / cellSizeY) -1;
    }
}
