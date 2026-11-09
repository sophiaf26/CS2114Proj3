package brewqueue;

import student.TestCase;

/**
 * Tests the LinkedQueue class.
 *
 * @author Rudra Dutta (drudra)
 * @version 2026.11.08
 */
public class LinkedQueueTest extends TestCase
{
    private LinkedQueue<String> queue;

    /**
     * Sets up a new queue before each test.
     */
    public void setUp()
    {
        queue = new LinkedQueue<String>();
    }

    /**
     * Tests adding an element.
     */
    public void testEnqueue()
    {
        queue.enqueue("Coffee");

        assertEquals(1, queue.getSize());
        assertEquals("Coffee", queue.peek());
    }

    /**
     * Tests removing an element.
     */
    public void testDequeue()
    {
        queue.enqueue("Coffee");
        queue.enqueue("Latte");

        assertEquals("Coffee", queue.dequeue());
        assertEquals(1, queue.getSize());
    }

    /**
     * Tests adding a priority order.
     */
    public void testEnqueueFront()
    {
        queue.enqueue("Coffee");
        queue.enqueue("Latte");
        queue.enqueueFront("Priority");

        assertEquals("Priority", queue.dequeue());
        assertEquals("Coffee", queue.dequeue());
    }

    /**
     * Tests whether the queue is empty.
     */
    public void testIsEmpty()
    {
        assertTrue(queue.isEmpty());

        queue.enqueue("Coffee");

        assertFalse(queue.isEmpty());
    }
}
