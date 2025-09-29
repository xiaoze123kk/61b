package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    int itemsSize;
    private Collection<Node>[] buckets;
    private int bucketsSize;
    private double loadSetFactor;
    private double loadFactor;
    private HashSet<K> keys;
    private HashSet<Node> nodes;

    /**
     * Constructors
     */
    public MyHashMap() {
        bucketsSize = 16;
        loadSetFactor = 0.75;
        itemsSize = 0;
        buckets = createTable(bucketsSize);
        keys = new HashSet<>();
        nodes = new HashSet<>();
        loadFactor = itemsSize / buckets.length;
        for (int i = 0; i < bucketsSize; i++) {
            buckets[i] = createBucket();
        }
    }

    public MyHashMap(int initialSize) {
        this.bucketsSize = initialSize;
        loadSetFactor = 0.75;
        itemsSize = 0;
        buckets = createTable(initialSize);
        keys = new HashSet<>();
        nodes = new HashSet<>();
        loadFactor = itemsSize / buckets.length;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of bucketsSize.
     * The load factor (# items / # buckets) should always be <= loadSetFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketsSize = initialSize;
        this.loadSetFactor = maxLoad;
        itemsSize = 0;
        buckets = createTable(initialSize);
        keys = new HashSet<>();
        nodes = new HashSet<>();
        loadFactor = itemsSize / buckets.length;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    //获取哈希值Index
    private int getHashIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        itemsSize = 0;
        keys.clear();
        nodes.clear();
    }

    @Override
    public boolean containsKey(K key) {
        int hashIndex = getHashIndex(key);
        for (Node node : buckets[hashIndex]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int hashIndex = getHashIndex(key);
        for (Node node : buckets[hashIndex]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return itemsSize;
    }

    private void resize() {
        bucketsSize*=2;
        buckets = createTable(bucketsSize);
        for (int i = 0; i < bucketsSize; i++) {
            buckets[i] = createBucket();
        }
        for (Node node : nodes){
            int hashIndex = getHashIndex(node.key);
            buckets[hashIndex].add(node);
        }
    }

    @Override
    public void put(K key, V value) {
        int hashIndex = getHashIndex(key);
        for (Node node : buckets[hashIndex]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        Node n = createNode(key,value);
        buckets[hashIndex].add(n);
        keys.add(key);
        nodes.add(n);
        itemsSize++;
        loadFactor = itemsSize / buckets.length;
        if (loadFactor > loadSetFactor) {
            resize();
        }
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

}
