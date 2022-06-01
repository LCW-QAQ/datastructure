package com.lcw.datastructure.queue;

import java.util.ArrayDeque;

/**
 * @author liuchongwei
 * @email lcwliuchongwei@qq.com
 * @date 2022-06-01
 */
public class MyArrayDeque<T> {
    private Object[] elements;

    /**
     * head表示当前队列头部的位置
     */
    private int head;

    /**
     * tail表示下一个尾部的位置
     */
    private int tail;

    private int size;

    private static final int DEFAULT_INITIAL_SIZE = 8;

    public MyArrayDeque() {
        elements = new Object[DEFAULT_INITIAL_SIZE];
    }

    public void offerLast(T t) {
        elements[tail] = t;
        if (cycleIndex(++tail) == head) {
            doubleCapacity();
        }
        size++;
    }

    public void offerFirst(T t) {
        elements[cycleIndex(--head)] = t;
        if (head == tail) {
            doubleCapacity();
        }
        size++;
    }

    public T pollFirst() {
        T res = (T) elements[head];
        // gc elements
        elements[head] = null;
        head = cycleIndex(head + 1);
        size--;
        return res;
    }

    public T pollLast() {
        tail = cycleIndex(tail - 1);
        T res = (T) elements[tail];
        // gc elements
        elements[tail] = null;
        size--;
        return res;
    }

    /**
     * 数组长度是2的n次幂，index & (n - 1) == index % n
     */
    private int cycleIndex(int index) {
        return index & (elements.length - 1);
    }

    private void doubleCapacity() {
        int h = head;
        int n = elements.length;
        int r = n - h;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalArgumentException("deque is too big!");
        }
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, h, newElements, 0, r);
        System.arraycopy(elements, 0, newElements, r, h);
        elements = newElements;
        head = 0;
        tail = n;
    }

    public static void main(String[] args) {
        ArrayDeque<Double> qStd = new ArrayDeque<>();
        MyArrayDeque<Double> q = new MyArrayDeque<>();
        for (int i = 0; i < 1000; i++) {
            double randNum = Math.random();
            qStd.offerLast(randNum);
            q.offerLast(randNum);
        }
        for (int i = 0; i < 500; i++) {
            if (!qStd.pollLast().equals(q.pollLast())) {
                throw new RuntimeException("error pollLast");
            }
        }
        for (int i = 0; i < 500; i++) {
            if (!qStd.pollFirst().equals(q.pollFirst())) {
                throw new RuntimeException("error pollLast");
            }
        }
    }
}
