package utils;

/**
 * Класс-калькулятор для рассчета параметров реки<br>
 *     Коэффициент поперечной диффузии:<br>
 *         <ul>
 *             <li>Метод Элдера для малых рек</li>
 *             <li>Метод Потапова для равниных рек</li>
 *             <li>Метод Карушева для естественных течений</li>
 *             <li>Метод Банзала для больших рек</li>
 *             <li>Комбинированный метод для естественных течений</li>
 *         </ul>
 *      Вспомогательные функции:
 *          <ul>
 *             <li>Расчет гидравлического радиуса</li>
 *             <li>Расход воды в реке</li>
 *             <li>Коэффициент Шези</li>
 *             <li>Средняя концентрация примеси</li>
 *          </ul>
 *
 */
public final class RiverMath {
    /**
     * Ускорение свободного падения на поверхности Земли
     */
    final static double g = 9.80665;

    /**
     * Метод Элдера для малых рек.
     * <hr><p>
     * Формула расчета: Dy = 0.23 * V. * H;
     * <hr><p>
     * Вспомогательные формулы:
     * <p>
     *     V. = √(g * R * J);
     * </p>
     * @param J Уклон свободной поверхности реки;
     * @param H Средняя глубина реки;
     * @param B Средняя ширина реки;
     * @return Dy - коэффициент поперечной диффузии
     */
    public static double methodElder(double J, double H, double B){
        double V = Math.sqrt(g * hydraulicRadius(H, B) * J);
        return 0.23 * V * H;
    }

    /**
     * Расчет гидравлического радиуса по формуле:<br><hr>
     * R = (B * H) / (B + 2 * H)
     * <hr>
     * @param H Cредняя глубина реки;
     * @param B Средняя ширина реки;
     * @return R - гидравлический радиус
     */
    public static double hydraulicRadius(double H, double B){
        return (B * H) / (B + 2 * H);
    }

    /**
     * Метод Потапова для равниных рек по формуле.
     * <hr>
     * Формула расчета: Dy = (Vx * H) / 200;
     * <hr>
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @return Dy - коэффициент поперечной диффузии
     */
    public static double methodPotapov(double H, double Vx){
        return (H * Vx) / 200;
    }

    /**
     * Метод Карушева для естественных течений.
     * <hr>
     * Формула расчета: Dy = (Vx * H) / M * Ch;
     * <hr>
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @param J Уклон свободной поверхности реки;
     * @return Dy - коэффициент поперечной диффузии
     */
    public static double methodKarushev(double H, double Vx, double J){
        double Ch = coefficientChezy(Vx, H, J);
        double M = 0.7 * Ch + 6;
        return (H * Vx) / M * Ch;
    }

    /**
     * Коэффициент Шези.
     * <hr>
     * Формула расчета: Ch = Vx / √(H * J);
     * <hr>
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @param J Уклон свободной поверхности реки;
     * @return Ch - коэффициент Шези
     */
    public static double coefficientChezy(double Vx, double H, double J){
        return Vx / Math.sqrt(H * J);
    }

    /**
     * Метод Банзала для больших рек.
     * <hr>
     * Формула расчета: lg(Dy / Vx*H) = -3.547 + 1.378 * lg(B * H));
     * <hr>
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @param B Средняя ширина реки;
     * @return Dy -коэффициент поперечной диффузии
     */
    public static double methodBanzal(double H, double Vx, double B){
        double rightPiece = -3.547 + (1.378 * Math.log10(B * H));
        return Math.pow(10, rightPiece) * (Vx * H);
    }

    /**
     * Комбинированный метод для естественных течений.
     * <hr>
     * Формула расчета: Dy = 0.72 * Vx * B * f^2 * Nh * H^m;
     * <hr>
     * Вспомогательная формула: <br>
     * m = 2.5 * √Nh + 0.75 * √H * (√Nh - 0.1) - 0.13;
     *
     * @param Vx Cредняя скорость течения реки;
     * @param B Средняя ширина реки;
     * @param f Коэффициент извилисости русла;
     * @param Nh Коэффициент шероховатости русла;
     * @param H Cредняя глубина реки;
     * @return  Dy - коэффициент поперечной диффузии
     */
    public static double combinedMethod(double Vx, double B,double f, double Nh, double H){
        double sqrtNh = Math.sqrt(Nh);
        double m = 2.5 * sqrtNh + 0.75 * Math.sqrt(H) * (sqrtNh - 0.1) - 0.13;
        return 0.72 * Vx * B * Math.pow(f, 2) * Nh * Math.pow(H,m);
    }

    /**
     * Расход воды в реке при наихудших гидрологических условиях.
     * <hr>
     * Q = B * H * Vx
     * <hr>
     * @param B Средняя ширина реки;
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @return Q - Расход воды в реке;
     */
    public static double waterConsumption(double B, double H, double Vx){
        return B*H*Vx;
    }

    /**
     * Средняя концентрация примеси
     * <hr>
     * Сср* = (q * Cct + Q * Cf) / (q + Q)
     * <hr>
     * @param Cct концентрация загрязняющего вещества в сточных водах по одному показателю.
     * @param Cf фоновая концентрация;
     * @param q Расход сточных вод;
     * @param B Средняя ширина реки;
     * @param H Cредняя глубина реки;
     * @param Vx Cредняя скорость течения реки;
     * @return Сср* - Средняя концентрация примеси
     */
    public static double averageImpurityConcentration(double Cct, double Cf, double q, double B, double H, double Vx){
        double Q = waterConsumption(B, H, Vx);
        return (q * Cct + Q * Cf) / (q + Q);
    }

    }
