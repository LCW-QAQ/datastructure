package com.lcw.datastructure;

import java.util.*;
import java.util.stream.IntStream;

public class MyArrayDequeAgain<T> implements Iterable<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private T[] elements;

    // 头指针
    private int head;

    // 尾指针
    private int tail;

    // 元素个数

    public MyArrayDequeAgain() {
        elements = (T[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * 向队列尾部追加元素
     *
     * @param t 需要添加的元素
     */
    public void offerLast(T t) {
        elements[tail] = t;
        /*
         tail永远只在下一个元素的位置
         (1) 当tail > head 则 8 & 7 = 0
         +----------------------+        +----------------------+
         | [0 1 2 3 4 5 6 null] |        |  [0 1 2 3 4 5 6 7]   |
         |  h           L   T   |   ->   |   h             L  T |
         +----------------------+        +----------------------+

         (2) 当tail < head 则 2 & 7 = 2
         +----------------------+        +----------------------+
         | [0 null 2 3 4 5 6 7] |        |  [0 1 2 3 4 5 6 7]   |
         |                      |        |       T              |
         |  L   T  h            |   ->   |     L h              |
         +----------------------+        +----------------------+
         原理:
         当数组长度是2的N次幂时, 任意0 <= x <= 2^n的数x, `(x & (2^n - 1)) <= (2^n - 1)`
         因此当`(tail & (elements.length - 1)) == head`时意味着头尾装上了即容器满了, 需要扩容
         还有个特殊情况就是head == 0时, 要offerFirst, head = (0 - 1) & 7, head == 7, 是一样的
         */
        // 刷新尾巴
        if ((tail = nextTail()) == head) {
            doubleCapacity();
        }
    }

    public void offerFirst(T t) {
        // 同理offerLast
        elements[head = prevHead()] = t;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * 弹出最后一项
     */
    public T pollLast() {
        int t = prevTail();
        T res = elements[t];
        elements[t] = null;
        tail = t;
        return res;
    }

    /**
     * 弹出第一项
     */
    public T pollFirst() {
        T res = elements[head];
        head = nextHead();
        return res;
    }

    /**
     * 将数组扩容两倍
     */
    private void doubleCapacity() {
        int h = head;
        int n = elements.length;
        int r = n - h; // 包括头在内, 头右边有多少个数 [head..n]
        int newCapacity = n << 1; // 扩容一倍
        if (newCapacity < 0) throw new IllegalStateException("sorry deque is too big!");
        T[] newElements = (T[]) new Object[newCapacity];
        System.arraycopy(elements, h, newElements, 0, r);
        System.arraycopy(elements, 0, newElements, r, h);
        elements = newElements;
        head = 0;
        tail = n;
    }

    /**
     * 返回容器中元素的数量
     */
    public int size() {
        throw new RuntimeException("not support now!");
    }

    private int nextTail() {
        return (tail + 1) & (elements.length - 1);
    }

    private int prevTail() {
        return (tail - 1) & (elements.length - 1);
    }

    private int nextHead() {
        return (head + 1) & (elements.length - 1);
    }

    private int prevHead() {
        return (head - 1) & (elements.length - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new DeqIterator(head, tail, -1);
    }

    private final class DeqIterator implements Iterator<T> {

        private int cursor;

        private int last;

        private int lastRet;

        public DeqIterator(int cursor, int last, int lastRet) {
            this.cursor = cursor;
            this.last = last;
            this.lastRet = lastRet;
        }

        @Override
        public boolean hasNext() {
            return cursor != last;
        }

        @Override
        public T next() {
            if (cursor == last) throw new NoSuchElementException();
            T res = elements[cursor];
            if (last != tail) throw new ConcurrentModificationException();
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return res;
        }
    }

    public static void main(String[] args) {
        final int COUNT = 10000;
        for (int i = 0; i < 10; i++) {
            final ArrayDeque<Integer> deque = IntStream.range(0, COUNT)
                    .collect(ArrayDeque::new, ArrayDeque::offerLast, ArrayDeque::addAll);
            final MyArrayDequeAgain<Integer> myDeque = new MyArrayDequeAgain<>();
            for (int j = 0; j < COUNT; j++) {
                myDeque.offerLast(j);
            }
            myDeque.iterator().forEachRemaining(System.out::println);
            for (int j = 0; j < COUNT / 2; j++) {
                final Integer a = deque.pollLast();
                final Integer b = myDeque.pollLast();
//                System.out.println(a + ", " + b);
                if (!Objects.equals(a, b))
                    throw new RuntimeException("failed");
            }
            for (int j = 0; j < COUNT / 2; j++) {
                final Integer a = deque.pollFirst();
                final Integer b = myDeque.pollFirst();
//                System.out.println(a + ", " + b);
                if (!Objects.equals(a, b))
                    throw new RuntimeException("failed");
            }
        }
        System.out.println("success");
    }
}