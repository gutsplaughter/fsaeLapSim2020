package lapSimulation;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class Sim {
    public static void main(String[] args) throws Exception {


        ////////////////////////
        // TRACK FILE READ IN //
        ////////////////////////
        BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));
        String fname = new String("");
        System.out.print("\nEnter track file name or press enter for \"track.txt\": " );
        String input = new String(buf.readLine());
        Track inputTrack = null;
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
        BufferedReader buf2 = new BufferedReader (new InputStreamReader(System.in));
        String fname2 = new String("");
        System.out.print("Enter car configuration file name or press enter for \"car.txt\": " );
        String input2 = new String(buf2.readLine());
        Car inputCar = null;
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
        buf.close();
        buf2.close();
        ////////////////////////


        ///////////////////////////////////////////////////////////
        // OPEN INSTANCE OF SOLVER AND PASS IT THE CAR AND TRACK //
        ///////////////////////////////////////////////////////////

        Solver solver = new Solver(inputTrack, inputCar);
        //Solve for lap time
        System.out.println("\n\n\nThe total lap time is " + solver.solveLapTime() + ". \n");
    }
}