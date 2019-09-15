package cs2030.simulator;

/**
 * Represents a HumanServer object. Human Servers differ from normal Server objects because they
 * take breaks occasionally. Apart from that, HumanServer objects function the same way as normal
 * Server objects.
 */
class HumanServer extends Server {

    /**
     * Creates a HumanServer object.
     * @param serverID the unique ID of this Server object.
     * @param nextAvailable the next available serving time for this Server object.
     * @param maxQueueLength the maximum allowed Customer queue length.
     */
    HumanServer(int serverID, double nextAvailable, int maxQueueLength) {
        super(serverID, nextAvailable, maxQueueLength);
    }

    /**
     * Evaluates an Event if their State is Done. The Event will not be modified, and will not be
     * re-inserted into the EventQueue. There is a possibility that this HumanServer will take a
     * break after finishing a service. If it does, it will rest. Else, if there is a queue at this
     * Server, the next Customer in line will immediately be served. Otherwise, the Server will be
     * available to serve a new Customer.
     * @param event the Event to be evaluated.
     */
    @Override
    void customerDone(Event event) {
        if (Manager.randGen.genRandomRest() < Manager.probabilityOfResting) {

            this.serverRest();

        } else {

            this.serverBack();

        }
    }

    /**
     * Represents the Server going for a break. A SERVER_REST Event is scheduled immediately.
     * As the Server rests, all Customers in its queue will wait for as long as the Server is
     * resting. A SERVER_BACK Event is scheduled at the end of the break.
     */
    void serverRest() {
        double restPeriod = Manager.randGen.genRestPeriod();
        Manager.addToEventQueue(new Event(0, this.getID(), this.getTime(), State.rests));
        this.setTime(this.getTime() + restPeriod);
        Manager.addToEventQueue(new Event(0, this.getID(), this.getTime(), State.back));
    }
}