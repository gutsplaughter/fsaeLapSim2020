package lapSimulation;

public class Test {

    private Logger logger;

    Test(Car inputCar, Track inputTrack){
        System.out.print("\nPerforming self tests...");
        logger = new Logger("testLog");
        Tire tireTest = new Tire();
        
        System.out.println("H is " + inputCar.getH());
    }
}