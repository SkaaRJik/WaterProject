package model.pipe.mode;

/**
 * Периодический режим выпуска. Содержит 2 таймера:
 *          <ul>
 *             <li>Время работы</li>
 *             <li>Время простоя</li>
 *         </ul>
 */
public class PeriodicalMode extends Mode {

    int currentTimeOfPause = 0;
    boolean status = true;

    double timeOfWorking;
    double timeOfDelay;

    /**
     * Конструктор
     * @param timeOfWorking время работы;
     * @param timeOfPause время простоя.
     */
    public PeriodicalMode(double timeOfWorking, double timeOfPause){
        this.timeOfWorking = timeOfWorking * 60;
        this.timeOfDelay = timeOfPause * 60;
    }

    @Override
    public boolean update(double currentTime) {
        /*if(this.status){
            if(this.currentTimeOfWorking >= this.timeOfWorking){
                this.status = false;
                this.currentTimeOfWorking = 0;
                return this.status;
            }
            currentTimeOfWorking++;
        }
        else {
            if (this.currentTimeOfPause >= timeOfDelay){
                this.status = true;
                this.currentTimeOfPause = 0;
                return this.status;
            }
            currentTimeOfPause++;
        }*/
        if (currentTime % (this.timeOfWorking + this.timeOfDelay) > this.timeOfWorking) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        str.append("Режим работы: Периодический\n");
        str.append("\tВремя работы(мин): "+this.timeOfWorking/60 + "\n");
        str.append("\tВремя простоя(мин): "+this.timeOfDelay/60 + "\n");
        return str.toString();
    }
}
