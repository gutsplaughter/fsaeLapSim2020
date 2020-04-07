package lapSimulation;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Solver {
    private Track inputTrack;
    private Car inputCar;
    private BufferedWriter logger;
    private double v;                   //velocity (m/s)
    private double ke;                  //kinetic energy (J)
    private double td;                  //step time increment (s)
    private double totalLapTime;        //Initial lap time starts at 0, this is the global timekeeper !!! (s)

    public Solver(Track inputTrack, Car inputCar){
        this.inputTrack = inputTrack;
        this.inputCar = inputCar;
        try{this.logger = new BufferedWriter(new FileWriter(System.currentTimeMillis() + "-solverLog.csv"));} catch(Exception e){System.out.println("ERROR: Solver could not generate an output log.");}
        this.write("time, distance, velocity, gear, torque");

        //Set up our basic variables for kinetic enery and velocity
        v = 0; //velocity cant start at zero or things cancel out
        ke = 0; //kinetic energy
        td = 0.001; //step time increment

        //Initial lap time starts at 0, this is the global timekeeper !!!
        totalLapTime = 0;
        
    }

    public void write(String line){
        try{logger.write(line + "\n"); logger.flush();} catch(Exception e){System.out.println("ERROR: Solver could not write to output log");}
        try{logger.flush();}catch(Exception e){}
    }
    
    public double solveLapTime(){
        ////////////////////////////////////////////
        //  SCROLL THROUGH ALL ELEMENTS OF TRACK  //
        ////////////////////////////////////////////
    
        System.out.println("\nProcessing through track elements...");
        //We can always see what is ahead next manuever is always 1 step ahead
        Track trackQueue = inputTrack;
        Manuever currentManuever = trackQueue.getNext();
        Manuever nextManuever = trackQueue.getNext();

        //TODO DELETE getBrakingDistance(15, 0);
        
        //Check to see if the track is only a single straight
        if (nextManuever.getEndOfTrack() && currentManuever.isStraight()){
            //Solve a straight of the required distance and starting with the speed v
            System.out.println("Processing " + currentManuever + "...");
            solveStraight(currentManuever.getDistance(), v);
        }
        //Loop through all track manuevers when there is more than just one
        while (!nextManuever.getEndOfTrack()){
            //Straight
            if (currentManuever.isStraight()){
                System.out.println("Processing " + currentManuever + "...");
                //if we have a straight followed by turn then we need to find the velocity of the next turn first to know our end velocity
                double endVel = solveCurveSpeed(nextManuever);
                //Set a variable to the total distance of this straight away
                solveStraight(currentManuever.getDistance(), v, endVel);
            }
            //Curve
            else{
                //Must be just a regular curve, so lets solve just for a curve
                System.out.println("Processing " + currentManuever + "...");
                solveCurve(currentManuever);
            }
            currentManuever = nextManuever;
            nextManuever = trackQueue.getNext();
        }
        //If the track ends in a curve it needs one more iteration on current manuever to solve the last curve
        if (!currentManuever.isStraight){
            System.out.println("Processing " + currentManuever + "...");
            solveCurve(currentManuever);
        }

        System.out.println("Done processing through track elements...");
        return totalLapTime;
    }

    //Method to solve a straight away with a known required exit speed
    public void solveStraight(double length, double entrySpeed, double exitSpeed){
        double x = 0;
        if(entrySpeed<0.01){v = 0.01;}else{v = entrySpeed;}
        ke = 0.5*inputCar.getMass()*Math.pow(v, 2);
        int gear = inputCar.getGear(entrySpeed);
        //Accelerate until we need to brake (like when our dist travelled + braking distance = length)
        while (x < (length-getBrakingDistance(v, exitSpeed))){
            v = Math.sqrt((2*ke/inputCar.getMass()));
            double dx = v*td;
            //Check if there was a shift once we got to new speed
            if (inputCar.getGear(v) > gear){
                System.out.println("Shift detected!");
                //If we found a shift then coast down for the shift time
                for (double t = 0; t < inputCar.getShiftTime(); t=t+td){
                    v = Math.sqrt((2*ke/inputCar.getMass()));
                    write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + 0 + ", " + inputCar.getRPM(v));
                    ke = ke - inputCar.getDragForce(v)*(dx); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
                    x += dx;
                    totalLapTime += td;
                }
                gear++;
            }
            //Log some metrics
            write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            //TODO remember here that the get power function may not yet be perfect here. It might need to be adjusted for idle RPM and the clutch engagement RPM
            System.out.println("Gear: " + gear);
            ke += (inputCar.getTorque(v, gear)*1.356)/(inputCar.getTireRadius()*0.3048)*(dx)-inputCar.getDragForce(v)*(dx); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
            x += v*td;
            totalLapTime += td;
        }
        double peakStraightSpeed = v;
        //Now perform the braking
        x = 0;
        double frontBrakingForce = inputCar.getBrakingTorqueFront()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double rearBrakingForce = inputCar.getBrakingTorqueRear()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        while (x < getBrakingDistance(peakStraightSpeed, exitSpeed)){
            double dx = v*td;
            ke = ke - (frontBrakingForce+rearBrakingForce)*dx-inputCar.getDragForce(v)*dx;  //Subtract the energy we lost to braking and drag
            v = Math.sqrt((2*ke/inputCar.getMass()));
            write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            x += dx;
            totalLapTime += td;
        }
    }

    //Method to solve straight with no known required exit speed
    public void solveStraight(double length, double entrySpeed){
        double x = 0;
        if(entrySpeed<0.01){v = 0.01;}else{v = entrySpeed;}
        ke = 0.5*inputCar.getMass()*Math.pow(v, 2);
        int gear = inputCar.getGear(entrySpeed);
        while (x < length){
            v = Math.sqrt((2*ke/inputCar.getMass()));
            double dx = v*td;
            //Check if there was a shift once we got to new speed
            int checkingGear = inputCar.getGear(v);
            if (checkingGear > gear){
                System.out.println("Shift detected!");
                //If we found a shift then coast down for the shift time
                for (double t = 0; t < inputCar.getShiftTime(); t=t+td){
                    v = Math.sqrt((2*ke/inputCar.getMass()));
                    write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + 0 + ", " + inputCar.getRPM(v));
                    ke = ke - inputCar.getDragForce(v)*(dx); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
                    x += dx;
                    totalLapTime += td;
                }
                gear++;
            }
            //Log some metrics
            write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + gear + ", " + inputCar.getTorque(v) + ", " + inputCar.getRPM(v));
            System.out.println("Gear: " + gear);
            //TODO remember here that the get power function may not yet be perfect here. It might need to be adjusted for idle RPM and the clutch engagement RPM
            ke += (inputCar.getTorque(v, gear)*1.356)/(inputCar.getTireRadius()*0.3048)*(dx)-inputCar.getDragForce(v)*(dx); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
            x += dx;
            totalLapTime += td;
        }            
    }

    //Returns the steady state speed to tackle the constant radius turn
    public double solveCurveSpeed(Manuever curve){
        //TODO add weight transfer and tire model????
        double curveSpeed = Math.sqrt(curve.getRadius()*9.81*inputCar.getLatFriction());
        return curveSpeed;
    }

    //This actually performs the curve
    public void solveCurve(Manuever curve){
        v = solveCurveSpeed(curve);
        double x = 0;
        while (x < curve.getDistance()){
            x += v*td;
            write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            totalLapTime += td;
        }
    }

    //Returns the distance it takes to go from velIn(m/s) to velOut(m/s)
    public double getBrakingDistance(double velIn, double velOut){
        double dist = 0;
        double changeInKE = 0.5*inputCar.getMass()*(Math.pow(velIn,2) - Math.pow(velOut,2)); //The energy we need to shed in the brakes is the KE diff between the start velocity and the end velocity
        double frontBrakingForce = inputCar.getBrakingTorqueFront()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double rearBrakingForce = inputCar.getBrakingTorqueRear()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double changeInDragForce = inputCar.getDragForce(velIn)-inputCar.getDragForce(velOut); 
        dist = changeInKE/(frontBrakingForce+rearBrakingForce+changeInDragForce);
        return dist;
    }


}