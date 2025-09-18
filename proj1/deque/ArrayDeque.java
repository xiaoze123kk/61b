//package deque;
//
//import java.util.Iterator;
//
//public class ArrayDeque<T> {
//    /**
//     * Adds an item of type T to the front of the deque.
//     * You can assume that item is never null.
//     * @param item
//     */
//    public void addFirst(T item){}
//
//    /**
//     * Adds an item of type T to the back of the deque.
//     * You can assume that item is never null.
//     * @param item
//     */
//    public void addLast(T item){}
//
//    /**
//     * Returns true if deque is empty, false otherwise.
//     * @return
//     */
//    public boolean isEmpty(){}
//
//    /**
//     * Returns the number of items in the deque.
//     * @return
//     */
//    public int size(){}
//
//    /**
//     *  Prints the items in the deque from first to last, separated by a space. Once all the items have been printed, print out a new line.
//     */
//    public void printDeque(){}
//
//    /**
//     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
//     * @return
//     */
//    public T removeFirst(){}
//
//
//    /**
//     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
//     * @return
//     */
//    public T removeLast(){}
//
//    /**
//     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
//     * @param index
//     * @return
//     */
//    public T get(int index){}
//
//    /**
//     * The Deque objects we’ll make are iterable (i.e. Iterable<T>) so we must provide this method to return an iterator.
//     * @return
//     */
//    public Iterator<T> iterator(){}
//
//
//    /**
//     * Returns whether or not the parameter o is equal to the Deque.
//     * o is considered equal if it is a Deque and if it contains the same contents
//     * (as goverened by the generic T’s equals method) in the same order.
//     * (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
//     * @param o   the reference object with which to compare.
//     * @return
//     */
//    public boolean equals(Object o){}
//
//}

package deque;

import afu.org.checkerframework.checker.igj.qual.I;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> , Iterable<T> {
    private Node sentinel;
    int size;
    private Node last;

    private class Node<T> {
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
        sentinel.next = new Node(item, sentinel, sentinel.next);
        size++;
        if (size == 1) {
            last = sentinel.next;
            sentinel.next.next = sentinel;
            sentinel.prev = last;
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
        last.next = new Node(item, last, sentinel);
        sentinel.prev = last.next;
        last = last.next;
        size++;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        if (sentinel.next == null || sentinel.next == sentinel) {
            return true;
        }
        return false;
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
        remove.prev.next = sentinel;
        sentinel.prev = remove.prev;
        last = remove.prev;
        size--;
        return (T) remove.item;
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
        }
        return null;
    }

    private class LinkedListDequeIterator<T> implements Iterator<T>{
        int index;
        Node p;
        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T returnItem =(T) p.item;
            p = p.next;
            return returnItem;
        }

        LinkedListDequeIterator(){
            index = 0;
            p = sentinel.next;
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
        return new LinkedListDequeIterator<T>();
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
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Deque<?> other = (Deque<?>) o;
        if (other.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            T everyItem = (T) other.get(i);
            if (!everyItem.equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates an empty linked list deque.
     */
    public ArrayDeque() {
        sentinel = new Node(0, null, null);
        size = 0;
        last = sentinel;
    }

    /**
     * A helper for getRecursive().
     * @param index
     * @param p
     * @return
     */
    public T getRecursiveHeper(int index, Node p) {
        if (index < 0 || index >= size || isEmpty()) {
            return null;
        }
        if (index == 0) {
            return (T) p.item;
        }
        return (T) getRecursiveHeper(index - 1, p.next);
    }

    /**
     * Same as get, but uses recursion.
     *
     * @param index
     * @return
     */
    public T getRecursive(int index) {
        return (T) getRecursiveHeper(index, sentinel.next);
    }

}

