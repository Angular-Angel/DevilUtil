/*
 * Copyright (c) 2016 Sam Johnson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.samrj.devil.graphics;

import com.samrj.devil.math.Mat3;
import com.samrj.devil.math.Mat4;
import com.samrj.devil.math.Quat;
import com.samrj.devil.math.Vec2;
import com.samrj.devil.math.Vec2i;
import com.samrj.devil.math.Vec3;
import com.samrj.devil.math.Vec4;
import org.lwjgl.opengl.GL11;

/**
 * 3D camera class.
 * 
 * @author Samuel Johnson (SmashMaster)
 */
public class Camera3D
{
    /**
     * Returns an array of eight vectors, each one a vertex of this camera's
     * frustum. Returned in local space.
     * 
     * @param near The near plane of the frustum.
     * @param far The far plane of the frustum.
     * @return The frustum of this camera.
     */
    public static Vec3[] getFrustum(float near, float far, float hSlope, float vSlope)
    {
        float wn = near*hSlope, wf = far*hSlope;
        float hn = near*vSlope, hf = far*vSlope;
        
        Vec3[] array = {
            new Vec3(-wn, -hn, -near),
            new Vec3(-wf, -hf, -far),
            new Vec3(-wn,  hn, -near),
            new Vec3(-wf,  hf, -far),
            new Vec3( wn, -hn, -near),
            new Vec3( wf, -hf, -far),
            new Vec3( wn,  hn, -near),
            new Vec3( wf,  hf, -far),
        };
        
        return array;
    }
    
    public final Vec3 pos = new Vec3();
    public final Quat dir = Quat.identity();
    public final float zNear, zFar;
    public final float fov;
    public final float hSlope, vSlope;
    public final Mat4 projMat, viewMat;
    public final Vec3 right, up, forward;
    
    public Camera3D(float zNear, float zFar, float fov, float aspectRatio)
    {
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
        float tanFov = (float)Math.tan(fov*0.5f);
        
        if (aspectRatio <= 1.0f) //Width is greater or equal to height.
        {
            hSlope = tanFov;
            vSlope = tanFov*aspectRatio;
        }
        else //Widgth is smaller than height.
        {
            hSlope = tanFov/aspectRatio;
            vSlope = tanFov;
        }
        
        projMat = Mat4.frustum(hSlope*zNear, vSlope*zNear, zNear, zFar);
        viewMat = Mat4.identity();
        right = new Vec3(1.0f, 0.0f, 0.0f);
        up = new Vec3(0.0f, 1.0f, 0.0f);
        forward = new Vec3(0.0f, 0.0f, -1.0f);
    }
    
    public Camera3D(float zNear, float zFar, float fov, Vec2i resolution)
    {
        this(zNear, zFar, fov, resolution.y/(float)resolution.x);
    }
    
    /**
     * Returns whether this camera frustum's width is smaller than its height.
     */
    public boolean isSkinny()
    {
        return hSlope < vSlope;
    }
    
    public void pointAt(Vec3 p)
    {
        dir.setRotation(new Vec3(0.0f, 0.0f, -1.0f), Vec3.sub(p, pos));
    }
    
    /**
     * Projects the given vector to this camera's clip space
     */
    public void project(Vec3 v, Vec3 result)
    {
        Vec4 h = new Vec4(v, 1.0f);
        h.mult(viewMat);
        h.mult(projMat);
        result.set(h.x, h.y, h.z).div(h.w);
    }
    
    public Vec3 project(Vec3 v)
    {
        Vec3 out = new Vec3();
        project(v, out);
        return out;
    }
    
    /**
     * Updates the matrices and axis directions for this camera.
     */
    public void update()
    {
        viewMat.setRotation(Quat.invert(dir));
        viewMat.translate(Vec3.negate(pos));
        
        Mat3 rot = Mat3.rotation(dir);
        right.setAsColumn(rot, 0);
        up.setAsColumn(rot, 1);
        forward.setAsColumn(rot, 2).negate();
    }
    
    /**
     * Loads this camera's matrices into OpenGL.
     */
    public void glLoadMatrices()
    {
        GraphicsUtil.glLoadMatrix(projMat, GL11.GL_PROJECTION);
        GraphicsUtil.glLoadMatrix(viewMat, GL11.GL_MODELVIEW);
    }
    
    /**
     * Returns an array of eight vectors, each one a vertex of this camera's
     * frustum. Returned in local space, with actual.
     * 
     * @return The frustum of this camera.
     */
    public Vec3[] getFrustum()
    {
        return getFrustum(zNear, zFar, hSlope, vSlope);
    }
    
    /**
     * There is a unique sphere whose surface touches each vertex of this
     * camera's frustum. This method returns the distance to and radius of that
     * sphere, in the first and second components of a 2D vector.
     * 
     * @param near The near plane to use.
     * @param far The far plane to use.
     * @return The circumsphere of this camera.
     */
    public Vec2 getFrustumCircumsphere(float near, float far)
    {
        float slopeSq = hSlope*hSlope + vSlope*vSlope;
        float z = (near*(slopeSq - 1.0f) + far*(slopeSq + 1.0f))*0.5f;
        float r = (float)Math.sqrt(near*near*slopeSq + z*z);
        
        return new Vec2(-(near + z), r);
    }
    
    /**
     * There is a unique sphere whose surface touches each vertex of this
     * camera's frustum. This method returns the distance to and radius of that
     * sphere, in the first and second components of a 2D vector.
     * 
     * @return The circumsphere of this camera.
     */
    public Vec2 getFrustumCircumsphere()
    {
        return getFrustumCircumsphere(zNear, zFar);
    }
}
