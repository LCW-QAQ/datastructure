package com.lcw

import com.lcw.tree.RBTree

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val tree = RBTree<Int, String>()
        for (i in 0..1000) {
            tree.put(i, i.toString())
        }
        while (tree.size() > 0) {
            tree.remove(tree.size() - 1)
        }
        println("may be success")
    }
}