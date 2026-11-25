public class Booking {
    public enum Status { CONFIRMED, WAITLISTED, CANCELLED }

    public final String id;
    public final int showId;
    public final String customerName;
    public final String phone;
    public final boolean priorityCustomer;
    public final long requestOrder;
    public Status status;
    public int row;
    public int column;

    public Booking(String id, int showId, String customerName, String phone,
                   boolean priorityCustomer, long requestOrder) {
        this.id = id;
        this.showId = showId;
        this.customerName = customerName;
        this.phone = phone;
        this.priorityCustomer = priorityCustomer;
        this.requestOrder = requestOrder;
        this.status = Status.WAITLISTED;
        this.row = -1;
        this.column = -1;
    }

    public String seatLabel() {
        return status == Status.CONFIRMED ? String.valueOf((char) ('A' + row)) + (column + 1) : "-";
    }
}
