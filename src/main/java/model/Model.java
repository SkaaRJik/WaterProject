package model;

/**
 * Класс, овечающий за решение задачи
 */
public class Model {
    double[][] river;
    int row = 10;
    int column = 0;
    double cellSizeX = 0;
    double cellSizeY = 0;
    public Model(int riverHeight, double concentration, double diffusion, double flowSpeed) {
        /* Делим реку на сектора*/
            calculateCellSize(riverHeight, diffusion, flowSpeed);
        /*__________________________________________*/

        /* Заполняем все сектора реки фоновой концентрацией */
        this.river = new double[this.row][this.column];
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
                this.river[i][j] = concentration;
            }
        }
        /*__________________________________________________ */
    }

    private void calculateCellSize(int riverHeight, double diffusion, double flowSpeed){
        this.sectorSizeY = riverHeight / row;
        this.sectorSizeX = this.sectorSizeY;
        double currentValue = (4 * diffusion) / (Math.pow(sectorSizeY, 2) * flowSpeed);
        if(currentValue > sectorSizeX){
            sectorSizeX = currentValue;
        }


    }
}
