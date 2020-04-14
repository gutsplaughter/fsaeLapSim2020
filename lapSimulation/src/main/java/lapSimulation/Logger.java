package lapSimulation;

import java.io.BufferedWriter;
import java.io.FileWriter;



public class Logger {
    private BufferedWriter logger;

    public Logger(String name){
        try{this.logger = new BufferedWriter(new FileWriter("output logs\\" + System.currentTimeMillis() + "-" + name + ".csv"));} catch(Exception e){System.out.println("ERROR: Logger could not generate an output log.");}
    }
    public void write(String line){
        try{logger.write(line + "\n"); logger.flush();} catch(Exception e){System.out.println("ERROR: Solver could not write to output log");}
        try{logger.flush();}catch(Exception e){}
    }
    
}