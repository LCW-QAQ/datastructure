package com.lcw.datastructure.stack;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * @author liuchongwei
 * @email lcwliuchongwei@qq.com
 * @date 2022-06-01
 */
public class MyStack<T> {
    private Object[] elements;

    private int size;

    private static final int DEFAULT_INITIAL_SIZE = 8;

    public MyStack() {
        elements = new Object[DEFAULT_INITIAL_SIZE];
    }

    public void push(T t) {
        ensureCapacity();
        elements[size++] = t;
    }

    public T pop() {
        T res = (T) elements[--size];
        elements[size] = null; // gc element
        return res;
    }

    private void ensureCapacity() {
        if (size + 1 > elements.length) {
            int newCapacity = elements.length << 1;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    public boolean empty() {
        return size == 0;
    }

    public static void main(String[] args) {
        Stack<Double> stackStd = new Stack<>();
        Stack<Double> myStack = new Stack<>();
        for (int i = 0; i < 1000; i++) {
            double randNum = Math.random();
            stackStd.push(randNum);
            myStack.push(randNum);
        }
        while (!stackStd.empty()) {
            if (!stackStd.pop().equals(myStack.pop())) {
                throw new RuntimeException("error");
            }
        }
    }
}
