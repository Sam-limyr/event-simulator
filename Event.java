package cs2030.simulator;

/**
 * Represents an Event object.
 * Event objects combine four fields:
 * (i) the ID of the assigned Customer object
 * (ii) the ID of the assigned Server object
 * (iii) the time of the Event occurring
 * (iv) the State of the Event occurring.
 */
class Event {

    /**
     * Represents the Customer assigned to this object.
     */
    private int customerID;

    /**
     * Represents the Server assigned to this object.
     */
    private int serverID;

    /**
     * Represents the current time of this object. This time changes as this Event is evaluated.
     */
    private double time;

    /**
     * Represents the current State of this object. This State changes as this Event is evaluated.
     */
    private State state;

    /**
     * Constructs an Event object.
     * @param customerID the ID of the assigned Customer.
     * @param serverID the ID of the assigned Server.
     * @param time the current time of the Event.
     * @param state the current State of the Event.
     */
    Event(int customerID, int serverID, double time, State state) {
        this.customerID = customerID;
        this.serverID = serverID;
        this.time = time;
        this.state = state;
    }

    
    int getCustomerID() {
        return this.customerID;
    }

    int getServerID() {
        return this.serverID;
    }

    double getTime() {
        return this.time;
    }

    /**
     * Increases the current time of this object.
     * @param addedTime the amount of time to be added.
     */
    void addTime(double addedTime) {
        time += addedTime;
    }

    /**
     * Sets the current time of this object.
     * @param newTime the new time to set this Event's time to.
     * @return returns a double representing the time difference.
     */
    double setTimeTo(double newTime) {
        double timeDifference = newTime - time;
        time = newTime;
        return timeDifference;
    }

    /**
     * Gets the current State of this object.
     * @return the current State, as a String.
     */
    String getState() {
        if (state == State.arrives) {
            return "arrives";
        } else if (state == State.served) {
            return "served";
        } else if (state == State.leaves) {
            return "leaves";
        } else if (state == State.waits) {
            return "waits";
        } else if (state == State.done) {
            return "done";
        } else if (state == State.rests) {
            return "rests";
        } else {
            return "back";
        }
    }

    /**
     * Sets the State of this object.
     * @param stateValue the new State value.
     */
    void setState(String stateValue) {
        if (stateValue.equals("served")) {
            state = State.served;
        } else if (stateValue.equals("leaves")) {
            state = State.leaves;
        } else if (stateValue.equals("waits")) {
            state = State.waits;
        } else {
            state = State.done;
        }
    }

    /**
     * Prints the details of this Event. The precise content of the String depends on:
     * (i) the time and State of this Event
     * (ii) whether the Customer assigned was a GreedyCustomer
     * (iii) whether the Server assigned was a HumanServer.
     * @return returns a String containing details of this Event.
     */
    @Override
    public String toString() {

        if (state == State.rests) {
            return String.format("%.3f", time) + " server " + serverID + " rest";
        } else if (state == State.back) {
            return String.format("%.3f", time) + " server " + serverID + " back";
        }

        String baseString;

        if (Manager.customerArray[this.customerID - 1] instanceof GreedyCustomer) {
            baseString = (String.format("%.3f", time) + " " + customerID + "(greedy) " + state);
        } else {
            baseString = (String.format("%.3f", time) + " " + customerID + " " + state);
        }

        if (state == State.arrives || state == State.leaves) {
            return baseString;
        }

        String serverString;

        if (Manager.serverArray[this.serverID - 1] instanceof HumanServer) {
            serverString = "server " + serverID;
        } else {
            serverString = "self-check " + serverID;
        }

        if (state == State.served) {
            return baseString + " by " + serverString;
        } else if (state == State.waits) {
            return baseString + " to be served by " + serverString;
        } else {
            return baseString + " serving by " + serverString;
        }
    }
}