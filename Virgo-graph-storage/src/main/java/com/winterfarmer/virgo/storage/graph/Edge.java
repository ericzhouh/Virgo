package com.winterfarmer.virgo.storage.graph;

import com.google.common.base.Charsets;

/**
 * Created by yangtianhang on 15/5/12.
 */
public class Edge {
    public static final int DELETED_EDGE = 0;
    public static final int NORMAL_EDGE = 1;

    private long head; // 起点
    private long tail; // 终点
    private int state; // 状态, 分为正常(1)和删除(0)两种

    private int position; // 人工排序
    private int category; // 分类
    private long updateAtMs; // 更新时间

    private long accessoryId; // 附带信息id
    private byte[] extInfo; // 扩展信息, 最好是json的格式

    public Edge() {
    }

    public Edge(long head, long tail) {
        this(head, tail, 1);
    }

    public Edge(long head, long tail, int state) {
        this(head, tail, state, 0);
    }

    public Edge(long head, long tail, int state, int position) {
        this(head, tail, state, position, 0, System.currentTimeMillis(), 0, null);
    }

    public Edge(long head, long tail, int state, int position, int category) {
        this(head, tail, state, position, category, System.currentTimeMillis(), 0, null);
    }

    public Edge(long head, long tail, int state, int position, int category, long updateAtMs) {
        this(head, tail, state, position, category, updateAtMs, 0, null);
    }

    public Edge(long head, long tail, int state, int position, int category, long updateAtMs,
                long accessoryId) {
        this(head, tail, state, position, category, updateAtMs, accessoryId, null);
    }

    public Edge(long head, long tail, int state, int position, int category, long updateAtMs,
                long accessoryId, String extInfo) {
        this.head = head;
        this.tail = tail;
        this.state = state;
        this.position = position;
        this.category = category;
        this.updateAtMs = updateAtMs;
        this.accessoryId = accessoryId;
        this.extInfo = extInfo == null ? null : extInfo.getBytes(Charsets.UTF_8);
    }

    public long getHead() {
        return head;
    }

    public void setHead(long head) {
        this.head = head;
    }

    public long getTail() {
        return tail;
    }

    public void setTail(long tail) {
        this.tail = tail;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public long getUpdateAtMs() {
        return updateAtMs;
    }

    public void setUpdateAtMs(long updateAtMs) {
        this.updateAtMs = updateAtMs;
    }

    public long getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(long accessoryId) {
        this.accessoryId = accessoryId;
    }

    public byte[] getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(byte[] extInfo) {
        this.extInfo = extInfo;
    }

    public String getExtInfoStr() {
        return this.extInfo == null ? null : new String(this.extInfo, Charsets.UTF_8);
    }

    public void setExtInfoStr(String extInfoStr) {
        this.extInfo = extInfoStr == null ? null : extInfoStr.getBytes(Charsets.UTF_8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (head != edge.head) return false;
        return tail == edge.tail;

    }

    @Override
    public int hashCode() {
        int result = (int) (head ^ (head >>> 32));
        result = 31 * result + (int) (tail ^ (tail >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Edge{"
                + "head:" + this.head + ","
                + "tail:" + this.tail + ","
                + "state:" + this.state + ","
                + "position:" + this.position + ","
                + "category:" + this.category + ","
                + "updateAtMs:" + this.updateAtMs + ","
                + "accessoryId:" + this.accessoryId + ","
                + "extInfo:" + this.getExtInfoStr() + "}";
    }
}
