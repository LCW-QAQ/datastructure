package com.lcw.datastructure;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayList<E> implements Iterable<E> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;

    private Object[] elements;

    private int size;

    public MyArrayList() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void add(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public void add(int index, E e) {
        checkIndex(index);
        ensureCapacity();
        Object pre = elements[index];
        for (int i = index + 1; i < size + 1; i++) {
            Object temp = elements[i];
            elements[i] = pre;
            pre = temp;
        }
        elements[index] = e;
        size++;
    }

    public E remove(int index) {
        checkIndex(index);
        E res = (E) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return res;
    }

    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    public void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public int size() {
        return size;
    }

    public void ensureCapacity() {
        if (size + 1 > elements.length) {
            int newCapacity = elements.length << 1;
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    private class MyArrayListIterator implements Iterator<E> {

        int cur = 0;

        @Override
        public boolean hasNext() {
            return cur < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) elements[cur++];
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new MyArrayListIterator();
    }

    public static void main(String[] args) {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(999);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        for (Integer i : list) {
            System.out.print(i + " ");
        }
        for (int i = 0; i < 10; i++) {
            list.remove(0);
        }
    }
}
