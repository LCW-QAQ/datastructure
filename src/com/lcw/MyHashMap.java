package com.lcw;

import java.util.*;

public class MyHashMap<K, V> implements Map<K, V> {

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    static final float DEFAULT_LOADER_FACTOR = 0.75f;

    static final int MAXIMUM_CAPACITY = 1 << 30;

    int size;

    // 扩容大小阈值
    int threshold;

    // 负载因子
    final float loadFactor;

    Node<K, V>[] table;

    public MyHashMap() {
        this.loadFactor = DEFAULT_LOADER_FACTOR;
    }

    public MyHashMap(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    static class Node<K, V> implements Map.Entry<K, V> {

        final int hash;

        final K key;

        V value;

        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key) && Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >> 16);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * 向Map中插入k-v键值对
     * @param hash key的hash值
     * @param key key
     * @param value value
     * @param onlyIfAbsent true表示只有在key不存在的时候才存放
     * @param evict
     * @return value
     */
    private V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K, V>[] tab;
        Node<K, V> p; // 当前要插入的元素
        int n, i; // n 表容量
        // 判断是否是空表
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length; // 是空表就初始化
        }

        // 槽位上没有数据, 直接插入
        if ((p = tab[i = (n - 1) & hash]) == null) {
            tab[i] = new Node<>(hash, key, value, null);
        } else { // 槽位有数据, hash冲突
            Node<K, V> e;
            K k;

            // 要插入的元素 hash 和 key都相等, 直接替换元素
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k)))) {
                e = p;
            } else { // 槽位上的元素不等于要插入的元素 拉链
                while (true) {
                    // p下面没有
                    if ((e = p.next) == null) { // break 后e为null
                        p.next = new Node<>(hash, key, value, null);
                        break;
                    }

                    // 拉链过程中发现元素相等, 直接替换
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k)))) {
                        break; // break 后e不为null
                    }
                    p = e;
                }
            }
            if (e != null) { // e不为null 一位置链表中存在可以映射的元素
                V oldValue = e.value;
                // 如果在不存在的时候也允许插入 或 之前插入的是null 直接替换value
                if (!onlyIfAbsent || oldValue == null) {
                    e.value = value;
                }
                return oldValue;
            }
        }
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    private Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        // 扩容前容量
        int oldCap = oldTab == null ? 0 : oldTab.length;
        // 扩容前扩容阈值
        int oldThr = threshold;
        int newCap;  // 新容量 与 新扩容阈值
        int newThr = 0;
        // 表中有数据
        if (oldCap > 0) {
            // 容量超过最大值
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY) {
                newThr = oldThr << 1;
            }
        } else if (oldThr > 0) { // 表中无数据, 但是扩容阈值>0 (意味着之前有数据, 只不过删除了)
            newCap = oldThr;
        } else { // 能走到这里来, 说明是第一次resize, 下面的代码块负责初始化
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_LOADER_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 这里新的扩容阈值为0, 说明是上面oldThr > 0的情况, 计算新的扩容阈值
        if (newThr == 0) {
            float ft = newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        // 创建新的表
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            // 搬运之前的数据到新的表中
            for (int i = 0; i < oldCap; i++) {
                Node<K, V> e;
                if ((e = oldTab[i]) != null) { // 槽位上有数据的话
                    oldTab[i] = null; // 置空旧表, help gc
                    // 没有形成链表直接替换
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    } else { // 处理链表
                        // 为了保持顺序, 分成low high
                        Node<K, V> loHead = null, loTail = null,
                                hiHead = null, hiTail = null;
                        Node<K, V> next;

                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            } else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[i] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[i + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
