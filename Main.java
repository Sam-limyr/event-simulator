import java.util.Scanner;
import cs2030.simulator.Manager;

public class Main {
    /**
     * Assigns Events to Servers and evaluates them.
     * Four functions are performed:
     * (1) a Manager object is created
     * (2) the Server, Customer, and Event objects are created
     * (3) the Event objects are evaluated until the Manager is empty
     * (4) the result and relevant statistics are printed out.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int seedValue = scanner.nextInt();
        int numberOfServers = scanner.nextInt();
        int numberOfCounters = scanner.nextInt();
        int maxQueueLength = scanner.nextInt();
        int numberOfCustomers = scanner.nextInt();
        double arrivalRate = scanner.nextDouble();
        double serviceRate = scanner.nextDouble();
        double restingRate = scanner.nextDouble();
        double probabilityOfResting = scanner.nextDouble();
        double probabilityOfGreedy = scanner.nextDouble();

        Manager manager = new Manager(seedValue, numberOfServers, numberOfCounters, maxQueueLength,
                numberOfCustomers, arrivalRate, serviceRate, restingRate, probabilityOfResting,
                probabilityOfGreedy);

        manager.initializeServers();

        manager.initializeCustomers();

        manager.initializeEvents();
        
        manager.evaluateAllEvents();

        System.out.println(manager.toString());
    }
}