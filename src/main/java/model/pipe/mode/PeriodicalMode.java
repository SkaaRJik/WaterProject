package model.pipe.mode;

/**
 * Периодический режим выпуска. Содержит 2 таймера:
 *          <ul>
 *             <li>Время работы</li>
 *             <li>Время простоя</li>
 *         </ul>
 */
public class PeriodicalMode extends Mode {

    int currentTimeOfWorking = 0;
    int currentTimeOfPause = 0;
    boolean status = true;

    int timeOfWorking;
    int timeOfPause;

    /**
     * Конструктор
     * @param timeOfWorking время работы;
     * @param timeOfPause время простоя.
     */
    public PeriodicalMode(int timeOfWorking, int timeOfPause){
        this.timeOfWorking = timeOfWorking;
        this.timeOfPause = timeOfPause;
    }

    @Override
    public boolean update() {
        if(this.status){
            if(this.currentTimeOfWorking >= this.timeOfWorking){
                this.status = false;
                this.currentTimeOfWorking = 0;
                return this.status;
            }
            currentTimeOfWorking++;
        }
        else {
            if (this.currentTimeOfPause >= timeOfPause){
                this.status = true;
                this.currentTimeOfPause = 0;
                return this.status;
            }
            currentTimeOfPause++;
        }
        return status;
    }
}
