package lapSimulation;

public class Solver {
    private Track inputTrack;
    private Car inputCar;
    private Logger logger;
    private double v;                   //velocity (m/s)
    private double ke;                  //kinetic energy (J)
    private double td;                  //step time increment (s)
    private double totalLapTime;        //Initial lap time starts at 0, this is the global timekeeper !!! (s)

    public Solver(Track inputTrack, Car inputCar){
        this.inputTrack = inputTrack;
        this.inputCar = inputCar;
        

        logger = new Logger("solverLog");
        logger.write("time, distance, velocity, gear, torque");

        //Set up our basic variables for kinetic enery and velocity
        v = 0; //velocity cant start at zero or things cancel out
        ke = 0; //kinetic energy
        td = 0.001; //step time increment

        //Initial lap time starts at 0, this is the global timekeeper !!!
        totalLapTime = 0;
        
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
        else{
            //Solve a straight of the required distance and starting with the speed v
            System.out.println("Processing " + currentManuever + "...");
            solveStraight(currentManuever.getDistance(), v);
        }

        System.out.println("Done processing through track elements...");
        return totalLapTime;
    }

    /////////////////////////////
    //    SOLVING METHODS      //
    /////////////////////////////


    /////////////////////////////////////////////////////////////////
    //Method to solve a straight away with a known required exit speed
    public void solveStraight(double length, double entrySpeed, double exitSpeed){
        double x = 0;
        double f = 0;
        double a = 0;
        double dx = 0;
        int gear = 0;
        if(entrySpeed<0.01){v = 0.01;}else{v = entrySpeed;}
        ke = 0.5*inputCar.getMass()*Math.pow(v, 2);
        gear = inputCar.getGear(entrySpeed);
        //Check to make sure the track is built such that the car can operate at its limit during the turn and still slow down enough on the straight for the next turn
        if (length < getBrakingDistance(v, exitSpeed)){
            System.out.println("ERROR: " + length + " straight too short, the vehicle is braking immediately out of corner");
            System.out.println("Braking distance minimum is " + getBrakingDistance(v, exitSpeed));
        }

        //Accelerate until we need to brake (like when our dist travelled + braking distance = length)
        while (x < (length-getBrakingDistance(v, exitSpeed))){
            v = Math.sqrt((2*ke/inputCar.getMass()));
            dx = v*td;
            //Check if there was a shift once we got to new speed
            if (inputCar.getGear(v) > gear){
                //If we found a shift then coast down for the shift time
                for (double t = 0; t < inputCar.getShiftTime(); t=t+td){
                    v = Math.sqrt((2*ke/inputCar.getMass()));
                    dx = v*td;
                    logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + 0 + ", " + inputCar.getRPM(v) + ", " + getBrakingDistance(v, exitSpeed));
                    f = inputCar.getDragForce(v);
                    a = f/inputCar.getMass();
                    ke = ke - f*(dx); //add the power of the engine (745.7 Watts/1hp) and then subtract drag work
                    x += dx;
                    totalLapTime += td;
                }
                gear++;
            }
            //Log some metrics
            logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            //Calculate the weight transfer based on the last iterations acceleration. If this is first iteration we defined a as zero (we give the long weight transfer function in units of g)
            double g = a/9.81;
            double downforce = inputCar.getDownForce(v);
            double totalFrontNorm = (inputCar.getMass()-inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double totalRearNorm = (inputCar.getMass()+inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double maxTractiveForce = inputCar.getLongMaxTractiveForcePerTire(totalRearNorm/2)*2;
            //TODO remember here that the get torque function may not yet be perfect here. It might need to be adjusted for idle RPM and the clutch engagement RPM
            double tireF = (inputCar.getTorque(v, gear)*1.356)/(inputCar.getTireRadius()*0.3048);
            //If the tire force is greater than max allowed just use the max allowed force
            if (tireF >= maxTractiveForce){
                tireF = maxTractiveForce;
                System.out.println("At traction limit accelerating");
            }
            double externalF = inputCar.getDragForce(v);
            f = tireF-externalF;
            a = f/inputCar.getMass();
            //If the forces we want to put to the tire are higher than what we can handle then we will just assume we are operating at tire limit here
            ke += f*(dx); 
            x += dx;
            totalLapTime += td;
        }
        double peakStraightSpeed = v;
        System.out.println("Peak speed = " + peakStraightSpeed);
        
        //Now perform the braking
        x = 0;
        a = 0;
        double frontBrakingForce = inputCar.getBrakingTorqueFront()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double rearBrakingForce = inputCar.getBrakingTorqueRear()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m)
        while (x < getBrakingDistance(peakStraightSpeed, exitSpeed)){
            double g = a/9.81;
            double downforce = inputCar.getDownForce(v);
            double totalFrontNorm = (inputCar.getMass()*inputCar.getCGfront()+inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double totalRearNorm = (inputCar.getMass()*(1-inputCar.getCGfront())-inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double frontMaxBrakingForce = inputCar.getLongMaxForcePerTire(totalFrontNorm/2)*2;
            double rearMaxBrakingForce = inputCar.getLongMaxForcePerTire(totalRearNorm/2)*2;
            if (frontBrakingForce >= frontMaxBrakingForce){
                frontBrakingForce = frontMaxBrakingForce;
            }
            if (rearBrakingForce >= rearMaxBrakingForce){
                rearBrakingForce = rearMaxBrakingForce;
            }
            dx = v*td;
            double tireF = frontBrakingForce+rearBrakingForce;
            double externalF = inputCar.getDownForce(v);
            ke = ke - tireF*dx-externalF*dx;  //Subtract the energy we lost to braking and drag know that we can only brake so hard tho so thats the max tractive force bit 
            v = Math.sqrt((2*ke/inputCar.getMass()));
            logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            x += dx;
            totalLapTime += td;
        }
    }

    /////////////////////////////////////////////////////////////////
    //Method to solve straight with no known required exit speed
    public void solveStraight(double length, double entrySpeed){
        double x = 0;
        double f = 0;
        double a = 0;
        double dx = 0;
        int gear = 0;
        if(entrySpeed<0.01){v = 0.01;}else{v = entrySpeed;}
        ke = 0.5*inputCar.getMass()*Math.pow(v, 2);
        gear = inputCar.getGear(entrySpeed);
        while (x < length){
            v = Math.sqrt((2*ke/inputCar.getMass()));
            dx = v*td;
            //Check if there was a shift once we got to new speed
            int checkingGear = inputCar.getGear(v);
            if (checkingGear > gear){
                //If we found a shift then coast down for the shift time
                for (double t = 0; t < inputCar.getShiftTime(); t=t+td){
                    v = Math.sqrt((2*ke/inputCar.getMass()));
                    logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + 0 + ", " + inputCar.getRPM(v));
                    f = inputCar.getDragForce(v); 
                    ke = ke - f*(dx); 
                    x += dx;
                    totalLapTime += td;
                }
                gear++;
            }
            logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + gear + ", " + inputCar.getTorque(v) + ", " + inputCar.getRPM(v));
            //Calculate the weight transfer based on the last iterations acceleration. If this is first iteration we defined a as zero (we give the long weight transfer function in units of g)
            double g = a/9.81;
            double downforce = inputCar.getDownForce(v);
            double totalFrontNorm = (inputCar.getMass()*inputCar.getCGfront()-inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double totalRearNorm = (inputCar.getMass()*(1-inputCar.getCGfront())+inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double maxTractiveForce = inputCar.getLongMaxTractiveForcePerTire(totalRearNorm/2)*2;
            //TODO remember here that the get torque function may not yet be perfect here. It might need to be adjusted for idle RPM and the clutch engagement RPM
            double tireF = (inputCar.getTorque(v, gear)*1.356)/(inputCar.getTireRadius()*0.3048);
            //If the tire force is greater than max allowed just use the max allowed force
            if (tireF >= maxTractiveForce){
                tireF = maxTractiveForce;
                System.out.println("At traction limit accelerating");
            }
            double externalF = inputCar.getDragForce(v);
            f = tireF-externalF;
            a = f/inputCar.getMass();
            //If the forces we want to put to the tire are higher than what we can handle then we will just assume we are operating at tire limit here
            ke += f*(dx); 
            x += dx;
            totalLapTime += td;
        }            
    }

    /////////////////////////////////////////////////////////////////
    //Returns the steady state speed to tackle the constant radius turn
    //It assumes all the turns are right hand turns. This doesnt matter anyway. The car is symmetric
    public double solveCurveSpeed(Manuever curve){
        //double curveSpeed = Math.sqrt(curve.getRadius()*9.81*inputCar.getLatFriction());
        //Setup Fz for each wheel
        double frNorm = 0;
        double flNorm = 0;
        double rrNorm = 0;
        double rlNorm = 0;
        double frontWeight = inputCar.getMass()*inputCar.getCGfront();
        double rearWeight = inputCar.getMass()*(1-inputCar.getCGfront());
        double curveSpeed = 0;
        //Determine how fast we can go by continuously increasing velocity
        for(double vel = 0; vel < 100; vel = vel + 0.01){
            double g = Math.pow(vel,2)/curve.getRadius();
            double inertialForce = g*inputCar.getMass();
            double frontWeightTransfer = inputCar.getFrontWeightTransfer(g);
            double rearWeightTransfer = inputCar.getRearWeightTransfer(g);
            //Get the downforce and divide by 4, this is downforce per wheel
            double downforce = inputCar.getDownForce(v)/4;
            frNorm = (frontWeight/2 - frontWeightTransfer)*9.81 + downforce;
            flNorm = (frontWeight/2 + frontWeightTransfer)*9.81 + downforce;
            rrNorm = (rearWeight/2 - rearWeightTransfer)*9.81 + downforce;
            rlNorm = (rearWeight/2 + rearWeightTransfer)*9.81 + downforce;
            //Get the total max lateral force the car can be outputting at this lateral acceleration in the turn
            double combinedLatForce = inputCar.getLatMaxForcePerTire(frNorm) + inputCar.getLatMaxForcePerTire(flNorm) + inputCar.getLatMaxForcePerTire(rrNorm) + inputCar.getLatMaxForcePerTire(rlNorm);
            //Check to see if it is equal to the inertial force of the turn. If it is equal or greater then we have reached the limit
            if (combinedLatForce <= inertialForce){
                curveSpeed = vel-0.01;
                break;
            }
        }
        return curveSpeed;
    }

    /////////////////////////////////////////////////////////////////
    //This actually performs the curve
    public void solveCurve(Manuever curve){
        v = solveCurveSpeed(curve);
        double x = 0;
        while (x < curve.getDistance()){
            x += v*td;
            logger.write(totalLapTime + ", " + x + ", " + v*2.23694 + ", " + inputCar.getGear(v) + ", " + inputCar.getPower(v));
            totalLapTime += td;
        }
    }

    /////////////////////////////////////////////////////////////////
    //Returns the distance it takes to go from velIn(m/s) to velOut(m/s)
    //TODO this can be made WAY faster with calculus but its still fast
    public double getBrakingDistance(double velIn, double velOut){
        double frontBrakingForce = inputCar.getBrakingTorqueFront()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double rearBrakingForce = inputCar.getBrakingTorqueRear()/(inputCar.getTireRadius()*0.3048); //Braking force is Torque (N*m)/Tire radius (m) 
        double internalV = velIn;
        double a = 0;
        double wt = 0;
        double dist = 0;
        double internalKE = 0.5*inputCar.getMass()*Math.pow(velIn,2);
        while (internalV > velOut){
            double g = a/9.81;
            double internalDx = internalV*td;
            double downforce = inputCar.getDownForce(v);
            double totalFrontNorm = (inputCar.getMass()*inputCar.getCGfront()+inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double totalRearNorm = (inputCar.getMass()*(1-inputCar.getCGfront())-inputCar.getLongWeightTransfer(g))*9.81+downforce/2;
            double frontMaxBrakingForce = inputCar.getLongMaxForcePerTire(totalFrontNorm/2)*2;
            double rearMaxBrakingForce = inputCar.getLongMaxForcePerTire(totalRearNorm/2)*2;
            if (frontBrakingForce >= frontMaxBrakingForce){
                frontBrakingForce = frontMaxBrakingForce;
            }
            if (rearBrakingForce >= rearMaxBrakingForce){
                rearBrakingForce = rearMaxBrakingForce;
            }
            double tireF = frontBrakingForce+rearBrakingForce;
            double externalF = inputCar.getDownForce(v);
            internalKE = internalKE - tireF*internalDx-externalF*internalDx;  //Subtract the energy we lost to braking and drag know that we can only brake so hard tho so thats the max tractive force bit 
            internalV = Math.sqrt((2*internalKE/inputCar.getMass()));
            dist += internalDx;
        }

        return dist;
    }
}