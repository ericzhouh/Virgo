package com.winterfarmer.virgo.storage.graph;

/**
 * Created by yangtianhang on 15/5/12.
 */
public abstract class Vertex {
    private long vertex;
    private int degree;

    private long createAtMs;
    private long updateAtMs;

    public Vertex() {
    }

    public Vertex(long vertex) {
        this(vertex, 0);
    }

    public Vertex(long vertex, int degree) {
        this(vertex, degree, System.currentTimeMillis());
    }

    public Vertex(long vertex, int degree, long createAtMs) {
        this.vertex = vertex;
        this.degree = degree;
        this.createAtMs = createAtMs;
        this.updateAtMs = createAtMs;
    }

    public long getVertex() {
        return vertex;
    }

    public void setVertex(long vertex) {
        this.vertex = vertex;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public long getCreateAtMs() {
        return createAtMs;
    }

    public void setCreateAtMs(long createAtMs) {
        this.createAtMs = createAtMs;
    }

    public long getUpdateAtMs() {
        return updateAtMs;
    }

    public void setUpdateAtMs(long updateAtMs) {
        this.updateAtMs = updateAtMs;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "vertex=" + vertex +
                ", degree=" + degree +
                ", createAtMs=" + createAtMs +
                ", updateAtMs=" + updateAtMs +
                '}';
    }
}
