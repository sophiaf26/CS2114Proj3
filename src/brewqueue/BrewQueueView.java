package brewqueue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Console view for Brew Queue. Displays the data it is given and collects
 * user input. It owns no queue or model reference and never calls the model;
 * the controller supplies everything it shows.
 *
 * @author Sophia Fussman
 * @version 2026.11.08
 */
public class BrewQueueView {
    private Scanner input;

    /**
     * Creates the console view and its input reader.
     */
    public BrewQueueView()
    {
        input = new Scanner(System.in);
    }

    /**
     * Displays the available actions and the exit option.
     */
    public void displayMenu()
    {
        System.out.println();
        System.out.println("===== Brew Queue =====");
        System.out.println("1. Add order");
        System.out.println("2. Start next order");
        System.out.println("3. Complete order");
        System.out.println("4. Show waiting orders");
        System.out.println("5. Show in-progress orders");
        System.out.println("6. Show completed orders");
        System.out.println("0. Exit");
    }

    /**
     * Displays a confirmation, error, or instructional message.
     *
     * @param message the message to show
     */
    public void displayMessage(String message)
    {
        System.out.println(message);
    }

    /**
     * Displays the waiting orders in queue order, marking the first as next.
     *
     * @param orders the waiting orders, front first
     */
    public void displayWaitingOrders(Order[] orders)
    {
        System.out.println();
        System.out.println("--- Waiting Orders ---");
        if (orders == null || orders.length == 0) {
            System.out.println("No orders are waiting.");
            return;
        }
        for (int i = 0; i < orders.length; i++) {
            String marker = (i == 0) ? " <- next" : "";
            System.out.println((i + 1) + ". " + describe(orders[i])
                + marker);
        }
    }

    /**
     * Displays the in-progress orders with their IDs and elapsed times.
     *
     * @param orders the orders being prepared
     */
    public void displayInProgressOrders(Order[] orders)
    {
        System.out.println();
        System.out.println("--- In Progress ---");
        if (orders == null || orders.length == 0) {
            System.out.println("No orders are being prepared.");
            return;
        }
        for (Order order : orders) {
            String elapsed = "";
            if (order.getStartedAt() != null) {
                elapsed = String.format(" - %.1f min elapsed (est. %.1f)",
                    minutesBetween(order.getStartedAt(),
                        LocalDateTime.now()),
                    order.getPreparationMinutes());
            }
            System.out.println(describe(order) + elapsed);
        }
    }

    /**
     * Displays the completed orders, or an empty-history message.
     *
     * @param orders the completed orders
     */
    public void displayCompletedOrders(Order[] orders)
    {
        System.out.println();
        System.out.println("--- Completed Orders ---");
        if (orders == null || orders.length == 0) {
            System.out.println("No orders have been completed yet.");
            return;
        }
        for (Order order : orders) {
            String took = "";
            if (order.getStartedAt() != null
                && order.getCompletedAt() != null) {
                took = String.format(" - took %.1f min",
                    minutesBetween(order.getStartedAt(),
                        order.getCompletedAt()));
            }
            System.out.println(describe(order) + took);
        }
    }

    /**
     * Displays the counts and average wait supplied by the controller.
     *
     * @param waitingCount number of waiting orders
     * @param inProgressCount number of in-progress orders
     * @param completedCount number of completed orders
     * @param averageWaitMinutes mean wait before starting, in minutes
     */
    public void displayStatistics(
        int waitingCount,
        int inProgressCount,
        int completedCount,
        double averageWaitMinutes)
    {
        System.out.println();
        System.out.println("--- Statistics ---");
        System.out.println("Waiting:     " + waitingCount);
        System.out.println("In progress: " + inProgressCount);
        System.out.println("Completed:   " + completedCount);
        System.out.println(String.format("Average wait: %.1f min",
            averageWaitMinutes));
    }

    /**
     * Displays a wait estimate, or a no-staff message when minutes is -1.
     *
     * @param orderId the order the estimate is for
     * @param minutes estimated minutes until it starts, or -1 if no staff
     */
    public void displayEstimatedWait(int orderId, double minutes)
    {
        if (minutes == -1) {
            System.out.println("Order #" + orderId
                + ": no baristas are on duty, so no estimate is available.");
        }
        else {
            System.out.println(String.format(
                "Order #%d should start in about %.1f min.",
                orderId, minutes));
        }
    }

    /**
     * Displays a reminder for each overdue order; nothing if none.
     *
     * @param orders the overdue in-progress orders
     */
    public void displayOverdueOrders(Order[] orders)
    {
        if (orders == null || orders.length == 0) {
            return;
        }
        System.out.println();
        for (Order order : orders) {
            System.out.println("REMINDER: " + describe(order)
                + " is running over its estimated time.");
        }
    }

    /**
     * Prompts until the input has non-whitespace text.
     *
     * @param prompt the prompt to show
     * @return the trimmed text
     */
    public String readNonBlankString(String prompt)
    {
        while (true) {
            System.out.print(prompt);
            String line = nextLine();
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("Input cannot be blank. Please try again.");
        }
    }

    /**
     * Prompts until the input parses as an integer.
     *
     * @param prompt the prompt to show
     * @return the integer entered
     */
    public int readInt(String prompt)
    {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(nextLine().trim());
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Prompts until the input parses as a finite decimal.
     *
     * @param prompt the prompt to show
     * @return the number entered
     */
    public double readDouble(String prompt)
    {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(nextLine().trim());
                if (Double.isFinite(value)) {
                    return value;
                }
            }
            catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Prompts until yes or no is entered.
     *
     * @param prompt the prompt to show
     * @return true for yes, false for no
     */
    public boolean readYesNo(String prompt)
    {
        while (true) {
            System.out.print(prompt);
            String answer = nextLine().trim().toLowerCase();
            if (answer.equals("yes") || answer.equals("y")) {
                return true;
            }
            if (answer.equals("no") || answer.equals("n")) {
                return false;
            }
            System.out.println("Please enter yes or no.");
        }
    }

    /**
     * Reads one line. If input has ended, exits cleanly instead of looping
     * forever or throwing.
     */
    private String nextLine()
    {
        if (!input.hasNextLine()) {
            System.out.println();
            System.out.println("Input ended. Exiting Brew Queue.");
            System.exit(0);
        }
        return input.nextLine();
    }

    private String describe(Order order)
    {
        String reward = order.isRewardMember() ? " [reward]" : "";
        return order.toString() + reward;
    }

    private double minutesBetween(LocalDateTime from, LocalDateTime to)
    {
        return Duration.between(from, to).toMillis() / 60000.0;
    }
}
