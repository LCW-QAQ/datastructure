package com.lcw.tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AVLTree<T extends Comparable<? super T>> implements Iterable<T> {

    private Node<T> root;

    private int size;

    public void add(T t) {
        root = add(root, t);
    }

    public void remove(T t) {
        root = remove(root, t);
    }

    /**
     * 删除给定节点上的值
     *
     * @param node 以node为根的子树
     * @param t    要删除的值
     * @return 删除后的新根节点
     */
    private Node<T> remove(Node<T> node, T t) {
        if (node == null) return null;

        final int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, t);
        } else if (cmp > 0) {
            node.right = remove(node.right, t);
        } else {
            if (node.right != null) { // 右边不为空, 右边一定有最小值, 找到后替换到当前位置
                node.value = findMin(node.right);
                node.right = remove(node.right, node.value);
            } else { // 只要右边是null的, 我们就之关心左边, 而左边只需要简单的替换即可, 在后面处理平衡
                node = node.left;
            }
            size--;
        }

        return rebalanced(node);
    }

    /**
     * 站到给定节点子树上最小的值
     *
     * @param node
     * @return 最小的值
     */
    private T findMin(Node<T> node) {
        assert node != null;
        while (node.left != null) {
            node = node.left;
        }
        return node.value;
    }

    /**
     * 在node为根的子树上添加一个值
     *
     * @param node 以node为根的子树
     * @param t    需要添加的值
     * @return 新的根节点
     */
    private Node<T> add(Node<T> node, T t) {
        if (node == null) { // 直接添加节点
            size++;
            return new Node<>(t);
        }

        final int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = add(node.left, t);
        } else if (cmp > 0) {
            node.right = add(node.right, t);
        }

        // 添加节点后更新高度
        node.height = getHeight(node);

        // 平衡节点
        return rebalanced(node);
    }

    /**
     * 平衡以node为根的子树
     *
     * @param node node根节点
     * @return 平衡后新的根节点
     */
    private Node<T> rebalanced(Node<T> node) {
        // 获取平衡因子
        final int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1) { // 左子树不平衡
            // 能进来就意味着 左子树不平衡 所以左子树一定不为null 不需要担心空指针
            if (node.left.left != null) { // LL
                return rightRotate(node);
            } else { // LR
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        } else if (balanceFactor < -1) { // 右子树不平衡
            // 能进来就意味着 右子树不平衡 所以右子树一定不为null 不需要担心空指针
            if (node.right.right != null) { // RR
                return leftRotate(node);
            } else { // RL
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }
        return node;
    }

    /**
     * 左旋
     * <p>
     * A                         B
     * \                       / \
     * B         ===>        A   C
     * \
     * C
     *
     * @param node node
     * @return 旋转后的新根节点
     */
    private Node<T> leftRotate(Node<T> node) {
        var newNode = node.right;

        node.right = newNode.left;
        newNode.left = node;

        newNode.height = getHeight(newNode);
        node.height = getHeight(node);

        return newNode;
    }

    /**
     * 右旋
     * <p>
     * A                        B
     * /                        / \
     * B          ===>          C   A
     * /
     * C
     *
     * @param node node
     * @return 旋转后的新根节点
     */
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
     *
     * @param node
     * @return
     */
    private int getHeight(Node<T> node) {
        if (node == null) return -1;

        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    /**
     * 获取指定节点平衡因子
     *
     * @param node node
     * @return node节点的平衡因子
     */
    private int getBalanceFactor(Node<T> node) {
        if (node == null) {
            return 0;
        }
        // 左树高度 - 右树高度 得出平衡因子
        // factor > 1表示左树不平衡
        // factor < -1表示右树不平衡
        return getHeight(node.left) - getHeight(node.right);
    }

    public int size() {
        return size;
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

    static class Tests {
        public static void main(String[] args) {
            test();
        }

        public static void test() {
            final int ITER_COUNT = 5;
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
                // 删除元素 再遍历是否一致
                for (int j = 0; j < samples.size(); j++) {
                    tree.remove(j);
                    treeSet.remove(j);
                    for (Iterator<Integer> treeIt = tree.iterator(),
                         treeSetIt = treeSet.iterator(); treeIt.hasNext() || treeSetIt.hasNext(); ) {
                        if (!treeIt.next().equals(treeSetIt.next()) && tree.size() == treeSet.size()) {
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

}