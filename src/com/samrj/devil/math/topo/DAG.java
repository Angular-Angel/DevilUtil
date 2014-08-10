package com.samrj.devil.math.topo;

import java.util.*;

/**
 * Hash-map based directed acyclic graph. To be used mainly for topological
 * sorting.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2014 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 * @param <TYPE> The kind of data to store in the node.
 */
public class DAG<TYPE>
{
    private final Map<TYPE, Vertex> vertices = new LinkedHashMap<>();
    
    /**
     * Hash-map based directed graph vertex class.
     */
    private class Vertex
    {
        private final TYPE data;
        /**
         * Set of vertices that have an outgoing edge whose end is this.
         */
        private final Set<Vertex> in = new LinkedHashSet<>();
        /**
         * Set of vertices that have an incoming edge whose start is this.
         */
        private final Set<Vertex> out = new LinkedHashSet<>();
        
        private Vertex(TYPE data)
        {
            this.data = data;
        }
        
        @Override
        public int hashCode()
        {
            return data.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            
            return data.equals(((Vertex)obj).data);
        }
    }
    
    /**
     * Publicly exported edge.
     */
    public static class Edge<TYPE>
    {
        public final TYPE start, end;
        
        private Edge(TYPE s, TYPE e)
        {
            start = s; end = e;
        }
    }
    
    /**
     * Adds vertex to DAG as an orphan. Does nothing if DAG already contains
     * vertex.
     * 
     * @return true if the vertex was added
     */
    public boolean add(TYPE vertex)
    {
        if (vertex == null) throw new NullPointerException();
        if (vertices.containsKey(vertex)) return false;
        
        Vertex n = new Vertex(vertex);
        
        vertices.put(vertex, n);
        return false;
    }
    
    public boolean remove(TYPE vertex)
    {
        if (vertex == null) throw new NullPointerException();
        
        Vertex v = vertices.remove(vertex);
        
        if (v == null) return false;
        
        for (Vertex cn : v.out) cn.in.remove(v);
        for (Vertex pn : v.in) pn.out.remove(v);
        
        return true;
    }
    
    private Vertex addGet(TYPE node)
    {
        Vertex out = vertices.get(node);
        if (out == null)
        {
            out = new Vertex(node);
            vertices.put(node, out);
        }
        return out;
    }
    
    /**
     * Adds an edge to the DAG.
     * 
     * @return true if the edge was added
     * @throws CyclicGraphException if adding this edge would make the
     *         graph cyclic
     */
    public boolean addEdge(TYPE start, TYPE end)
    {
        if (start == null || end == null) throw new NullPointerException();
        
        Vertex pv = addGet(start);
        Vertex cv = addGet(end);
        
        if (pv.out.contains(cv)) return false;
        
        pv.out.add(cv);
        cv.in.add(pv);
        
        if (isCyclic())
        {
            pv.out.remove(cv);
            cv.in.remove(pv);
            throw new CyclicGraphException();
        }
        
        return true;
    }
    
    public boolean removeEdge(TYPE parent, TYPE child)
    {
        if (parent == null || child == null) throw new NullPointerException();
        
        Vertex pv = vertices.get(parent);
        if (pv == null) return false;
        
        Vertex cv = vertices.get(child);
        if (cv == null) return false;
        
        if (!pv.out.contains(cv)) return false;
        
        pv.out.remove(cv);
        cv.in.remove(pv);
        
        return true;
    }
    
    /**
     * Returns a set containing all sinks: vertices that have no outgoing edges.
     */
    public Set<TYPE> getSinks()
    {
        Set<TYPE> out = new LinkedHashSet<>();
        for (Vertex v : vertices.values()) if (v.out.isEmpty()) out.add(v.data);
        return out;
    }
    
    /**
     * Returns a set containing all sources: vertices that have no incoming
     * edges.
     */
    public Set<TYPE> getSources()
    {
        Set<TYPE> out = new LinkedHashSet<>();
        for (Vertex v : vertices.values()) if (v.in.isEmpty()) out.add(v.data);
        return out;
    }
    
    /**
     * Returns a list containing every edge represented as an array. The first
     * element in each array is the start of the edge. The second is the end of
     * the edge.
     */
    public List<Edge<TYPE>> getEdges()
    {
        List<Edge<TYPE>> out = new LinkedList<>();
        for (Vertex st : vertices.values()) for (Vertex en : st.out)
            out.add(new Edge<>(st.data, en.data));
        return out;
    }
    
    /**
     * Same as getEdges, but the edges are topologically sorted.
     */
    public List<Edge<TYPE>> getSortedEdges()
    {
        List<TYPE> order = sort();
        List<Edge<TYPE>> out = new LinkedList<>();
        for (TYPE stData : order)
        {
            Vertex st = vertices.get(stData);
            for (Vertex en : st.out) out.add(new Edge<>(stData, en.data));
        }
        return out;
    }
    
    private boolean isCyclic()
    {
        Set<Vertex> unmarked = new LinkedHashSet<>();
        Set<Vertex> tempmarked = new LinkedHashSet<>();
        unmarked.addAll(vertices.values());
        
        while (!unmarked.isEmpty())
        {
            Vertex v = unmarked.iterator().next();
            if (checkVisit(v, unmarked, tempmarked)) return true;
        }
        
        return false;
    }
    
    private boolean checkVisit(Vertex v, Set<Vertex> unmarked, Set<Vertex> tempmarked)
    {
        if (tempmarked.contains(v)) return true;
        if (!unmarked.remove(v)) return false;
        
        tempmarked.add(v);
        for (Vertex cv : v.in) if (checkVisit(cv, unmarked, tempmarked)) return true;
        tempmarked.remove(v);
        
        return false;
    }
    
    public boolean contains(TYPE vertex)
    {
        return vertices.containsKey(vertex);
    }
    
    public boolean hasEdge(TYPE start, TYPE end)
    {
        Vertex pv = vertices.get(start);
        if (pv == null) return false;
        Vertex cv = vertices.get(end);
        if (cv == null) return false;
        
        return pv.out.contains(cv);
    }
    
    public Set<TYPE> getIn(TYPE vertex)
    {
        Vertex v = vertices.get(vertex);
        if (v == null) return null;
        
        Set<TYPE> out = new LinkedHashSet<>();
        for (Vertex pv : v.in) out.add(pv.data);
        return out;
    }
    
    public Set<TYPE> getOut(TYPE vertex)
    {
        Vertex v = vertices.get(vertex);
        if (v == null) return null;
        
        Set<TYPE> out = new LinkedHashSet<>();
        for (Vertex cv : v.out) out.add(cv.data);
        return out;
    }
    
    public Set<TYPE> getAll()
    {
        Set<TYPE> out = new LinkedHashSet<>();
        for (TYPE data : vertices.keySet()) out.add(data);
        return out;
    }
    
    /**
     * Performs a topological sort on this DAG and returns the result in a list,
     * in ascending order of depth.
     */
    public List<TYPE> sort()
    {
        List<TYPE> out = new ArrayList<>(vertices.size());
        
        if (isEmpty()) return out;
        
        Set<Vertex> unmarked = new LinkedHashSet<>();
        Set<Vertex> tempmarked = new LinkedHashSet<>();
        unmarked.addAll(vertices.values());
        
        while (!unmarked.isEmpty())
        {
            Vertex v = unmarked.iterator().next();
            sortVisit(v, out, unmarked, tempmarked);
        }
        
        return out;
    }
    
    private void sortVisit(Vertex v, List<TYPE> out, Set<Vertex> unmarked, Set<Vertex> tempmarked)
    {
        if (tempmarked.contains(v)) throw new CyclicGraphException();
        if (!unmarked.remove(v)) return;
        
        tempmarked.add(v);
        for (Vertex cn : v.in) sortVisit(cn, out, unmarked, tempmarked);
        tempmarked.remove(v);
        
        out.add(v.data);
    }
    
    public void clear()
    {
        vertices.clear();
    }
    
    public int size()
    {
        return vertices.size();
    }
    
    public boolean isEmpty()
    {
        return vertices.isEmpty();
    }
}