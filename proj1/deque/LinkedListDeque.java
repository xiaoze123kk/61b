package deque;

import java.util.Iterator;

public class LinkedListDeque<T> {
    private Node sentinel;
    int size;
    private Node front;//追踪双端队列的最前端
    private Node back;//追踪双端队列的最后端
    private class Node<T> {
        public T item;
        public Node prev;//前驱
        public Node next;//后继

        Node(T i, Node pre, Node nex) {
            item = i;
            prev = pre;
            next = nex;
        }
    }

    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     * @param item
     */
    public void addFirst(T item) {
        front.prev = new Node(item, null,front);
        front = front.prev;
        size++;
    }

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     * @param item
     */
    public void addLast(T item) {
        back.next = new Node(item,back,null);
        back = back.next;
        size++;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     * @return
     */
    public boolean isEmpty() {
        if(sentinel.prev!=null || sentinel.next!=null){
            return true;
        }
        return false;
    }

    /**
     * Returns the number of items in the deque.
     * @return
     */
    public int size() {
        return size;
    }

    /**
     *  Prints the items in the deque from first to last, separated by a space.
     *  Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeFirst() {

    }


    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeLast() {

    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth
     * If no such item exists, returns null. Must not alter the deque
     * @param index
     * @return
     */
    public T get(int index)  {

    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>) so we must provide this method to return an iterator.
     * @return
     */
    public Iterator<T> iterator() {

    }


    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
     * @param o   the reference object with which to compare.
     * @return
     */
    public boolean equals(Object o) {

    }

    /**
     * Creates an empty linked list deque.
     */
    public LinkedListDeque() {
        sentinel.item = 0;
        sentinel.prev = null;
        sentinel.next = sentinel.prev;
        size = 0;
        front = sentinel;
        back = sentinel;
    }



}
