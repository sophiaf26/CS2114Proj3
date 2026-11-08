import java.time.Duration;
import java.util.NoSuchElementException;
import student.TestCase
package brewqueue;

/**
 * Tests the mdoel without view or controller
 * @author Will Fleming
 * @version 2026.11.07
 */
public class BrewQueueModelTest extends TestCase
{
    private BrewQueueModel model;

    /**
     * Creates a fresh model for every test.
     */
    public void setUp()
    {
        model = new BrewQueueModel(2);
    }

    /**
     * Checks the initial state.
     */
    public void testInitialState()
    {
        assertTrue(model.isEmpty());
        assertEquals(2, model.getActiveBaristas());
        assertEquals(0, model.getWaitingOrderCount());
        assertEquals(0, model.getInProgressOrderCount());
        assertEquals(0, model.getCompletedCount());
        assertEquals(0, model.getWaitingOrders().length);
        assertEquals(0, model.getInProgressOrders().length);
        assertEquals(0, model.getCompletedOrders().length);
        assertEquals(0.0, model.getAverageWaitMinutes(), 0.0);
    }

    /**
     * Checks constructor validation.
     */
    public void testConstructorValidation()
    {
        assertEquals(
            0,
            new BrewQueueModel(0).getActiveBaristas());

        try
        {
            new BrewQueueModel(-1);
            fail("Negative staffing must be rejected.");
        }
        catch (IllegalArgumentException exception)
        {
            // Expected.
        }
    }

    /**
     * Checks FIFO order, trimmed input, and automatic IDs.
     */
    public void testAddOrder()
    {
        Order first = model.addOrder(
            " Will ", " Latte ", 2, 3, false);

        Order second = model.addOrder(
            "Sophia", "Coffee", 1, 2, false);

        assertEquals(1, first.getOrderId());
        assertEquals(2, second.getOrderId());
        assertEquals("Will", first.getCustomerName());
        assertEquals("Latte", first.getDrink());
        assertEquals(2, first.getQuantity());
        assertEquals(6.0, first.getPreparationMinutes(), 0.0);
        assertFalse(first.isRewardMember());
        assertFalse(model.isEmpty());
        assertSame(first, model.getNextOrder());
        assertSame(second, model.getWaitingOrders()[1]);
        assertEquals(2, model.getWaitingOrderCount());
        assertNull(first.getStartedAt());
        assertNull(first.getCompletedAt());
    }

    /**
     * Invalid input must not add an order or consume an ID.
     */
    public void testInvalidOrderInputs()
    {
        String[][] strings = {
            {null, "Latte"},
            {"", "Latte"},
            {"   ", "Latte"},
            {"Will", null},
            {"Will", ""},
            {"Will", "   "}
        };

        for (String[] pair : strings)
        {
            try
            {
                model.addOrder(
                    pair[0], pair[1], 1, 2, false);

                fail("Blank or null input must be rejected.");
            }
            catch (IllegalArgumentException exception)
            {
                assertTrue(model.isEmpty());
            }
        }

        double[] times = {
            0,
            -1,
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        };

        for (double time : times)
        {
            try
            {
                model.addOrder(
                    "Will", "Latte", 1, time, false);

                fail("Invalid preparation time must be rejected.");
            }
            catch (IllegalArgumentException exception)
            {
                assertTrue(model.isEmpty());
            }
        }

        for (int quantity : new int[] {0, -1})
        {
            try
            {
                model.addOrder(
                    "Will", "Latte", quantity, 2, false);

                fail("Invalid quantity must be rejected.");
            }
            catch (IllegalArgumentException exception)
            {
                assertTrue(model.isEmpty());
            }
        }

        try
        {
            model.addOrder(
                "Will", "Latte", 2, Double.MAX_VALUE, false);

            fail("An overflowing total time must be rejected.");
        }
        catch (IllegalArgumentException exception)
        {
            assertTrue(model.isEmpty());
        }

        assertEquals(
            1,
            model.addOrder(
                "Will", "Latte", 1, 2, false).getOrderId());
    }

    /**
     * New reward orders go ahead of all waiting orders.
     */
    public void testRewardPriority()
    {
        Order regular = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order reward1 = model.addOrder(
            "B", "Coffee", 1, 4, true);

        Order reward2 = model.addOrder(
            "C", "Coffee", 1, 4, true);

        assertTrue(reward2.isRewardMember());

        Order[] waiting = model.getWaitingOrders();

        assertSame(reward2, waiting[0]);
        assertSame(reward1, waiting[1]);
        assertSame(regular, waiting[2]);
    }

    /**
     * Peeking does not remove an order; starting does.
     */
    public void testPeekAndStart()
    {
        Order order = model.addOrder(
            "A", "Coffee", 1, 4, false);

        assertSame(order, model.getNextOrder());
        assertSame(order, model.getNextOrder());
        assertEquals(1, model.getWaitingOrderCount());

        assertSame(order, model.startNextOrder());
        assertTrue(model.isEmpty());
        assertEquals(1, model.getInProgressOrderCount());
        assertSame(order, model.getInProgressOrders()[0]);
        assertNotNull(order.getStartedAt());
    }

    /**
     * Checks operations on an empty queue.
     */
    public void testEmptyOperations()
    {
        try
        {
            model.getNextOrder();
            fail("An empty queue has no next order.");
        }
        catch (NoSuchElementException exception)
        {
            assertTrue(model.isEmpty());
        }

        try
        {
            model.startNextOrder();
            fail("An empty queue cannot start an order.");
        }
        catch (NoSuchElementException exception)
        {
            assertEquals(0, model.getInProgressOrderCount());
        }

        assertFalse(model.cancelOrder(1));
    }

    /**
     * Busy baristas cannot start extra orders.
     */
    public void testStaffCapacity()
    {
        model.setActiveBaristas(1);

        Order first = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order second = model.addOrder(
            "B", "Coffee", 1, 4, false);

        model.startNextOrder();

        try
        {
            model.startNextOrder();
            fail("One barista cannot start two orders.");
        }
        catch (IllegalStateException exception)
        {
            assertSame(second, model.getNextOrder());
            assertNull(second.getStartedAt());
            assertEquals(1, model.getInProgressOrderCount());
        }

        model.completeOrder(first.getOrderId());

        assertSame(second, model.startNextOrder());
    }

    /**
     * Orders may finish in a different order from when they started.
     */
    public void testCompleteOutOfOrder()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 2, false);

        model.startNextOrder();
        model.startNextOrder();

        assertSame(b, model.completeOrder(b.getOrderId()));
        assertSame(a, model.getInProgressOrders()[0]);
        assertNotNull(b.getCompletedAt());
        assertEquals(1, model.getCompletedCount());

        model.completeOrder(a.getOrderId());

        assertSame(b, model.getCompletedOrders()[0]);
        assertSame(a, model.getCompletedOrders()[1]);
        assertEquals(0, model.getInProgressOrderCount());
    }

    /**
     * Only in-progress orders can be completed.
     */
    public void testInvalidCompletion()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        for (int id : new int[] {-1, 999, a.getOrderId()})
        {
            try
            {
                model.completeOrder(id);
                fail("Only in-progress orders can complete.");
            }
            catch (IllegalArgumentException exception)
            {
                assertEquals(0, model.getCompletedCount());
                assertEquals(1, model.getWaitingOrderCount());
            }
        }

        model.startNextOrder();
        model.completeOrder(a.getOrderId());

        try
        {
            model.completeOrder(a.getOrderId());
            fail("An order cannot complete twice.");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals(1, model.getCompletedCount());
        }
    }

    /**
     * Cancellation preserves the remaining order.
     */
    public void testCancelPreservesOrder()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 4, false);

        Order c = model.addOrder(
            "C", "Coffee", 1, 4, false);

        Order d = model.addOrder(
            "D", "Coffee", 1, 4, false);

        assertTrue(model.cancelOrder(b.getOrderId()));
        assertSame(a, model.getWaitingOrders()[0]);
        assertSame(c, model.getWaitingOrders()[1]);
        assertSame(d, model.getWaitingOrders()[2]);

        assertTrue(model.cancelOrder(a.getOrderId()));
        assertTrue(model.cancelOrder(d.getOrderId()));
        assertSame(c, model.getNextOrder());

        assertTrue(model.cancelOrder(c.getOrderId()));
        assertTrue(model.isEmpty());

        Order next = model.addOrder(
            "E", "Coffee", 1, 4, false);

        assertEquals(5, next.getOrderId());
        assertSame(next, model.getNextOrder());
    }

    /**
     * In-progress and completed orders cannot be canceled.
     */
    public void testInvalidCancellation()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        assertFalse(model.cancelOrder(-1));

        model.startNextOrder();

        assertFalse(model.cancelOrder(a.getOrderId()));
        assertEquals(1, model.getInProgressOrderCount());

        model.completeOrder(a.getOrderId());

        assertFalse(model.cancelOrder(a.getOrderId()));
        assertEquals(1, model.getCompletedCount());
    }

    /**
     * Replacing array entries does not change the stored collections.
     */
    public void testReturnedArraysAreIndependent()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        model.getWaitingOrders()[0] = null;
        assertSame(a, model.getNextOrder());

        model.startNextOrder();

        model.getInProgressOrders()[0] = null;
        assertSame(a, model.getInProgressOrders()[0]);

        model.completeOrder(a.getOrderId());

        model.getCompletedOrders()[0] = null;
        assertSame(a, model.getCompletedOrders()[0]);
    }

    /**
     * One barista must finish all orders ahead.
     */
    public void testEstimateOneBarista()
    {
        model.setActiveBaristas(1);

        Order a = model.addOrder(
            "A", "Coffee", 2, 3, false);

        model.addOrder("B", "Coffee", 1, 4, false);

        Order c = model.addOrder(
            "C", "Coffee", 1, 8, false);

        assertEquals(
            0.0, model.estimateWaitMinutes(a.getOrderId()), 0.0);

        assertEquals(
            10.0, model.estimateWaitMinutes(c.getOrderId()), 0.0);

        assertEquals(3, model.getWaitingOrderCount());
        assertEquals(0, model.getInProgressOrderCount());
        assertNull(a.getStartedAt());
    }

    /**
     * The third four-minute order waits four minutes with two baristas.
     */
    public void testEstimateTwoBaristas()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 4, false);

        Order c = model.addOrder(
            "C", "Coffee", 1, 4, false);

        assertEquals(
            0.0, model.estimateWaitMinutes(a.getOrderId()), 0.0);

        assertEquals(
            0.0, model.estimateWaitMinutes(b.getOrderId()), 0.0);

        assertEquals(
            4.0, model.estimateWaitMinutes(c.getOrderId()), 0.0);
    }

    /**
     * Checks different preparation times and reward priority.
     */
    public void testEstimateUnequalDurations()
    {
        model.addOrder("A", "Coffee", 1, 10, false);
        model.addOrder("B", "Coffee", 1, 2, false);
        model.addOrder("C", "Coffee", 1, 3, false);

        Order target = model.addOrder(
            "D", "Coffee", 1, 4, false);

        assertEquals(
            5.0,
            model.estimateWaitMinutes(target.getOrderId()),
            0.0);

        Order reward = model.addOrder(
            "R", "Coffee", 1, 1, true);

        assertEquals(
            0.0,
            model.estimateWaitMinutes(reward.getOrderId()),
            0.0);

        assertEquals(
            6.0,
            model.estimateWaitMinutes(target.getOrderId()),
            0.0);
    }

    /**
     * Checks estimates when both baristas are busy.
     */
    public void testEstimateBusyBaristas()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 1000, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 2000, false);

        model.startNextOrder();
        model.startNextOrder();

        model.addOrder("C", "Coffee", 1, 300, false);

        Order target = model.addOrder(
            "D", "Coffee", 1, 10, false);

        double wait =
            model.estimateWaitMinutes(target.getOrderId());

        double elapsedA = Duration.between(
            a.getStartedAt(),
            java.time.LocalDateTime.now()).toMillis() / 60000.0;

        assertEquals(
            Math.max(0, 1000 - elapsedA) + 300,
            wait,
            0.05);

        assertEquals(2, model.getInProgressOrderCount());
        assertNull(b.getCompletedAt());
    }

    /**
     * Checks invalid IDs and zero staffing.
     */
    public void testEstimateInvalidAndNoStaff()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        model.setActiveBaristas(0);

        assertEquals(
            -1.0,
            model.estimateWaitMinutes(a.getOrderId()),
            0.0);

        try
        {
            model.estimateWaitMinutes(999);
            fail("A missing ID must be rejected even without staff.");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals(1, model.getWaitingOrderCount());
        }

        try
        {
            model.startNextOrder();
            fail("Zero staff cannot start an order.");
        }
        catch (IllegalStateException exception)
        {
            assertNull(a.getStartedAt());
        }

        model.setActiveBaristas(1);
        model.startNextOrder();

        try
        {
            model.estimateWaitMinutes(a.getOrderId());
            fail("An in-progress order is no longer waiting.");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals(1, model.getInProgressOrderCount());
        }
    }

    /**
     * Staff departures preserve existing work.
     */
    public void testStaffDeparture()
    {
        model.addOrder("A", "Coffee", 1, 1000, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 2000, false);

        Order a = model.startNextOrder();
        model.startNextOrder();

        Order target = model.addOrder(
            "C", "Coffee", 1, 10, false);

        model.setActiveBaristas(1);

        double wait =
            model.estimateWaitMinutes(target.getOrderId());

        double elapsed = Duration.between(
            b.getStartedAt(),
            java.time.LocalDateTime.now()).toMillis() / 60000.0;

        assertEquals(
            Math.max(0, 2000 - elapsed),
            wait,
            0.05);

        model.completeOrder(a.getOrderId());

        try
        {
            model.startNextOrder();
            fail("Remaining barista is still busy.");
        }
        catch (IllegalStateException exception)
        {
            assertSame(target, model.getNextOrder());
        }

        model.completeOrder(b.getOrderId());

        assertSame(target, model.startNextOrder());
    }

    /**
     * Checks valid and invalid staffing updates.
     */
    public void testStaffUpdates()
    {
        model.setActiveBaristas(0);

        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        model.setActiveBaristas(3);

        assertEquals(3, model.getActiveBaristas());
        assertSame(a, model.startNextOrder());

        try
        {
            model.setActiveBaristas(-2);
            fail("Negative staffing must be rejected.");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals(3, model.getActiveBaristas());
        }

        model.setActiveBaristas(Integer.MAX_VALUE);

        Order b = model.addOrder(
            "B", "Coffee", 1, 4, false);

        assertEquals(
            0.0,
            model.estimateWaitMinutes(b.getOrderId()),
            0.0);
    }

    /**
     * Only completed orders contribute to average wait.
     */
    public void testAverageWait()
    {
        Order a = model.addOrder(
            "A", "Coffee", 1, 4, false);

        Order b = model.addOrder(
            "B", "Coffee", 1, 4, false);

        model.startNextOrder();
        model.startNextOrder();

        assertEquals(
            0.0, model.getAverageWaitMinutes(), 0.0);

        model.completeOrder(a.getOrderId());
        model.completeOrder(b.getOrderId());

        double first = Duration.between(
            a.getPlacedAt(),
            a.getStartedAt()).toNanos() / 60000000000.0;

        double second = Duration.between(
            b.getPlacedAt(),
            b.getStartedAt()).toNanos() / 60000000000.0;

        assertEquals(
            (first + second) / 2,
            model.getAverageWaitMinutes(),
            0.00000001);
    }
}