package com.lcw.datastructure.tree.notok;

import com.lcw.datastructure.tree.AVLTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AVLTreeNotOkYet<T extends Comparable<? super T>> implements Iterable<T> {

    private Node<T> root;

    private int size;

    public void add(T t) {
        root = add(root, t);
    }

    public void remove(T t) {
        root = remove(root, t);
    }

    private Node<T> remove(Node<T> node, T t) {
        if (node == null) return null;

        final int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, t);
        } else if (cmp > 0) {
            node.right = remove(node.right, t);
        } else {
            if (node.right != null) { // 右边一定有最小值
//                node.value = removeMin(node.right).value;
                node.value = findMin(node.right);
                node.right = remove(node.right, node.value);
            } else {
                node = node.left;
            }
        }

        return rebalanced_v2(node);
    }

    // ERROR
    private Node<T> removeMin(Node<T> node) {
        assert node != null;
        Node<T> pre = null;
        while (node.left != null) {
            pre = node;
            node = node.left;
        }
        return remove(pre, node.value);
    }

    private T findMin(Node<T> node) {
        assert node != null;
        while (node.left != null) {
            node = node.left;
        }
        return node.value;
    }

    private Node<T> add(Node<T> node, T t) {
        if (node == null) return new Node<>(t);

        final int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = add(node.left, t);
        } else if (cmp > 0) {
            node.right = add(node.right, t);
        }

        node.height = getHeight(node);

        // 平衡节点
        return rebalanced_v2(node);
    }

    private Node<T> rebalanced(Node<T> node) {
        final int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1 && getBalanceFactor(node.left) > 0) { // LL
            return rightRotate(node);
        } else if (balanceFactor > 1 && getBalanceFactor(node.left) < 0) { // LR
            node.left = leftRotate(node.left);
            return rightRotate(node);
        } else if (balanceFactor < -1 && getBalanceFactor(node.right) < 0) { // RR
            return leftRotate(node);
        } else if (balanceFactor < -1 && getBalanceFactor(node.right) > 0) { // RL
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    // 2021/11/30 21:36
    // 感觉可能ok了, 之前的问题都是删除节点后没有正确扩容, 明知左右子树不平衡, 但是&&后面判断子树平衡因子为false导致没有旋转  ==> 还是空指针了, 貌似只适合插入
    private Node<T> rebalanced_(Node<T> node, T t) {
        final int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1) { // 左子树不平衡
            if (t.compareTo(node.left.value) < 0) { // LL
                return rightRotate(node);
            } else if (t.compareTo(node.left.value) > 0) { // LR
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        } else if (balanceFactor < -1) { // 右子树不平衡
            if (t.compareTo(node.right.value) < 0) { // RL
                node.right = rightRotate(node.right);
                return leftRotate(node);
            } else if (t.compareTo(node.right.value) > 0) { // RR
                return leftRotate(node);
            }
        }
        return node;
    }

    private Node<T> rebalanced_v2(Node<T> node) {
        final int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1) { // 左子树不平衡
            if (node.left.left != null) { // LL
                return rightRotate(node);
            } else { // LR
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        } else if (balanceFactor < -1) { // 右子树不平衡
            if (node.right.right != null) { // RR
                return leftRotate(node);
            } else { // RL
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }
        return node;
    }

    private Node<T> rebalanced(Node<T> node, T t) {
        var balancedFactor = getBalanceFactor(node);
        if (balancedFactor > 1 && t.compareTo(node.value) < 0)
            return rightRotate(node);     //case 1
//        if (balancedFactor < -1 && insertedKey > node.key)
        if (balancedFactor < -1 && t.compareTo(node.value) > 0)
            return leftRotate(node);      //case 4
//        if (balancedFactor > 1 && insertedKey > node.left.key) {
        if (balancedFactor > 1 && t.compareTo(node.left.value) > 0) {
            // case 2
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
//        if (balancedFactor < -1 && insertedKey < node.left.key) {
        if (balancedFactor < -1 && t.compareTo(node.left.value) < 0) {
            // case 3
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        // if do not need rebalanced (all conditions cannot satisfied)
        // then return the current node
        return node;
    }

    private Node<T> leftRotate(Node<T> node) {
        var newNode = node.right;

        node.right = newNode.left;
        newNode.left = node;

        newNode.height = getHeight(newNode);
        node.height = getHeight(node);

        return newNode;
    }

    private Node<T> rightRotate(Node<T> node) {
        var newNode = node.left;

        node.left = newNode.right;
        newNode.right = node;

        newNode.height = getHeight(newNode);
        node.height = getHeight(node);

        return newNode;
    }

    /**
     * 获取给定节点的高度
     * 有点坑边界条件, 如果在节点为null时返回0和返回-1
     * 返回0时, 只有一个节点的时候高度为1
     * 返回-1时, 只有一个节点的时候高度为0
     * <p>
     * 在判断树高的时候:
     * 返回0时:
     * 树高差
     * 返回1时:
     * 树高差
     *
     * @param node
     * @return
     */
    private int getHeight(Node<T> node) {
        if (node == null) return -1;

        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    private int getBalanceFactor(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }

    private static final class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        public Node(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", height=" + height +
                    '}';
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new AVLTreeIter<>(root);
    }

    private static final class AVLTreeIter<T> implements Iterator<T> {

        private Deque<Node<T>> deque = new ArrayDeque<>();

        public AVLTreeIter(Node<T> root) {
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
            // 该节点已经是最左边的节点了, 左边为null
            final Node<T> node = deque.pop();
            var cur = node.right;

            while (cur != null) {
                deque.push(cur);
                cur = cur.left;
            }

            return node.value;
        }
    }
}

class Tests {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        final int ITER_COUNT = 100;
        for (int i = 0; i < ITER_COUNT; i++) {
            final AVLTree<Integer> tree = new AVLTree<>();
            final TreeSet<Integer> treeSet = new TreeSet<>();
            final Random r = new Random();
            final List<Integer> samples = Stream.generate(() -> r.nextInt(1000000))
                    .limit(10000).collect(Collectors.toList());
            samples.forEach(item -> {
                tree.add(item);
                treeSet.add(item);
            });
            // 遍历元素 是否一致
            for (Iterator<Integer> treeIt = tree.iterator(),
                 treeSetIt = treeSet.iterator(); treeIt.hasNext() || treeSetIt.hasNext(); ) {
                if (!treeIt.next().equals(treeSetIt.next())) {
                    throw new VerifyError("failed");
                }
            }
            // 删除元素 再遍历是否一直
            for (int j = 0; j < samples.size(); j++) {
                tree.remove(j);
                treeSet.remove(j);
                for (Iterator<Integer> treeIt = tree.iterator(),
                     treeSetIt = treeSet.iterator(); treeIt.hasNext() || treeSetIt.hasNext(); ) {
                    if (!treeIt.next().equals(treeSetIt.next())) {
                        throw new VerifyError("failed");
                    }
                }
            }
        }

        System.out.println("success!");
    }

    public static void test_10demo() {
        final AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 0; i <= 10; i++) {
            tree.add(i);
        }
        tree.forEach(item -> System.out.printf("%s ", item));
        System.out.println();
        for (int i = 0; i <= 10; i++) {
            tree.remove(i);
            tree.forEach(item -> System.out.printf("%s ", item));
            System.out.println();
        }
    }
}