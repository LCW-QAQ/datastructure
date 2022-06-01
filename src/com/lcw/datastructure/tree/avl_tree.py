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
    left: Optional["Node"] = None
    right: Optional["Node"] = None
    height: int = 0


class AVLTree:
    def __init__(self):
        self.root = None
        self.size = 0

    def add(self, key: Any, val: Any):
        self.root = self._add(self.root, key, val)

    def remove(self, key: Any):
        self.root = self._remove(self.root, key)

    def traverse(self):
        def process(node, res):
            if not node:
                return
            process(node.left, res)
            res.append(node.val)
            process(node.right, res)

        res = []
        process(self.root, res)
        return res

    def _add(self, node: Optional[Node], key: Any, val: Any) -> Node:
        if not node:
            self.size += 1
            return Node(key, val)
        if node.key < key:
            node.right = self._add(node.right, key, val)
        elif node.key > key:
            node.left = self._add(node.left, key, val)
        else:
            node.val = val
            # return node 这里不返回, 还没有做平衡处理
        node.height = self._height(node)
        return self._rebalanced(node)

    def _remove(self, node: Optional[Node], key: Any) -> Optional[Node]:
        if not node:
            return node
        if node.key < key:
            node.right = self._remove(node.right, key)
        elif node.key > key:
            node.left = self._remove(node.left, key)
        else:
            # 右边不为None, 右边一定有最小值, 找到最小值替换当前节点
            if node.right:
                min_node = self._find_min(node.right)
                node.key = min_node.key
                node.val = min_node.val
                node.right = self._remove(node.right, min_node.key)
            else:  # 右边为None就只需要关心左边, 简单替换左边即可
                node = node.left
            self.size -= 1
        return self._rebalanced(node)

    @staticmethod
    def _find_min(node: Node) -> Node:
        assert node is not None
        while node.left:
            node = node.left
        return node

    def _height(self, node: Node) -> int:
        # 节点为None, 高度为0
        if not node:
            return 0
        return max(self._height(node.left), self._height(node.right))

    def _rebalanced(self, node: Node) -> Node:
        if not node:
            return node
        """平衡以node为头节点的子树"""
        factor = self._get_balanced_factor(node)
        if factor > 1:
            # 左子树不平衡
            if not node.left.left:  # LR
                node.left = self._left_rotate(node.left)
            return self._right_rotate(node)  # LL
        elif factor < -1:
            # 右子树不平衡
            if not node.right.right:  # RL
                node.right = self._right_rotate(node.right)
            return self._left_rotate(node)
        else:
            # 当前节点是平衡的, 无需操作
            return node

    def _get_balanced_factor(self, node: Node) -> int:
        """获取平衡因子
        返回左子树高度 - 右子树高度
        factor > 1 表示左子树不平衡
        factor < 1 表示右子树不平衡
        """
        lh, rh = 0, 0
        if node.left:
            lh = node.left.height
        if node.right:
            rh = node.right.height
        return lh - rh

    def _left_rotate(self, node: Node) -> Node:
        """左旋
        右边多出了节点, 我们需要填补左边
        """
        new_root = node.right
        node.right = new_root.left
        new_root.left = node

        new_root.height = self._height(new_root)
        node.height = self._height(node)
        return new_root

    def _right_rotate(self, node: Node) -> Node:
        """左旋
        左边多出了节点, 我们需要填补右边
        """
        new_root = node.left
        node.left = new_root.right
        new_root.right = node

        new_root.height = self._height(new_root)
        node.height = self._height(node)
        return new_root


if __name__ == '__main__':
    ITER_COUNT = 10
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
