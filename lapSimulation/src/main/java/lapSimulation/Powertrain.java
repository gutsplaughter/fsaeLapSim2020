package lapSimulation;

import java.io.*;

public class Powertrain{
    
    private double[][] powerMap = null;
    private double primaryRatio;         //none
    private double finalDrive;           //none
    //5 gears as an array
    private double[] gear = new double[5]; //none
    private double tireRadius;           //ft

    public Powertrain(String engineCSV, int rpmColumn, int powerColumn, double primaryRatio, double finalDrive, double gear1, double gear2, double gear3, double gear4, double gear5, double tireRadius) throws IOException{
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
        powerMap = new double[len][2];

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
            i++;
        }

        buf.close();

        System.out.println("Done reading engine power curve file...");
    }

    //Determines the current maximum power and then scales all values up to create a power curve more like the desired power
    public void scalePower(double powerGoal){
        double maxPower = -1;
        for (double[] power : powerMap) {
            if (power[1]>maxPower){
                maxPower = power[1];
            }
        }
        System.out.println("peak power in the provided input is " + maxPower + ", scaling to " + powerGoal + ".");
        //Calculate the scaling factor
        double scalingFactor = powerGoal/maxPower;
        for (double[] power : powerMap) {
            power[1] = power[1]*scalingFactor;
        }
    }

    //Finds the power output at a given RPM
    public double getPowerAtRPM(double speed){
        double closestRPM = -1;
        double revLimit = -1;
        int index = -1;
        int i;
        for(i = 0; i < powerMap.length; i++) {
            if (Math.abs(powerMap[i][0]-speed)<Math.abs(closestRPM-speed)){
                closestRPM = powerMap[i][0];
                index = i;
            }
            if (powerMap[i][0] > revLimit){
                revLimit = powerMap[i][0];
            }
        }
        //If the RPM is less than clutch engaged just assume the clutch is now engaged and we are at that RPM
        //The engine would spool up to this speed almost instantly <100ms
        if (closestRPM < 4000){
            //Find the nearest data point to 4500 and then return the power here
            for(i = 0; i < powerMap.length; i++) {
                if (Math.abs(powerMap[i][0]-4500)<Math.abs(closestRPM-4500)){
                    closestRPM = powerMap[i][0];
                    index = i;
                }
            }
            return powerMap[index][1];
        }       
        //If we are past the rev limitter we will get 0 power;
        if (speed > revLimit){
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

    //finds the optimum gear to be in at this vehicle speed
    public int getOptimumGear(double vehicleSpeed){
        //Find the respective rpm in all of the gears
        double[] rpmInDifferentGears = new double[5];
        for(int i = 0; i < rpmInDifferentGears.length; i++) {
            rpmInDifferentGears[i] = (double)((this.primaryRatio*this.gear[i]*this.finalDrive*vehicleSpeed)/(60*2*3.14159*this.tireRadius)*5280);
        }
        //Find which of these RPMS would have the highest power
        int gear = 0;
        double highestPower = -1;
        int i;
        for(i = 0; i < rpmInDifferentGears.length; i++) {
            if (this.getPowerAtRPM(rpmInDifferentGears[i]) > highestPower){
                gear = i+1;
                highestPower = this.getPowerAtRPM(rpmInDifferentGears[i]);
            }
        }
        return gear;
    }
}