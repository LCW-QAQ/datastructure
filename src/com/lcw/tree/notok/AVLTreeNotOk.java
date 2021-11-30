package com.lcw.tree.notok;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: 2021/11/27 有大问题, 删除出错 
public class AVLTreeNotOk<T extends Comparable<? super T>> implements Iterable<T> {
    private Node<T> root;

    private int size;

    public T get(T t) {
        if (t == null) return null;

        return get(root, t);
    }

    private T get(Node<T> node, T t) {
        if (node == null) return null;

        final int cmp = t.compareTo(node.value);

        if (cmp < 0) {
            return get(node.left, t);
        } else if (cmp > 0) {
            return get(node.right, t);
        }

        return node.value;
    }

    public boolean contains(T t) {
        return contains(root, t);
    }

    private boolean contains(Node<T> node, T t) {
        if (node == null) return false;

        final int cmp = t.compareTo(node.value);

        if (cmp < 0) {
            return contains(node.left, t);
        } else if (cmp > 0) {
            return contains(node.right, t);
        } else {
            return true;
        }
    }

    public T remove(T t) {
        if (t == null) return null;

        Node<T> node = remove(root, t);
        return node != null ? node.value : null;
    }

    private Node<T> remove(Node<T> node, T t) {
        if (node == null) return null;

        final int cmp = t.compareTo(node.value);

        Node<T> resNode = null;

        if (cmp < 0) {
            node.left = remove(node.left, t);
            resNode = node;
        } else if (cmp > 0) {
            node.right = remove(node.right, t);
            resNode = node;
        } else if (node.left != null && node.right != null) {
            node.value = minNode(node.right).value;
            node.right = remove(node.right, node.value);

            resNode = node;
        } else if (node.left == null){
//            node = node.left != null ? node.left : node.right;
            var rNode= node.right;
            node.right = null;
            resNode = rNode;
            size--;
        } else if (node.right == null) {
            var lNode = node.left;
            node.left = null;
            resNode = lNode;
            size --;
        }

        if (resNode == null) return null;

        resNode.height = getHeight(resNode);

        final int balanceFactor = getBalanceFactor(resNode);

        if (balanceFactor > 1 && getBalanceFactor(resNode.left) > 0) {
            return rightRotate(resNode);
            // 右子树不平衡, 且右子树的右子树不平衡
        } else if (balanceFactor < -1 && getBalanceFactor(resNode.right) < 0) {
            return leftRotate(resNode);
            // 左子树不平衡, 且左子树的右子树不平衡
        } else if (balanceFactor > 1 && getBalanceFactor(resNode.left) < 0) {
            node.left = leftRotate(resNode.left);
            return rightRotate(resNode);
            // 右子树不平衡, 且右子树的左子树不平衡
        } else if (balanceFactor < -1 && getBalanceFactor(resNode.right) > 0) {
            node.right = rightRotate(resNode.right);
            return leftRotate(resNode);
        }

        return resNode;
    }

    /**
     * 返回给定数中最小的节点
     *
     * @param node node
     */
    private Node<T> minNode(Node<T> node) {
        if (node != null) {
            while (node.left != null) {
                node = node.left;
            }
        }
        return node;
    }

    public void add(T t) {
        root = add(root, t);
    }

    /**
     * 以给定节点为根节点, 插入T
     *
     * @param node
     * @param t
     * @return
     */
    private Node<T> add(Node<T> node, T t) {
        size++;
        if (node == null) {
            return new Node<>(t);
        }

        final int cmp = t.compareTo(node.value);

        if (cmp < 0) {
            node.left = add(node.left, t);
        } else if (cmp > 0) {
            node.right = add(node.right, t);
        }

        // 设置当前节点的高度
        node.height = getHeight(node);

        return rebalanced(node);
    }

    /**
     * 平衡以node节点为根的树
     *
     * @param node node
     * @return 返回平衡后的根节点
     */
    private Node<T> rebalanced(Node<T> node) {
        final int balanceFactor = getBalanceFactor(node);

        // 左子树不平衡, 且左子树的左子树不平衡
        if (balanceFactor > 1 && getBalanceFactor(node.left) > 0) {
            return rightRotate(node);
            // 右子树不平衡, 且右子树的右子树不平衡
        } else if (balanceFactor < -1 && getBalanceFactor(node.right) < 0) {
            return leftRotate(node);
            // 左子树不平衡, 且左子树的右子树不平衡
        } else if (balanceFactor > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
            // 右子树不平衡, 且右子树的左子树不平衡
        } else if (balanceFactor < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * 将node节点左旋
     *
     * @param node
     * @return
     */
    private Node<T> leftRotate(Node<T> node) {
        var newRoot = node.right;
        var lNode = newRoot.left;
        newRoot.left = node;
        node.right = lNode;

        newRoot.height = getHeight(newRoot);
        node.height = getHeight(node);

        return newRoot;
    }

    /**
     * 将node节点右旋
     *
     * @param node
     * @return
     */
    private Node<T> rightRotate(Node<T> node) {
        var newRoot = node.left;
        var rNode = newRoot.right;
        newRoot.right = node;
        node.left = rNode;

        // 更新高度
        newRoot.height = getHeight(newRoot);
        node.height = getHeight(node);

        return newRoot;
    }

    /**
     * 判断给定节点子树是否为AVL平衡树
     *
     * @param node
     * @return
     */
    private boolean isBalance(Node<T> node) {
        if (node == null) return true;

        // 获取当前节点的平衡因子
        int nodeBalanceFactor = Math.abs(getBalanceFactor(node));

        // 当前节点子树不是平衡
        if (nodeBalanceFactor > 1) return false;

        return isBalance(node.left) && isBalance(node.right);
    }

    /**
     * 获取节点的平衡因子(左右子树高度差)
     */
    public int getBalanceFactor(Node<T> node) {
        if (node == null) return 0;

        return getHeight(node.left) - getHeight(node.right);
    }

    private int getHeight(Node<T> node) {
        if (node == null) return 0;

        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    public void display() {
        display(root);
    }

    public void display(Node<T> node) {
        displayProcess(node, 0, "H", 10);
    }

    public void displayProcess(Node<T> node, int height, String placeholder, int stdSpaceLen) {
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

    @Override
    public Iterator<T> iterator() {
        return new AVLIter<>(root);
    }

    private static final class AVLIter<T> implements Iterator<T> {

        Deque<Node<T>> deque = new ArrayDeque<>();

        public AVLIter(Node<T> node) {
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

            var cur = node.right;

            while (cur != null) {
                deque.push(cur);
                cur = cur.left;
            }

            return node.value;
        }
    }

    private static final class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        public Node(T data) {
            this.value = data;
        }
    }

    public static void main(String[] args) {
        final int ITER_COUNT = 1;
        final int DATA_COUNT = 100;
        for (int i = 0; i < ITER_COUNT; i++) {
            AVLTreeNotOk<Integer> tree = new AVLTreeNotOk<>();
            for (int j = 0; j < DATA_COUNT; j++) {
                tree.add(j);
            }
            long time = System.currentTimeMillis();
            for (int j = DATA_COUNT - 1; j >= 0; j--) {
                tree.contains(j);
            }
            tree.display();
            System.out.println("-------------");
            for (int j = DATA_COUNT >> 1; j < DATA_COUNT; j++) {
                tree.remove(j);
                tree.display();
                System.out.println("-------------");
            }
            System.out.println("AVLTree: " + (System.currentTimeMillis() - time));
            tree.display();
            System.out.println("last -------------");
            tree.forEach(System.out::println);

            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < DATA_COUNT; j++) {
                list.add(j);
            }
            time = System.currentTimeMillis();
            for (int j = DATA_COUNT - 1; j >= 0; j--) {
                list.contains(j);
            }
            for (int j = DATA_COUNT >> 1; j < DATA_COUNT; j++) {
                list.remove(Integer.valueOf(j));
            }
            System.out.println("ArrayList: " + (System.currentTimeMillis() - time));
            list.forEach(System.out::println);
        }
    }
}
