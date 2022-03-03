package com.lcw.tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
红黑树特性:
    1. 节点要么是红要么是黑
    2. 根节点是黑色
    3. 每个为即将插入的叶子结点(null节点)都是黑色
    4. 如果一个节点是红色那么他的子节点必须是黑色
    5. 从任意节点出发到他子节点的所有路径上, 黑色节点数量都必须一样
 */
public class RBTree<K extends Comparable<? super K>, V> implements Iterable<RBTree.Entry<K, V>> {

    private Entry<K, V> root;

    private int size;

    public V put(K key, V value) {
        return put(key, value, true);
    }

    private V put(K key, V value, boolean replaceOld) {
        if (root == null) {
            addEntryToEmptyMap(key, value);
            return null;
        }
        int cmp;
        var node = root;
        Entry<K, V> parent;
        // 这个do while就是在找该在哪里插入或者已经有值了替换
        do {
            parent = node;
            cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                var oldValue = node.value;
                if (replaceOld) {
                    node.value = value;
                }
                return oldValue;
            }
        } while (node != null);
        addEntry(key, value, parent, cmp < 0);
        return null;
    }

    private void addEntry(K key, V value, Entry<K, V> parent, boolean addToLeft) {
        var newEntry = new Entry<>(key, value);
        newEntry.parent = parent;
        if (addToLeft) {
            parent.left = newEntry;
        } else {
            parent.right = newEntry;
        }
        fixAfterInsertion(newEntry);
        size++;
    }

    private void fixAfterInsertion(Entry<K, V> x) {
        // 新插入的节点只能是红色, 因为要保证特性5
        x.color = RED;

        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Entry<K, V> y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else { // 叔叔节点是黑色
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        leftRotate(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rightRotate(parentOf(parentOf(x)));
                }
            } else {
                Entry<K, V> y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rightRotate(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    leftRotate(parentOf(parentOf(x)));
                }
            }
        }

        root.color = BLACK;
    }

    public V remove(Object key) {
        final Entry<K, V> entry = getEntry(key);
        if (entry == null) return null;

        V oldValue = entry.value;
        deleteEntry(entry);

        return oldValue;
    }

    private void deleteEntry(Entry<K, V> entry) {
        size--;

        if (entry.right != null) { // 右边不为null, 找到后继节点替换
            final Entry<K, V> minEntry = findMin(entry.right);
            entry.key = minEntry.key;
            entry.value = minEntry.value;
            deleteEntry(minEntry);
        } else { // 右边为null只需要处理左边
            if (entry.parent != null) { // 不是根节点
                if (entry.parent.left == entry) {
                    entry.parent.left = entry.left;
                } else {
                    entry.parent.right = entry.left;
                }
            } else {
                root = null;
            }
        }

        if (entry.color == BLACK) {
            fixAfterDeletion(entry);
        }
    }

    private void fixAfterDeletion(Entry<K,V> x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                Entry<K,V> sib = rightOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    leftRotate(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                if (colorOf(leftOf(sib))  == BLACK &&
                        colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rightRotate(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    leftRotate(parentOf(x));
                    x = root;
                }
            } else { // symmetric
                Entry<K,V> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rightRotate(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                        colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        leftRotate(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rightRotate(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }

    private Entry<K, V> getEntry(Object key) {
        Objects.requireNonNull(key);
        var node = root;
        var k = (Comparable<? super K>) key;
        while (node != null) {
            final int cmp = k.compareTo(node.key);
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

    private void rightRotate(Entry<K, V> e) {
        if (e != null) {
            var l = e.left;
            e.left = l.right;
            if (l.right != null) {
                l.right.parent = e;
            }
            l.parent = e.parent;
            if (e.parent == null) { // 当前节点就是根节点
                root = l;
            } else if (e.parent.left == e) {
                e.parent.left = l;
            } else {
                e.parent.right = l;
            }
            l.right = e;
            e.parent = l;
        }
    }

    private void leftRotate(Entry<K, V> e) {
        if (e != null) {
            var r = e.right;
            e.right = r.left;
            if (r.left != null) {
                r.left.parent = e;
            }
            r.parent = e.parent;
            if (e.parent == null) {
                root = r;
            } else if (e.parent.left == e) {
                e.parent.left = r;
            } else {
                e.parent.right = r;
            }
            r.left = e;
            e.parent = r;
        }
    }

    private Entry<K, V> findMin(Entry<K, V> entry) {
        while (entry.left != null) {
            entry = entry.left;
        }
        return entry;
    }

    private Entry<K, V> parentOf(Entry<K, V> e) {
        return e == null ? null : e.parent;
    }

    private Entry<K, V> leftOf(Entry<K, V> e) {
        return e == null ? null : e.left;
    }

    private Entry<K, V> rightOf(Entry<K, V> e) {
        return e == null ? null : e.right;
    }

    private boolean colorOf(Entry<K, V> e) {
        return e == null ? BLACK : e.color;
    }

    private void setColor(Entry<K, V> e, boolean color) {
        if (e != null)
            e.color = color;
    }

    public int size() {
        return size;
    }

    private void addEntryToEmptyMap(K key, V value) {
        root = new Entry<>(key, value);
        size = 1;
    }

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    static final class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> left;
        Entry<K, V> right;
        Entry<K, V> parent;

        // 节点默认是黑色, 因为要插入的子节点都是黑色的
        boolean color = BLACK;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            var oldValue = this.value;
            this.value = value;
            return oldValue;

        }
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new RBTreeIter<>(root);
    }

    static final class RBTreeIter<K, V> implements Iterator<Entry<K, V>> {
         private Deque<Entry<K, V>> deque = new ArrayDeque<>();

         public RBTreeIter(Entry<K, V> entry) {
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
            final Entry<K, V> e = deque.pop();
            var x = e.right;
            while (x != null) {
                deque.push(x);
                x = x.left;
            }
            return e;
        }
    }

    static class Tests {
        public static void main(String[] args) {
            test();
        }

        public static void test() {
            final int ITER_COUNT = 10;
            for (int i = 0; i < ITER_COUNT; i++) {
                final RBTreeMap<Integer, Integer> tree = new RBTreeMap<>();
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
                Iterator<RBTreeMap.Entry<Integer, Integer>> treeIt = tree.iterator();
                for (Map.Entry<Integer, Integer> treeMapEntry : treeMapEntrySet) {
                    final RBTreeMap.Entry<Integer, Integer> treeEntry = treeIt.next();
                    if (!(treeMapEntry.getKey().compareTo(treeEntry.getKey()) == 0)) throw new VerifyError("failed");
                }
                // 删除元素 再遍历是否一致
                for (int j = 0; j < samples.size(); j++) {
                    tree.remove(j);
                    treeMap.remove(j);
                    treeMapEntrySet = treeMap.entrySet();
                    treeIt = tree.iterator();
                    for (Map.Entry<Integer, Integer> treeMapEntry : treeMapEntrySet) {
                        final RBTreeMap.Entry<Integer, Integer> treeEntry = treeIt.next();
                        if (!(treeMapEntry.getKey().compareTo(treeEntry.getKey()) == 0) &&
                                tree.containsValue(j + 1) == treeMap.containsValue(j + 1))
                            throw new VerifyError("failed");
                    }
                }
            }

            System.out.println("success!");
        }
    }
}