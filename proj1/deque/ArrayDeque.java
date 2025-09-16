package deque;

import java.util.Iterator;

public class ArrayDeque<T> {
    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     * @param item
     */
    public void addFirst(T item){}

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     * @param item
     */
    public void addLast(T item){}

    /**
     * Returns true if deque is empty, false otherwise.
     * @return
     */
    public boolean isEmpty(){}

    /**
     * Returns the number of items in the deque.
     * @return
     */
    public int size(){}

    /**
     *  Prints the items in the deque from first to last, separated by a space. Once all the items have been printed, print out a new line.
     */
    public void printDeque(){}

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeFirst(){}


    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeLast(){}

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
     * @param index
     * @return
     */
    public T get(int index){}

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>) so we must provide this method to return an iterator.
     * @return
     */
    public Iterator<T> iterator(){}


    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
     * @param o   the reference object with which to compare.
     * @return
     */
    public boolean equals(Object o){}

}
