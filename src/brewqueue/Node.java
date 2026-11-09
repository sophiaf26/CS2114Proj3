package brewqueue;

/**
 * Model support role that stores generic data and the next link.
 *
 * @author Andersson Galvez
 * @version 2026.11.08
 * @param <T>
 *            the type of data stored in the node
 */
public class Node<T> {
    // ~ Fields ................................................................
    private T data;
    private Node<T> next;

    // ~ Constructors ..........................................................
    /**
     * Creates a node containing data with no next node.
     *
     * @param data
     *            the item to store in this node
     */
    public Node(T data) {
        this.data = data;
        this.next = null;
    }


    /**
     * Creates a node containing data and the supplied next link.
     *
     * @param data
     *            the item to store in this node
     * @param next
     *            the next node in the sequence
     */
    public Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }


    // ~Public Methods ........................................................
    /**
     * Returns the stored item.
     *
     * @return the data stored in the node
     */
    public T getData() {
        return data;
    }


    /**
     * Returns the next node, or null at the end.
     *
     * @return the next node in the sequence
     */
    public Node<T> getNext() {
        return next;
    }


    /**
     * Updates the next link; null marks the end.
     *
     * @param next
     *            the new next node
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }
}
