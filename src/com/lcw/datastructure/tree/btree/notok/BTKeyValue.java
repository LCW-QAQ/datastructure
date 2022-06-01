package com.lcw.datastructure.tree.btree.notok;

//键值对 就是关键字
public class BTKeyValue<K extends Comparable<K>, V> {

    protected K mKey;
    protected V mValue;

    //构造函数
    public BTKeyValue(K mKey, V mValue) {
        super();
        this.mKey = mKey;
        this.mValue = mValue;
    }
}