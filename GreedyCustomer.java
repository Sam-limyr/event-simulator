package cs2030.simulator;

/**
 * Represents one greedy customer. GreedyCustomer objects are similar to Customer objects,
 * differing only when they choose which Server queue to join.
 */
class GreedyCustomer extends Customer {
    
    /**
     * Constructs a GreedyCustomer object.
     * @param customerID a unique ID assigned to each Customer for identification.
     * @param arrivalTime designates the arrival time for this Customer.
     */
    GreedyCustomer(int customerID, double arrivalTime) {
        super(customerID, arrivalTime);
    }

    /**
     * Selects which Server queue to join. Differs from the non-greedy Customer
     * by joining the shortest queue rather than the first non-full queue.
     * @return returns the Server ID of the selected queue that will be joined.
     */
    @Override
    int scanAllQueues() {

        int currentShortestQueue = Manager.maxQueueLength;
        int selectedServerID = 0;

        for (Server server : Manager.serverArray) {
            if (server.lengthOfQueue() < currentShortestQueue) {
                currentShortestQueue = server.lengthOfQueue();
                selectedServerID = server.getID();
            }
        }
        
        return selectedServerID;

    }
}