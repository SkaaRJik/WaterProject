package model.pipe;

import model.pipe.mode.Mode;
import model.river.River;
import utils.RiverMath;

/**
 * Береговой распределенный выпуск.
 */
public class CoastalSpreadPipe extends BasePipe{

    double length;
    double padding;
    int rowLocation;
    int[] columnsLocation;

    /**
     * Береговой распределенный выпуск.
     * @param x Координата начальной точки выпуска(км);
     * @param coast берег расположения: false - правый; true - левый;
     * @param mode режим функционирования выброса
     * @param length длина распределенной части выпска(м);
     * @param padding расстояние между патрубками(м);
     * @param concentartion концентрация примеси в стоках (мг/л)
     * @param wastewaterConsumption расход сточных вод (мг<sup>3</sup>/с)
     */
    public CoastalSpreadPipe(double x, boolean coast, Mode mode, double length, double padding, double concentartion, double wastewaterConsumption ) {
        this.x = x * 1000; //Переводим км в м.
        this.endX = this.x + length; //Помечаем конец трубы
        this.bank = coast;
        this.length = length;
        this.padding = padding;
        this.mode = mode;
        this.concentration = concentartion;
        this.wastewaterConsumption = wastewaterConsumption;
    }




    @Override
    public void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns) {
        int startIndex = (int)Math.ceil(this.x / cellSizeX);

        /* Упрощенная модель рассчета */
        int endIndex = (int) Math.ceil((this.endX) / cellSizeX);
        this.cellsOccupied = (endIndex - startIndex)+1;
        this.columnsLocation = new int[this.cellsOccupied];
        for (int i = 0; i < cellsOccupied; i++) {
            this.columnsLocation[i] = startIndex+i;
        }

         /* bank  false - правый берег; true - левый берег;  */
        this.rowLocation = (this.bank) ? 1 : rows - 2;

    }

    @Override
    public boolean isPipeLocated(int i, int j) {
        for (int k = this.cellsOccupied-1; k >= 0; k--) {
            if(i  == this.columnsLocation[k] && j == this.rowLocation) return true;
        }
        return false;
    }


    @Override
    public String toString() {

        String strSuper = super.toString();
        StringBuilder str = new StringBuilder();
        str.append("Тип выпуска: Береговой распределенный\n");
        str.append("\tДлина распределенной части выпуска (м): " + this.length + "\n");
        str.append("\tРасстояние между патрубками (м): " + this.padding + "\n");
        str.append(strSuper);


        return str.toString();

    }
}
