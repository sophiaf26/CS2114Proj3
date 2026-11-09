package brewqueue;

import student.TestCase;

/**
 * Tests the Order class constructor validation and state transitions.
 * 
 * @author Andersson Steve Galvez Chicas
 * @version Nov 8, 2026
 */
public class OrderTest extends TestCase {

    // ~ Fields ................................................................
    private Order order;

    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................
    /**
     * Sets up a valid default order before each test.
     */
    public void setUp() {
        order = new Order(1, "Alice", "Latte", 2, 2.5, true);
    }


    /**
     * Tests that a valid order correctly initializes fields.
     */
    public void testValidConstructorAndGetters() {
        assertEquals(1, order.getOrderId());
        assertEquals("Alice", order.getCustomerName());
        assertEquals("Latte", order.getDrink());
        assertEquals(2, order.getQuantity());
        assertTrue(order.isRewardMember());
        assertEquals(5.0, order.getPreparationMinutes(), 0.01);
        assertNotNull(order.getPlacedAt());
        assertNull(order.getStartedAt());
        assertNull(order.getCompletedAt());
    }


    /**
     * Tests constructor rejection of invalid numeric inputs.
     */
    public void testConstructorInvalidNumbers() {
        Exception thrown = null;
        try {
            new Order(0, "Alice", "Latte", 2, 2.5, true);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try {
            new Order(1, "Alice", "Latte", -1, 2.5, true);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try {
            new Order(1, "Alice", "Latte", 2, Double.NaN, true);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests constructor rejection of blank string inputs.
     */
    public void testConstructorInvalidStrings() {
        Exception thrown = null;
        try {
            new Order(1, "  ", "Latte", 2, 2.5, true);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try {
            new Order(1, "Alice", null, 2, 2.5, true);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests successful start of an order.
     */
    public void testMarkStartedValid() {
        order.markStarted();
        assertNotNull(order.getStartedAt());
    }


    /**
     * Tests that an order cannot be started twice.
     */
    public void testMarkStartedTwice() {
        order.markStarted();
        Exception thrown = null;
        try {
            order.markStarted();
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
    }


    /**
     * Tests that an order cannot be completed before starting.
     */
    public void testMarkCompletedBeforeStarted() {
        Exception thrown = null;
        try {
            order.markCompleted();
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
    }


    /**
     * Tests successful completion of an order.
     */
    public void testMarkCompletedValid() {
        order.markStarted();
        order.markCompleted();
        assertNotNull(order.getCompletedAt());
    }


    /**
     * Tests that an order cannot be completed twice.
     */
    public void testMarkCompletedTwice() {
        order.markStarted();
        order.markCompleted();
        Exception thrown = null;
        try {
            order.markCompleted();
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
    }


    /**
     * Tests the formatted string output.
     */
    public void testToString() {
        assertEquals("Order #1: Alice (2x Latte)", order.toString());
    }
}
