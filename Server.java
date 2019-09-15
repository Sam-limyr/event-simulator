package cs2030.simulator;

import java.util.Queue;
import java.util.LinkedList;

/**
 * Represents a Server that modifies Events. 
 * Server objects evaluate the new State of an Event depending on
 * (i) current State of Event
 * (ii) internal variables of Server.
 */
class Server {

    /**
     * Unique ID of this Server.
     */
    private int serverID;

    /**
     * Next available serving time of this Server.
     */
    private double nextAvailable;

    /**
     * Represents whether this Server is currently serving a Customer.
     */
    private boolean isServingCustomer;

    /**
     * Represents the maximum allowed queue length of this Server.
     */
    private int maxQueueLength;

    /**
     * Is the Customer Queue of this Server. Populated by Events that represent Customers.
     */
    private Queue<Event> customerQueue;
    
    /**
     * Represents the total number of Customers arrived across all Servers.
     */
    static int numberOfCustomers;

    /**
     * Represents the total number of Customers served across all Servers.
     */
    static int numberOfCustomersServed;

    /**
     * Represents the total amount of time waited by all Customers across all Servers.
     */
    static double totalTimeWaited;

    /**
     * Creates a Server object.
     * @param serverID the unique ID of this Server object.
     * @param nextAvailable the next available serving time for this Server object.
     * @param maxQueueLength the maximum allowed Customer queue length.
     */
    Server(int serverID, double nextAvailable, int maxQueueLength) {
        this.serverID = serverID;
        this.nextAvailable = nextAvailable;
        this.maxQueueLength = maxQueueLength;
        this.customerQueue = new LinkedList<Event>();
    }

    int getID() {
        return this.serverID;
    }

    double getTime() {
        return this.nextAvailable;
    }

    /**
     * Sets the next available time of this Server object.
     * When the next available time is set, all Customers waiting in the queue will have their
     * waiting time increased by the time difference.
     * @param newTime the new time to be set to.
     */
    void setTime(double newTime) {
        double timeDifference = newTime - nextAvailable;
        this.nextAvailable = newTime;
        for (Event event : customerQueue) {
            event.addTime(timeDifference);
            Server.totalTimeWaited += timeDifference;
        }
    }

    boolean isServingCustomer() {
        return isServingCustomer;
    }
    
    boolean hasFullQueue() {
        return customerQueue.size() >= maxQueueLength;
    }
    
    int lengthOfQueue() {
        return customerQueue.size();
    }
    
    /**
     * Evaluates an Event if their State is Arrives. The new State of the Event will be one of
     * Leaves, Waits, or Served, depending on whether this Server is serving a Customer, and
     * whether this Server's Customer queue is full.
     * @param event the Event to be evaluated.
     * @return returns the evaluated and modified Event.
     */
    Event customerArrives(Event event) {
        if (event.getTime() < this.nextAvailable) {

            if (this.hasFullQueue()) {
                event.setState("leaves");
            } else {
                event.setState("waits");
            }

        } else {
            event.setState("served");
        }

        return event;
    }
    
    /**
     * Evaluates an Event if their State is Served. The new State of the Event will be Done.
     * It will take a randomly-generated amount of time to finish serving this Customer, so
     * the next available time of this Server is incremented.
     * @param event the Event to be evaluated.
     * @return returns the evaluated and modified Event.
     */
    Event customerServed(Event event) {
        event.addTime(Manager.randGen.genServiceTime());
        event.setState("done");

        this.setTime(event.getTime());
        this.isServingCustomer = true;
        Server.numberOfCustomersServed++;

        return event;
    }

    /**
     * Evaluates an Event if their State is Waits. The new State of the Event will be Served.
     * The Customer will wait until this Server is next available. The total waiting time is
     * incremented.
     * @param event the Event to be evaluated.
     * @return returns the evaluated and modified Event.
     */
    Event customerWaits(Event event) {
        event.setState("served");

        this.customerQueue.offer(event);
        Server.totalTimeWaited += event.setTimeTo(this.nextAvailable);

        return event;
    }

    /**
     * Evaluates an Event if their State is Done. The Event will not be modified, and will not be
     * re-inserted into the EventQueue. If there is a queue at this Server, the next Customer in
     * line will immediately be served. Otherwise, the Server will be available to serve a new
     * Customer.
     * @param event the Event to be evaluated.
     */
    void customerDone(Event event) {
        if (!customerQueue.isEmpty()) {
            this.customerQueue.poll();
        } else {
            this.isServingCustomer = false;
        }
    }

    /**
     * Represents this Server returning from a break. If there is a queue at this Server,
     * the next Customer in line will immediately be served. Otherwise, the Server will be
     * available to serve a new Customer.
     */
    void serverBack() {
        if (!customerQueue.isEmpty()) {
            this.customerQueue.poll();
        } else {
            this.isServingCustomer = false;
        }
    }
}