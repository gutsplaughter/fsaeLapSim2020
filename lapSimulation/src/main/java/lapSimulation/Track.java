package lapSimulation;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Track {
    //create a new stack to hold the elements of the track
    private Queue<Manuever> trackManuevers = new LinkedList<>();

    //Constructor to read in the track attributes
    public Track(String fname) throws IOException {
        FileReader fr=new FileReader(fname);   
        BufferedReader buf = new BufferedReader (fr);

        String line;
        System.out.print("Reading track file...");
        while ((line = buf.readLine()) != null) {
            Manuever nextManuever = new Manuever(line);
            trackManuevers.add(nextManuever);
        }
        //Add end of track element
        Manuever nextManuever = new Manuever("end");
        trackManuevers.add(nextManuever);

        System.out.println("Done\n");
        buf.close();
    }

    public Manuever getNext(){
        return (trackManuevers.remove());
    }
    public Manuever peekNext(){
        return (trackManuevers.peek());
    }
}