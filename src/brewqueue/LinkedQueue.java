
package brewqueue;

import java.util.NoSuchElementException;

/**
 * A singly linked queue that stores elements in FIFO order.
 * Allows elements to be added to both the front and back of the queue.
 *
 * @author Rudra Dutta (drudra)
 * @version 2026.11.08
 * @param <T> the type of elements stored in the queue
 */
public class LinkedQueue<T> {
    private Node<T> front;
    private Node<T> back;
    private int size;

    /**
     * Creates an empty queue.
     */
    public LinkedQueue(){
        front = null;
        back = null;
        size = 0;
    }

    /**
     * Adds an element to the back of the queue.
     *
     * @param item the element to add
     * @throws IllegalArgumentException if item is null
     */
    public void enqueue(T item){
        if (item == null){
            throw new IllegalArgumentException(
                "Cannot add null to the queue.");
        }

        Node<T> newNode = new Node<T>(item);
        if (isEmpty()){
            front = newNode;
            back = newNode;
        }
        else {
            back.setNext(newNode);
            back = newNode;
        }
        size++;
    }

    /**
     * Adds an element to the front of the queue.
     * Used for priority orders.
     *
     * @param item the element to add
     * @throws IllegalArgumentException if item is null
     */
    public void enqueueFront(T item){
        if (item == null){
            throw new IllegalArgumentException(
                "Cannot add null to the queue.");
        }
        Node<T> newNode = new Node<T>(item, front);
        front = newNode;
        if (back == null){
            back = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the first element.
     *
     * @return the element at the front
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue(){
        if (isEmpty()){
            throw new NoSuchElementException(
                "The queue is empty.");
        }
        T removedItem = front.getData();
        front = front.getNext();
        size--;
        if (front == null){
            back = null;
        }
        return removedItem;
    }

    /**
     * Returns the first element without removing it.
     *
     * @return the element at the front
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek(){
        if (isEmpty()){
            throw new NoSuchElementException("The queue is empty.");
        }
        return front.getData();
    }

    /**
     * Removes the first matching element from the queue.
     * The order of the remaining elements stays the same.
     *
     * @param item the element to remove
     * @return true if the element was removed, false otherwise
     */
    public boolean remove(T item){
        if (item == null || isEmpty()){
            return false;
        }

        if (front.getData().equals(item)){
            dequeue();
            return true;
        }

        Node<T> current = front;
        while (current.getNext() != null){
            if (current.getNext().getData().equals(item)){
                Node<T> removedNode = current.getNext();
                current.setNext(removedNode.getNext());
                if (removedNode == back){
                    back = current;
                }
                size--;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Checks whether the queue is empty.
     *
     * @return true if the queue contains no elements
     */
    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the queue size
     */
    public int getSize(){
        return size;
    }

    /**
     * Removes every element from the queue.
     */
    public void clear(){
        front = null;
        back = null;
        size = 0;
    }

    /**
     * Returns the queue elements in front-to-back order.
     * Does not modify the queue.
     *
     * @return an array containing the queue elements
     */
    public Object[] toArray(){
        Object[] items = new Object[size];
        Node<T> current = front;
        for (int i = 0; i < size; i++){
            items[i] = current.getData();
            current = current.getNext();
        }
        return items;
    }
}
