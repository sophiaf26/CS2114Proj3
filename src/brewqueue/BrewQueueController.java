
package brewqueue;

/**
 * Connects the BrewQueueModel and BrewQueueView.
 * Handles menu choices and communicates with the model.
 *
 * @author Rudra Dutta (drudra)
 * @version 2026.11.16
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
                handleCompleteOrder();
                refreshDisplays();
            }
            else if (choice == 3){
                displayWaitingOrders();
            }
            else if (choice == 4){
                displayInProgressOrders();
            }
            else if (choice == 5){
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
     * Collects information and adds a new order using the drink menu.
     */
    public void handleAddOrder()
    {
        String customerName =
            view.readNonBlankString("Enter customer name: ");

        int drinkChoice = view.readDrinkChoice();

        String drink = getDrinkName(drinkChoice);
        double minutesPerDrink = getDrinkTime(drinkChoice);

        int quantity = view.readInt("Enter quantity: ");

        try {
            Order order = model.addOrder(
                customerName,
                drink,
                quantity,
                minutesPerDrink,
                false);

            view.displayMessage("Order #" + order.getOrderId()
                + " for " + quantity + " " + drink
                + " was added.");

            boolean assigned = false;

            for (Order active : model.getInProgressOrders()){
                if (active.getOrderId() == order.getOrderId()){
                    assigned = true;
                }
            }

            if (assigned){
                view.displayMessage("A barista has started preparing this order.");
            }
            else {
                view.displayMessage("All baristas are busy. Order added to waiting queue.");
            }
        }
        catch (IllegalArgumentException e){
            view.displayMessage("Could not add order: " + e.getMessage());
        }
        catch (IllegalStateException e){
            view.displayMessage("Could not add order: " + e.getMessage());
        }
    }

    /**
     * Returns the drink name associated with a menu selection.
     *
     * @param choice selected menu number
     * @return drink name
     */
    private String getDrinkName(int choice)
    {
        if (choice == 1){
            return "Coffee";
        }
        else if (choice == 2){
            return "Tea";
        }
        else if (choice == 3){
            return "Iced Coffee";
        }
        else if (choice == 4){
            return "Latte";
        }
        else if (choice == 5){
            return "Espresso";
        }
        else if (choice == 6){
            return "Cappuccino";
        }
        else if (choice == 7){
            return "Americano";
        }
        else if (choice == 8){
            return "Matcha";
        }
        else if (choice == 9){
            return "Cold Brew";
        }
        else {
            return "Macchiato";
        }
    }

    /**
     * Returns the preparation time associated with a menu selection.
     *
     * @param choice selected menu number
     * @return preparation time per drink
     */
    private double getDrinkTime(int choice)
    {
        if (choice == 1 || choice == 2 || choice == 3 || choice == 9){
            return 1.0;
        }
        else if (choice == 4 || choice == 10){
            return 2.0;
        }
        else if (choice == 5 || choice == 7){
            return 1.5;
        }
        else if (choice == 6){
            return 2.5;
        }
        else {
            return 3.0;
        }
    }

    /**
     * Completes an order that is currently in progress.
     * The model automatically assigns the next waiting order.
     */
    public void handleCompleteOrder(){
        int orderId = view.readInt("Enter the order ID to complete: ");

        try {
            Order order = model.completeOrder(orderId);
            view.displayMessage("Order #" + order.getOrderId()
                + " was completed.");

            view.displayMessage(
                "Any waiting orders have been assigned to available baristas.");
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
     * @param args command-line arguments
     */
    public static void main(String[] args){
        BrewQueueModel model = new BrewQueueModel(3);
        BrewQueueView view = new BrewQueueView();

        BrewQueueController controller =
            new BrewQueueController(model, view);
        controller.run();
    }
}
