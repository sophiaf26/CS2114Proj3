package brewqueue;

import java.time.LocalDateTime;

// -------------------------------------------------------------------------
/**
 * Stores one customer drink selection, quantity, membership status, and
 * timestamps.
 * This model data class is immutable for the customer details, while allowing
 * the queue model to update the preparation and completion timestamps as the
 * order
 * progresses through the coffee shop workflow.
 * 
 * @author Andersson Steve Galvez Chicas
 * @version Nov 8, 2026
 */
public class Order {

    // ~ Fields ................................................................
    private int orderId;
    private String customerName;
    private String drink;
    private int quantity;
    private double minutesPerDrink;
    private boolean rewardMember;
    private LocalDateTime placedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // ~ Constructors ..........................................................
    // ----------------------------------------------------------
    /**
     * Creates an order and records the placement time. Rejects nonpositive
     * limits and blank text inputs.
     * 
     * @param orderId
     *            the unique identifier for this order
     * @param customerName
     *            the name of the customer
     * @param drink
     *            the drink selection
     * @param quantity
     *            the number of drinks ordered
     * @param minutesPerDrink
     *            the time required to make one drink
     * @param rewardMember
     *            true if the order receives priority placement
     * @throws IllegalArgumentException
     *             if numbers are not positive/finite or strings are blank
     */
    public Order(
        int orderId,
        String customerName,
        String drink,
        int quantity,
        double minutesPerDrink,
        boolean rewardMember) {
        if (orderId <= 0 || quantity <= 0 || minutesPerDrink <= 0 || !Double
            .isFinite(minutesPerDrink)) {
            throw new IllegalArgumentException(
                "ID, quantity, and time must be positive finite numbers.");

        }

        if (customerName == null || customerName.trim().isEmpty()
            || drink == null || drink.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Customer name and drink cannot be blank.");
        }

        this.orderId = orderId;
        this.customerName = customerName;
        this.drink = drink;
        this.quantity = quantity;
        this.minutesPerDrink = minutesPerDrink;
        this.rewardMember = rewardMember;
        this.placedAt = LocalDateTime.now();
    }


    // ~Public Methods ........................................................
    // ----------------------------------------------------------
    /**
     * Returns the unique order ID.
     * 
     * @return the order ID
     */
    public int getOrderId() {
        return orderId;
    }


    // ----------------------------------------------------------
    /**
     * Returns the customer name.
     * 
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }


    // ----------------------------------------------------------
    /**
     * Returns the drink selection.
     * 
     * @return the drink name
     */
    public String getDrink() {
        return drink;
    }


    // ----------------------------------------------------------
    /**
     * Returns the number of drinks in the order.
     * 
     * @return the quantity ordered
     */
    public int getQuantity() {
        return quantity;
    }


    // ----------------------------------------------------------
    /**
     * Returns whether the order receives reward priority.
     * 
     * @return true if priority is granted, false otherwise
     */
    public boolean isRewardMember() {
        return rewardMember;
    }


    // ----------------------------------------------------------
    /**
     * Calculates the total preparation time for the order.
     * 
     * @return quantity multiplied by minutes per drink
     */
    public double getPreparationMinutes() {
        return quantity * minutesPerDrink;
    }


    // ----------------------------------------------------------
    /**
     * Returns the placement timestamp.
     * 
     * @return the time the order was created
     */
    public LocalDateTime getPlacedAt() {
        return placedAt;
    }


    // ----------------------------------------------------------
    /**
     * Returns the start timestamp.
     * 
     * @return the time preparation began, or null if not yet started
     */
    public LocalDateTime getStartedAt() {
        return startedAt;
    }


    // ----------------------------------------------------------
    /**
     * Returns the completion timestamp.
     * 
     * @return the time the order finished, or null if not yet complete
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }


    // ----------------------------------------------------------
    /**
     * Records the start time.
     * 
     * @throws IllegalStateException
     *             if the order is already started
     */
    public void markStarted() {
        if (this.startedAt != null) {
            throw new IllegalStateException("Order already started.");
        }
        this.startedAt = LocalDateTime.now();
    }


    // ----------------------------------------------------------
    /**
     * Records the completion time.
     * 
     * @throws IllegalStateException
     *             if the order is not started or is already complete
     */
    public void markCompleted() {
        if (this.startedAt == null) {
            throw new IllegalStateException("Cannot complete unstarted order.");
        }
        if (this.completedAt != null) {
            throw new IllegalStateException("Order already complete.");
        }
        this.completedAt = LocalDateTime.now();
    }


    // ----------------------------------------------------------
    /**
     * Returns a readable summary of the order.
     * 
     * @return formatted string containing ID, name, quantity, and drink
     */
    @Override
    public String toString() {
        return String.format("Order #%d: %s (%dx %s)", orderId, customerName,
            quantity, drink);
    }
}
