package be.howest.ti.adria.logic.domain;

public class Notification {
    private final Event event;
    private final String company;
    private final boolean read;


    public Notification(Event event, String company, boolean read) {
        this.event = event;
        this.company = company;
        this.read = read;
    }


    public Event getEvent() {
        return event;
    }

    public String getCompany() {
        return company;
    }

    public boolean isRead() {
        return read;
    }
}
