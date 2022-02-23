from dataclasses import dataclass
from typing import Any, Optional


@dataclass
class Node:
    val: Any = None
    left: Optional["Node"] = None
    right: Optional["Node"] = None


class BinarySearchTree:
    def __init__(self):
        self.root = None

    def add(self, val):
        self.root = self._add_by_iteration(self.root, Node(val))

    def remove(self, val):
        self.root = self._remove_by_recursion(self.root, val)

    def _add_by_recursion(self, node: Optional[Node], x: Node) -> Optional[Node]:
        if not node:
            return x
        if node.val < x.val:
            node.right = self._add_by_recursion(node.right, x)
        elif node.val > x.val:
            node.left = self._add_by_recursion(node.left, x)
        else:
            node.val = x.val
        return node

    def _remove_by_recursion(self, node: Optional[Node], val) -> Optional[Node]:
        if not node:
            return node
        if node.val < val:
            node.right = self._remove_by_recursion(node.right, val)
        elif node.val > val:
            node.left = self._remove_by_recursion(node.left, val)
        # 找到要删除的值了, 不能直接删除叶子节点
        elif node.left and node.right:
            # 找到右边的最小值, 将它移动到当前节点
            min_node = self._find_min(node.right)
            if min_node:
                node.val = min_node.val
                node.right = self._remove_by_recursion(node.right, node.val)
        else:
            node = node.left if node.left else node.right
        return node

    def _find_min(self, node: Optional[Node]) -> Optional[Node]:
        if node:
            while node.left:
                node = node.left
        return node

    def _search_by_recursion(self, node: Optional[Node], val) -> Optional[Node]:
        if not node:
            return node
        if node.val < val:
            return self._search_by_recursion(node.right, val)
        elif node.val > val:
            return self._search_by_recursion(node.left, val)
        return node

    def _traverse(self):
        def process(node):
            if not node:
                return
            process(node.left)
            print(node.val, end=" ")
            process(node.right)

        process(self.root)
        print()

    def __contains__(self, item):
        return self._search_by_recursion(self.root, item)


if __name__ == '__main__':
    bst = BinarySearchTree()
    bst.add(4)
    bst.add(2)
    bst.add(5)
    bst.add(3)
    bst.add(6)
    bst.remove(4)
    bst.remove(6)
    bst._traverse()
    print(10 in bst)
    print(5 in bst)
    print(2 in bst)
