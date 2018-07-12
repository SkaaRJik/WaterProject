package model.pipe;

import model.river.River;

/**
 * Интерфейс труб. Содержит главные методы для всех труб
 */
public interface IPipe {
    /**
     * Сливает сточные воды в реку, согласно своему положению на реке
     * @param riverAverageConsumption расход реки
     * @param currentConcentration текущая концентрация в ячейке
     * @param currentTime текущее время моделирования
     * @return новая концентрация с расчетом сброса
     */
    double mergeWaste(double riverAverageConsumption, double currentConcentration, double currentTime);

    /**
     * Масштабирует и устанавливает трубу на реку.
     * @param cellSizeX Масштаб. Обозначает длину 1 ячейки
     * @param cellSizeY Масштаб. Обозначает ширину 1 ячейки
     * @param rows Количесвто рядов ячеек у реки
     * @param columns Количесвто столбцов ячеек у реки
     */
    void putPipeOnRiver(River riverInfo, double cellSizeX, double cellSizeY, int rows, int columns);

    /**
     * Подсчитывает коэффициент начального разбавления для трубы.<br>
     * Это значение будет сбрасываться в реку
     * Формула:<hr>
     *     Cн.р. = (Сср' * Ny - Сф * Ny - Nз) / Nз<br>
     * @param  riverInfo информация о реке, введенная пользователем;
     * @param rows количество ячеек вдоль реки
     */
    void calculateInitialDilution(River riverInfo, int rows);

    double getInitialDilution();

    boolean isPipeLocated(int i, int j);

    double getFirstLaunchConcentration();




}
