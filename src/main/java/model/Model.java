package model;

import model.river.River;
import model.pipe.BasePipe;
import model.substance.Substance;
import utils.RiverMath;

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
    Substance substance;
    double[][][] river; //Поле для моделирования

    int rows = 12; //Количество рядов, на которое разделена река по ширине (10 ячеек + 2 на берега)
    int columns = 0; //Количество колоннок, на которое разделена река по длине
    int timeSlices = 0; //Количество временных срезов

    double cellSizeX = 0; //Масштаб. Обозначает длину 1 ячейки
    double cellSizeY = 0; //Масштаб. Обозначает ширину 1 ячейки

    int currentTimeSlice = 0; //номер кадра.
    double endTime = 60; //Конечное время моделирование (сек);
    double dt = 1; //Шаг моделирования(сек)


    double[] dy;    //расстояния между датчиками (и между берегами и датчиками) контрольного створа
    double dx; //расстояние между ячейками, располагающиеся вдоль реки
    double[] speedFlow;     //скорость течения (струи) в точке i-го датчика
    double[][] H; //матрица глубин(в узлах)
    double[] Diffuy = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }; //вектор коэф.диффузии по Y (в узлах)
    //double maxSpeedFlow = 100.001;

    int round;

    Double CMAX;
    Double degreeOfMixing;
    Double dilutionRatio;
    Double CMAXDevidedByPDK;
    Double NDS;
    Double CCTD;

    public Model(River river, BasePipe[] pipes, Substance substance, int round, double endTime, double dt) {
        this.riverInfo = river; //Сохраним информацию о реке
        this.pipes = pipes; //Сохраним трубы, которые используются в модели
        this.endTime = endTime; //Сохраняем конечное время моделирования
        this.dt = dt; // Сохраняем шаг по времени
        this.round = round; //Сохраняем округление
        this.substance = substance;
        Arrays.sort(pipes); //Отсоритируем трубы в порядке удаленности от контрольного створа
        this.calculateCellSize(); //Делим реку на сектора
        //this.river = new double[this.columns][this.rows][this.timeSlices]; //Длину реки берем на 2 ячейку больше (для рассчетов)
        this.river = new double[this.columns+2][this.rows][this.timeSlices]; //Длину реки берем на 2 ячейку больше (для рассчетов)
        H = new double[columns+2][rows];
        for (int i = 0; i < this.columns+2; i++)
            for (int j = 0; j < this.rows; j++) {
                if (j == 0 || j == rows - 1) H[i][j] = 0; //берега
                    else H[i][j] = this.riverInfo.riverDepth;

                for (int k = 0; k < this.timeSlices; k++) {
                    this.river[i][j][k] = this.riverInfo.backgroundConcentration;
                }
            }
        for (int i = 0; i < this.rows-2; i++) {
            this.Diffuy[i] = this.riverInfo.diffusion;
        }

        for(BasePipe pipe : this.pipes){
            pipe.putPipeOnRiver(this.riverInfo, this.dx, this.cellSizeY, this.rows, this.columns); //Определяем положение труб на модели
        }
        this.process(this.endTime);
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

        this.cellSizeY = this.riverInfo.riverWidth / (this.rows-2);
        this.dy = new double[this.rows];
        for (int i = 0; i < this.rows; i++) {
            this.dy[i] = cellSizeY;
        }

        this.speedFlow = new double[this.rows];

        for (int i = 0; i < this.rows; i++){
            this.speedFlow[i] = this.riverInfo.flowSpeed;
        }
        this.dx = this.getMinSpeedFlow() * dt;
        /*this.cellSizeX = Math.pow(this.cellSizeY, 2) * this.riverInfo.flowSpeed /2.2 / this.riverInfo.diffusion;*/
        this.columns = (int) Math.ceil(this.pipes[this.pipes.length-1].getEndX()/dx);
        this.timeSlices = (int)Math.ceil(this.endTime / dt + 1);

    }


    /**
     * Запускает процесс моделирования процессов, происходящих на реке.
     * <hr>
     * Запускать <b>ТОЛЬКО</b> после выполнения {@link #calculateCellSize()} и {@link BasePipe#putPipeOnRiver(River, double, double, int, int)}
     */

    private void process(double aTime){
        int limk = (int)Math.floor(aTime / dt); // предельный номер кадра. может быть от 1 и выше, т.к. самый первый aTime = dt
        this.currentTimeSlice=1;     aTime = dt;
        while (this.currentTimeSlice<=limk){
            this.oneStep(aTime);

            aTime += dt;
            this.currentTimeSlice = (int)Math.floor(aTime / dt); //номер кадра.
        }
    }

    private void oneStep(double aTime){ // из кадра aTime-dt   получает кадр aTime
        this.currentTimeSlice = (int)Math.floor(aTime / dt); //номер кадра. может быть от 1 и выше, т.к. самый первый aTime = dt
        //dx=?? может быть меньше на последнем шаге
        for (int i = this.columns; i >= 0; i--){
            for (int j = 1; j < rows-1; j++){
                double c1 = this.river[i + 1][j + 1][this.currentTimeSlice - 1];
                double c2 = this.river[i + 1][j][this.currentTimeSlice - 1];
                double c3 = this.river[i + 1][j - 1][this.currentTimeSlice - 1];
                this.river[i][j][this.currentTimeSlice] = this.getNewConcentration(i, j, c1 , dy[j + 1], c2, dy[j], c3,
                        H[i+1][j+1],H[i+1][j],H[i+1][j-1],H[i][j]); // Концентрация в узле = по 3 узлам предыдущего кадра
                //System.out.print(this.river[i][j][this.currentTimeSlice] + " ");
            }
        }
    }

    /**
     * Уравнение переноса жидкости. Самая главная функция в модели.
     * @param i индекс рассчитываемой ячейки
     * @param j индекс рассчитываемой ячейки
     * @return
     */
    private double getNewConcentration(int i, int j, double c1, double dy1, double c2, double dy2, double c3, double H1, double H2, double H3, double H4){
        //double Kh = 1;

        // H1 =  H[i+1][j+1], H2 = H[i+1][j],H3 = H[i+1][j-1], H4 = H[i][j]

        double Kh = H1 / H2;
        if (Kh > 1) Kh = 1;
        double dc1 = Kh * (c1 - c2) / dy1; //наклон прямой, произовдная
        Kh = H3 / H2; if (Kh > 1) Kh = 1;
        double dc2 = Kh * (c3 - c2) / dy2; //наклон прямой, произовдная


        double dC_by_dx = (this.Diffuy[j - 1] * (dc1 + dc2) / 2 - this.riverInfo.coefficientOfNonConservatism * c2) / this.speedFlow[j - 1] / this.dx;              //double dC_by_dx =  (Diffuy[j-1] * (dc1 * dy1 - dc2 * dy2) / (dy1 + dy2) - K1*c2)/ Kh / this.speedFlow[j - 1] / dx;
        if (dC_by_dx >= 0) { Kh = H2 / H4; if (Kh > 1) Kh = 1;
            dC_by_dx *= Kh;}
        else { Kh = H2 / H4; double tmp = (c2 - this.riverInfo.backgroundConcentration) / -dC_by_dx/ dx;
            if (Kh > tmp) Kh = tmp;
            dC_by_dx *= Kh; }

        double res = c2 +  dC_by_dx * dx;

        int indexOfPipe = -1;
        for (int k = 0; k < this.pipes.length; k++) {
            if(pipes[k].isPipeLocated(i,j)) {
                indexOfPipe = k;
                break;
            }
        }
        if (indexOfPipe > -1){
            double avgH = (H[i][j - 1] + 2 * H[i][j] + H[i][j + 1]) / 4;//средняя глубина
            double avgB = (dy1 + dy2) / 2;
            double Qriver = RiverMath.waterConsumption(avgH, avgB, this.speedFlow[j]);//расход реки в этом узле B,U,H
            res = pipes[indexOfPipe].mergeWaste( Qriver, res, this.dt*this.currentTimeSlice); //параметр res - как фоновая концентрация для функции A_to_Cxy
        }
        return res;
    }

    /**
     * Нахождение минимального значения в векторе скоростей
     * @return минимальное значение скорости
     */
    public double getMinSpeedFlow() {
        double minSpeedFlow = 100;
        for (int i = 0; i < this.rows; i++) {
            if (minSpeedFlow > this.speedFlow[i]) minSpeedFlow = this.speedFlow[i];
        }
        return minSpeedFlow;
    }

    public String[][] getResult() {

        String[][] result = new String[this.timeSlices][this.rows-1];

        int currentTimeSlice = (int)Math.floor((endTime - 0) / dt);
        result[0][0] = "Время (сек.)";
        for (int i = 1; i < result[0].length; i++) {
            result[0][i] = "К.С. №" + i;
        }
        for (int k = 1; k < this.timeSlices; k++){
            double showt = k * dt;
            result[k][0] = String.valueOf(showt);
            for (int j = 1; j < rows - 1; j++){
                result[k][j] = String.format("%." + this.round + "f",this.river[0][j][k]);
            }
        }

        this.calculateParametres();

        return result;
    }

    public String[][] getShot(double currentTime) //вывод кадра сетки через время aTime
    {
        String[][] result = new String[this.columns+2][this.rows-1];
        int currentTimeSlice = (int)Math.floor((currentTime) / dt); // номер кадра или величина счетчика 3-го измерения

        result[0][0] = "Расстояние до КС (м.)";
        for (int i = 1; i < result[0].length; i++) {
            result[0][i] = "К.С. №" + i;
        }
        for (int i = 0; i < columns+1; i++){
            result[i+1][0] = String.valueOf(i*dx);
            for (int j = 1; j < rows-1; j++) {
                result[i+1][j] = String.format("%." + this.round + "f",this.river[i][j][currentTimeSlice]);
            }
        }
        return result;
    }

    public double getDt() {
        return dt;
    }

    public double getEndTime() {
        return endTime;
    }

    public River getRiverInfo() {
        return riverInfo;
    }

    private void calculateParametres(){
        double max = -1; int indexMax;
        double averageConcentrationOnCP = 0;
        for (int k = 0; k < this.timeSlices; k++) {
            double rowSum = 0;
            for (int j = 0; j < this.rows; j++) {
                if (this.river[0][j][k] > max) {
                    max = this.river[0][j][k];
                }
                rowSum +=this.river[0][j][k];
            }
            averageConcentrationOnCP += (rowSum/this.rows);
        }
        averageConcentrationOnCP /= this.timeSlices;
        this.CMAX = max;
        this.degreeOfMixing = (averageConcentrationOnCP / max) * 100;

        if(pipes.length == 1){
            this.dilutionRatio = (this.pipes[0].getConcentration()) /  (this.CMAX - this.riverInfo.backgroundConcentration);
            this.CCTD = this.dilutionRatio * (this.substance.getLAC() - this.riverInfo.backgroundConcentration)  + this.riverInfo.backgroundConcentration;
            this.NDS = pipes[0].getWastewaterConsumption() * 3600 * CCTD;
        }
        this.CMAXDevidedByPDK = max / this.substance.getLAC();


    }


    public Substance getSubstance() {
        return substance;
    }

    public Double getCMAX() {
        return CMAX;
    }

    public Double getDegreeOfMixing() {
        return degreeOfMixing;
    }

    public Double getDilutionRatio() {
        return dilutionRatio;
    }

    public Double getCMAXDevidedByPDK() {
        return CMAXDevidedByPDK;
    }

    public int getRound() {
        return round;
    }

    public BasePipe[] getPipes() {
        return pipes;
    }

    public double[][][] getRiver() {
        return river;
    }

    public double[] getDy() {
        return dy;
    }

    public double getDx() {
        return dx;
    }

    public Double getNDS() {
        return NDS;
    }

    public Double getCCTD() {
        return CCTD;
    }
}
