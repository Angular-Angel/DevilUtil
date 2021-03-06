package com.samrj.devil.graphics;

import com.samrj.devil.geo2d.AAB2;
import com.samrj.devil.math.Mat3;
import com.samrj.devil.math.Mat4;
import com.samrj.devil.math.Vec2;
import com.samrj.devil.math.Vec2i;

/**
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2019 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Camera2D
{
    public final Vec2 pos;
    public float scale;
    private Mat4 proj;
    private Vec2 mid;
    private Vec2i res;
    
    public Camera2D(int resX, int resY, Vec2 pos, float height)
    {
        if (height <= 0f) throw new IllegalArgumentException();
        this.pos = new Vec2(pos);
        
        res = new Vec2i(resX, resY);
        mid = new Vec2(res.x, res.y).div(2.0f);
        
        proj = Mat4.orthographic(mid.x, mid.y, -1f, 1f);
        scale = res.y/height;
    }
    
    public void setHeight(float height)
    {
        scale = res.y/height;
    }
    
    public Vec2 getMid()
    {
        return new Vec2(mid);
    }
    
    public Vec2i getRes()
    {
        return new Vec2i(res);
    }
    
    public float getAspectRatio()
    {
        return res.y/(float)res.x;
    }
    
    public Mat4 getProj()
    {
        return new Mat4(proj);
    }
    
    public Mat3 getView()
    {
        Mat3 out = Mat3.scaling(scale);
        out.translate(Vec2.negate(pos));
        return out;
    }
    
    public Mat3 toScreen()
    {
        Mat3 out = Mat3.translation(mid);
        out.mult(scale);
        out.translate(Vec2.negate(pos));
        return out;
    }
    
    public Mat3 toWorld()
    {
        Mat3 out = Mat3.translation(pos);
        out.div(scale);
        out.translate(Vec2.negate(mid));
        return out;
    }
    
    public AAB2 getViewBounds()
    {
        Mat3 toWorld = toWorld();
        return AAB2.bounds(new Vec2().mult(toWorld),
                          new Vec2(res.x, res.y).mult(toWorld));
    }
    
    public void zoom(Vec2 pos, float factor)
    {
        Vec2 newPos = Vec2.mult(pos, toScreen());
        scale *= factor;
        newPos.mult(toWorld());
        this.pos.sub(newPos).add(pos);
    }
}
