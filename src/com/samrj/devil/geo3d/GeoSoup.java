package com.samrj.devil.geo3d;

import com.samrj.devil.math.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Basic geometry class which accepts unordered, unstructured mesh data.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2016 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class GeoSoup implements Geometry
{
    public final List<Vertex3> verts = new ArrayList<>();
    public final List<Edge3> edges = new ArrayList<>();
    public final List<Triangle3> faces = new ArrayList<>();
    
    private final Box3 bounds = Box3.infinite();
    private boolean boundsDirty = true;

    @Override
    public Stream<RaycastResult> raycastUnsorted(Vec3 p0, Vec3 dp, boolean terminated)
    {
        return faces.stream()
                .map(f -> Geo3DUtil.raycast(f, p0, dp, terminated))
                .filter(e -> e != null);
    }

    @Override
    public Stream<IsectResult> intersectUnsorted(ConvexShape shape)
    {
        return Stream.concat(Stream.concat(
                faces.stream().map(f -> shape.isect(f)),
                edges.stream().map(e -> shape.isect(e))),
                verts.stream().map(v -> shape.isect(v)))
                    .filter(e -> e != null);
    }

    @Override
    public Stream<SweepResult> sweepUnsorted(ConvexShape shape, Vec3 dp)
    {
        return Stream.concat(Stream.concat(
                faces.stream().map(f -> shape.sweep(dp, f)),
                edges.stream().map(e -> shape.sweep(dp, e))),
                verts.stream().map(v -> shape.sweep(dp, v)))
                    .filter(e -> e != null);
    }
    
    @Override
    public Box3 getBounds()
    {
        return new Box3(bounds);
    }
    
    public void markBoundsDirty()
    {
        boundsDirty = true;
        bounds.setInfinite();
    }
    
    @Override
    public boolean areBoundsDirty()
    {
        return boundsDirty;
    }
    
    @Override
    public void updateBounds()
    {
        bounds.setEmpty();
        for (Vertex3 v : verts) bounds.expand(v.a());
        for (Edge3 e : edges)
        {
            bounds.expand(e.a());
            bounds.expand(e.b());
        }
        for (Triangle3 f : faces)
        {
            bounds.expand(f.a());
            bounds.expand(f.b());
            bounds.expand(f.c());
        }
        
        boundsDirty = false;
    }
}
