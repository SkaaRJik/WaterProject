package model.pipe;

import model.river.River;
import utils.RiverMath;

/**
 * Береговой сосредоточенный выпуск
 */
public class CoastalConcentratedPipe extends BasePipe {

    int rowLocation;
    int columnLocation;

    /**
     * Конструткор для берегового сосредоточенного выпуска.
     * @param x расстояние от контрольного створа вдоль берега (в км);
     * @param coast берег (false - правый; true - левый );
     * @param turnedOn режим выпуска (false - выключенный, true - включенный);
     * @param concentartion концентрация примеси в стоках;
     * @param wastewaterConsumption расход сточных вод;
     */
    public CoastalConcentratedPipe(double x, boolean coast, boolean turnedOn, double concentartion, double wastewaterConsumption ) {
        this.x = x * 1000;  //Переводим км в м.
        this.endX = this.x;
        this.coast = coast;
        this.turnedOn = turnedOn;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
        this.initialDilution = 0;
    }



    @Override
    public void mergeWaste(double[][] river) {
        river[columnLocation][rowLocation] = concentration;
    }

    @Override
    public void putPipeOnRiver(double cellSizeX, double cellSizeY, int rows, int columnss) {
        this.columnLocation = (int)(this.x / cellSizeX);
         /* coast  false - правый берег; true - левый берег;  */
        if(this.coast == false) this.rowLocation = 0;
        else this.rowLocation = rows - 1;
    }

    @Override
    public void calculateInitialDilution(River riverInfo, int rows) {
        double Ccp = RiverMath.averageImpurityConcentration(this.concentration,
                riverInfo.backgroundConcentration, this.wastewaterConsumption, riverInfo.riverWidth,
                riverInfo.riverDepth,riverInfo.flowSpeed);
        this.initialDilution = Ccp * rows - riverInfo.backgroundConcentration * rows - 1;
    }
}
