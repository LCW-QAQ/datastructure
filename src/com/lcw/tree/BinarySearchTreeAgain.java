package com.lcw.tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinarySearchTreeAgain<T extends Comparable<? super T>> implements Iterable<T> {

    private Node<T> root;

    public void insert(T t) {
        root = insert(root, t);
    }

    public Node<T> insert(Node<T> node, T t) {
        if (node == null) return new Node<>(null, t, null);
        final int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, t);
        } else if (cmp > 0) {
            node.right = insert(node.right, t);
        } else {
            node.value = t;
        }
        return node;
    }

    public T remove(T t) {
        final Node<T> n = remove(root, t);
        return n != null ? n.value : null;
    }

    private Node<T> remove(Node<T> node, T t) {
        if (node == null) return null;
        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, t);
        } else if (cmp > 0) {
            node.right = remove(node.right, t);
        } else if (node.left != null && node.right != null) { // 找到要删除的了, 但是左右两边都有子节点
            node.value = findMinNode(node.right).value; // 找到右边最小的值替换
            node.right = remove(node.right, node.value); // 删除右边最小的值
        } else {
            node = node.left != null ? node.left : node.right;
        }
        return node;
    }

    private Node<T> findMinNode(Node<T> node) {
        if (node != null) {
            while (node.left != null) {
                node = node.left;
            }
        }
        return node;
    }

    public boolean contains(Node<T> node, T t) {
        if (node == null) return false;
        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            return contains(node.left, t);
        } else if (cmp > 0) {
            return contains(node.right, t);
        } else {
            return true;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new BSTIter(root);
    }

    private class BSTIter implements Iterator<T> {
        Deque<Node<T>> deque = new ArrayDeque<>();

        public BSTIter(Node<T> node) {
            // 先将左边压入队列
            while (node != null) {
                deque.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !deque.isEmpty();
        }

        @Override
        public T next() {
            final Node<T> node = deque.pop();
            Node<T> cur = node.right;
            while (cur != null) {
                deque.push(cur);
                cur = cur.left;
            }
            return node.value;
        }
    }

    static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        public Node(Node<T> left, T value, Node<T> right) {
            this.left = left;
            this.value = value;
            this.right = right;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            final List<Integer> list = IntStream.range(0, 1000)
                    .boxed().collect(Collectors.toList());
            final BinarySearchTreeAgain<Integer> tree = new BinarySearchTreeAgain<>();
            for (int j = 0; j < 1000; j++) {
                tree.insert(j);
            }
            for (Iterator<Integer> itList = list.iterator(), itTree = tree.iterator();
                 itList.hasNext() && itTree.hasNext(); ) {
                if (!Objects.equals(itList.next(), itTree.next()))
                    throw new IllegalStateException("failed");
            }
        }
        System.out.println("success");
    }
}