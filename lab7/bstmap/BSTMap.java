package bstmap;

import org.w3c.dom.Node;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private int size;
    private BSTNode root;

    BSTMap() {
        root = null;
        size = 0;
    }

    private boolean findKey(BSTNode N, K key) {
        if (N == null) {
            return false;
        } else if (key.compareTo(N.key) == 0) {
            return true;
        } else if (key.compareTo(N.key) < 0) {
            return findKey(N.left, key);
        } else {
            return findKey(N.right, key);
        }
    }


    public void printInOrder() {

    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    //find key
    @Override
    public boolean containsKey(K key) {
        return findKey(root, key);
    }

    private BSTNode getKey(BSTNode N, K key) {
        if (key.equals(N.key)) {
            return N;
        } else if (key.compareTo(N.key) < 0) {
            return getKey(N.left, key);
        } else {
            return getKey(N.right, key);
        }
    }


    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        } else {
            return getKey(root, key).value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    private BSTNode insert(BSTNode N, K key, V value) {
        if (N == null) {
            N = new BSTNode(key, value);
        } else if (key.compareTo(N.key) < 0) {
            N.left = insert(N.left, key, value);
        } else {
            N.right = insert(N.right, key, value);
        }
        return N;
    }

    @Override
    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
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
        return null;
    }

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode() {
            key = null;
            value = null;
            left = null;
            right = null;
        }

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            size++;
        }

    }
}
