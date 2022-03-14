package com.lcw.hash;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author liuchongwei
 * @email lcwliuchongwei@qq.com
 * @date 2022-03-14
 * 一个拉链法的k-v容器实现
 */
public class MyHashtable<K, V> implements Map<K, V> {

    Node<K, V>[] table;

    int size;

    // 扩容阈值 = 容量 * 负载因子
    int threshold;

    // 负载因子
    final float loadFactor;

    static final float DEFAULT_LOAD_FACTOR = 0.75F;

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    static final int MAXIMUM_CAPACITY = 1 << 30;

    public MyHashtable() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public MyHashtable(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    /**
     * Map中的entry节点
     *
     * @param <K>
     * @param <V>
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        // 存储hash值, 只会计算一次, 不会改变使用final修饰
        final int hash;

        final K key;

        V val;

        Node<K, V> next;

        public Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return val;
        }

        @Override
        public V setValue(V value) {
            V oldVal = this.val;
            this.val = value;
            return oldVal;
        }
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
        final Node<K, V> node = getNode(key);
        return node != null ? node.getValue() : null;
    }

    public Node<K, V> getNode(Object key) {
        Node<K, V>[] tab;
        Node<K, V> e;
        int n, hash;
        if ((tab = table) != null && (n = table.length) > 0) {
            // 槽位上真的有数据
            if ((e = tab[(n - 1) & (hash = hash(key))]) != null) {
                do {
                    if (e.hash == hash && (Objects.equals(key, e.key))) {
                        return e;
                    }
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false);
    }

    /**
     * 将给定key-val存入map
     *
     * @param hash         key的hash值
     * @param key          key
     * @param val          val
     * @param onlyIfAbsent 为true表示只有在不存在的时候才存入map
     */
    private V putVal(int hash, K key, V val, boolean onlyIfAbsent) {
        // 表
        Node<K, V>[] tab;
        // 表容量
        int n;
        // 插入的索引
        int i;
        // 如果表为null或者没有元素, resize
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        // 当前插入的节点
        Node<K, V> p;
        // 槽位上没有数据
        if ((p = tab[(i = (n - 1) & hash)]) == null) {
            tab[i] = new Node<>(hash, key, val, null);
        } else {
            // hash碰撞, 槽位上有数据, 拉链

            Node<K, V> e;

            // hash值与equals都相等, 替换元素
            if (p.hash == hash && (Objects.equals(key, p.key))) {
                // 逻辑重复了, 下面也有替换元素, 在下面统一替换
                e = p;
            } else {
                while (true) {
                    // 没有下一个节点了, 直接插入新节点
                    if ((e = p.next) == null) {
                        p.next = new Node<>(hash, key, val, null);
                        break;
                    }

                    // hash值与equals都相等, 替换元素
                    if (e.hash == hash && (Objects.equals(key, e.key))) {
                        break;
                    }
                    // e在上面被赋值为p.next, 下面的操作相当于p = p.next
                    p = e;
                }
            }
            // 替换元素
            if (e != null) {
                V oldValue = p.val;
                if (!onlyIfAbsent) {
                    p.val = val;
                }
                return oldValue;
            }
        }
        // map中元素数量达到扩容阈值, 扩容
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    /**
     * 扩容table数组容量, 达到扩容阈值才需要调用
     */
    private Node<K, V>[] resize() {
        Node<K, V>[] oldTab = this.table;
        // 旧tab的容量
        int oldCap = oldTab == null ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap;
        int newThr = 0;
        // 表中有数据, 更新扩容阈值
        if (oldCap > 0) {
            // 容器长度 达到最大容量限制 直接将扩容阈值拉满 不允许继续扩容
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY) {
                // 扩容阈值 * 2
                newThr = oldThr << 1;
            }
        } else if (oldThr > 0) {
            // 表中没有数据, 但是扩容阈值 > 0, 意味着之前有数据被删除了
            newCap = oldThr;
            float newThrCandidate = loadFactor * newCap;
            // 计算新的扩容阈值
            newThr = (newCap < MAXIMUM_CAPACITY && newThrCandidate < MAXIMUM_CAPACITY)
                    ? (int) newThrCandidate : Integer.MAX_VALUE;
        } else {
            // 表中没有数据, 扩容阈值 <= 0, 代表第一次扩容, 当前table是null
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (loadFactor * DEFAULT_INITIAL_CAPACITY);
        }

        // 上面主要计算了, 新的容量, 新的扩容阈值
        threshold = newThr;
        Node<K, V>[] newTab = new Node[newCap];
        this.table = newTab;
        for (int i = 0; i < oldCap; i++) {
            Node<K, V> e;
            // 槽位上有数据
            if ((e = oldTab[i]) != null) {
                oldTab[i] = null; // help gc
                if (e.next == null) {
                    newTab[e.hash & (newCap - 1)] = e;
                } else {
                    // 处理链表, 保证相对顺序
                    // 利用cap为2的n次幂
                    Node<K, V> loHead = null, loTail = null, hiHead = null, hiTail = null;
                    Node<K, V> next;
                    // lo链表表示还在原来位置的节点, hi链表表示需要移位的节点
                    // 在容量是2的n次幂, 有hash & (n - 1) == hash % n的规律, 所以可以用与运算替代mod运算
                    /*
                    且扩容后无需再次计算hash, 例如n = 16
                    n:     0001 0000
                    n - 1: 0000 1111
                    hash1: 0001 1001  -->|与运算后|  0000 1001  索引为9
                    hash2: 0100 1001  -->|与运算后|  0000 1001  索引为9
                    现在扩容 n << 1
                    n:     0010 0000
                    n - 1: 0001 1111
                    hash1: 0001 1001  -->|与运算后|  0001 1001  索引为16 + 9, 即oldCap + index
                    hash2: 0100 1001  -->|与运算后|  0000 1001  索引为9
                    发现扩容后n-1前面只是多了一个1, 位置是否变化取决于hash前面是否有与扩容后的n-1对应的1, 即:
                    当 n & hash == 0时, 表示扩容后n-1多出来的1 hash中没有与之对应的1 位置不会变化
                    当 n & hash != 0时, 表示扩容后n-1多出来的1 hash中有与之对应的1 位置发生变化 新的位置为 oldCap + index
                     */
                    // 参考文章 https://blog.csdn.net/u012501054/article/details/103710171/
                    do {
                        next = e.next;
                        // 扩容后位置不变
                        if ((e.hash & oldCap) == 0) {
                            if (loHead == null) {
                                loHead = e;
                            } else {
                                loTail.next = e;
                            }
                            loTail = e;
                        } else {
                            // 扩容后位置变化
                            if (hiHead == null) {
                                hiHead = e;
                            } else {
                                hiTail.next = e;
                            }
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

    static final int hash(Object key) {
        int h;
        // 高低位异或, 尽可能生成随机性hash即减少hash碰撞
        return key == null ? 0 : (h = key.hashCode()) ^ (h >> 16);
    }

    public static void main(String[] args) {
        final Map<Integer, Integer> map = new MyHashtable<>();
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        for (int i = 0; i < 1000; i++) {
            System.out.println(map.get(i));
        }
    }
}