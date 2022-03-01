import random
from dataclasses import dataclass
from typing import Optional, Any

"""
python 实现二叉搜索树
"""


@dataclass
class Node:
    val: Any = None
    left: Optional["Node"] = None
    right: Optional["Node"] = None


class BinarySearchTree:
    def __init__(self):
        self.root = None

    def add(self, val: Any):
        # self.root = self._add_by_recursion(self.root, val)
        self._add_by_iteration(self.root, val)

    def remove(self, val: Any):
        self.root = self._remove_by_recursion(self.root, val)
        # self._remove_by_iteration(self.root, val)

    def _add_by_iteration(self, node: Optional[Node], val: Any):
        if not node:
            self.root = Node(val)
            return
        parent, cur = None, node
        while cur:
            parent = cur
            if cur.val < val:
                cur = cur.right
            elif cur.val > val:
                cur = cur.left
            else:
                cur.val = val
                return
        new_node = Node(val)
        # 上面处理了==, 这里只可能 < or >
        if parent.val < val:
            parent.right = new_node
        else:
            parent.left = new_node

    def _add_by_recursion(self, node: Optional[Node], val: Any) -> Node:
        """递归方式插入元素"""
        if not node:
            return Node(val)
        if node.val < val:
            node.right = self._add_by_recursion(node.right, val)
        elif node.val > val:
            node.left = self._add_by_recursion(node.left, val)
        else:
            node.val = val
        return node

    def _remove_by_iteration(self, node: Optional[Node], val: Any):
        """有问题的实现, 想方便的迭代删除, 要依赖与parent, 需要在Node节点中加入parent引用"""
        if not node:
            return
        parent, cur = None, node
        while cur:
            parent = cur
            if cur.val < val:
                cur = cur.right
            elif cur.val > val:
                cur = cur.left
            else:  # 找到要删除的值了
                if cur.left and cur.right:
                    min_val = self._find_min(node.right).val
                    cur.val = min_val
                    self._remove_by_iteration(node.right, min_val)
                else:
                    if cur is self.root:
                        self.root = cur.left if cur.left else cur.right
                    else:
                        if cur is parent.left:
                            parent.left = cur.left if cur.left else cur.right
                        else:
                            parent.right = cur.left if cur.left else cur.right
                return

    def _remove_by_recursion(self, node: Optional[Node], val: Any) -> Optional[Node]:
        if not node:
            return node
        if node.val < val:
            node.right = self._remove_by_recursion(node.right, val)
        elif node.val > val:
            node.left = self._remove_by_recursion(node.left, val)
        else:  # 找到要删除的元素了
            # 左右都不为None
            if node.left and node.right:
                # 找到右边的最小值
                min_val = self._find_min(node.right).val
                node.val = min_val
                node.right = self._remove_by_recursion(node.right, min_val)
            else:
                node = node.left if node.left else node.right
        return node

    @staticmethod
    def _find_min(node) -> Node:
        assert node is not None
        """找到指定节点下的最小值"""
        while node.left:
            node = node.left
        return node

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


if __name__ == '__main__':
    ADD_COUNT = 5
    RM_COUNT = 1
    ITER_COUNT = 1
    for _ in range(ITER_COUNT):
        rand_seq = [x for x in range(100)]
        bst = BinarySearchTree()
        sample_1 = random.sample(rand_seq, ADD_COUNT)
        for item in sample_1:
            bst.add(item)
        res = bst.traverse()
        print(res)
        sample_2 = random.sample(res, RM_COUNT)
        for item in sample_2:
            bst.remove(item)
        res2 = bst.traverse()
        print(res2)
        print(len(res), len(res2))
        assert len(res2) == len(res) - RM_COUNT
