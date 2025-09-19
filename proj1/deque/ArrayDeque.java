package deque;

import java.util.Iterator;

public class ArrayDeque<T> {
    private int size;
    private T[] array;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        size = 0;
        array = (T[]) new Object[8];
        nextFirst = 7;
        nextLast = size;
    }


    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     *
     * @param item
     */
    public void addFirst(T item) {
        array[nextFirst] = item;
        int originSize = size;
        size++;
        nextFirst = (nextFirst - 1 + array.length) % array.length;
        if (size > array.length * 0.5) {
            resizeBig(array.length * 2);
        }
    }

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     *
     * @param item
     */
    public void addLast(T item) {
        array[nextLast] = item;
        int originSize = size;
        size++;
        nextLast = (nextLast + 1) % array.length;
        if (size > array.length * 0.5) {
            resizeBig(array.length * 2);
        }
    }

    /**
     * Returns true if deque is empty, false otherwise.
     *
     * @return
     */
    public boolean isEmpty() {
        if (size != 0) {
            return false;
        }
        return true;
    }

    /**
     * Returns the number of items in the deque.
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        int head = (nextFirst + 1) % array.length;
        while (head != nextLast) {
            System.out.println(array[head] + " ");
            head = (head + 1) % array.length;
        }
        System.out.println("\n");
    }

    /**
     * 获取nextFirst的前一个index
     *
     * @return
     */
    public int getFirstPre() {
        return (nextFirst + 1) % array.length;
    }

    /**
     * removeFirst的帮助函数，获取要删除的First元素
     *
     * @return
     */
    public T getRemoveFirst() {
        int firstPre = getFirstPre();
        T remove = array[firstPre];
        array[firstPre] = null;
        nextFirst = firstPre;
        size--;
        return remove;
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     *
     * @return
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int afterSize = size - 1;
        if (size >= 16) {
            if (afterSize < array.length * 0.25) {
                resizeSmall(array.length / 2);//此时改变了nextFirst和nextLast
            }
        }
        return getRemoveFirst();

    }

    public int getLastPre() {
        return (nextLast - 1 + array.length) % array.length;
    }

    /**
     * removeLast的帮助方法,获取要删除的Last元素
     *
     * @return
     */
    public T getRemoveLast() {
        int lastPre = getLastPre();
        T remove = array[lastPre];
        array[lastPre] = null;
        nextLast = lastPre;
        size--;
        return remove;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     *
     * @return
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int afterSize = size - 1;
        if (size >= 16) {
            if (afterSize < array.length * 0.25) {
                resizeSmall(array.length / 2);//此时改变了nextFirst和nextLast
            }
        }
        return getRemoveLast();
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     *
     * @param index
     * @return
     */
    public T get(int index) {

    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator.
     *
     * @return
     */
//    public Iterator<T> iterator() {
//    }


    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
     *
     * @param o the reference object with which to compare.
     * @return
     */
    public boolean equals(Object o) {
        return true;
    }

    public void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        if (nextFirst < nextLast) {
            System.arraycopy(array, nextFirst + 1, newArray, 0, size);
        } else {
            int firstSize = array.length - 1 - nextFirst;
            System.arraycopy(array, nextFirst + 1, newArray, 0, firstSize);
            System.arraycopy(array, 0, newArray, firstSize, nextLast);
        }
        //重新赋值nextFirst,nextLast
        nextFirst = newArray.length - 1;
        nextLast = size;
        array = newArray;
    }

    /**
     * 动态改变数组大小(变小).
     */
    public void resizeSmall(int capacity) {
        resize(capacity);
    }

    /**
     * 动态改变数组大小（变大）.
     */
    public void resizeBig(int capacity) {
        resize(capacity);
    }

}

