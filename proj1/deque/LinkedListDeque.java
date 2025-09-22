package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> , Iterable<T> {
    private Node sentinel;
    int size;
    private Node last;

    private class Node {
        private T item;
        private Node prev; //前驱
        private Node next; //后继

        Node(T i, Node pre, Node nex) {
            item = i;
            prev = pre;
            next = nex;
        }
    }

    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     *
     * @param item
     */
    @Override
    public void addFirst(T item) {
        Node firstNode = new Node(item, sentinel, sentinel.next);
        sentinel.next = firstNode;
        firstNode.next.prev = firstNode;
        size++;
        if (size == 1) {
            last = sentinel.next;
        }
    }

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     *
     * @param item
     */
    @Override
    public void addLast(T item) {
        Node lastNode = new Node(item,sentinel.prev,sentinel);
        sentinel.prev = lastNode;
        lastNode.prev.next = lastNode;
        last = lastNode;
        size++;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
//        if (sentinel.next == null || sentinel.next == sentinel) {
//            return true;
//        }
        if (size != 0){
            return false;
        }
        return true;
    }

    /**
     * Returns the number of items in the deque.
     *
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        if (isEmpty()) {
            System.out.println("\n");
            return;
        }
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.println(p.item + " ");
            p = p.next;
        }
        System.out.println("\n");
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     *
     * @return
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node remove = sentinel.next;
        sentinel.next = remove.next;
        remove.next.prev = sentinel;
        size--;
        return (T) remove.item;
    }


    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     *
     * @return
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        Node remove = last;
        T returnItem = (T) remove.item;
        remove.prev.next = sentinel;
        sentinel.prev = remove.prev;
        last = remove.prev;
        size--;
        return returnItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth
     * If no such item exists, returns null. Must not alter the deque
     *
     * @param index
     * @return
     */
    @Override
    public T get(int index) {
        //        if (isEmpty() || (index - 1) > size || index < 0) {
        //            return null;
        //        }
        Node p = sentinel.next;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                return (T) p.item;
            }
            p = p.next;
        }
        return null;
    }

    private class LinkedListDequeIterator implements Iterator{
        int index;
        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T returnItem = get(index);
            index++;
            return returnItem;
        }

        LinkedListDequeIterator(){
            index = 0;
        }

    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator.
     *
     * @return
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }


    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
     *
     * @param o the reference object with which to compare.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deque<?>)) return false;
        Deque<?> other = (Deque<?>) o;
        if (other.size() != this.size) return false;
        for (int i = 0; i < size; i++) {
            Object aVal = this.get(i);
            Object bVal = other.get(i);
            if (!aVal.equals(bVal)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Creates an empty linked list deque.
     */
    public LinkedListDeque() {
        sentinel = new Node( null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
        last = sentinel;
    }

    /**
     * A helper for getRecursive().
     * @param index
     * @param p
     * @return
     */
    public T getRecursive(int index, Node p) {
        if (index < 0 || index >= size || isEmpty()) {
            return null;
        }
        if (index == 0) {
            return (T) p.item;
        }
        return (T) getRecursive(index - 1, p.next);
    }

    /**
     * Same as get, but uses recursion.
     *
     * @param index
     * @return
     */
    public T getRecursive(int index) {
        return (T) getRecursive(index, sentinel.next);
    }

}
