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
        this.write("time, distance, velocity, gear, power");

        //Set up our basic variables for kinetic enery and velocity
        this.v = 0; //velocity
        this.ke = 0; //kinetic energy
        this.td = 0.01; //step time increment

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
                double endVel = solveCurve(nextManuever);
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
            totalLapTime += 3;
        }




        System.out.println("Done processing through track elements...");
        return totalLapTime;
    }

    //Method to solve a straight away with a known required exit speed
    public void solveStraight(double length, double entrySpeed, double exitSpeed){

    }

    //Method to solve straight with no known required exit speed
    public void solveStraight(double length, double entrySpeed){
        double x = 0;
        while (x < length){
            v = (double)Math.sqrt((2*ke/inputCar.getMass()));
            //Log some metrics
            this.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v*2.23694) + ", " + inputCar.getPower(v*2.23694));
            //TODO remember here that the get power function may not yet be perfect here. It might need to be adjusted for idle RPM and the clutch engagement RPM
            ke += inputCar.getPower(v*2.23694)*745.7*td-inputCar.getDragForce(v)*(v*td); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
            x += v*td;
            totalLapTime += td;
        }            
    }

    public double solveCurve(Manuever curve){
        System.out.println("Just assume the curve is always 20m/s");
        //TODO Go through and add velocities for each time throughout the whole time the curve takes to complete
        return 20;
    }


}