package com.lcw.datastructure.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BinarySearchTree<T extends Comparable<? super T>> implements Iterable {

    private BinaryNode<T> root;

    public void insert(T t) {
        root = insert(t, root);
    }

    public BinaryNode<T> insert(T t, BinaryNode<T> node) {
        if (node == null)
            return new BinaryNode<>(null, t, null);
        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(t, node.left);
        } else if (cmp > 0) {
            node.right = insert(t, node.right);
        } else {
            node.value = t;
        }
        return node;
    }

    public void remove(T t) {
        root = remove(t, root);
    }

    public BinaryNode<T> remove(T t, BinaryNode<T> node) {
        if (node == null) return null;
        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(t, node.left);
        } else if (cmp > 0) {
            node.right = remove(t, node.right);
        } else if (node.left != null && node.right != null) {
            node.value = findMinNode(node.right).value;
            node.right = remove(node.value, node.right);
        } else {
            node = node.left != null ? node.left : node.right;
        }
        return node;
    }

    public boolean contains(T t) {
        return contains(t, root);
    }

    public BinaryNode<T> findMinNode(BinaryNode<T> node) {
        if (node != null) {
            while (node.left != null) {
                node = node.left;
            }
        }
        return node;
    }

    public BinaryNode<T> findMaxNode(BinaryNode<T> node) {
        if (node != null) {
            while (node.right != null)
                node = node.right;
        }
        return node;
    }

    public boolean contains(T t, BinaryNode<T> node) {
        if (node == null) return false;
        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            return contains(t, node.left);
        } else if (cmp > 0) {
            return contains(t, node.right);
        } else {
            return true;
        }
    }

    public void display() {
        display(root);
    }

    public void display(BinaryNode<T> node) {
        displayProcess(node, 0, "H", 10);
    }

    public void displayProcess(BinaryNode<T> node, int height, String placeholder, int stdSpaceLen) {
        if (node == null) return;
        displayProcess(node.right, height + 1, "V", stdSpaceLen);
        String val = placeholder + node.value + placeholder;
        int lenVal = val.length();
        int lenL = (stdSpaceLen - lenVal) >> 1;
        int lenR = stdSpaceLen - lenVal - lenL;
        System.out.printf("%s%s%s%s\r\n", getSpace(height * stdSpaceLen), getSpace(lenL), val, getSpace(lenR));
        displayProcess(node.left, height + 1, "^", stdSpaceLen);
    }

    public String getSpace(int num) {
        return Stream.iterate(" ", (s) -> s).limit(num).collect(Collectors.joining());
    }

    public boolean isEmpty() {
        return root == null;
    }

    private class BinarySearchTreeIterator implements Iterator<T> {
        Deque<BinaryNode<T>> deque = new ArrayDeque<>();

        public BinarySearchTreeIterator(BinaryNode<T> root) {
            while (root != null) {
                deque.push(root);
                root = root.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !deque.isEmpty();
        }

        @Override
        public T next() {
            BinaryNode<T> node = deque.pop();
            BinaryNode<T> cur = node.right;

            while (cur != null) {
                deque.push(cur);
                cur = cur.left;
            }
            return node.value;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator(root);
    }

    private class BinaryNode<T> {
        T value;
        BinaryNode<T> left;
        BinaryNode<T> right;

        public BinaryNode(BinaryNode<T> left, T value, BinaryNode<T> right) {
            this.left = left;
            this.value = value;
            this.right = right;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        IntStream.generate(() -> (int) (Math.random() * 100))
                .limit(20)
                .forEach(tree::insert);
        Iterator<Integer> it = tree.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();
        tree.display();
    }
}
