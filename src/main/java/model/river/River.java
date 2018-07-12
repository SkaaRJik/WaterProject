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

   public River(double riverWidth, double riverDepth, double backgroundConcentration, double diffusion, double flowSpeed, double coefficientOfNonConservatism) {
      this.riverWidth = riverWidth;
      this.riverDepth = riverDepth;
      this.backgroundConcentration = backgroundConcentration;
      this.diffusion = diffusion;
      this.flowSpeed = flowSpeed;
      this.coefficientOfNonConservatism = coefficientOfNonConservatism;
   }
}
