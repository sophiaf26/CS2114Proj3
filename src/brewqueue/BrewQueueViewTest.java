package brewqueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import student.TestCase;

/**
 * Tests the BrewQueueView class by feeding it console input and checking
 * what it prints.
 *
 * @author Sophia Fussman
 * @version 2026.11.08
 */
public class BrewQueueViewTest extends TestCase {
    private InputStream realIn;
    private PrintStream realOut;
    private ByteArrayOutputStream captured;

    /**
     * Saves the real console streams and starts capturing output.
     */
    public void setUp()
    {
        realIn = System.in;
        realOut = System.out;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    /**
     * Restores the real console streams.
     */
    public void tearDown()
    {
        System.setIn(realIn);
        System.setOut(realOut);
    }

    private BrewQueueView viewWithInput(String text)
    {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
        return new BrewQueueView();
    }

    private String output()
    {
        return captured.toString();
    }

    public void testDisplayMenu()
    {
        viewWithInput("").displayMenu();
        assertTrue(output().contains("Add order"));
        assertTrue(output().contains("0. Exit"));
    }

    public void testDisplayMessage()
    {
        viewWithInput("").displayMessage("Hello");
        assertTrue(output().contains("Hello"));
    }

    public void testDisplayWaitingOrdersEmpty()
    {
        viewWithInput("").displayWaitingOrders(new Order[0]);
        assertTrue(output().contains("No orders are waiting"));
    }

    public void testDisplayWaitingOrdersMarksNext()
    {
        Order a = new Order(1, "Ann", "Latte", 1, 2.0, false);
        Order b = new Order(2, "Bob", "Mocha", 2, 2.0, true);
        viewWithInput("").displayWaitingOrders(new Order[] {a, b});
        String out = output();
        assertTrue(out.contains("Ann"));
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains("<- next"));
        assertTrue(out.indexOf("Ann") < out.indexOf("Bob"));
        assertTrue(out.contains("[reward]"));
    }

    public void testDisplayInProgressOrders()
    {
        viewWithInput("").displayInProgressOrders(new Order[0]);
        assertTrue(output().contains("No orders are being prepared"));

        Order a = new Order(1, "Ann", "Latte", 1, 2.0, false);
        a.markStarted();
        viewWithInput("").displayInProgressOrders(new Order[] {a});
        assertTrue(output().contains("Order #1"));
        assertTrue(output().contains("elapsed"));
    }

    public void testDisplayCompletedOrders()
    {
        viewWithInput("").displayCompletedOrders(new Order[0]);
        assertTrue(output().contains("No orders have been completed"));

        Order a = new Order(1, "Ann", "Latte", 1, 2.0, false);
        a.markStarted();
        a.markCompleted();
        viewWithInput("").displayCompletedOrders(new Order[] {a});
        assertTrue(output().contains("took"));
    }

    public void testDisplayStatistics()
    {
        viewWithInput("").displayStatistics(1, 2, 3, 4.5);
        assertTrue(output().contains("Waiting:     1"));
        assertTrue(output().contains("In progress: 2"));
        assertTrue(output().contains("Completed:   3"));
        assertTrue(output().contains("4.5"));
    }

    public void testDisplayEstimatedWait()
    {
        viewWithInput("").displayEstimatedWait(4, 3.0);
        assertTrue(output().contains("Order #4"));
        assertTrue(output().contains("3.0"));
        captured.reset();
        viewWithInput("").displayEstimatedWait(4, -1);
        assertTrue(output().contains("no baristas"));
    }

    public void testDisplayOverdueOrders()
    {
        viewWithInput("").displayOverdueOrders(new Order[0]);
        assertEquals("", output());
        Order a = new Order(1, "Ann", "Latte", 1, 2.0, false);
        viewWithInput("").displayOverdueOrders(new Order[] {a});
        assertTrue(output().contains("REMINDER"));
    }

    public void testReadNonBlankStringReprompts()
    {
        BrewQueueView v = viewWithInput("\n   \n  Ann  \n");
        assertEquals("Ann", v.readNonBlankString("Name: "));
        assertTrue(output().contains("cannot be blank"));
    }

    public void testReadIntReprompts()
    {
        BrewQueueView v = viewWithInput("latte\n2.5\n 7 \n");
        assertEquals(7, v.readInt("Qty: "));
        assertTrue(output().contains("whole number"));
    }

    public void testReadDoubleReprompts()
    {
        BrewQueueView v = viewWithInput("abc\nNaN\nInfinity\n3.5\n");
        assertEquals(3.5, v.readDouble("Min: "), 0.0001);
        assertTrue(output().contains("valid number"));
    }

    public void testReadYesNo()
    {
        BrewQueueView v = viewWithInput("maybe\nY\nno\n");
        assertTrue(v.readYesNo("Reward? "));
        assertFalse(v.readYesNo("Reward? "));
        assertTrue(output().contains("yes or no"));
    }
}
