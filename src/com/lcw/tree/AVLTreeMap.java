package com.lcw.tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AVLTreeMap<K extends Comparable<? super K>, V> implements Iterable<AVLTreeMap.Entry<K, V>> {

    private Entry<K, V> root;

    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public V get(Object key) {
        final Entry<K, V> node = getEntry(key);
        return node != null ? node.value : null;
    }

    private Entry<K, V> getEntry(Object key) {
        var node = root;
        @SuppressWarnings("unchecked") final Comparable<? super K> k = (Comparable<? super K>) key;
        while (node != null) {
            int cmp = k.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        return put(key, value, root);
    }

    private V put(K key, V value, Entry<K, V> node) {
        if (node == null) {
            addEntryToEmptyMap(key, value);
            return null;
        }
        Entry<K, V> parent, n = node;
        int cmp;
        do {
            parent = n;
            cmp = key.compareTo(n.key);
            if (cmp < 0) {
                n = n.left;
            } else if (cmp > 0) {
                n = n.right;
            } else {
                V oldValue = n.value;
                n.value = value; // key重复时, 覆盖value
                return oldValue;
            }
        } while (n != null);
        // 能运行到这就说明key没重复, 我们需要插入到parent下面
        addEntry(key, value, parent, cmp < 0);
        return null;
    }

    private void addEntryToEmptyMap(K key, V value) {
        root = new Entry<>(key, value, null);
        size++;
    }

    /**
     * 添加节点
     *
     * @param key       key
     * @param value     value
     * @param parent    向谁下面加
     * @param addToLeft 是否插入到左边
     */
    private void addEntry(K key, V value, Entry<K, V> parent, boolean addToLeft) {
        var newEntry = new Entry<>(key, value, parent);
        if (addToLeft) {
            parent.left = newEntry;
        } else {
            parent.right = newEntry;
        }
        parent.height = getHeight(parent); // 感觉高度可能不会用到了
        newEntry.height = getHeight(newEntry);
        rebalanced(newEntry);
        size++;
    }

    private void rebalanced(Entry<K, V> node) {
        while (node != null) {
            final int balanceFactor = getBalanceFactor(node);
            if (balanceFactor > 1) { // 左树不平衡
                if (node.left.right != null) { // LR
                    rotateLeft(node.left);
                    rotateRight(node);
                } else { // LL
                    rotateRight(node);
                }
                // 下面的有问题, 需要判断LR在判断LL, ? 可能是规律, 需要先判断双旋情况
                /*if (node.left.left != null) { // LL
//                    node = rightRotate(node);
                    rotateRight(node);
                } else { // LR
//                    node.left = leftRotate(node.left);
//                    node = rightRotate(node);
                    rotateLeft(node.left);
                    rotateRight(node);
                }*/
            } else if (balanceFactor < -1) { // 右树不平衡
                if (node.right.right != null) { // RR
//                    node = leftRotate(node);
                    rotateLeft(node);
                } else { // RL
//                    node.right = rightRotate(node.right);
//                    node = leftRotate(node);
                    rotateRight(node.right);
                    rotateLeft(node);
                }
            }
            node = node.parent;
        }
    }

    private Entry<K, V> rightRotate(Entry<K, V> node) {
        var newEntry = node.left;

        node.left = newEntry.right;
        newEntry.right = node;

        node.height = getHeight(node);
        newEntry.height = getHeight(newEntry);

        return newEntry;
    }

    private Entry<K, V> leftRotate(Entry<K, V> node) {
        var newEntry = node.right;

        node.right = newEntry.left;
        newEntry.left = node;

        node.height = getHeight(node);
        newEntry.height = getHeight(newEntry);

        return newEntry;
    }

    private void rotateLeft(Entry<K, V> node) {
        if (node != null) {
            Entry<K, V> newEntry = node.right;
            node.right = newEntry.left;
            if (newEntry.left != null)
                newEntry.left.parent = node;
            newEntry.parent = node.parent;
            if (node.parent == null)
                root = newEntry;
            else if (node.parent.left == node)
                node.parent.left = newEntry;
            else
                node.parent.right = newEntry;
            newEntry.left = node;
            node.parent = newEntry;
        }
    }

    private void rotateRight(Entry<K, V> node) {
        if (node != null) {
            Entry<K, V> newEntry = node.left;
            node.left = newEntry.right;
            if (newEntry.right != null) newEntry.right.parent = node;
            newEntry.parent = node.parent;
            if (node.parent == null)
                root = newEntry;
            else if (node.parent.right == node)
                node.parent.right = newEntry;
            else node.parent.left = newEntry;
            newEntry.right = node;
            node.parent = newEntry;
        }
    }

    private int getHeight(Entry<K, V> node) {
        if (node == null) return -1;

        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    private int getBalanceFactor(Entry<K, V> node) {
        if (node == null) return 0;

        return getHeight(node.left) - getHeight(node.right);
    }

    public V remove(Object key) {
        Entry<K, V> entry = getEntry(key);
        if (entry == null) return null;

        deleteEntry(entry);
        return entry.value;
    }

    private void deleteEntry(Entry<K, V> entry) {
        size--;

        if (entry.right != null) { // 如果右边不为null, 那么右边一定需要找到后继替换当前节点
            final Entry<K, V> minEntry = findMin(entry.right);
            entry.key = minEntry.key;
            entry.value = minEntry.value;
            deleteEntry(minEntry);
        } else { // 如果右边为null, 只需要处理左边即可
            if (entry.parent != null) { // entry 不是根节点
                if (entry.parent.left == entry) {
                    entry.parent.left = entry.left;
                } else {
                    entry.parent.right = entry.left;
                }
            } else {
                root = null;
            }
        }

        rebalanced(entry);
    }

    private Entry<K, V> findMin(Entry<K, V> entry) {
        while (entry.left != null) {
            entry = entry.left;
        }
        return entry;
    }

    public void clear() {

    }

    public Set<K> keySet() {
        return null;
    }

    public Collection<V> values() {
        return null;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new AVLTreeIter<>(root);
    }

    private static final class AVLTreeIter<K, V> implements Iterator<Entry<K, V>> {

        private final Deque<Entry<K, V>> deque = new ArrayDeque<>();

        public AVLTreeIter(Entry<K, V> entry) {
            while (entry != null) {
                deque.push(entry);
                entry = entry.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !deque.isEmpty();
        }

        @Override
        public Entry<K, V> next() {
            final Entry<K, V> entry = deque.pop();
            var cur = entry.right;
            while (cur != null) {
                deque.push(cur);
                cur = cur.left;
            }
            return entry;
        }
    }

    static final class Entry<K, V> {

        K key;
        V value;
        Entry<K, V> left;
        Entry<K, V> right;
        Entry<K, V> parent;
        int height;

        public Entry(K key, V value, Entry<K, V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    ", height=" + height +
                    '}';
        }
    }

    static class Tests {
        public static void main(String[] args) {
            test();
        }

        public static void test() {
            final int ITER_COUNT = 10;
            for (int i = 0; i < ITER_COUNT; i++) {
                final AVLTreeMap<Integer, Integer> tree = new AVLTreeMap<>();
                final TreeMap<Integer, Integer> treeMap = new TreeMap<>();
                final Random r = new Random();
                final List<Integer> samples = Stream.generate(() -> r.nextInt(100000))
                        .limit(10000).collect(Collectors.toList());
                samples.forEach(item -> {
//                    System.out.println(item);
                    tree.put(item, item);
                    treeMap.put(item, item);
                });
                // 遍历元素 是否一致
                Set<Map.Entry<Integer, Integer>> treeMapEntrySet = treeMap.entrySet();
                Iterator<Entry<Integer, Integer>> treeIt = tree.iterator();
                for (Map.Entry<Integer, Integer> treeMapEntry : treeMapEntrySet) {
                    final Entry<Integer, Integer> treeEntry = treeIt.next();
                    if (!(treeMapEntry.getKey().compareTo(treeEntry.getKey()) == 0)) throw new VerifyError("failed");
                }
                // 删除元素 再遍历是否一直
                for (int j = 0; j < samples.size(); j++) {
                    tree.remove(j);
                    treeMap.remove(j);
                    treeMapEntrySet = treeMap.entrySet();
                    treeIt = tree.iterator();
                    for (Map.Entry<Integer, Integer> treeMapEntry : treeMapEntrySet) {
                        final Entry<Integer, Integer> treeEntry = treeIt.next();
                        if (!(treeMapEntry.getKey().compareTo(treeEntry.getKey()) == 0))
                            throw new VerifyError("failed");
                    }
                }
            }

            System.out.println("success!");
        }

        public static void test_demo() {
            final AVLTreeMap<Integer, Object> tree = new AVLTreeMap<>();
            tree.put(1, 1);
            tree.put(8, 8);
            tree.put(5, 5);
            tree.put(8, 8);
            tree.put(1, 1);
            tree.put(1, 1);
            tree.put(8, 8);
            tree.put(0, 0);
            tree.put(8, 8);
            tree.put(5, 5);
            tree.put(1, 1);
            tree.put(0, 0);
            tree.put(2, 2);
            tree.put(1, 1);
            tree.put(3, 3);
            System.out.println("hello");
        }

        public static void test_10demo() {
            final AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
            for (int i = 0; i <= 10; i++) {
                map.put(i, i);
            }
            map.forEach(item -> {
                System.out.printf("%s ", item.getKey());
            });
            System.out.println();
            for (int i = 0; i <= 10; i++) {
                map.remove(i);
            }
        }
    }
}