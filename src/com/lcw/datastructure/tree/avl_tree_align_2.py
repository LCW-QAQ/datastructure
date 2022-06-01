import random
import timeit
from dataclasses import dataclass
from typing import Optional, Any

from sortedcontainers import SortedList


@dataclass
class Node:
    key: Any = None
    val: Any = None
    left: Optional["Node"] = None
    right: Optional["Node"] = None
    height: int = 0


class AVLTree:
    def __init__(self):
        self._root = None
        self.size = 0

    def add(self, key: Any, val: Any):
        self._root = self._add(self._root, key, val)

    def remove(self, key: Any):
        self._root = self._remove(self._root, key)

    def _remove(self, node: Optional[Node], key) -> Optional[Node]:
        if node is None:
            return node
        if node.key < key:
            node.right = self._remove(node.right, key)
        elif node.key > key:
            node.left = self._remove(node.left, key)
        else:
            # 找到要删除的了
            # 左右两边都有值
            if node.left is not None and node.right is not None:
                min_node = self._find_min(node.right)
                node.key = min_node.key
                node.val = min_node.val
                node.right = self._remove(node.right, min_node.key)
            else:
                node = node.left if node.left is not None else node.right
        return self._rebalanced(node)

    def _add(self, node: Optional[Node], key, val) -> Node:
        """将k-v键值对插入到以node节点为的子树中"""
        if node is None:
            self.size += 1
            return Node(key, val)
        if node.key < key:
            node.right = self._add(node.right, key, val)
        elif node.key > key:
            node.left = self._add(node.left, key, val)
        else:
            # 找到了数值替换
            node.val = val
        node.height = self._height(node)
        return self._rebalanced(node)

    def _find_min(self, node: Node) -> Node:
        while node.left is not None:
            node = node.left
        return node

    def _height(self, node: Node):
        if node is None:
            return 0
        return max(self._height(node.left), self._height(node.right))

    def _rebalanced(self, node: Optional[Node]) -> Node:
        if node is not None:
            factor = self._balanced_factory(node)
            if factor > 1:
                # 左边不平衡
                if node.left.left is None:
                    # LR
                    node.left = self._left_rotate(node.left)
                # LL
                return self._right_rotate(node)
            elif factor < -1:
                # 右边不平衡
                if node.right.right is None:
                    # RL
                    node.right = self._right_rotate(node.right)
                # RR
                return self._left_rotate(node)
            else:
                return node

    def _balanced_factory(self, node: Node) -> int:
        """返回给定节点负载因子"""
        lh, rh = 0, 0
        if node.left is not None:
            lh = node.left.height
        if node.right is not None:
            rh = node.right.height
        return lh - rh

    def _left_rotate(self, node: Node) -> Node:
        r = node.right
        node.right = r.left
        r.left = node

        r.height = self._height(r)
        node.height = self._height(node)
        return r

    def _right_rotate(self, node: Node) -> Node:
        l = node.left
        node.left = l.right
        l.right = node

        l.height = self._height(l)
        node.height = self._height(node)
        return l

    def traverse(self):
        def process(node, res):
            if not node:
                return
            process(node.left, res)
            res.append(node.val)
            process(node.right, res)

        res = []
        process(self._root, res)
        return res


if __name__ == '__main__':
    ITER_COUNT = 20
    ELEMENT_COUNT = 1000
    RM_ELEMENT_COUNT = 300
    rand_seq = [x for x in range(ELEMENT_COUNT * 145)]


    def test():
        sl = SortedList()
        tree = AVLTree()
        elements = random.sample(rand_seq, ELEMENT_COUNT)
        for item in elements:
            tree.add(item, item)
            sl.add(item)
        res1 = tree.traverse()
        assert len(res1) == len(set(res1)) == len(sl) == ELEMENT_COUNT
        assert sl == res1
        rm_elements = random.sample(res1, RM_ELEMENT_COUNT)
        for item in rm_elements:
            tree.remove(item)
            sl.remove(item)
        res2 = tree.traverse()
        assert len(res2) == len(set(res2)) == len(sl) == ELEMENT_COUNT - RM_ELEMENT_COUNT
        assert res2 == sl


    print(timeit.timeit(stmt=test, number=ITER_COUNT))
