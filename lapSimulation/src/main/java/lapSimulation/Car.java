package lapSimulation;

import java.io.*;
import java.util.Scanner;

public class Car {
    private double mass;                 //mass of the car in kg, the mass of a 180lb driver will be added
    private double latfriction;          //lateral friction coeff for tire
    private double longfriction;         //long friction coeff for tire
    private double power;                //hp to the wheels, the power curve will get scaled to this
    private double cD;                   //none 
    private double cL;                   //none
    private double frontalArea;          //m^2
    private double primaryRatio;         //none
    private double finalDrive;           //none
    private double gear1;                //none
    private double gear2;                //none
    private double gear3;                //none
    private double gear4;                //none
    private double gear5;                //none
    private double tireRadius;           //ft
    private double shiftTime;            //s
    private double wheelbase;            //m
    private double trackFront;           //m
    private double trackRear;            //m
    private double brakingTorqueFront;        //in-lb
    private double brakingTorqueRear;        //in-lb
    private double CGheight;             //m
    private double CGfront;              //m
    private double rearRollStiffness;    //N*m/rad
    private double frontRollStiffness;   //N*m/rad
    private double rearRollCenterHeight;    //m
    private double frontRollCenterHeight;   //m
    private Powertrain powertrain;
    private Tire tire;

    //Constructor to read in the track attributes
    public Car(String fname) throws IOException {
        //Setup buffered reader to read in from txt file
        
        FileReader fr=new FileReader(fname);   
        BufferedReader buf = new BufferedReader (fr);

        //Variable to hold each line of the car input file
        String line;

        System.out.print("Reading car file...");
        //Search through each line and assign it to the right variable
        while ((line = buf.readLine()) != null) {
            //scanner to help read through the lines easy
            Scanner myScanner = new Scanner(line);
            String varType = null;
            if (myScanner.hasNext()){
                varType = new String(myScanner.next());
            }
            switch(varType){
                case "mass" :
                    mass = myScanner.nextDouble()+(double)81.8181;
                    System.out.println("Added mass of driver to get total mass of " + this.mass);
                    break;
                case "latFriction" :
                    latfriction = myScanner.nextDouble();   
                    break;
                case "longFriction" :
                    longfriction = myScanner.nextDouble();   
                    break;
                case "power" :
                    power = myScanner.nextDouble();   
                    break;
                case "cD" :
                    cD = myScanner.nextDouble();   
                    break;
                case "cL" :
                    cL = myScanner.nextDouble();   
                    break;
                case "frontalArea" :
                    frontalArea = myScanner.nextDouble();   
                    break;
                case "primaryRatio" :
                    primaryRatio = myScanner.nextDouble();   
                    break;
                case "finalDrive" :
                    finalDrive = myScanner.nextDouble();       
                    break;
                case "1stGear" :
                    gear1 = myScanner.nextDouble();   
                    break;
                case "2ndGear" :
                    gear2 = myScanner.nextDouble();   
                    break;
                case "3rdGear" :
                    gear3 = myScanner.nextDouble();   
                    break;
                case "4thGear" :
                    gear4 = myScanner.nextDouble();   
                    break;
                case "5thGear" :
                    gear5 = myScanner.nextDouble();   
                    break;
                case "tireRadius" :
                    tireRadius = myScanner.nextDouble();   
                    break;
                case "shiftTime" :
                    shiftTime = myScanner.nextDouble();   
                    break;
                case "wheelbase" :
                    wheelbase = myScanner.nextDouble();       
                    break;
                case "trackFront" :
                    trackFront = myScanner.nextDouble();
                    break;
                case "trackRear" :
                    trackRear = myScanner.nextDouble();
                    break;
                case "brakingTorqueFront" :
                    brakingTorqueFront = myScanner.nextDouble();   
                    break;
                case "brakingTorqueRear" :
                    brakingTorqueRear = myScanner.nextDouble();   
                    break;
                case "CGheight" :
                    CGheight = myScanner.nextDouble();   
                    break;
                case "CGfront" :
                    CGfront = myScanner.nextDouble();   
                    break;
                case "rearRollStiffness" :
                    rearRollStiffness = myScanner.nextDouble();   
                    break;
                case "frontRollStiffness" :
                    frontRollStiffness = myScanner.nextDouble();   
                    break;
                case "rearRollCenterHeight" :
                    rearRollCenterHeight = myScanner.nextDouble();   
                    break;
                case "frontRollCenterHeight" :
                    frontRollCenterHeight = myScanner.nextDouble();   
                    break;
                default : 
                    System.out.println("ERROR: " + varType + " not recognized");
            }
            myScanner.close();
        }
        
        
        System.out.println("Done\n");
        
        //TODO Beging checking that all needed variables are present, alert user if one is missing
        //System.out.println("Verifying car inputs (MAYBE)...");

        //Close out that file reader and scanner BOI
        buf.close();



        //////////////////////////////
        //  ENGINE/DRIVETRAIN SETUP //
        //////////////////////////////
        String engineCSV = new String("engineData.csv");
        System.out.print("\nEnter engine data file name or press enter for \"engineData.csv\" (First row will be ignored): ");
        //Read user input and overwrite if not empty
        Scanner myScanner = new Scanner(System.in);
        String input = myScanner.nextLine();
        if (!input.isEmpty()){
            engineCSV = input;
        }
        System.out.println("Engine data file entered as " + engineCSV);

        //Get the column data for power and rpm
        System.out.print("Enter the column for rpm data (0 is the first column): ");
        int rpmColumn = myScanner.nextInt();
        System.out.print("Enter the column for power data (0 is the first column): ");
        int powerColumn = myScanner.nextInt();
        System.out.print("Enter the column for torque data (0 is the first column): ");
        int torqueColumn = myScanner.nextInt();

        //Close scanner
        myScanner.close();

        System.out.println("\nImporting engine power curve data...");

        try{
            this.powertrain = new Powertrain(engineCSV, rpmColumn, powerColumn, torqueColumn, primaryRatio, finalDrive, gear1, gear2, gear3, gear4, gear5, tireRadius);
        }
        catch(Exception e){
            System.out.println("ERROR: Invalid engine power curve file name.");
        }
        
        //Scale the peak power of the curve to the user input peak power
        this.powertrain.scalePower(this.power);

        ///////////////////
        //  TIRE SET UP  //
        ///////////////////
        tire = new Tire();

    }

    ///////////////////////////////////////////////////////////////////////
    //Functions the solver might need
    //This one is odd, check page 681 of Milliken. Its the value "H"
    public double getH(){
        double distFromFront = wheelbase*CGfront;
        double H = CGheight-(distFromFront*(rearRollCenterHeight-frontRollCenterHeight)/wheelbase+frontRollCenterHeight);
        return H;
    }
    public double getPower(double v){
        return  this.powertrain.getOptimumPower(v*2.23694);  //Gotta go from metric to MPH
    }
    public double getTorque(double v){
        return  this.powertrain.getOptimumTorque(v*2.23694);  //Gotta go from metric to MPH
    }
    public double getTorque(double v, int forcedGear){
        return  this.powertrain.getOptimumTorque(v*2.23694, forcedGear);  //Gotta go from metric to MPH
    }
    public int getGear(double v){
        return  this.powertrain.getOptimumGear(v*2.23694); //Gotta go from metric to MPH
    }
    public double getRPM(double v){
        return  this.powertrain.getRPM(v*2.23694); //Gotta go from metric to MPH
    }
    public double getDragForce(double v){
        return  this.getcD()*this.getFrontalArea()*1.225*Math.pow(v,2)/2;
    }
    public double getDownForce(double v){
        return  -this.getcL()*this.getFrontalArea()*1.225*Math.pow(v,2)/2;
    }
    public double getLongMaxTractiveForce(double v, double F){
        double normalForce = getDownForce(v)+mass;
        double maxTractiveForce = normalForce*longfriction;
        if (maxTractiveForce >= F){
            return F;
        }
        else{
            return maxTractiveForce;
        }
    }


    ////////////////////////////////////////////////////////////////////////
    //Getters for basic vars
    public double getMass(){
        return mass;
    }
    public double getLatFriction(){
        return latfriction;
    }
    public double getLongFriction(){
        return longfriction;
    }
    public double getFrontalArea(){
        return frontalArea;
    }
    public double getcD(){
        return cD;
    }
    public double getcL(){
        return cL;
    }
    public double getTireRadius(){
        return tireRadius;
    }
    public double getShiftTime(){
        return shiftTime;
    }
    public double getWheelbase(){
        return wheelbase;
    }
    public double getTrackFront(){
        return trackFront;
    }
    public double getTrackRear(){
        return trackRear;
    }
    public double getBrakingTorqueFront(){
        return brakingTorqueFront;
    }
    public double getBrakingTorqueRear(){
        return brakingTorqueRear;
    }
    public double getCGheight(){
        return CGheight;
    }
    public double getCGfront(){
        return CGfront;
    }
    public double getRearRollStiffness(){
        return rearRollStiffness;
    }
    public double getFrontRollStiffness(){
        return frontRollStiffness;
    }
    public double getFrontRollCenterHeight(){
        return frontRollCenterHeight;
    }
    public double getRearRollCenterHeight(){
        return rearRollCenterHeight;
    }


    ////////////////////////////////////////////////////////////////////////
    //Setters for basic vars
    public void setMass(double mass){
         this.mass = mass;
    }
    public void setLatFriction(double latfriction){
         this.latfriction = latfriction;
    }
    public void setLongFriction(double longfriction){
         this.longfriction = longfriction;
    }
    public void setFrontalArea(double frontalArea){
         this.frontalArea = frontalArea;
    }
    public void setcD(double cD){
         this.cD = cD;
    }
    public void setcL(double cL){
         this.cL = cL;
    }
    public void setTireRadius(double tireRadius){
         this.tireRadius = tireRadius;
    }
    public void setShiftTime(double shiftTime){
         this.shiftTime = shiftTime;
    }
    public void setWheelbase(double wheelbase){
         this.wheelbase = wheelbase;
    }
    public void setTrackFront(double trackFront){
         this.trackFront = trackFront;
    }
    public void setTrackRear(double trackRear){
         this.trackRear = trackRear;
    }
    public void setBrakingTorqueFront(double brakingTorqueFront){
         this.brakingTorqueFront = brakingTorqueFront;
    }
    public void setBrakingTorqueRear(double brakingTorqueRear){
         this.brakingTorqueRear = brakingTorqueRear;
    }
    public void setCGheight(double CGheight){
         this.CGheight = CGheight;
    }
    public void setCGfront(double CGfront){
         this.CGfront = CGfront;
    }
    public void setRearRollStiffness(double rearRollStiffness){
         this.rearRollStiffness = rearRollStiffness;
    }
    public void setFrontRollStiffness(double frontRollStiffness){
         this.frontRollStiffness = frontRollStiffness;
    }
    public void setFrontRollCenterHeight(double frontRollCenterHeight){
         this.frontRollCenterHeight = frontRollCenterHeight;
    }
    public void setRearRollCenterHeight(double rearRollCenterHeight){
         this.rearRollCenterHeight = rearRollCenterHeight;
    }



}