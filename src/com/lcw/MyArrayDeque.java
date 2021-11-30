package com.lcw;

import java.util.ArrayDeque;

public class MyArrayDeque<E> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private Object[] elements;

    private int head;

    private int tail;

    private int size;

    public MyArrayDeque() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void offer(E e) {
        offerLast(e);
    }

    public void offerLast(E e) {
        elements[tail] = e;
        if ((++tail & (elements.length - 1)) == head) {
            doubleCapacity();
        }
    }

    public void offerFirst(E e) {
        elements[--head & (elements.length - 1)] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    public E poll() {
        return pollFirst();
    }

    private E pollFirst() {
        int h = head;
        E res = (E) elements[h];
        elements[h] = null; // help gc
        head = (h + 1) & (elements.length - 1);
        return res;
    }

    public E pollLast() {
        int l = (tail - 1) & (elements.length - 1);
        E res = (E) elements[l];
        elements[l] = null;
        tail = l;
        return res;
    }

    public boolean isEmpty() {
        return head == tail;
    }

    private void doubleCapacity() {
        int h = head;
        int n = elements.length;
        int r = n - h;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("deque is to big !");
        }
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, h, newElements, 0, r);
        System.arraycopy(elements, 0, newElements, r, h);
        elements = newElements;
        head = 0;
        tail = n;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            deque.offer(i);
        }
        while (!deque.isEmpty()) {
            System.out.print(deque.pollFirst() + " ");
        }
    }
}