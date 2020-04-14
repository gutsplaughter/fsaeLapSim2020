package lapSimulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Sim {
    public static String inputMode;
    public static String runMode;
    public static void main(String[] args) throws Exception {

        //Declare the track and car objects in this scope
        Track inputTrack = null;
        Car inputCar = null;
        //Setup the input reader
        BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));

        //Assign the run mode to a global variable
        if(args.length != 0){
            if(args.length == 1){
                inputMode = new String(args[0]);
                runMode = new String("lap");
            }
            if(args.length == 2){
                inputMode = new String(args[0]);
                runMode = new String(args[1]);
            }
        }
        else{
            inputMode = "-1";
            runMode = "-1";
        }
        
        //Check if we want to launch with default files
        if (inputMode.equals("default")){
            ////////////////////////
            // TRACK FILE READ IN //
            ////////////////////////
            inputTrack = new Track("track.txt");
            ////////////////////////
            //  CAR FILE READ IN  //
            ////////////////////////
            inputCar = new Car("car.txt");
        }
        else{
            String fname = new String("");
            System.out.print("\nEnter track file name or press enter for \"track.txt\": " );
            String input = new String(buf.readLine());
            if (input.isEmpty()){
                fname = "track.txt";
            }
            else{
                fname = input;
            }
            try{
                System.out.println("Track file entered as " + fname);
                System.out.println("\nImporting track data...");
                inputTrack = new Track(fname);
            }
            catch(Exception e){
                System.out.println("ERROR: Track file could not be found");
            }
            ////////////////////////

            ////////////////////////
            //  CAR FILE READ IN  //
            ////////////////////////
            String fname2 = new String("");
            System.out.print("Enter car configuration file name or press enter for \"car.txt\": " );
            String input2 = new String(buf.readLine());
            if (input2.isEmpty()){
                fname2 = "car.txt";
            }
            else{
                fname2 = input2;
            }
            try{
                System.out.println("Car file entered as " + fname2);
                System.out.println("\nImporting car data...");
                inputCar = new Car(fname2);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println("ERROR: Car file could not be found.");
            }
            
            ////////////////////////
        }
        
        //////////////////////////////////////////////////
        // RUN TESTS TO CHECK SOME FUNCTIONS OR METHODS //
        //////////////////////////////////////////////////
        new Test(inputCar, inputTrack);



        ///////////////////////////////////////////////////////////
        // OPEN INSTANCE OF SOLVER AND PASS IT THE CAR AND TRACK //
        ///////////////////////////////////////////////////////////
        if (runMode.equals("sensitivity")){
            System.out.println("\nRunning sensitivity study...");
            String param = new String("");
            System.out.print("Enter the parameter you would like to modify by its name in the textFile: " );
            String input = new String(buf.readLine());
            while (input.isEmpty()){
                System.out.println("ERROR: No parameter could be found");
                input = new String(buf.readLine());
            }
            param = input;
            double[] minMaxStep;
            Logger logger = new Logger(param+"Sensitivity");
            switch(param){
                case "mass" :
                    minMaxStep = getMinMaxStep("mass");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        //Refill the track object as it gets cleared every run TODO make this more efficient on all cases
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with mass: " + minMaxStep[0]);
                        inputCar.setMass(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "driverMass" :
                    minMaxStep = getMinMaxStep("driverMass");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with driverMass: " + minMaxStep[0]);
                        inputCar.setDriverMass(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "power" :
                    minMaxStep = getMinMaxStep("power");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with power: " + minMaxStep[0]);
                        inputCar.setPower(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "cD" :
                    minMaxStep = getMinMaxStep("cD");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with cD: " + minMaxStep[0]);
                        inputCar.setcD(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "cL" :
                    minMaxStep = getMinMaxStep("cL");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with cL: " + minMaxStep[0]);
                        inputCar.setcL(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    } 
                    break;
                case "frontalArea" :
                    minMaxStep = getMinMaxStep("frontalArea");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with frontalArea: " + minMaxStep[0]);
                        inputCar.setPower(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "primaryRatio" :
                    minMaxStep = getMinMaxStep("primaryRatio");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with primaryRatio: " + minMaxStep[0]);
                        inputCar.setPrimaryRatio(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "finalDrive" :
                    minMaxStep = getMinMaxStep("finalDrive");
                        for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                            inputTrack = new Track("track.txt");
                            System.out.println("Solving with finalDrive: " + minMaxStep[0]);
                            inputCar.setFinalDrive(minMaxStep[0]);
                            Solver solver = new Solver(inputTrack, inputCar);
                            logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    } 
                    break;
                case "1stGear" :
                    minMaxStep = getMinMaxStep("1stGear");
                        for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                            inputTrack = new Track("track.txt");
                            System.out.println("Solving with 1stGear: " + minMaxStep[0]);
                            inputCar.setGear1(minMaxStep[0]);
                            Solver solver = new Solver(inputTrack, inputCar);
                            logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    } 
                    break;
                case "2ndGear" :
                    minMaxStep = getMinMaxStep("2ndGear");
                        for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                            inputTrack = new Track("track.txt");
                            System.out.println("Solving with 2ndGear: " + minMaxStep[0]);
                            inputCar.setGear2(minMaxStep[0]);
                            Solver solver = new Solver(inputTrack, inputCar);
                            logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "3rdGear" :
                    minMaxStep = getMinMaxStep("3rdGear");
                        for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                            inputTrack = new Track("track.txt");
                            System.out.println("Solving with 3rdGear: " + minMaxStep[0]);
                            inputCar.setGear3(minMaxStep[0]);
                            Solver solver = new Solver(inputTrack, inputCar);
                            logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "4thGear" :
                    minMaxStep = getMinMaxStep("4thGear");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with 4thGear: " + minMaxStep[0]);
                        inputCar.setGear4(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "5thGear" :
                    minMaxStep = getMinMaxStep("5thGear");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with 5thGear: " + minMaxStep[0]);
                        inputCar.setGear5(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "tireRadius" :
                    minMaxStep = getMinMaxStep("tireRadius");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with tireRadius: " + minMaxStep[0]);
                        inputCar.setTireRadius(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "shiftTime" :
                    minMaxStep = getMinMaxStep("shiftTime");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with shiftTime: " + minMaxStep[0]);
                        inputCar.setShiftTime(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "wheelbase" :
                    minMaxStep = getMinMaxStep("wheelbase");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with wheelbase: " + minMaxStep[0]);
                        inputCar.setWheelbase(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }      
                    break;
                case "trackFront" :
                    minMaxStep = getMinMaxStep("trackFront");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with trackFront: " + minMaxStep[0]);
                        inputCar.setTrackFront(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "trackRear" :
                    minMaxStep = getMinMaxStep("trackRear");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with trackRear: " + minMaxStep[0]);
                        inputCar.setTrackRear(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "brakingTorqueFront" :
                    minMaxStep = getMinMaxStep("brakingTorqueFront");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with brakingTorqueFront: " + minMaxStep[0]);
                        inputCar.setBrakingTorqueFront(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "brakingTorqueRear" :   
                    minMaxStep = getMinMaxStep("brakingTorqueRear");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with brakingTorqueRear: " + minMaxStep[0]);
                        inputCar.setBrakingTorqueRear(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "CGheight" :   
                    minMaxStep = getMinMaxStep("CGheight");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with CGheight: " + minMaxStep[0]);
                        inputCar.setCGheight(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "CGfront" :
                    minMaxStep = getMinMaxStep("CGfront");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with CGfront: " + minMaxStep[0]);
                        inputCar.setCGfront(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "rearRollStiffness" : 
                    minMaxStep = getMinMaxStep("rearRollStiffness");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with rearRollStiffness: " + minMaxStep[0]);
                        inputCar.setRearRollStiffness(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "frontRollStiffness" :   
                    minMaxStep = getMinMaxStep("frontRollStiffness");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with frontRollStiffness: " + minMaxStep[0]);
                        inputCar.setFrontRollStiffness(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "rearRollCenterHeight" :
                    minMaxStep = getMinMaxStep("rearRollCenterHeight");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with rearRollCenterHeight: " + minMaxStep[0]);
                        inputCar.setRearRollCenterHeight(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                case "frontRollCenterHeight" :
                    minMaxStep = getMinMaxStep("frontRollCenterHeight");
                    for(minMaxStep[0] = minMaxStep[0]; minMaxStep[0] <= minMaxStep[1]; minMaxStep[0] += minMaxStep[2]){
                        inputTrack = new Track("track.txt");
                        System.out.println("Solving with frontRollCenterHeight: " + minMaxStep[0]);
                        inputCar.setFrontRollCenterHeight(minMaxStep[0]);
                        Solver solver = new Solver(inputTrack, inputCar);
                        logger.write(minMaxStep[0] + ", " + solver.solveLapTime());
                    }
                    break;
                default : 
                    System.out.println("ERROR: " + param + " not recognized");
            }
            System.out.println("Sensitivity study complete");
            return;
        }
        else{
            Solver solver = new Solver(inputTrack, inputCar);
            //Solve for lap time
            System.out.println("\nThe total lap time is " + solver.solveLapTime() + ". \n");
            return;
        }
        
    }

    static double[] getMinMaxStep(String param) throws IOException{
        BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));
        //Get min
        double[] minMaxStep = new double[3];
        String min = new String("");
        System.out.print("Enter the minimum value: " );
        min = new String(buf.readLine());
        while (min.isEmpty()){
            System.out.println("ERROR: No value entered");
            min = new String(buf.readLine());
        }
        minMaxStep[0] = Double.parseDouble(min);
        //Get max
        String max = new String("");
        System.out.print("Enter the max value: " );
        max = new String(buf.readLine());
        while (min.isEmpty()){
            System.out.println("ERROR: No value entered");
            max = new String(buf.readLine());
        }
        minMaxStep[1] = Double.parseDouble(max);
        //Get step
        String step = new String("");
        System.out.print("Enter the step value: " );
        step = new String(buf.readLine());
        while (step.isEmpty()){
            System.out.println("ERROR: No value entered");
            step = new String(buf.readLine());
        }
        minMaxStep[2] = Double.parseDouble(step);

        return minMaxStep;
    }
}