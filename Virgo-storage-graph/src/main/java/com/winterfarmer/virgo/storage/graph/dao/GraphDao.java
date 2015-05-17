package com.winterfarmer.virgo.storage.graph.dao;

import com.winterfarmer.virgo.storage.graph.Edge;
import com.winterfarmer.virgo.storage.graph.HeadVertex;
import com.winterfarmer.virgo.storage.graph.TailVertex;

import java.util.Collection;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/12.
 * 点集: V
 * 边集: e = <a, b>属于E, 是有 向边. 其中, a, b属于V
 */
public interface GraphDao {
    long DB_WRITE_TIMEOUT_MS = 2000L;
    long DB_QUERY_TIMEOUT_MS = 300L;

    void init();

    /**
     * Create a set of edges
     *
     * @param edges
     * @return
     */
    int insertOrUpdateEdges(Edge... edges);

    int insertOrUpdateEdges(Collection<Edge> edges);

    /**
     * Query normal edge by head point, order by updateAtMs desc.
     *
     * @param head
     * @param limit
     * @param offset
     * @return
     */
    List<Edge> queryEdgesByHead(long head, int limit, int offset);

    /**
     * @param head
     * @param limit
     * @param offset
     * @param updateAtDesc
     * @return
     */
    List<Edge> queryEdgesByHead(long head, int limit, int offset, boolean updateAtDesc);

    /**
     * Query normal edge by tail point, order by updateAtMs desc.
     *
     * @param tail
     * @param limit
     * @param offset
     * @return
     */
    List<Edge> queryEdgesByTail(long tail, int limit, int offset);

    /**
     * @param tail
     * @param limit
     * @param offset
     * @param updateAtDesc
     * @return
     */
    List<Edge> queryEdgesByTail(long tail, int limit, int offset, boolean updateAtDesc);

    /**
     * Query normal edge by head and tail
     *
     * @param head
     * @param tail
     * @return
     */
    Edge queryEdge(long head, long tail);

    /**
     * Query head vertex data by head id
     *
     * @param head
     * @return
     */
    HeadVertex queryHeadVertex(long head);


    /**
     * Query tail vertex data by tail id
     *
     * @param tail
     * @return
     */
    TailVertex queryTailVertex(long tail);
}
