import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieTicketBooking {
    private final List<MovieShow> shows = new ArrayList<>();

    // Hashing makes a booking lookup by ID fast on average.
    private final Map<String, Booking> bookingsById = new HashMap<>();
    private int nextBookingNumber = 1;
    private long nextRequestOrder = 1;

    public MovieTicketBooking() {
        shows.add(new MovieShow(1, "Inception", "10:00 AM", 180));
        shows.add(new MovieShow(2, "Interstellar", "2:00 PM", 220));
        shows.add(new MovieShow(3, "The Dark Knight", "6:00 PM", 200));
    }

    public MovieShow findShow(int showId) {
        for (MovieShow show : shows) {
            if (show.id == showId) return show;
        }
        return null;
    }

    public Booking findBooking(String bookingId) {
        return bookingsById.get(bookingId.toUpperCase());
    }

    public List<MovieShow> sortedShows(Comparator<MovieShow> comparator) {
        List<MovieShow> result = new ArrayList<>(shows);
        result.sort(comparator.thenComparingInt(show -> show.id));
        return result;
    }

    public Booking book(int showId, String name, String phone, boolean priority,
                        int row, int column) {
        MovieShow show = findShow(showId);
        if (show == null) throw new IllegalArgumentException("Invalid show ID.");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Customer name is required.");
        if (phone == null || !phone.matches("[0-9]{10}"))
            throw new IllegalArgumentException("Enter a 10-digit phone number.");
        if (!show.isFull() && !show.isAvailable(row, column))
            throw new IllegalArgumentException("Choose an available seat from the map.");

        String id = String.format("B%04d", nextBookingNumber++);
        Booking booking = new Booking(id, showId, name.trim(), phone, priority,
                                      nextRequestOrder++);
        bookingsById.put(id, booking);
        if (show.isFull()) {
            show.addToWaitlist(booking);
        } else {
            show.assignSeat(booking, row, column);
        }
        return booking;
    }

    // Returns the waitlisted booking promoted into the cancelled seat, if any.
    public Booking cancel(String bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking == null) throw new IllegalArgumentException("Booking ID not found.");
        if (booking.status == Booking.Status.CANCELLED)
            throw new IllegalArgumentException("This booking is already cancelled.");

        MovieShow show = findShow(booking.showId);
        if (booking.status == Booking.Status.WAITLISTED) {
            // It is skipped when it reaches the head of the priority queue.
            booking.status = Booking.Status.CANCELLED;
            return null;
        }

        int row = booking.row;
        int column = booking.column;
        show.releaseSeat(booking);
        booking.status = Booking.Status.CANCELLED;
        booking.row = -1;
        booking.column = -1;

        Booking next = show.nextWaitingBooking();
        if (next != null) show.assignSeat(next, row, column);
        return next;
    }
}
