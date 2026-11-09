package brewqueue;

import student.TestCase;

/**
 * Tests the generic Node helper class.
 * 
 * @author Andersson Steve Galvez Chicas
 * @version Nov 8, 2026
 */
public class NodeTest extends TestCase {

    // ~ Fields ................................................................
    private Node<String> node1;
    private Node<String> node2;

    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................
    /**
     * Sets up isolated and linked nodes before each test.
     */
    public void setUp() {
        node2 = new Node<String>("Second");
        node1 = new Node<String>("First", node2);
    }


    /**
     * Tests both constructors and the data getter.
     */
    public void testConstructorsAndGetData() {
        assertEquals("First", node1.getData());
        assertEquals("Second", node2.getData());
    }


    /**
     * Tests the next node links.
     */
    public void testGetNext() {
        assertEquals(node2, node1.getNext());
        assertNull(node2.getNext());
    }


    /**
     * Tests updating the next node reference.
     */
    public void testSetNext() {
        Node<String> node3 = new Node<String>("Third");
        node2.setNext(node3);

        assertEquals(node3, node2.getNext());

        node2.setNext(null);
        assertNull(node2.getNext());
    }
}
