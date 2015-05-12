package com.winterfarmer.virgo.storage.graph;

/**
 * Created by yangtianhang on 15/5/12.
 */
public class TailVertex extends Vertex {
    public TailVertex() {
    }

    public TailVertex(long vertex) {
        super(vertex);
    }

    public TailVertex(long vertex, int degree) {
        super(vertex, degree);
    }

    public TailVertex(long vertex, int degree, long createAtMs) {
        super(vertex, degree, createAtMs);
    }
}
