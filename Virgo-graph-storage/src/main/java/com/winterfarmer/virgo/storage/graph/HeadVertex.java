package com.winterfarmer.virgo.storage.graph;

/**
 * Created by yangtianhang on 15/5/12.
 */
public class HeadVertex extends Vertex {
    public static HeadVertex notExistVertex(long vertex) {
        return new HeadVertex(vertex, 0, -1);
    }

    public HeadVertex() {
    }

    public HeadVertex(long vertex) {
        super(vertex);
    }

    public HeadVertex(long vertex, int degree) {
        super(vertex, degree);
    }

    public HeadVertex(long vertex, int degree, long createAtMs) {
        super(vertex, degree, createAtMs);
    }

    @Override
    public String toString() {
        return "Head" + super.toString();
    }
}
