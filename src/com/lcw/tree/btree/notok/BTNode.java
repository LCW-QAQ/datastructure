package com.lcw.tree.btree.notok;
//Comparable�˽ӿ�ǿ�ж�ʵ������ÿ����Ķ��������������
public class BTNode<K extends Comparable<K>, V> {

    // ����B������С����
    public final static int MIN_DEGREE = 3;
    // �����ڵ��⣬ÿ��������ܼ���������
    public final static int LOWER_BOUND_KEYNUM = MIN_DEGREE - 1;
    // �������ڵ��⣬ÿ��������ܼ���������
    public final static int UPPER_BOUND_KEYNUM = (MIN_DEGREE * 2) - 1;

    protected boolean mIsLeaf;// ��Ǵ˽ڵ��Ƿ�ΪҶ�ӽ��
    protected int mCurrentKeyNum;// �˽ڵ�Ĺؼ�������������
    protected BTKeyValue<K, V>[] mKeys;// ���ڴ��ֵ�Ե����� ���ǹؼ���
    protected BTNode<K, V>[] mChildren;// ���ڴ��ӽ�������

    /**
     * ���캯��
     */
    @SuppressWarnings("unchecked")//���߱��������Ծ���ȡ����ʾ�ľ��漯
    public BTNode() {
        mIsLeaf = true;
        mCurrentKeyNum = 0;
        mKeys = new BTKeyValue[UPPER_BOUND_KEYNUM];
        mChildren = new BTNode[UPPER_BOUND_KEYNUM + 1];
    }

    protected static BTNode<?, ?> getChildNodeAtIndex(BTNode<?, ?> btNode, int keyIdx, int nDirection) {
        if (btNode.mIsLeaf) {
            return null;
        }
        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > btNode.mCurrentKeyNum)) {
            throw new IllegalArgumentException();
        }

        return btNode.mChildren[keyIdx];
    }

    /**
     * ����btNode�ڵ���λ��keyIdxλ�ϵļ���ߵ��ӽ��
     * @param btNode
     * @param keyIdx
     * @return
     */
    protected static BTNode<?, ?> getLeftChildAtIndex(BTNode<?, ?> btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 0);
    }

    /**
     * ����btNode�ڵ���λ��keyIdxλ�ϵļ��ұߵ��ӽ��
     * @param btNode
     * @param keyIdx
     * @return
     */
    protected static BTNode<?, ?> getRightChildAtIndex(BTNode<?, ?> btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 1);
    }

    /**
     * @param parentNode
     * @param keyIdx
     * @return ���ظ�����keyIdxλ�ϵ��ӽ������ֵܽ��
     */
    protected static BTNode<?, ?> getLeftSiblingAtIndex(BTNode<?, ?> parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, -1);
    }

    /**
     *
     * @param parentNode
     * @param keyIdx
     * @return	���ظ�����keyIdxλ�ϵ��ӽ������ֵܽ��
     */
    protected static BTNode<?, ?> getRightSiblingAtIndex(BTNode<?, ?> parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, 1);
    }


    /**
     * �жϸ�����keyIdxλ�ϵ��ӽ���Ƿ�������ֵܽ��
     * @param parentNode
     * @param keyIdx
     * @return
     */
    protected static boolean hasLeftSiblingAtIndex(BTNode<?, ?> parentNode, int keyIdx) {
        if (keyIdx - 1 < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * �жϸ�����keyIdxλ�ϵ��ӽ���Ƿ�������ֵܽ��
     * @param parentNode
     * @param keyIdx
     * @return
     */
    protected static boolean hasRightSiblingAtIndex(BTNode<?, ?> parentNode, int keyIdx) {
        if (keyIdx + 1 > parentNode.mCurrentKeyNum) {
            return false;
        } else {
            return true;
        }
    }
}
