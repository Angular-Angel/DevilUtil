package com.samrj.devil.geo3d;

import com.samrj.devil.math.Vec3;

/**
 * 3D edge class.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2014 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Edge implements EllipsoidCast.Testable, EllipsoidClip.Testable
{
    public Vertex a, b;
    
    public Edge(Vertex a, Vertex b)
    {
        this.a = a; this.b = b;
    }
    
    public Edge()
    {
    }
    
    public boolean equals(Vertex a, Vertex b)
    {
        return (this.a == a && this.b == b) ||
               (this.a == b && this.b == a);
    }
    
    public boolean equals(Edge edge)
    {
        return equals(edge.a, edge.b);
    }
    
    /**
     * Returns a new edge contact if the given ellipsoid cast hits this edge,
     * or null if it doesn't.
     * 
     * @param cast The ellipsoid cast to test against this edge.
     * @return A new edge contact if the given ellipsoid cast hits this edge,
     *         or null if it doesn't.
     */
    @Override
    public EdgeContact test(EllipsoidCast cast)
    {
        Vec3 p0 = Vec3.div(cast.p0, cast.radius);
        Vec3 p1 = Vec3.div(cast.p1, cast.radius);
        Vec3 cDir = Vec3.sub(p1, p0);
        float cSqLen = cDir.squareLength();
        
        Vec3 a = Vec3.div(this.a, cast.radius);
        Vec3 b = Vec3.div(this.b, cast.radius);

        Vec3 edgeDir = Vec3.sub(b, a);
        float edgeSqLen = edgeDir.squareLength();
        Vec3 posDir = Vec3.sub(a, p0);

        float edgeDotCDir = edgeDir.dot(cDir);
        float edgeDotPos = edgeDir.dot(posDir);

        float t = Geometry.solveQuadratic(
                edgeDotCDir*edgeDotCDir - edgeSqLen*cSqLen,
                2.0f*(edgeSqLen*cDir.dot(posDir) - edgeDotCDir*edgeDotPos),
                edgeSqLen*(1.0f - posDir.squareLength()) + edgeDotPos*edgeDotPos);

        if (Float.isNaN(t)) return null; //We miss the line entirely.
        if (t <= 0.0f || (cast.terminated && t >= 1.0f))
            return null; //Moving away, or won't get there in time.

        float f = (edgeDotCDir*t - edgeDotPos)/edgeSqLen;
        if (f <= 0.0f || f >= 1.0f) return null; //We hit the line but missed the segment.
        
        float dist = cast.p0.dist(p1)*t;
        Vec3 cp = Vec3.lerp(cast.p0, cast.p1, t);
        Vec3 p = edgeDir.mult(f).add(a);
        Vec3 n = Vec3.sub(cp, p).normalize();
        return new EdgeContact(t, dist, cp, p, n, f);
    }
    
    @Override
    public EdgeIntersection test(EllipsoidClip clip)
    {
        return null;
    }
    
    /**
     * Contact class for edges.
     */
    public class EdgeContact extends Contact<Edge>
    {
        /**
         * The edge contact interpolant.
         */
        public final float et;

        EdgeContact(float t, float d, Vec3 cp, Vec3 p, Vec3 n, float et)
        {
            super(t, d, cp, p, n);
            this.et = et;
        }
        
        @Override
        public Type type()
        {
            return Type.EDGE;
        }

        @Override
        public Edge contacted()
        {
            return Edge.this;
        }
    }
    
    public final class EdgeIntersection extends Intersection<Edge>
    {
        /**
         * The edge interpolant.
         */
        public final float et;
        
        EdgeIntersection(float d, Vec3 p, Vec3 n, float et)
        {
            super(d, p, n);
            this.et = et;
        }
        
        @Override
        public Type type()
        {
            return Type.EDGE;
        }
        
        @Override
        public Edge intersected()
        {
            return Edge.this;
        }
    }
}
