package cs2030.simulator;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;
import cs2030.simulator.RandomGenerator;

/**
 * Evaluates the order of Events. 'Manager' contains methods for
 * (i) creating Customer, Server, and Event objects and storing them
 * (ii) determining which Customer and Server objects are assigned to which Event objects
 * (iii) maintaining and modifying an internal PriorityQueue to sort Events.
 */
public class Manager {

    /**
     * Sorts the Event objects using an EventComparator.
     */
    private static PriorityQueue<Event> eventQueue;

    /**
     * Contains the Customer objects.
     */
    static Customer[] customerArray;

    /**
     * Contains the Server objects.
     */
    static Server[] serverArray;

    /**
     * A RandomGenerator that generates numbers.
     */
    static RandomGenerator randGen;

    /**
     * Represents the maximum allowed queue length per Server.
     */
    static int maxQueueLength;

    /**
     * Represents the probability that a HumanServer will rest after serving.
     */
    static double probabilityOfResting;

    /**
     * Represents the number of HumanServer objects to create.
     */
    private int numberOfServers;

    /**
     * Represents the number of normal Server objects to create.
     */
    private int numberOfCounters;
    
    /**
     * Represents the number of Customer objects to create.
     */
    private int numberOfCustomers;

    /**
     * Represents the probability that a new Customer object will be a GreedyCustomer.
     */
    private double probabilityOfGreedy;
    
    /**
     * Constructs a Manager object.
     * @param seedValue is the seed value used for the RandomGenerator object.
     * @param numberOfServers sets the number of HumanServers to create.
     * @param numberOfCounters sets the number of normal Servers to create.
     * @param maxQueueLength is the maximum allowed queue length per Server.
     * @param numberOfCustomers sets the number of Customers to create.
     * @param arrivalRate is the arrival rate of Customers, used for the RandomGenerator object.
     * @param serviceRate is the service rate of Servers, used for the RandomGenerator object.
     * @param restingRate is the resting rate of HumanServers, used for the RandomGenerator object.
     * @param probabilityOfResting is the probability of resting for HumanServers.
     * @param probabilityOfGreedy is the probability of a GreedyCustomer arriving.
     */
    public Manager(int seedValue, int numberOfServers, int numberOfCounters, int maxQueueLength,
            int numberOfCustomers, double arrivalRate, double serviceRate, double restingRate,
            double probabilityOfResting, double probabilityOfGreedy) {
        this.numberOfServers = numberOfServers;
        this.numberOfCounters = numberOfCounters;
        this.maxQueueLength = maxQueueLength; 
        this.numberOfCustomers = numberOfCustomers;
        this.probabilityOfResting = probabilityOfResting;
        this.probabilityOfGreedy = probabilityOfGreedy;
        this.randGen = new RandomGenerator(seedValue, arrivalRate, serviceRate, restingRate);
        serverArray = new Server[numberOfServers + numberOfCounters];
        customerArray = new Customer[numberOfCustomers];
        eventQueue = new PriorityQueue<Event>(new EventComparator());
    }
    
    /**
     * Adds an Event to the PriorityQueue.
     * @param event the Event to be added.
     */
    static void addToEventQueue(Event event) {
        eventQueue.offer(event);
    }

    /**
     * Initializes the Server objects. A numberOfServers number of HumanServers and a
     * numberOfCounters number of Servers will be created. Server objects are stored in an array.
     */
    public void initializeServers() {
        for (int i = 0; i < numberOfServers; i++) {
            Server newServer = new HumanServer(i + 1, 0.000, maxQueueLength);
            serverArray[i] = newServer;
        }
        for (int i = numberOfServers; i < numberOfServers + numberOfCounters; i++) {
            Server newServer = new Server(i + 1, 0.000, maxQueueLength);
            serverArray[i] = newServer;
        }
    }

    /**
     * Initializes the Customer objects. A numberOfCustomers number of Customers are created.
     * The values of the RandomGenerator object determine their arrival times and whether
     * they are GreedyCustomers. Customer objects are stored in an array.
     */
    public void initializeCustomers() {
        double currentTime = 0;
        for (int i = 0; i < numberOfCustomers; i++) {
            Server.numberOfCustomers++;
            double timeToNextArrival = randGen.genInterArrivalTime();
            
            Customer nextCustomer;
            if (randGen.genCustomerType() < probabilityOfGreedy) {
                nextCustomer = new GreedyCustomer(Server.numberOfCustomers, currentTime);
            } else {
                nextCustomer = new Customer(Server.numberOfCustomers, currentTime);
            }
            
            currentTime += timeToNextArrival;
            customerArray[i] = nextCustomer;
        }
    }

    /**
     * Initializes the Event objects. Event objects are created in a one-to-one ratio with
     * Customer objects, and each Event object represents its respective Customer object.
     * Event objects are initialized with no assigned Server object, and are inserted into
     * the PriorityQueue of Events.
     */
    public void initializeEvents() {
        for (Customer customer : customerArray) {
            Event event = new Event(customer.getID(), 0, customer.getTime(), State.arrives);
            eventQueue.offer(event);
        }
    }

    /**
     * Updates the PriorityQueue of Events. The PriorityQueue must be re-sorted after the time
     * values of the Event objects are modified in-place.
     */
    void reorderEventQueue() {
        Queue<Event> rearrangementQueue = new LinkedList<Event>();
        while (!eventQueue.isEmpty()) {
            rearrangementQueue.offer(eventQueue.poll());
        }
        while (!rearrangementQueue.isEmpty()) {
            eventQueue.offer(rearrangementQueue.poll());
        }
    }

    /**
     * Assigns a Server to a Customer. The choice of Server depends on whether the Customer is
     * greedy or not. If the Customer is greedy:
     * (i) the Customer goes to the first available Server, if any,
     * (ii) else the Customer goes to the first shortest queue, if any,
     * (iii) else the Customer leaves.
     * If the Customer is not greedy:
     * (i) the Customer goes to the first available Server, if any,
     * (ii) else the Customer goes to the first available queue, if any,
     * (iii) else the Customer leaves.
     * @param customer the Customer that the Server is being assigned to.
     */
    void assignServer(Customer customer) {
        boolean customerServed = false;

        for (Server server : serverArray) {
            if (!server.isServingCustomer()) {
                eventQueue.offer(new Event(customer.getID(), server.getID(), customer.getTime(),
                    State.arrives));
                customerServed = true;
                break;
            }
        }

        if (!customerServed) {
            int selectedServerID = customer.scanAllQueues();

            if (selectedServerID != 0) {
                eventQueue.offer(new Event(customer.getID(), selectedServerID, customer.getTime(),
                    State.arrives));
                customerServed = true;
            }
        }

        if (!customerServed) {
            Event event = new Event(customer.getID(), 0, customer.getTime(), State.arrives);
            System.out.println(event.toString());
            event.setState("leaves");
            System.out.println(event.toString());
        }
    }

    /**
     * Sends an Event to its assigned Server for evaluation. The method which the Server
     * executes depends on the current State of the Event.
     * @param event the Event object to be evaluated.
     */
    void doEventLogic(Event event) {
        String eventState = event.getState();
        Server server = serverArray[event.getServerID() - 1];

        System.out.println(event.toString());

        if (eventState.equals("arrives")) {

            eventQueue.offer(server.customerArrives(event));

        } else if (eventState.equals("served")) {

            eventQueue.offer(server.customerServed(event));
            reorderEventQueue();

        } else if (eventState.equals("waits")) {

            eventQueue.offer(server.customerWaits(event));

        } else if (eventState.equals("done")) {

            server.customerDone(event);

        } else if (eventState.equals("rests")) {

            reorderEventQueue();

        } else if (eventState.equals("back")) { 

            server.serverBack();

        }
    }

    /**
     * Evaluates the next Event in the PriorityQueue. If the Event does not have a Server assigned
     * to it, a Server is assigned to it. If the Event already has a Server assigned to it, then
     * the Server is tasked with evaluating it.
     */
    void evaluateNextEvent() {
        Event event = eventQueue.poll();
        if (event.getServerID() == 0) {
            assignServer(customerArray[event.getCustomerID() - 1]);
        } else {
            doEventLogic(event);
        }
    }
    
    /**
     * Evaluates all Events in the PriorityQueue. Stops evaluating only when PriorityQueue is
     * empty.
     */
    public void evaluateAllEvents() {
        while (!eventQueue.isEmpty()) {
            evaluateNextEvent();
        }
    }

    /**
     * Prints the statistics of the simulation.
     * @return a String containing the statistics.
     */
    @Override
    public String toString() {
        int count = Server.numberOfCustomersServed;
        double timeWaited = count == 0 ? 0 : Server.totalTimeWaited / (double) count;
        return ("[" + String.format("%.3f", 
            timeWaited) + " " + count + " " + (Server.numberOfCustomers - count) + "]");
    }
}