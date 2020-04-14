package lapSimulation;

import java.util.Scanner;

public class Manuever {
    public boolean isStraight;
    public double distance;
    public double radius;
    public boolean endOfTrack;


    public Manuever(String line){
        Scanner myScanner = new Scanner(line);
        if (line.contains("s")){
            isStraight = true;
            distance = myScanner.nextDouble();
            radius = -1;
        }
        else if (line.contains("c")){
            isStraight = false;
            radius = myScanner.nextDouble();
            myScanner.next();
            double curvature = myScanner.nextDouble();
            distance = 2*3.14159*radius*(curvature/100);
        }
        else if (line.contains("end")){
           endOfTrack = true;
        }
        else{
            System.out.println("ERROR: A track manuever has invalid format. Specify 's' for straight and 'c' for curve.");
        }
        myScanner.close();
    }

    public boolean isStraight(){
        return isStraight;
    }
    public double getDistance(){
        return distance;
    }
    public double getRadius(){
        return radius;
    }
    public boolean getEndOfTrack(){
        return endOfTrack;
    }
    public String toString(){
        return ("Manuever with attributes -- straight: " + isStraight + ", distance: " + distance + ", radius: " + radius + ", end of track: " + endOfTrack);
    }
}