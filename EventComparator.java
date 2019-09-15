package cs2030.simulator;

import java.util.Comparator;

/**
 * Compares Event objects.
 * The Comparator checks for:
 * (i) Event time, where the earlier Event takes priority,
 * (ii) if both Event times are the same, a SERVER_REST or SERVER_BACK Event takes priority,
 * (iii) if neither is the case, the Event with the smaller customer ID takes priority.
 */
class EventComparator implements Comparator<Event> {
    public int compare(Event firstEvent, Event secondEvent) {
        if (firstEvent.getTime() == secondEvent.getTime()) {
            if (secondEvent.getState().equals("rests") || secondEvent.getState().equals("back")) {
                return 1;
            } else {
                return firstEvent.getCustomerID() - secondEvent.getCustomerID();
            }
        } else {
            return (int) Math.signum(firstEvent.getTime() - secondEvent.getTime());
        }
    }
}