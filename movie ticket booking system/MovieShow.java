import java.util.Comparator;
import java.util.PriorityQueue;

public class MovieShow {
    public static final int ROWS = 5;
    public static final int COLUMNS = 8;

    public final int id;
    public final String title;
    public final String time;
    public final int price;

    // A 2D array stores the booking ID assigned to each seat; null means available.
    private final String[][] seats = new String[ROWS][COLUMNS];

    // Priority customers go first. Within the same group, requests stay in arrival order.
    private final PriorityQueue<Booking> waitlist = new PriorityQueue<>(
        Comparator.comparing((Booking b) -> b.priorityCustomer).reversed()
                  .thenComparingLong(b -> b.requestOrder)
    );

    public MovieShow(int id, String title, String time, int price) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.price = price;
    }

    public boolean isAvailable(int row, int column) {
        return row >= 0 && row < ROWS && column >= 0 && column < COLUMNS
            && seats[row][column] == null;
    }

    public boolean isFull() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (seats[row][column] == null) return false;
            }
        }
        return true;
    }

    public int availableSeats() {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (seats[row][column] == null) count++;
            }
        }
        return count;
    }

    public void assignSeat(Booking booking, int row, int column) {
        seats[row][column] = booking.id;
        booking.row = row;
        booking.column = column;
        booking.status = Booking.Status.CONFIRMED;
    }

    public void releaseSeat(Booking booking) {
        seats[booking.row][booking.column] = null;
    }

    public void addToWaitlist(Booking booking) {
        waitlist.add(booking);
    }

    public Booking nextWaitingBooking() {
        while (!waitlist.isEmpty()) {
            Booking booking = waitlist.poll();
            if (booking.status == Booking.Status.WAITLISTED) return booking;
        }
        return null;
    }

    public int waitlistSize() {
        int count = 0;
        for (Booking booking : waitlist) {
            if (booking.status == Booking.Status.WAITLISTED) count++;
        }
        return count;
    }

    public String seatMap() {
        StringBuilder map = new StringBuilder("       1  2  3  4  5  6  7  8\n");
        for (int row = 0; row < ROWS; row++) {
            map.append("    ").append((char) ('A' + row)).append("  ");
            for (int column = 0; column < COLUMNS; column++) {
                map.append(seats[row][column] == null ? "O" : "X").append("  ");
            }
            map.append('\n');
        }
        map.append("    O = available, X = booked");
        return map.toString();
    }
}
