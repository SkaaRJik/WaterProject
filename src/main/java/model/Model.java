package model;

import model.river.River;
import model.pipe.BasePipe;

import java.util.Arrays;

/**
 * Класс, овечающий за решение задачи
 */
public class Model {

    /**
     * Информация о реке, заданная пользователем<br>
     * Смотреть: {@link River}
     */
    River riverInfo;

    /**
     * Содержит только включенные трубы разных типов<br>
     * Смотреть: {@link BasePipe}
     */
    BasePipe[] pipes;

    double[][] river; //Поле для моделирования

    int rows = 10; //Количество рядов, на которое разделена река по ширине
    int columns = 0; //Количество колоннок, на которое разделена река по длине
    double cellSizeX = 0; //Масштаб. Обозначает длину 1 ячейки
    double cellSizeY = 0; //Масштаб. Обозначает ширину 1 ячейки

    public Model(River river, BasePipe[] pipes) {
        this.riverInfo = river; //Сохраним информацию о реке
        this.pipes = pipes; //Сохраним трубы, которые используются в модели
        Arrays.sort(pipes); //Отсоритируем трубы в порядке удаленности от контрольного створа
        calculateCellSize(); //Делим реку на сектора
        this.river = new double[this.rows][this.columns+1]; //По длине реки, берем одну ячейку на запас
        for(BasePipe pipe : pipes){
            pipe.putPipeOnRiver(this.cellSizeX, this.cellSizeY, this.rows, this.columns); //Определяем положение труб на модели
            pipe.calculateInitialDilution(riverInfo, this.rows); //Определяем начальное разбавление для каждой трубы
        }
    }


    /**
     * Проводит масштабирование модели.<br>
     * Определяет кол-во ячеек на реке, определяет их масштаб.<br>
     * Количество ячеек по длине реки сильно зависит от труб.<br>
     * Берется самая отдаленная от контрольного створа труба, относительно конца этой трубы берется и конец реки
     * <hr>
     * Запускать <b>ТОЛЬКО</b> после инициализации {@link #riverInfo} и {@link #pipes}
     */
    private void calculateCellSize(){
        this.cellSizeY = this.riverInfo.riverWidth / this.rows;
        this.cellSizeX = this.cellSizeY;
        double currentValue = (4 * this.riverInfo.diffusion) / (Math.pow(cellSizeY, 2) * this.riverInfo.flowSpeed);
        if(currentValue > this.cellSizeX){
            this.cellSizeX = currentValue;
        }
        this.columns = (int)(this.pipes[this.pipes.length-1].getEndX()/this.cellSizeX);
    }


    /**
     * Запускает процесс моделирования процессов, происходящих на реке.
     * <hr>
     * Запускать <b>ТОЛЬКО</b> после выполнения {@link #calculateCellSize()} и {@link BasePipe#putPipeOnRiver(double, double, int, int)}
     */
    public void runModelling(){
        while (true){
            for(BasePipe pipe : this.pipes) pipe.mergeWaste(this.river);
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < columns; j++) {
                    this.river[i][j] = getNewConcentration(i, j);
                }
            }
        }

    }

    /**
     * <b>ДОДЕЛАТЬ</b>
     * Уравнение переноса жидкости. Самая главная функция в модели.
     * @param i индекс рассчитываемой ячейки
     * @param j индекс рассчитываемой ячейки
     * @return
     */
    private double getNewConcentration(int i, int j){
        double c1=0,c2=0,c3=0,c4=0;
        if(i-1 >= 0) c1 = this.river[i-1][j+1];
        c2 = this.river[i][j+1];
        if(i+1 < rows) c1 = river[i+1][j+1];
        if (j+2<columns+1) c4 = river[i][j+2];

        return (1/this.riverInfo.riverDepth) * ((c1-2*c2+c3)/Math.pow(cellSizeY,2)) - this.riverInfo.backgroundConcentration * this.riverInfo.coefficientOfNonConservatism  - this.riverInfo.flowSpeed*((c2-c4)/cellSizeX);
    }
}
