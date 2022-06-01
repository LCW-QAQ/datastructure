import random
import timeit
from dataclasses import dataclass
from typing import Any, Optional
from sortedcontainers import SortedList

"""
AVL绝对平衡树实现
"""


@dataclass
class Node:
    key: Any = None
    val: Any = None
    parent: Optional["None"] = None
    left: Optional["Node"] = None
    right: Optional["Node"] = None
    height: int = 0


class AVLTree:
    def __init__(self):
        self._root = None
        self.size = 0

    def add(self, key: Any, val: Any) -> Any:
        if key is None:
            raise KeyError("key cant not be None!")
        """向树avl树中添加k-v键值对, 如果已经存在则替换值, 同时返回旧值"""
        return self._add(key, val)

    def remove(self, key: Any) -> Any:
        """在树中删除给定key, 返回删除节点的val"""
        if key is None:
            raise KeyError("key cant not be None!")
        node = self._get_node(key)
        if not node:
            return node
        old_val = node.val
        self._remove_node(node)
        return old_val

    def get(self, key: Any) -> Any:
        if key is None:
            raise KeyError("key cant not be None!")
        node = self._get_node(key)
        return node.val if node else None

    def _remove_node(self, node: Node):
        """在树中删除给定节点"""
        self.size -= 1
        # 左右两个子节点都不为空
        if node.left and node.right:
            # 右边有值, 需要找到右子节点的后继节点, 其实就右边的最小值
            successor_node = self._successor(node.right)
            node.key = successor_node.key
            node.val = successor_node.val
            self._remove_node(successor_node)
        else:
            # 父节点不为空, 直接替换父节点的左右子节点引用即可
            if node.parent:
                if node.parent.left is node:
                    node.parent.left = node.left if node.left else node.right
                elif node.parent.right is node:
                    node.parent.right = node.left if node.left else node.right
            else:
                # 父节点为空, 说明当前要删除的节点就是根节点, 将根节点设置为左右两个子节点不为空的那个
                if node.left:
                    self._root = node.left
                    node.left.parent = None
                else:
                    self._root = node.right
                    node.right.parent = None

        self._rebalanced(node)

    @staticmethod
    def _successor(node: Node) -> Node:
        while node.left:
            node = node.left
        return node

    def _get_node(self, key: Any) -> Optional[Node]:
        node = self._root
        while node:
            if node.key < key:
                node = node.right
            elif node.key > key:
                node = node.left
            else:
                return node
        return node

    def _add(self, key: Any, val: Any) -> Any:
        node = self._root
        if not node:
            self._add_node_to_empty_tree(key, val)
            return None
        parent = None
        while node:
            parent = node
            if node.key < key:
                node = node.right
            elif node.key > key:
                node = node.left
            else:
                # 树中已经有值了, 替换val, 返回旧值
                old_value = node.val
                node.val = val
                return old_value
        # 树中没有值需要插入新节点
        self._add_node(key, val, parent, parent.val > val)
        return None

    def _add_node(self, key: Any, val: Any, parent: Optional[Node], add_to_left: bool):
        """将给定k-v插入到parent节点左边或右边"""
        new_node = Node(key, val, parent)
        if add_to_left:
            parent.left = new_node
        else:
            parent.right = new_node
        new_node.height = self._height(new_node)
        self._rebalanced(new_node)
        self.size += 1

    def _add_node_to_empty_tree(self, key: Any, val: Any):
        """将给定key-val插入到空树中"""
        self._root = Node(key, val)
        self.size = 1
        self._root.height = self._height(self._root)

    def _rebalanced(self, node: Optional[Node]) -> Optional[Node]:
        """平衡以node为头节点的子树"""
        if not node:
            return node
        factor = self._balanced_factor(node)
        if factor > 1:
            # 左子树不平衡
            if not node.left.left:
                # LR
                node.left = self._left_rotate(node.left)
            # LL
            return self._right_rotate(node)
        elif factor < -1:
            # 右子树不平衡
            if not node.right.right:
                # RL
                node.right = self._right_rotate(node.right)
            return self._left_rotate(node)
        else:
            # 当前节点是平衡的, 无需操作
            return node

    def _left_rotate(self, node: Node) -> Node:
        """左旋
        右边多出了节点, 我们需要填补左边
        有一点麻烦, 需要维护父节点
        """
        new_root = node.right
        node.right = new_root.left
        if new_root.left:
            new_root.left.parent = node
        new_root.left = node
        # 更新new_root与node的父节点
        new_root.parent = node.parent
        # node父节点为None, 表示node是根节点, 同时需要更新根节点
        if not node.parent:
            self._root = new_root
        elif node.parent.left is node:
            node.parent.left = new_root
        elif node.parent.right is node:
            node.parent.right = new_root
        node.parent = new_root

        new_root.height = self._height(new_root)
        node.height = self._height(node)
        return new_root

    def _right_rotate(self, node: Node) -> Node:
        """左旋
        左边多出了节点, 我们需要填补右边
        有一点麻烦, 需要维护父节点
        """
        new_root = node.left
        node.left = new_root.right
        if new_root.right:
            new_root.right.parent = node
        new_root.right = node

        new_root.parent = node.parent
        if not node.parent:
            self._root = new_root
        elif node.parent.left is node:
            node.parent.left = new_root
        elif node.parent.right is node:
            node.parent.right = new_root
        node.parent = new_root

        new_root.height = self._height(new_root)
        node.height = self._height(node)
        return new_root

    def _height(self, node: Node) -> int:
        """获取给定节点的高度
        只有一个节点时高度为1, 节点为None高度为0
        """
        if not node:
            return 0
        return max(self._height(node.left), self._height(node.right))

    @staticmethod
    def _balanced_factor(node: Node) -> int:
        """计算给定节点的负载因子"""
        lh, rh = 0, 0
        if node.left:
            lh = node.left.height
        if node.right:
            rh = node.right.height
        return lh - rh

    def traverse(self):
        def process(node, keys, values):
            if not node:
                return
            process(node.left, keys, values)
            keys.append(node.key)
            values.append(node.val)
            process(node.right, keys, values)

        keys, values = [], []
        process(self._root, keys, values)
        return keys, values

    # TODO __contains__ __getitem__ __setitem__ __iter__ __next__


if __name__ == '__main__':
    ITER_COUNT = 20
    ELEMENT_COUNT = 5
    RM_ELEMENT_COUNT = 3
    rand_seq = [x for x in range(ELEMENT_COUNT * 145)]


    def test():
        sl = SortedList()
        tree = AVLTree()
        elements = random.sample(rand_seq, ELEMENT_COUNT)
        for item in elements:
            tree.add(item, item)
            sl.add(item)
        res1_keys, res1_vals = tree.traverse()
        assert len(res1_vals) == len(set(res1_vals)) == len(sl) == ELEMENT_COUNT
        assert sl == res1_vals

        # k, v = res1_keys[8], res1_vals[8]
        # vv = tree.get(k)
        # assert tree.add(k, -100) == v == vv

        rm_elements = random.sample(res1_keys, RM_ELEMENT_COUNT)
        print(rm_elements)
        print(res1_keys, res1_vals)
        print(sl)
        for item in rm_elements:
            tree.remove(item)
            sl.remove(item)
        print("node is not None:", bool(tree._root))
        res2_keys, res2_vals = tree.traverse()
        print(res2_keys, res2_vals)
        print(sl)
        assert len(res2_keys) == len(set(res2_keys)) == len(sl) == ELEMENT_COUNT - RM_ELEMENT_COUNT
        assert res2_keys == sl
        print()


    def test2():
        rm_elements = [659]
        elements = [258, 289, 427, 429, 659]
        tree = AVLTree()
        sl = SortedList()
        for item in elements:
            sl.add(item)
            tree.add(item, item)
        print(rm_elements)
        print(sl)
        print(tree.traverse())
        for item in rm_elements:
            tree.remove(item)
            sl.remove(item)
        print(sl)
        print(tree.traverse())
        print()


    print(timeit.timeit(stmt=test, number=ITER_COUNT))
