package com.lcw;

public class MyStack<E> {
    private Object[] elements;

    private static int DEFAULT_INITIAL_CAPACITY = 10;

    int size;

    public MyStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        E res = (E) elements[size - 1];
        elements[--size] = null;
        return res;
    }

    private void ensureCapacity() {
        if (size + 1 > elements.length) {
            Object[] newElements = new Object[elements.length << 1];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    public static void main(String[] args) {
        MyStack<Integer> stack = new MyStack<>();
        for (int i = 0; i < 10; i++) {
            stack.push(i);
        }
        for (int i = 0; i < 10; i++) {
            System.out.print(stack.pop() + " ");
        }
    }
}