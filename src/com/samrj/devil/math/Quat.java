package com.samrj.devil.math;

import com.samrj.devil.io.Bufferable;
import com.samrj.devil.io.Streamable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Quaternion class.
 * 
 * @author SmashMaster
 * @copyright 2015 Samuel Johnson
 */
public class Quat implements Bufferable<FloatBuffer>, Streamable
{
    private static final Quat tempQuat = new Quat();
    
    // <editor-fold defaultstate="collapsed" desc="Static accessor methods">
    /**
     * Returns the dot product of the two given quaternions.
     * 
     * @param q0 The first quaternion to multiply.
     * @param q1 The second quaternion to multiply.
     * @return The dot product of the two given quaternions.
     */
    public static final float dot(Quat q0, Quat q1)
    {
        return q0.w*q1.w + q0.x*q1.x + q0.y*q1.y + q0.z*q1.z;
    }
    
    /**
     * Returns the square length of the given quaternion. May be alternately
     * defined as the dot product of the quaternion with itself.
     * 
     * @param q A quaternion.
     * @return The dot product of the given quaternion.
     */
    public static final float squareLength(Quat q)
    {
        return q.w*q.w + q.x*q.x + q.y*q.y + q.z*q.z;
    }
    
    /**
     * Returns the length of the given quaternion.
     * 
     * @param q A quaternion.
     * @return The length of the given quaternion.
     */
    public static final float length(Quat q)
    {
        return (float)Math.sqrt(squareLength(q));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Static mutator methods">
    /**
     *  Copies {@code source} into {@code target}.
     * 
     * @param source The quaternion to copy.
     * @param target The quaternion to copy into.
     */
    public static final void copy(Quat source, Quat target)
    {
        target.w = source.w;
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
    }
    
    /**
     * Adds the two given quaternions and stores the result in {@code result}.
     * 
     * @param q0 The first quaternion to add.
     * @param q1 The second quaternion to add.
     * @param result The quaternion in which to store the result.
     */
    public static final void add(Quat q0, Quat q1, Quat result)
    {
        result.w = q0.w+q1.w;
        result.x = q0.x+q1.x;
        result.y = q0.y+q1.y;
        result.z = q0.z+q1.z;
    }
    
    /**
     * Subtracts {@code q1} from {@code q0} and stores the result in {@code result}.
     * 
     * @param q0 The quaternion to subtract from.
     * @param q1 The quaternion to subtract by.
     * @param result The quaternion in which to store the result.
     */
    public static final void sub(Quat q0, Quat q1, Quat result)
    {
        result.w = q0.w-q1.w;
        result.x = q0.x-q1.x;
        result.y = q0.y-q1.y;
        result.z = q0.z-q1.z;
    }
    
    /**
     * Multiplies {@code q0} by {@code q1} and stores the result in {@code result}.
     * 
     * @param q0 The left-hand quaternion to multiply.
     * @param q1 The right-hand quaternion to multiply by.
     * @param result The quaternion in which to store the result.
     */
    public static final void mult(Quat q0, Quat q1, Quat result)
    {
        float w = q0.w*q1.w - q0.x*q1.x - q0.y*q1.y - q0.z*q1.z;
        float x = q0.w*q1.x + q0.x*q1.w + q0.y*q1.z - q0.z*q1.y;
        float y = q0.w*q1.y - q0.x*q1.z + q0.y*q1.w + q0.z*q1.x;
        float z = q0.w*q1.z + q0.x*q1.y - q0.y*q1.x + q0.z*q1.w;
        
        result.w = w; result.x = x; result.y = y; result.z = z;
    }
    
    /**
     * Multiplies the given quaternion by the given scalar and stores the result
     * in {@code result}.
     * 
     * @param q The quaternion to multiply.
     * @param s The scalar to multiply by.
     * @param result The quaternion in which to store the result.
     */
    public static final void mult(Quat q, float s, Quat result)
    {
        result.w = q.w*s;
        result.x = q.x*s;
        result.y = q.y*s;
        result.z = q.z*s;
    }
    
    /**
     * Divides the given quaternion by the given scalar and stores the result
     * in {@code result}.
     * 
     * @param q The quaternion to multiply.
     * @param s The scalar to multiply by.
     * @param result The quaternion in which to store the result.
     */
    public static final void div(Quat q, float s, Quat result)
    {
        result.w = q.w/s;
        result.x = q.x/s;
        result.y = q.y/s;
        result.z = q.z/s;
    }
    
    /**
     * Negates the given quaternion and stores the result in {@code result}.
     * 
     * @param q The quaternion to negate.
     * @param result The quaternion in which to store the result.
     */
    public static final void negate(Quat q, Quat result)
    {
        result.w = -q.w;
        result.x = -q.x;
        result.y = -q.y;
        result.z = -q.z;
    }
    
    /**
     * Calculates the conjugate of the given quaternion and stores the result
     * in {@code result}.
     * 
     * @param q The quaternion to conjugate.
     * @param result The quaternion in which to store the result.
     */
    public static final void conjugate(Quat q, Quat result)
    {
        result.w = q.w;
        result.x = -q.x;
        result.y = -q.y;
        result.z = -q.z;
    }
    
    /**
     * Normalizes the given quaternion and stores the result in {@code result}.
     * 
     * @param q The quaternion to normalize.
     * @param result The quaternion in which to store the result. 
     */
    public static final void normalize(Quat q, Quat result)
    {
        div(q, length(q), result);
    }
    
    /**
     * Inverts the given quaternion and stores the result in {@code result}.
     * 
     * @param q The quaternion to invert.
     * @param result The quaternion in which to store the result.
     */
    public static final void invert(Quat q, Quat result)
    {
        conjugate(q, result);
        div(result, squareLength(result), result);
    }
    
    /**
     * Linearly interpolates between the two given quaternions using the given
     * scalar and stores the result in {@code result}.
     * 
     * @param q0 The 'start' quaternion to interpolate from.
     * @param q1 The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @param result The vector in which to store the result.
     */
    public static final void lerp(Quat q0, Quat q1, float t, Quat result)
    {
        result.w = Util.lerp(q0.w, q1.w, t);
        result.x = Util.lerp(q0.x, q1.x, t);
        result.y = Util.lerp(q0.y, q1.y, t);
        result.z = Util.lerp(q0.z, q1.z, t);
    }
    
    /**
     * Performs a spherical linear interpolation between the two given
     * quaternions and stores the result in {@code r}.
     * 
     * @param q0 The 'start' quaternion to interpolate from.
     * @param q1 The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @param result The quaternion in which to store the result.
     */
    public static final void slerp(Quat q0, Quat q1, float t, Quat result)
    {
        float cos = dot(q0, q1);
        if (cos < 0f)
        {
            negate(q1, tempQuat);
            cos = -cos;
        }
        else copy(q1, tempQuat);
        
        if (cos == 1.0f) lerp(q0, tempQuat, t, result);
        else
        {
            float angle = (float)Math.acos(cos);
            mult(q0, (float)Math.sin((1.0f - t)*angle), result);
            mult(tempQuat, (float)Math.sin(t*angle), tempQuat);
            add(result, tempQuat, result);
            div(result, (float)Math.sin(angle), result);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Static factory methods">
    /**
     * Adds the two given quaternions and returns a new vector containing the
     * result.
     * 
     * @param q0 The first quaternion to add.
     * @param q1 The second quaternion to add.
     * @return A new vector containing the result.
     */
    public static final Quat add(Quat q0, Quat q1)
    {
        Quat result = new Quat();
        add(q0, q1, result);
        return result;
    }
    
    /**
     * Subtracts {@code q1} from {@code q0} and returns a new vector containing
     * the result.
     * 
     * @param q0 The quaternion to subtract from.
     * @param q1 The quaternion to subtract by.
     * @return A new vector containing the result.
     */
    public static final Quat sub(Quat q0, Quat q1)
    {
        Quat result = new Quat();
        sub(q0, q1, result);
        return result;
    }
    
    /**
     * Multiplies {@code q0} by {@code q1} and returns a new vector containing
     * the result.
     * 
     * @param q0 The left-hand quaternion to multiply.
     * @param q1 The right-hand quaternion to multiply by.
     * @return A new vector containing the result.
     */
    public static final Quat mult(Quat q0, Quat q1)
    {
        Quat result = new Quat();
        mult(q0, q1, result);
        return result;
    }
    
    /**
     * Multiplies the given quaternion by the given scalar and returns a new
     * vector containing the result.
     * 
     * @param q The quaternion to multiply.
     * @param s The scalar to multiply by.
     * @return A new vector containing the result.
     */
    public static final Quat mult(Quat q, float s)
    {
        Quat result = new Quat();
        mult(q, s, result);
        return result;
    }
    
    /**
     * Divides the given quaternion by the given scalar and returns a new vector
     * containing the result.
     * 
     * @param q The quaternion to multiply.
     * @param s The scalar to multiply by.
     * @return A new vector containing the result.
     */
    public static final Quat div(Quat q, float s)
    {
        Quat result = new Quat();
        div(q, s, result);
        return result;
    }
    
    /**
     * Negates the given quaternion and returns a new vector containing the result.
     * 
     * @param q The quaternion to negate.
     * @return A new vector containing the result.
     */
    public static final Quat negate(Quat q)
    {
        Quat result = new Quat();
        negate(q, result);
        return result;
    }
    
    /**
     * Calculates the conjugate of the given quaternion and returns a new vector
     * containing the result.
     * 
     * @param q The quaternion to conjugate.
     * @return A new vector containing the result.
     */
    public static final Quat conjugate(Quat q)
    {
        Quat result = new Quat();
        conjugate(q, result);
        return result;
    }
    
    /**
     * Normalizes the given quaternion and returns a new vector containing the result.
     * 
     * @param q The quaternion to normalize.
     * @return A new vector containing the result.
     */
    public static final Quat normalize(Quat q)
    {
        Quat result = new Quat();
        normalize(q, result);
        return result;
    }
    
    /**
     * Inverts the given quaternion and returns a new vector containing the result.
     * 
     * @param q The quaternion to invert.
     * @return A new vector containing the result.
     */
    public static final Quat invert(Quat q)
    {
        Quat result = new Quat();
        invert(q, result);
        return result;
    }
    
    /**
     * Linearly interpolates between the two given quaternions using the given
     * scalar and returns a new vector containing the result.
     * 
     * @param q0 The 'start' quaternion to interpolate from.
     * @param q1 The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @return A new vector containing the result.
     */
    public static final Quat lerp(Quat q0, Quat q1, float t)
    {
        Quat result = new Quat();
        lerp(q0, q1, t, result);
        return result;
    }
    
    /**
     * Performs a spherical linear interpolation between the two given
     * quaternions and returns a new vector containing the result.
     * 
     * @param q0 The 'start' quaternion to interpolate from.
     * @param q1 The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @return A new vector containing the result.
     */
    public static final Quat slerp(Quat q0, Quat q1, float t)
    {
        Quat result = new Quat();
        slerp(q0, q1, t, result);
        return result;
    }
    // </editor-fold>
    
    public float w, x, y, z;
    
    /**
     * Creates a zero quaternion. NOT the identity quaternion.
     */
    public Quat()
    {
    }
    
    public Quat(float w, float x, float y, float z)
    {
        this.w = w; this.x = x; this.y = y; this.z = z;
    }
    
    public Quat(Quat q)
    {
        w = q.w; x = q.x; y = q.y; z = q.z;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Instance accessor methods">
    /**
     * Returns the dot product of this and the given quaternion.
     * 
     * @param q The quaternion with which to calculate the dot product.
     * @return The dot product of this and the given quaternion.
     */
    public float dot(Quat q)
    {
        return dot(this, q);
    }
    
    /**
     * Returns the square length of this quaternion.
     * 
     * @return The square length of this quaternion.
     */
    public float squareLength()
    {
        return squareLength(this);
    }
    
    /**
     * Returns the length of this quaternion.
     * 
     * @return The length of this quaternion.
     */
    public float length()
    {
        return length(this);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Instance mutator methods">
    /**
     * Sets this to the given quaternion.
     * 
     * @param q The quaternion to set this to.
     * @return This quaternion.
     */
    public Quat set(Quat q)
    {
        copy(q, this);
        return this;
    }
    
    /**
     * Sets the coordinates of this quaternion.
     * 
     * @return This quaternion.
     */
    public Quat set(float w, float x, float y, float z)
    {
        this.w = w; this.x = x; this.y = y; this.z = z;
        return this;
    }
    
    /**
     * Adds the given quaternion to this.
     * 
     * @param q The quaternion to add.
     * @return This quaternion.
     */
    public Quat add(Quat q)
    {
        add(this, q, this);
        return this;
    }
    
    /**
     * Subtracts the given quaternion from this.
     * 
     * @param q The quaternion to subtract.
     * @return This quaternion.
     */
    public Quat sub(Quat q)
    {
        sub(this, q, this);
        return this;
    }
    
    /**
     * Multiplies this by the given quaternion.
     * 
     * @param q The right-hand quaternion to multiply by.
     * @return This quaternion.
     */
    public Quat mult(Quat q)
    {
        mult(this, q, this);
        return this;
    }
    
    /**
     * Multiplies this by the given scalar.
     * 
     * @param s The given scalar to multiply by.
     * @return This quaternion.
     */
    public Quat mult(float s)
    {
        mult(this, s, this);
        return this;
    }
    
    /**
     * Divides this by the given scalar.
     * 
     * @param s The given scalar to divide by.
     * @return This quaternion.
     */
    public Quat div(float s)
    {
        div(this, s, this);
        return this;
    }
    
    /**
     * Negates this quaternion.
     * 
     * @return This quaternion.
     */
    public Quat negate()
    {
        negate(this, this);
        return this;
    }
    
    /**
     * Conjugates this quaternion.
     * 
     * @return This quaternion.
     */
    public Quat conjugate()
    {
        conjugate(this, this);
        return this;
    }
    
    /**
     * Normalizes this quaternion.
     * 
     * @return This quaternion.
     */
    public Quat normalize()
    {
        normalize(this, this);
        return this;
    }
    
    /**
     * Inverts this quaternion.
     * 
     * @return This quaternion.
     */
    public Quat invert()
    {
        invert(this, this);
        return this;
    }
    
    /**
     * Linearly interpolates this towards the given quaternion using the given
     * scalar interpolant.
     * 
     * @param q The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @return This quaternion.
     */
    public Quat lerp(Quat q, float t)
    {
        lerp(this, q, t, this);
        return this;
    }
    
    /**
     * Spherical-linearly interpolates this towards the given quaternion using
     * the given scalar interpolant.
     * 
     * @param q The 'end' quaternion to interpolate to.
     * @param t The scalar interpolant, between zero and one (inclusive).
     * @return This quaternion.
     */
    public Quat slerp(Quat q, float t)
    {
        slerp(this, q, t, this);
        return this;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Overriden/implemented methods">
    /**
     * WARNING: W buffered last instead of first, for GLSL.
     */
    @Override
    public void read(FloatBuffer buffer)
    {
        x = buffer.get();
        y = buffer.get();
        z = buffer.get();
        w = buffer.get();
    }

    @Override
    public void write(FloatBuffer buffer)
    {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
    }

    @Override
    public void read(DataInputStream in) throws IOException
    {
        w = in.readFloat();
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        out.writeFloat(w);
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
    }
    
    @Override
    public String toString()
    {
        return "(" + w + ": " + x + ", " + y + ", " + z + ")";
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        final Quat q = (Quat)o;
        return q.w == w && q.x == x && q.y == y && q.z == z;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 177 + Float.floatToIntBits(this.w);
        hash = 59*hash + Float.floatToIntBits(this.x);
        hash = 59*hash + Float.floatToIntBits(this.y);
        return 59*hash + Float.floatToIntBits(this.z);
    }
    // </editor-fold>
}
