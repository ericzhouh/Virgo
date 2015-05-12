package com.winterfarmer.virgo.storage.graph;

/**
 * Created by yangtianhang on 15/5/12.
 */
public class HeadVertex extends Vertex {
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
}
