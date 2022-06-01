import unittest
from dataclasses import dataclass
from typing import Optional, Any


@dataclass
class Node:
    prev: Optional["Node"] = None
    val: Any = None
    next: Optional["Node"] = None


class LinkList:
    def __init__(self):
        # dummy node
        self.__head = Node()
        self.__tail = Node()

        self.__head.next = self.__tail
        self.__tail.prev = self.__head

        self.__size = 0

    def add(self, val):
        self.__link_last(val)

    def insert(self, index, val):
        self.__check_position(index)
        mid = self.__size >> 1
        if index <= mid:
            self.__link_node(index, val)
        else:
            self.__link_last(val)

    def remove(self, val) -> bool:
        """删除链表中给定val，返回是否删除成功"""
        if self.__head.next is self.__tail:
            raise IndexError("link list is empty")
        cur = self.__head.next
        while cur is not self.__tail:
            if cur.val == val:
                cur.prev.next = cur.next
                cur.next.prev = cur.prev
                return True
            cur = cur.next
        return False

    def remove_index(self, index):
        self.__check__element_position(index)
        node = self.__node(index)
        node.prev.next = node.next
        node.next.prev = node.prev

    def __check__element_position(self, index):
        if index < 0 or index >= self.__size:
            raise IndexError(index)

    def __check_position(self, index):
        """索引检查包含size"""
        if index < 0 or index > self.__size:
            raise IndexError(index)

    def __node(self, index) -> Node:
        """获取第index索引位置的node
        请在调用该方法时，保证索引有效
        """
        mid = self.__size >> 1
        if index < mid:
            cur = self.__head
            for _ in range(index + 1):
                cur = cur.next
            return cur
        else:
            cur = self.__tail
            for _ in range(self.__size - 1, index - 1, -1):
                cur = cur.prev
            return cur

    def __link_node(self, index, val):
        node = self.__node(index)
        new_node = Node(prev=node.prev, val=val, next=node)
        node.prev.next = new_node
        node.prev = new_node
        self.__size += 1

    def __link_last(self, val):
        tail_prev = self.__tail.prev
        new_node = Node(prev=tail_prev, val=val, next=self.__tail)
        self.__tail.prev = new_node
        tail_prev.next = new_node
        self.__size += 1

    def __iter__(self):
        class NodeIter:
            def __init__(self, node, tail):
                self.node = node
                self.tail = tail

            def __next__(self):
                if self.node is self.tail:
                    raise StopIteration
                res = self.node.val
                self.node = self.node.next
                return res

        return NodeIter(self.__head.next if self.__head.next is not self.__tail else None, self.__tail)

    def __len__(self):
        return self.__size


class TestCase(unittest.TestCase):
    def test_(self):
        l = LinkList()
        l.add(1)
        l.insert(0, 2)
        print(list(l))
        l.insert(0, 10)
        print(list(l))
        l.insert(2, 3)
        print(list(l))
        l.insert(2, 4)
        print(list(l))
        l.insert(5, 99)
        print(list(l))
        self.assertTrue(list(l) == [10, 2, 4, 1, 3, 99])
        l.remove(90)
        self.assertTrue(list(l) == [10, 2, 4, 1, 3, 99])
        l.remove(1)
        print(list(l))
        self.assertTrue(list(l) == [10, 2, 4, 3, 99])
        l.remove_index(2)
        self.assertTrue(list(l) == [10, 2, 3, 99])
        l.remove_index(0)
        self.assertTrue(list(l) == [2, 3, 99])
        l.remove_index(len(l) - 1)
        self.assertTrue(list(l) == [2, 3])
