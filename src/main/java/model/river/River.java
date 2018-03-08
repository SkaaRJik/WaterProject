package model.river;

/**
 * Created by SkaaRJ on 01.03.2018.
 */
public class River {
   public double riverWidth;
   public double riverDepth;
   public double backgroundConcentration;
   public double diffusion;
   public double flowSpeed;
   public double coefficientOfNonConservatism;

   public River(double riverWidth, double riverDepth, double flowSpeed){
      this.riverWidth = riverWidth;
      this.riverDepth = riverDepth;
      this.flowSpeed = flowSpeed;
   }
}
