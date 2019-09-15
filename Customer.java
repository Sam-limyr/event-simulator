package cs2030.simulator;

/**
 * Represents one customer. Contains its unique customer ID and arrival time.
 */
class Customer {

    /**
     * Unique ID of this Customer object.
     */
    private int customerID;

    /**
     * Time of arrival of this Customer object.
     */
    private double arrivalTime;

    /**
     * Constructs a Customer object.
     * @param customerID a unique ID assigned to each Customer for identification.
     * @param arrivalTime designates the arrival time for this Customer.
     */
    Customer(int customerID, double arrivalTime) {
        this.customerID = customerID;
        this.arrivalTime = arrivalTime;
    }

    int getID() {
        return this.customerID;
    }

    double getTime() {
        return this.arrivalTime;
    }

    /**
     * Selects which Server queue to join.
     * @return returns the ServerID of the selected queue that will be joined.
     */
    int scanAllQueues() {
        int selectedServerID = 0;
        for (Server server : Manager.serverArray) {
            if (!server.hasFullQueue()) {
                selectedServerID = server.getID();
                break;
            }
        }
        return selectedServerID;
    }
}