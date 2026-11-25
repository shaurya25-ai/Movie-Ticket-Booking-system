import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final MovieTicketBooking SYSTEM = new MovieTicketBooking();

    public static void main(String[] args) {
        System.out.println("=== Movie Ticket Booking ===");
        while (true) {
            System.out.println("\n1. List shows");
            System.out.println("2. View seat map");
            System.out.println("3. Book a ticket");
            System.out.println("4. Find booking by ID");
            System.out.println("5. Cancel booking");
            System.out.println("6. Sort shows by title");
            System.out.println("7. Sort shows by price");
            System.out.println("0. Exit");
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1: printShows(SYSTEM.sortedShows(Comparator.comparingInt(s -> s.id))); break;
                    case 2: viewSeats(); break;
                    case 3: bookTicket(); break;
                    case 4: findBooking(); break;
                    case 5: cancelBooking(); break;
                    case 6: printShows(SYSTEM.sortedShows(Comparator.comparing(s -> s.title))); break;
                    case 7: printShows(SYSTEM.sortedShows(Comparator.comparingInt(s -> s.price))); break;
                    case 0: System.out.println("Goodbye!"); return;
                    default: System.out.println("Choose a number from the menu.");
                }
            } catch (IllegalArgumentException error) {
                System.out.println("Error: " + error.getMessage());
            }
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = INPUT.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException error) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine().trim();
    }

    private static MovieShow chooseShow() {
        printShows(SYSTEM.sortedShows(Comparator.comparingInt(s -> s.id)));
        MovieShow show = SYSTEM.findShow(readInt("Show ID: "));
        if (show == null) throw new IllegalArgumentException("Invalid show ID.");
        return show;
    }

    private static void printShows(List<MovieShow> shows) {
        System.out.printf("%-4s %-20s %-10s %-8s %-10s %-8s%n",
                          "ID", "Movie", "Time", "Price", "Available", "Waiting");
        for (MovieShow show : shows) {
            System.out.printf("%-4d %-20s %-10s Rs.%-5d %-10d %-8d%n",
                              show.id, show.title, show.time, show.price,
                              show.availableSeats(), show.waitlistSize());
        }
    }

    private static void viewSeats() {
        MovieShow show = chooseShow();
        System.out.println("\n" + show.title + " - " + show.time);
        System.out.println(show.seatMap());
    }

    private static void bookTicket() {
        MovieShow show = chooseShow();
        int row = -1;
        int column = -1;
        if (!show.isFull()) {
            System.out.println(show.seatMap());
            String seat = readText("Seat (for example A1): ").toUpperCase();
            if (!seat.matches("[A-E][1-8]"))
                throw new IllegalArgumentException("Enter a seat from A1 to E8.");
            row = seat.charAt(0) - 'A';
            column = seat.charAt(1) - '1';
        } else {
            System.out.println("This show is full. Your request will join the waitlist.");
        }
        String name = readText("Customer name: ");
        String phone = readText("10-digit phone number: ");
        String priorityAnswer = readText("Priority customer? (y/n): ");
        if (!priorityAnswer.equalsIgnoreCase("y") && !priorityAnswer.equalsIgnoreCase("n"))
            throw new IllegalArgumentException("Enter y or n for priority customer.");

        Booking booking = SYSTEM.book(show.id, name, phone,
                                      priorityAnswer.equalsIgnoreCase("y"), row, column);
        System.out.println("Booking ID: " + booking.id);
        System.out.println("Status: " + booking.status);
        if (booking.status == Booking.Status.CONFIRMED)
            System.out.println("Seat: " + booking.seatLabel() + " | Price: Rs." + show.price);
    }

    private static void findBooking() {
        Booking booking = SYSTEM.findBooking(readText("Booking ID: "));
        if (booking == null) throw new IllegalArgumentException("Booking ID not found.");
        MovieShow show = SYSTEM.findShow(booking.showId);
        System.out.println("ID: " + booking.id + " | Name: " + booking.customerName);
        System.out.println("Phone: " + booking.phone + " | Movie: " + show.title
                           + " | Time: " + show.time);
        System.out.println("Status: " + booking.status + " | Seat: " + booking.seatLabel());
    }

    private static void cancelBooking() {
        String id = readText("Booking ID to cancel: ");
        Booking promoted = SYSTEM.cancel(id);
        System.out.println("Booking cancelled.");
        if (promoted != null)
            System.out.println("Waitlisted booking " + promoted.id
                               + " is now confirmed for seat " + promoted.seatLabel() + ".");
    }
}
