package lapSimulation;

import java.io.*;

public class Powertrain{
    
    private double[][] powerMap = null;
    private double primaryRatio;         //none
    private double finalDrive;           //none
    //5 gears as an array
    private double[] gear = new double[5]; //none
    private double tireRadius;           //ft
    private double shift12;
    private double shift23;
    private double shift34;
    private double shift45;
    private double revLimit;
    private double clutchEngagementRPM = 5700;

    public Powertrain(String engineCSV, int rpmColumn, int powerColumn, int torqueColumn, double primaryRatio, double finalDrive, double gear1, double gear2, double gear3, double gear4, double gear5, double tireRadius) throws IOException{
        //assign in all the gear ratios
        this.primaryRatio = primaryRatio;
        this.finalDrive = primaryRatio;
        this.gear[0] = gear1;
        this.gear[1] = gear2;
        this.gear[2] = gear3;
        this.gear[3] = gear4;
        this.gear[4] = gear5;
        this.tireRadius = tireRadius;


        FileReader fr=new FileReader(engineCSV);   
        BufferedReader buf = new BufferedReader (fr);

        String line;
        System.out.println("Reading engine power curve file...");
        //ignore the first line, who wants that anyway
        line = buf.readLine();

        //count the number of lines we got in this csv
        int len = 0;
        while ((line = buf.readLine()) != null) {
            len++;
        }
        System.out.println("Found CSV file with " + len + " rows...");
        //alot the size to the power now that we know the length
        powerMap = new double[len][3];

        //Restart the buffered reader back in the start of file
        fr = new FileReader(engineCSV);
        buf = new BufferedReader (fr);
        line = buf.readLine();
        int i = 0;
        //Scroll through and read out all the rpm and power entries and add them to our powerMap variable that is really just a nested array
        while ((line = buf.readLine()) != null) {
            String[] lineSplitted = line.split(", ");
            double rpm = Double.valueOf(lineSplitted[rpmColumn]);
            powerMap[i][0] = rpm;
            double power = Double.valueOf(lineSplitted[powerColumn]);
            powerMap[i][1] = power;
            double torque = Double.valueOf(lineSplitted[torqueColumn]);
            powerMap[i][2] = torque;
            i++;
        }
        buf.close();
        System.out.println("Done");



        //Determine optimal shift points
        System.out.print("Determining optimal shift points...");
        //Find rev limit
        revLimit = -1;
        for(i = 0; i < powerMap.length; i++) {
            if (powerMap[i][0] > revLimit){
                revLimit = powerMap[i][0];
            }
        }
        //Find the shift points (assume redline shift, this is a pretty good assumption)
        shift12 = (revLimit/(this.primaryRatio*this.gear[0]*this.finalDrive)*60*this.tireRadius*2*3.14159/5280);  //Calculates the end of first
        shift23 = (revLimit/(this.primaryRatio*this.gear[1]*this.finalDrive)*60*this.tireRadius*2*3.14159/5280);  //Calculates the end of second
        shift34 = (revLimit/(this.primaryRatio*this.gear[2]*this.finalDrive)*60*this.tireRadius*2*3.14159/5280);  //Calculates the end of third
        shift45 = (revLimit/(this.primaryRatio*this.gear[3]*this.finalDrive)*60*this.tireRadius*2*3.14159/5280);  //Calculates the end of fourth
        
        System.out.println("Done");
    }

    //Determines the current maximum power and torque and then scales all values up to create a power torque curve more like the desired power torque curve
    public void scalePower(double powerGoal){
        double maxPower = -1;
        for (double[] power : powerMap) {
            if (power[1]>maxPower){
                maxPower = power[1];
            }
        }
        System.out.println("Peak power in the provided input is " + maxPower + ", scaling to " + powerGoal + ".");
        //Calculate the scaling factor
        double scalingFactor = powerGoal/maxPower;
        //Actually scale the power and torque
        for (double[] power : powerMap) {
            power[1] = power[1]*scalingFactor; //Scale power
            power[2] = power[2]*scalingFactor; //Scale torque
        }
    }

    //finds the optimum gear to be in at this vehicle speed
    public int getOptimumGear(double vehicleSpeed){
        if (vehicleSpeed <= shift12){
            return 1;
        }
        if (vehicleSpeed >= shift12 && vehicleSpeed < shift23){
            return 2;
        }
        if (vehicleSpeed >= shift23 && vehicleSpeed < shift34){
            return 3;
        }
        if (vehicleSpeed >= shift34 && vehicleSpeed < shift45){
            return 4;
        }
        if (vehicleSpeed >= shift45){
            return 5;
        }

        //Return -1 and hopefully someone realizes something went wrong
        return -1;
    }

    //Finds the power output at a given RPM
    public double getPowerAtRPM(double rpm){
        double closestRPM = -1;
        int index = -1;
        int i;
        for(i = 0; i < powerMap.length; i++) {
            if (Math.abs(powerMap[i][0]-rpm)<Math.abs(closestRPM-rpm)){
                closestRPM = powerMap[i][0];
                index = i;
            }
        }
        //If the RPM is less than clutch engaged just assume the clutch is now engaged and we are at that RPM
        //The engine would spool up to this speed almost instantly <100ms
        if (closestRPM < clutchEngagementRPM){
            //Find the nearest data point to 4500 and then return the power here
            for(i = 0; i < powerMap.length; i++) {
                if (Math.abs(powerMap[i][0]-clutchEngagementRPM)<Math.abs(closestRPM-clutchEngagementRPM)){
                    closestRPM = powerMap[i][0];
                    index = i;
                }
            }
            return powerMap[index][1];
        }       
        //If we are past the rev limitter we will get 0 power;
        if (rpm > revLimit){
            return 0;
        }
        
        return powerMap[index][1];
    }

    //Finds the power output at a given RPM
    public double getTorqueAtRPM(double rpm){
        double closestRPM = -1;
        int index = -1;
        int i;
        for(i = 0; i < powerMap.length; i++) {
            if (Math.abs(powerMap[i][0]-rpm)<Math.abs(closestRPM-rpm)){
                closestRPM = powerMap[i][0];
                index = i;
            }
        }
        //If the RPM is less than clutch engaged just assume the clutch is now engaged and we are at that RPM
        //The engine would spool up to this speed almost instantly <100ms
        if (closestRPM < clutchEngagementRPM){
            //Find the nearest data point to 4500 and then return the power here
            for(i = 0; i < powerMap.length; i++) {
                if (Math.abs(powerMap[i][0]-clutchEngagementRPM)<Math.abs(closestRPM-clutchEngagementRPM)){
                    closestRPM = powerMap[i][0];
                    index = i;
                }
            }
            return powerMap[index][2];
        }       
        //If we are past the rev limitter we will get 0 power;
        if (rpm > revLimit){
            return 0;
        }
        
        return powerMap[index][1];
    }

    //Finds the most power the engine can be making (HP) at this vehicle speed (MPH)
    public double getOptimumPower(double vehicleSpeed){
        //Find the respective rpm in all of the gears
        double[] rpmInDifferentGears = new double[5];
        for(int i = 0; i < rpmInDifferentGears.length; i++) {
            rpmInDifferentGears[i] = (double)((this.primaryRatio*this.gear[i]*this.finalDrive*vehicleSpeed)/(60*2*3.14159*this.tireRadius)*5280);
        }
        //Find which of these RPMS would have the highest power
        double highestPower = -1;
        int i;
        for(i = 0; i < rpmInDifferentGears.length; i++) {
            if (this.getPowerAtRPM(rpmInDifferentGears[i]) > highestPower){
                highestPower = getPowerAtRPM(rpmInDifferentGears[i]);
            }
        }
        return highestPower;
    }

    //Finds the most power the engine can be making (HP) at this vehicle speed (MPH)
    public double getOptimumTorque(double vehicleSpeed){
        //Find the gear we are in
        int currentGear = this.getOptimumGear(vehicleSpeed);
        //Find which of the RPM at that gear
        double rpmInGear = (double)((primaryRatio*gear[currentGear-1]*finalDrive*vehicleSpeed)/(60*2*3.14159*tireRadius)*5280);
        double torque = getTorqueAtRPM(rpmInGear);
        double torqueAtWheel = torque*primaryRatio*gear[currentGear-1]*finalDrive;
        return torqueAtWheel;
    }

    //Finds the most power the engine can be making (HP) at this vehicle speed (MPH)
    public double getOptimumTorque(double vehicleSpeed, int forcedGear){
        //Find the gear we are in
        int currentGear = forcedGear;
        //Find which of the RPM at that gear
        double rpmInGear = (double)((primaryRatio*gear[currentGear-1]*finalDrive*vehicleSpeed)/(60*2*3.14159*tireRadius)*5280);
        double torque = getTorqueAtRPM(rpmInGear);
        double torqueAtWheel = torque*primaryRatio*gear[currentGear-1]*finalDrive;
        return torqueAtWheel;
    }

    //Return the RPM of the engine at this wheel speed (MPH)
    public double getRPM(double vehicleSpeed){
        //Find the gear we are in
        int currentGear = this.getOptimumGear(vehicleSpeed);
        //Find which of the RPM at that gear
        double rpmInGear = (double)((primaryRatio*gear[currentGear-1]*finalDrive*vehicleSpeed)/(60*2*3.14159*tireRadius)*5280);
        if (rpmInGear < clutchEngagementRPM){
            return clutchEngagementRPM;
        }
        return rpmInGear;
    }

    //Setters
    public void setPrimaryRatio(double primaryRatio){
        this.primaryRatio = primaryRatio;
    }
    public void setGear1(double gear1){
        this.gear[0] = gear1;
    }
    public void setGear2(double gear2){
        this.gear[1] = gear2;
    }
    public void setGear3(double gear3){
        this.gear[2] = gear3;
    }
    public void setGear4(double gear4){
        this.gear[3] = gear4;
    }
    public void setGear5(double gear5){
        this.gear[4] = gear5;
    }
    public void setFinalDrive(double finalDrive){
        this.finalDrive = finalDrive;
    }
}