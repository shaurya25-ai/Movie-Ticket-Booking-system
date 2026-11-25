public class MovieTicketBookingTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        MovieTicketBooking system = new MovieTicketBooking();
        MovieShow show = system.findShow(1);
        Booking first = null;

        for (int row = 0; row < MovieShow.ROWS; row++) {
            for (int column = 0; column < MovieShow.COLUMNS; column++) {
                Booking booking = system.book(1, "Customer", "9876543210", false, row, column);
                if (first == null) first = booking;
            }
        }
        check(show.isFull(), "The show should be full after 40 bookings.");

        Booking regular1 = system.book(1, "Regular 1", "9876543210", false, -1, -1);
        Booking regular2 = system.book(1, "Regular 2", "9876543210", false, -1, -1);
        Booking priority = system.book(1, "Priority", "9876543210", true, -1, -1);
        check(show.waitlistSize() == 3, "Three customers should be waitlisted.");

        check(system.cancel(first.id) == priority, "Priority customer should receive the first seat.");
        check(priority.status == Booking.Status.CONFIRMED && priority.seatLabel().equals("A1"),
              "Priority customer should receive seat A1.");
        check(system.cancel(priority.id) == regular1, "First regular customer should be next.");
        check(system.cancel(regular1.id) == regular2, "Second regular customer should follow.");
        check(system.findBooking(first.id).status == Booking.Status.CANCELLED,
              "Cancelled booking should remain searchable by ID.");

        System.out.println("All booking checks passed.");
    }
}
