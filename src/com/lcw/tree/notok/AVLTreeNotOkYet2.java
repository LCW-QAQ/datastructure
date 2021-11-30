package com.lcw.tree.notok;

public class AVLTreeNotOkYet2<T extends Comparable<T>> {

    private Node<T> root;

    private int size;

    public void insert(T t) {
        root = insert(root, t);
    }

    private Node<T> insert(Node<T> node, T t) {
        if (node == null) return new Node<>(t);

        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, t);

            // 检测是否需要旋转
            // t插入在左子树上只有左子树的平衡才会被打破
            if (height(node.left) - height(node.right) == 2) {
                if (t.compareTo(node.left.value) < 0) { // LL
                    node = rightRotate(node);
                } else { // LR
                    node = leftRightRotate(node);
                }
            }
        } else if (cmp > 0) {
            node.right = insert(node.right, t);

            // 节点插入在右子树上, 只有右子树的平衡才会被打破
            if (height(node.right) - height(node.left) == 2) {
                if (t.compareTo(node.right.value) > 0) { // RR
                    node = leftRotate(node);
                } else { // RL
                    node = rightLeftRotate(node);
                }
            }
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        return node;
    }

    public void remove(T t) {
        remove(root, t);
    }

    private Node<T> remove(Node<T> node, T t) {
        if (t == null) return null;

        int cmp = t.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, t);

            // node节点右边为null一定平衡, 这个数本身是AVL树, 右边若为null左子树最多高度差为1, 因此无论怎么删除不会影响平衡
            if (node.right != null) {
                if (node.left == null) { // 左子树为null, 看右子树, RR或RL
                    if (height(node.right) - node.height == 2) {
                        var tmp = node.right;
                        if (tmp.right != null) { // RR
                            node = leftRotate(node);
                        } else { // RL
                            node = rightLeftRotate(node);
                        }
                    }
                } else { // 左子树不为null
                    var tmp = node.left;

                    if (tmp.right != null) {
                        if (height(tmp.left) - height(tmp.right) == 2) {
                        }
                    } else {

                    }
                }
            }
        } else if (cmp > 0) {
            node.right = remove(node.right, t);
        } else if (node.left != null && node.right != null) {

        } else {

        }

        return node;
    }

    private Node<T> rightRotate(Node<T> node) {
        var newNode = node.left;

        node.left = newNode.right;
        newNode.right = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        newNode.height = Math.max(height(newNode.left), height(newNode.right)) + 1;

        return newNode;
    }

    private Node<T> leftRotate(Node<T> node) {
        var newNode = node.right;

        node.right = newNode.left;
        newNode.left = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        newNode.height = Math.max(height(newNode.left), height(newNode.right)) + 1;

        return newNode;
    }

    private Node<T> leftRightRotate(Node<T> node) {
        node.left = leftRotate(node.left);
        return rightRotate(node);
    }

    private Node<T> rightLeftRotate(Node<T> node) {
        node.right = rightRotate(node.right);
        return leftRotate(node);
    }

    private int height(Node<T> node) {
        return node == null ? -1 : node.height;
    }

    private static final class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        public Node(T value) {
            this(value, null, null, 0);
        }

        public Node(T value, Node<T> left, Node<T> right, int height) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.height = height;
        }
    }
}
