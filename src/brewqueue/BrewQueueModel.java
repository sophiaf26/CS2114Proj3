package brewqueue;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Stores orders and applies the coffee shop's business rules.
 * No console input, output, or view dependency belongs in this class.
 * Uses the Order and LinkedQueue APIs in the project specification.
 * One barista prepares one whole order at a time.
 * 
 * @author Will Fleming
 * @version 2026.11.07
 */
public class BrewQueueModel
{
    private LinkedQueue<Order> waitingOrders;
    private List<Order> inProgressOrders;
    private List<Order> completedOrders;
    private int activeBaristas;
    private int nextOrderId;

    /**
     * Creates an empty model.
     *
     * @param activeBaristas initial number of baristas
     * @throws IllegalArgumentException if staffing is negative
     */
    public BrewQueueModel(int activeBaristas)
    {
        if (activeBaristas < 0)
        {
            throw new IllegalArgumentException(
                "Barista count cannot be negative.");
        }

        this.activeBaristas = activeBaristas;
        waitingOrders = new LinkedQueue<Order>();
        inProgressOrders = new ArrayList<Order>();
        completedOrders = new ArrayList<Order>();
        nextOrderId = 1;
    }

    /**
     * Creates and queues a validated order.
     * Reward orders go to the very front.
     *
     * @param customerName customer name
     * @param drink drink selection
     * @param quantity number of drinks
     * @param minutesPerDrink preparation minutes per drink
     * @param rewardMember whether the order receives priority
     * @return the newly created order
     * @throws IllegalArgumentException if input is invalid
     */
    public Order addOrder(
        String customerName,
        String drink,
        int quantity,
        double minutesPerDrink,
        boolean rewardMember)
    {
        if (customerName == null
            || customerName.trim().isEmpty()
            || drink == null
            || drink.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Name and drink cannot be blank.");
        }

        if (quantity <= 0
            || minutesPerDrink <= 0
            || !Double.isFinite(minutesPerDrink)
            || !Double.isFinite(quantity * minutesPerDrink))
        {
            throw new IllegalArgumentException(
                "Quantity and preparation time must be positive and finite.");
        }

        if (nextOrderId == Integer.MAX_VALUE)
        {
            throw new IllegalStateException(
                "No more order IDs are available.");
        }

        Order order = new Order(
            nextOrderId,
            customerName.trim(),
            drink.trim(),
            quantity,
            minutesPerDrink,
            rewardMember);

        if (rewardMember)
        {
            waitingOrders.enqueueFront(order);
        }
        else
        {
            waitingOrders.enqueue(order);
        }

        nextOrderId++;
        return order;
    }

    /**
     * Returns the next order without removing it.
     *
     * @return the next waiting order
     * @throws NoSuchElementException if no orders are waiting
     */
    public Order getNextOrder()
    {
        if (waitingOrders.isEmpty())
        {
            throw new NoSuchElementException(
                "No orders are waiting.");
        }

        return waitingOrders.peek();
    }

    /**
     * Moves the next waiting order into preparation.
     *
     * @return the order that started
     * @throws NoSuchElementException if no orders are waiting
     * @throws IllegalStateException if no barista is available
     */
    public Order startNextOrder()
    {
        Order order = getNextOrder();

        if (inProgressOrders.size() >= activeBaristas)
        {
            throw new IllegalStateException(
                "No barista is available.");
        }

        order.markStarted();
        waitingOrders.dequeue();
        inProgressOrders.add(order);

        return order;
    }

    /**
     * Completes an in-progress order.
     *
     * @param orderId ID of the order being prepared
     * @return the completed order
     * @throws IllegalArgumentException if the ID is not in progress
     */
    public Order completeOrder(int orderId)
    {
        for (int i = 0; i < inProgressOrders.size(); i++)
        {
            Order order = inProgressOrders.get(i);

            if (order.getOrderId() == orderId)
            {
                order.markCompleted();
                inProgressOrders.remove(i);
                completedOrders.add(order);

                return order;
            }
        }

        throw new IllegalArgumentException(
            "That order is not in progress.");
    }

    /**
     * Cancels a waiting order without reordering the others.
     *
     * @param orderId ID to cancel
     * @return true if the order was removed, false otherwise
     */
    public boolean cancelOrder(int orderId)
    {
        for (Order order : getWaitingOrders())
        {
            if (order.getOrderId() == orderId)
            {
                return waitingOrders.remove(order);
            }
        }

        return false;
    }

    /**
     * Returns a new array of waiting orders.
     * The Order objects themselves are shared.
     *
     * @return waiting orders from front to back
     */
    public Order[] getWaitingOrders()
    {
        Object[] items = waitingOrders.toArray();
        Order[] orders = new Order[items.length];

        for (int i = 0; i < items.length; i++)
        {
            orders[i] = (Order)items[i];
        }

        return orders;
    }

    /**
     * @return a new array of in-progress orders
     */
    public Order[] getInProgressOrders()
    {
        return inProgressOrders.toArray(
            new Order[inProgressOrders.size()]);
    }

    /**
     * @return a new array of orders in completion order
     */
    public Order[] getCompletedOrders()
    {
        return completedOrders.toArray(
            new Order[completedOrders.size()]);
    }

    /**
     * @return whether the waiting queue is empty
     */
    public boolean isEmpty()
    {
        return waitingOrders.isEmpty();
    }

    /**
     * @return number of waiting orders
     */
    public int getWaitingOrderCount()
    {
        return waitingOrders.getSize();
    }

    /**
     * @return number of orders being prepared
     */
    public int getInProgressOrderCount()
    {
        return inProgressOrders.size();
    }

    /**
     * @return number of completed orders
     */
    public int getCompletedCount()
    {
        return completedOrders.size();
    }

    /**
     * Estimates minutes until a waiting order starts preparation.
     * Does not modify the actual queue or order timestamps.
     * Assumes current staffing and queue priority remain unchanged.
     *
     * @param orderId ID of a waiting order
     * @return estimated waiting minutes, or -1 with no baristas
     * @throws IllegalArgumentException if the ID is not waiting
     */
    public double estimateWaitMinutes(int orderId)
    {
        Order[] waiting = getWaitingOrders();
        int targetIndex = -1;

        for (int i = 0; i < waiting.length; i++)
        {
            if (waiting[i].getOrderId() == orderId)
            {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex < 0)
        {
            throw new IllegalArgumentException(
                "That order is not waiting.");
        }

        if (activeBaristas == 0)
        {
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();
        int busyCount = inProgressOrders.size();
        double[] remaining = new double[busyCount];

        for (int i = 0; i < busyCount; i++)
        {
            Order order = inProgressOrders.get(i);

            double elapsed = Math.max(
                0,
                minutesBetween(order.getStartedAt(), now));

            remaining[i] = Math.max(
                0,
                order.getPreparationMinutes() - elapsed);
        }

        Arrays.sort(remaining);

        // More slots than orders are unnecessary.
        int slotCount = Math.min(
            activeBaristas,
            busyCount + targetIndex + 1);

        double[] available = new double[slotCount];

        if (busyCount > activeBaristas)
        {
            // After staff leave, enough existing orders must finish
            // before new work can start.
            int surplus = busyCount - activeBaristas;

            for (int i = 0; i < slotCount; i++)
            {
                available[i] = remaining[surplus + i];
            }
        }
        else
        {
            System.arraycopy(
                remaining, 0, available, 0, busyCount);
        }

        for (int i = 0; i <= targetIndex; i++)
        {
            int earliest = 0;

            for (int j = 1; j < available.length; j++)
            {
                if (available[j] < available[earliest])
                {
                    earliest = j;
                }
            }

            if (i == targetIndex)
            {
                return available[earliest];
            }

            available[earliest] +=
                waiting[i].getPreparationMinutes();
        }

        throw new IllegalStateException(
            "Waiting order was not processed.");
    }

    /**
     * Calculates average placement-to-start time for completed orders.
     *
     * @return average waiting minutes, or zero if none completed
     */
    public double getAverageWaitMinutes()
    {
        if (completedOrders.isEmpty())
        {
            return 0;
        }

        double total = 0;

        for (Order order : completedOrders)
        {
            total += Math.max(
                0,
                minutesBetween(
                    order.getPlacedAt(),
                    order.getStartedAt()));
        }

        return total / completedOrders.size();
    }

    /**
     * @return current number of active baristas
     */
    public int getActiveBaristas()
    {
        return activeBaristas;
    }

    /**
     * Updates staffing without canceling existing preparation.
     *
     * @param count nonnegative staffing count
     * @throws IllegalArgumentException if count is negative
     */
    public void setActiveBaristas(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException(
                "Barista count cannot be negative.");
        }

        activeBaristas = count;
    }

    /**
     * Calculates the difference between two timestamps in minutes.
     */
    private double minutesBetween(
        LocalDateTime start,
        LocalDateTime end)
    {
        Duration duration = Duration.between(start, end);

        return duration.getSeconds() / 60.0
            + duration.getNano() / 60000000000.0;
    }
}
