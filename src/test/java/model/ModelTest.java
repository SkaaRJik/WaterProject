package model;

import org.junit.Test;
import utils.RiverMath;

/**
 * Created by SkaaRJ on 19.02.2018.
 */
public class ModelTest {
    @Test
    public void test(){
        double[] v_ar = new double[20];
        double[] c_ar = new double[100];
        double[] u_ar = new double[100];
        double[] y_ar = new double[100];
        double v,b0,h0,d,q0,y_var,x,q,a,b;
        int n,n2,z,o1,x1,x2,a5,e,k1;
   /* System.out.println("Введите исходные гидрологические данные");

    System.out.print("Скорость течения V[м/c] = ");
    v=sc.nextDouble(); */ v=0.23; /*данные взял с страницы 371*/

    /*System.out.print("Ширина реки BCP[м] = ");
    b0=sc.nextDouble();   */  b0=250.0; /*BCP=";B0 строка программы 1390 */

     /*System.out.print("Глубина реки HCP[м/c] = ");
    h0=sc.nextDouble();   */  h0=5.3; /* "HCP=";H0 строка программы 1390 */

     /*System.out.print("Коэффициент диффузии D[м2/c] = ");
    d=sc.nextDouble();   */  d=0.106; /* "D=";D строка программы 1390 */

     /*System.out.print("Расход сточных вод QСТ[м/c] = ");
    q0=sc.nextDouble();   */  q0=0.72; /* "QCT=";Q0 строка программы 1390 */

    /*
!160 PRINT "Введите исходные данные решаемой задачи"
!170 PRINT "Общее количество клеток (37<NY<100)"; \ N
!180 N2=N-2 \ Y=B0/N2 \ X=Y^2*V/2.2/D \ Q=V*H0*B0
    */

    /*System.out.println("Введите исходные данные решаемой задачи");
    System.out.print("Общее количество клеток (37<NY<100)");
    n=sc.nextInt();    */  n=37;
        n2=n-2;
        y_var= b0/n2;
        x=y_var*y_var*v/2.2/d;
        q=v*h0*b0;
    /*
  !290 PRINT "Ввод рапределения поля концентрации в начальном створе"
!300 PRINT "******************************************************"
!310 PRINT
!320 PRINT "[ клетки выброса : 1 , клетки, где нет выброса : 0 ]"
!330 PRINT
!340 PRINT " Л е в ы й   берег" \ Z=0
!350 PRINT "_________________________________________________________"
  */
        z=0;
 /*данные взял с страницы 371*/

        for(int o=6; o<30; o++)  {    // от 6 до 30 - из примера
            c_ar[o]=100.0;
            z++;
        }
        for(int o=1; o<n; o++)  {
            y_ar[o]=c_ar[o];
            z++;
        }
 /*M=0 \ P=1 \ X2=0 \ O1=6 \  A5=0 \ E=0*/
        o1=6;x2=0;a5=0;e=0;
/*
!500 PRINT "Расстояние до контрольного створа"; \ INPUT X1
!510 X2=X2+X1 \ K1=X1/X \ A=D*X/V/Y/Y \ B=1-2*A \ A7%=N*K1/1400
*/
    /*System.out.print("Расстояние до контрольного створа");
    x1=sc.nextInt();    */  x1=100;
        x2 += x1;
        k1=  (int) (x1/x);
        a=d*x/v/y_var/y_var;
        b=1-2*a;
        if(b>=0)  {
      /*
 !670 FOR I=1 TO K1
!680 	C(1)=C(2)
!682 	FOR J=2 TO N-1
!690 		IF C(J-1)+C(J)+C(J+1)>1.00000E-07 THEN GO TO 710
!700 		U(J)+0 \ GO TO 720
!710 		U(J)=A*C(J-1)*B*C(J)+A*C(J+1)
!720 	NEXT J
!730 	U(1)=U(2) \ U(N)=U(N-1)
!740 	FOR J=1 TO N
!750 		C(J)=U(J)
!760 	NEXT J
      */
            for(int i=0;i<k1;i++){
                c_ar[1]=c_ar[2] ;
                for(int j=2;j<n-1;j++)  {
                    if(c_ar[j-1]+c_ar[j]+c_ar[j+1]>1e-7) {
                        u_ar[j]=a*c_ar[j-1]*b*c_ar[j] +a*c_ar[j+1];
                    }else {
                        u_ar[j]=0.0;
                    }
                }
                u_ar[1]=u_ar[2];
                u_ar[n]=u_ar[n-1];
                for(int j=1;j<n;j++)  {
                    c_ar[j]=u_ar[j];
                }
            }
            for (int i = 0; i < n-1; i++) {
                System.out.println(u_ar[i]);
            }

        }

    }
}