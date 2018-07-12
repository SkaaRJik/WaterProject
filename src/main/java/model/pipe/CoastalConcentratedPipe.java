package model.pipe;

import model.pipe.mode.Mode;
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
     * @param mode режим выпуска;
     * @param concentartion концентрация примеси в стоках;
     * @param wastewaterConsumption расход сточных вод;
     */
    public CoastalConcentratedPipe(double x, boolean coast, Mode mode, double concentartion, double wastewaterConsumption ) {
        this.x = x*1000;  //Переводим км в м.
        this.endX = this.x;
        this.bank = coast;
        this.mode = mode;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
        this.initialDilution = 0;
        this.cellsOccupied = 1;
    }


    @Override
    public void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns) {
        //this.columnLocation = (int)Math.floor(this.x / cellSizeX);
         /* bank  false - правый берег; true - левый берег;  */
        //if(this.bank == false) this.rowLocation = 1;
        //else this.rowLocation = rows - 2;
        //this.calculateInitialDilution(riverInfo, rows);


        this.columnLocation = (int)Math.ceil( this.x / cellSizeX);
        this.rowLocation = (this.bank) ?  1 : rows - 2;


    }

    @Override
    public boolean isPipeLocated(int i, int j) {
        if(i  == this.columnLocation && j == this.rowLocation) return true;
        return false;
    }



    @Override
    public String toString() {

        String strSuper = super.toString();
        StringBuilder str = new StringBuilder();
        str.append("Тип выпуска: Береговой сосредоточенный\n");
        str.append(strSuper);

        return str.toString();

    }

}
