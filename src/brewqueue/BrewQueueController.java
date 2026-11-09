
package brewqueue;

import java.util.NoSuchElementException;

/**
 * Connects the BrewQueueModel and BrewQueueView.
 * Handles menu choices and communicates with the model.
 *
 * @author Rudra Dutta
 * @version 2026.11.08
 */
public class BrewQueueController {
    private BrewQueueModel model;
    private BrewQueueView view;

    /**
     * Creates a controller using the given model and view.
     *
     * @param model the order model
     * @param view the console view
     * @throws IllegalArgumentException if either argument is null
     */
    public BrewQueueController(BrewQueueModel model, BrewQueueView view)
    {
        if (model == null || view == null){
            throw new IllegalArgumentException(
                "Model and view cannot be null.");
        }
        this.model = model;
        this.view = view;
    }

    /**
     * Runs the main menu until the user chooses to exit.
     */
    public void run(){
        boolean running = true;

        while (running){
            view.displayMenu();
            int choice = view.readInt("Enter your choice: ");
            if (choice == 1){
                handleAddOrder();
                refreshDisplays();
            }
            else if (choice == 2){
                handleStartNextOrder();
                refreshDisplays();
            }
            else if (choice == 3){
                handleCompleteOrder();
                refreshDisplays();
            }
            else if (choice == 4){
                displayWaitingOrders();
            }
            else if (choice == 5){
                displayInProgressOrders();
            }
            else if (choice == 6){
                displayCompletedOrders();
            }
            else if (choice == 0){
                view.displayMessage("Exiting Brew Queue.");
                running = false;
            }
            else {
                view.displayMessage("Invalid menu choice. Please try again.");
            }
        }
    }

    /**
     * Collects information and adds a new order.
     */
    public void handleAddOrder()
    {
        String customerName = view.readNonBlankString("Enter customer name: ");
        String drink = view.readNonBlankString("Enter drink: ");
        int quantity = view.readInt("Enter quantity: ");
        double minutesPerDrink = view.readDouble("Enter preparation minutes per drink: ");

        try {
            Order order = model.addOrder(
                customerName,
                drink,
                quantity,
                minutesPerDrink,
                false);

            view.displayMessage("Order #" + order.getOrderId() + " was added.");
        }
        catch (IllegalArgumentException e){
            view.displayMessage("Could not add order: " + e.getMessage());
        }
        catch (IllegalStateException e){
            view.displayMessage("Could not add order: " + e.getMessage());
        }
    }

    /**
     * Starts preparing the next waiting order.
     */
    public void handleStartNextOrder()
    {
        try {
            Order order = model.startNextOrder();
            view.displayMessage("Started preparing order #" + order.getOrderId() + ".");
        }
        catch (NoSuchElementException e){
            view.displayMessage("There are no waiting orders.");
        }
        catch (IllegalStateException e){
            view.displayMessage("Could not start order: " + e.getMessage());
        }
    }

    /**
     * Completes an order that is currently in progress.
     */
    public void handleCompleteOrder(){
        int orderId = view.readInt("Enter the order ID to complete: ");

        try {
            Order order = model.completeOrder(orderId);
            view.displayMessage("Order #" + order.getOrderId() + " was completed.");
        }
        catch (IllegalArgumentException e){
            view.displayMessage("Could not complete order: " + e.getMessage());
        }
    }

    /**
     * Displays all waiting orders.
     */
    public void displayWaitingOrders(){
        view.displayWaitingOrders(model.getWaitingOrders());
    }

    /**
     * Displays all orders currently being prepared.
     */
    public void displayInProgressOrders(){
        view.displayInProgressOrders(model.getInProgressOrders());
    }

    /**
     * Displays all completed orders.
     */
    public void displayCompletedOrders(){
        view.displayCompletedOrders(model.getCompletedOrders());
    }

    /**
     * Refreshes the order displays after an action.
     */
    private void refreshDisplays(){
        displayWaitingOrders();
        displayInProgressOrders();
        displayCompletedOrders();
    }

    /**
     * Creates the model, view, and controller and starts the program.
     *
     * @param args 
     */
    public static void main(String[] args){
        BrewQueueModel model = new BrewQueueModel(1);
        BrewQueueView view = new BrewQueueView();

        BrewQueueController controller = new BrewQueueController(model, view);
        controller.run();
    }
}
