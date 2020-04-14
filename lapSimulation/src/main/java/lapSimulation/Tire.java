package lapSimulation;

public class Tire {

    private double B;       //Magic Formula Coefficient
    private double C;       //Magic Formula Coefficient
    private double D;       //Magic Formula Coefficient
    private double E = 1; //Magic Formula Coefficient
    private Logger logger;

    public Tire(){
        //write("Lat force SA = 0.2, " + getLatForce(IA, Fz, 0.2)*getMu(IA, Fz)*Fz);
        //write("Lat force SA = 0.3, " + getLatForce(IA, Fz, 0.3)*getMu(IA, Fz)*Fz);
        //write("Lat force SA = 0.4, " + getLatForce(IA, Fz, 0.4)*getMu(IA, Fz)*Fz);
        //write("Lat force SA = 0.5, " + getLatForce(IA, Fz, 0.5)*getMu(IA, Fz)*Fz);
        //write("Lat force SA = 0.6, " + getLatForce(IA, Fz, 0.6)*getMu(IA, Fz)*Fz);
        
    }

    //Takes in the SA in DEGREES and outputs nonDim and radians???? for MF5.2 usage I have no idea how it works
    public double nonDimSA(double SA, double IA, double Fz){
        double nonDimSA =  (getCS(IA, Fz)*Math.tan(Math.toRadians(SA))/(getMuLat(IA, Fz)*Fz));
        return nonDimSA;
    }

    public double getB(double IA, double Fz){
        //Linear model Poly22:
        //val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //Coefficients (with 95% confidence bounds):
        //  p00 =       1.336  (-2.656, 5.328)
        //  p10 =      -1.534  (-3.444, 0.3758)
        //  p01 =   -0.001102  (-0.01342, 0.01121)
        //  p20 =      0.2394  (-0.1594, 0.6382)
        //  p11 =   -1.97e-05  (-0.001484, 0.001444)
        //  p02 =   1.403e-06  (-7.682e-06, 1.049e-05)

        double B = 1.336-1.534*IA-0.001102*Fz+0.2394*Math.pow(IA, 2)-0.0000197*IA*Fz+0.000001403*Math.pow(Fz,2);
        return B;
    }

    public double getC(double IA, double Fz){
        //  Linear model Poly22:
        //  val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //  Coefficients (with 95% confidence bounds):
        //    p00 =     0.01296  (-1.778, 1.804)
        //    p10 =      -1.036  (-1.893, -0.1792)
        //    p01 =    0.002211  (-0.003313, 0.007735)
        //    p20 =      0.1858  (0.006901, 0.3647)
        //    p11 =   8.781e-06  (-0.0006479, 0.0006655)
        //    p02 =  -1.255e-06  (-5.33e-06, 2.82e-06)

        double C = 0.01296-1.036*IA+0.002211*Fz+0.1858*Math.pow(IA, 2)+0.000008781*IA*Fz-0.000001255*Math.pow(Fz,2);
        return C;
    }
    
    public double getD(){
        return 1;
    }

    public double getE(double IA, double Fz){
        //    Linear model Poly22:
        //    val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //    Coefficients (with 95% confidence bounds):
        //      p00 =       31.91  (-8.073, 71.9)
        //      p10 =       2.987  (-16.14, 22.12)
        //      p01 =    -0.01554  (-0.1389, 0.1078)
        //      p20 =      -1.056  (-5.05, 2.938)
        //      p11 =  -2.108e-06  (-0.01467, 0.01466)
        //      p02 =   4.686e-06  (-8.63e-05, 9.568e-05)  

        double E = 31.91+2.987*IA-0.01554*Fz-1.056*Math.pow(IA, 2)-0.000002108*IA*Fz+0.000004686*Math.pow(Fz,2);
        return E;
    }

    public double getMuLat(double IA, double Fz){
        //     Linear model Poly22:
        //     val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //     Coefficients (with 95% confidence bounds):
        //       p00 =       3.327  (3.154, 3.5)
        //       p10 =     -0.3734  (-0.4562, -0.2907)
        //       p01 =  -0.0008586  (-0.001392, -0.0003251)
        //       p20 =     0.03327  (0.01599, 0.05055)
        //       p11 =   0.0001477  (8.427e-05, 0.0002111)
        //       p02 =   1.795e-07  (-2.141e-07, 5.732e-07)
   

        double Mu = 3.327-0.3734*IA-0.0008586*Fz+0.03327*Math.pow(IA, 2)+0.0002108*IA*Fz+0.0000001795*Math.pow(Fz,2);
        //The CALSPAN tire test rig is about 1/0.7 times as sticky as a road
        return Mu*0.7;
    }

    //THIS IS NOT EXACT TO OUR TIRE THIS IS DATA FROM ANOTHER TEAMS TIRES IT IS UNKOWN WHAT TIRE BUT IT IS BETTER THAN NOTHING PROBABLY WITHIN 10% OF ANY OTHER TIRE
    public double getMuLong(double IA, double Fz){
        
        //Linear model Poly22:
        //val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //Coefficients (with 95% confidence bounds):
        //p00 =       3.298  (2.756, 3.84)
        //p10 =     -0.3503  (-0.6513, -0.04922)
        //p01 =   0.0004348  (-0.001159, 0.002028)
        //p20 =     0.03625  (-0.02656, 0.09906)
        //p11 =   5.925e-05  (-0.0001536, 0.0002721)
        //p02 =  -6.576e-07  (-1.854e-06, 5.389e-07)

        double MuLong = 3.298-0.3503*IA+0.0004348*Fz+0.03625*Math.pow(IA,2)+0.00005925*IA*Fz-0.0000006576*Math.pow(Fz,2);
        //The CALSPAN tire test rig is about 1/0.7 times as sticky as a road
        return MuLong*0.7;
    }

    public double getCS(double IA, double Fz){

        //      Linear model Poly22:
        //      val(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
        //      Coefficients (with 95% confidence bounds):
        //        p00 =        8131  (684.3, 1.558e+04)
        //        p10 =        5925  (2363, 9488)
        //        p01 =       7.738  (-15.23, 30.71)
        //        p20 =      -444.5  (-1188, 299.4)
        //        p11 =      -1.526  (-4.257, 1.205)
        //        p02 =    0.001159  (-0.01579, 0.0181)
   
   
        double CS = 8131+5925*IA+7.738*Fz-444.5*Math.pow(IA, 2)-1.526*IA*Fz+0.001159*Math.pow(Fz,2);
        return CS;
    }

    //Requires IA (degrees), Fz (N), and SA (degrees) and returns lat force in N
    public double getLatForce(double IA, double Fz, double SA){
        //Get the coefficients for this IA and Fz, note D is constrained to 1 by the matlab
        //script that was used to generated the surface map for B, C, and E. 
        B = getB(IA,Fz);
        C = getC(IA,Fz);
        D = getD();
        E = getE(IA,Fz);

        SA = nonDimSA(SA, IA, Fz);

        //Use the overaching formula from MF_5.2 to calculate the peak lateral force the tire
        //Can output
        double F_lat = Math.sin(1/B*Math.atan(B*(1-E)*SA+E*Math.atan(B*SA)));
        return F_lat*getMuLat(IA, Fz)*Fz;
    }

    //TODO Longitudinal Tire Force
    public double getLongForce(){
        System.out.println("DONT USE THIS");
        return 0.231312;
    }
}