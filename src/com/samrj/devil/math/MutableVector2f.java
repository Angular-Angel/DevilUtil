package com.samrj.devil.math;

/**
 * Mutable 32 bit, 2 component vector with method chaining for local mutators.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 */
public class MutableVector2f extends Vector2f
{
    public float x, y;
    
    // <editor-fold defaultstate="collapsed" desc="Constructors/Overriden Vector2f methods">
    /**
     * Initializes this to the given coordinates.
     */
    public MutableVector2f(float x, float y)
    {
        this.x = x; this.y = y;
    }
    
    /**
     * Initializes this to the given vector.
     */
    public MutableVector2f(Vector2f v)
    {
        this(v.x(), v.y());
    }
    
    /**
     * Initializes this to the null vector.
     */
    public MutableVector2f()
    {
        this(0.0f, 0.0f);
    }

    @Override
    public float x()
    {
        return x;
    }

    @Override
    public float y()
    {
        return y;
    }
    
    @Override
    public ImmutableVector2f toImmutable()
    {
        return new ImmutableVector2f(x, y);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Mutators">
    /**
     * Sets the x component of this vector to the given value and returns this.
     */
    public MutableVector2f setX(float x)
    {
        this.x = x;
        return this;
    }
    
    /**
     * Sets the y component of this vector to the given value and returns this.
     */
    public MutableVector2f setY(float y)
    {
        this.y = y;
        return this;
    }
    
    /**
     * Sets this to the null vector and returns this.
     */
    public MutableVector2f set()
    {
        this.x = 0.0f; this.y = 0.0f;
        return this;
    }
    
    /**
     * Sets this vector to the given coordinates and returns this.
     */
    public MutableVector2f set(float x, float y)
    {
        this.x = x; this.y = y;
        return this;
    }
    
    /**
     * Sets this vector to the given vector and returns this.
     */
    public MutableVector2f set(Vector2f v)
    {
        this.x = v.x(); this.y = v.y();
        return this;
    }
    
    /**
     * Adds the given coordinates to this vector and returns this.
     */
    public MutableVector2f addLocal(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }
    
    /**
     * Adds the given vector to and returns this.
     */
    public MutableVector2f addLocal(Vector2f v)
    {
        x += v.x();
        y += v.y();
        return this;
    }
    
    /**
     * Subtracts the given coordinates from this vector and returns this.
     */
    public MutableVector2f subLocal(float x, float y)
    {
        this.x -= x;
        this.y -= y;
        return this;
    }
    
    /**
     * Subtracts the given vector from and returns this.
     */
    public MutableVector2f subLocal(Vector2f v)
    {
        x -= v.x();
        y -= v.y();
        return this;
    }
    
    /**
     * Multiplies this vector by the given value and returns this.
     */
    public MutableVector2f multLocal(float f)
    {
        x *= f;
        y *= f;
        return this;
    }
    
    /**
     * Negates this vector and returns this.
     */
    public MutableVector2f negateLocal()
    {
        x = -x;
        y = -y;
        return this;
    }
    
    /**
     * Sets this vector to the cross product of this and a three-dimensional
     * vector of the form <0.0, 0.0, z> and returns this.
     * 
     * @param z The z value of the vector to use as a multiplier.
     */
    public MutableVector2f crossLocal(float z)
    {
        return set(y*z, -x*z);
    }
    
    /**
     * Divides this vector by the given value and returns this.
     */
    public MutableVector2f divLocal(float f)
    {
        x /= f;
        y /= f;
        return this;
    }
    
    /**
     * Sets this vector to the average of this and the given vector, and returns
     * this.
     */
    public MutableVector2f avgLocal(Vector2f v)
    {
        x = (x + v.x())*0.5f;
        y = (y + v.y())*0.5f;
        return this;
    }
    
    /**
     * Normalizes this vector and returns this.
     */
    public MutableVector2f normLocal()
    {
        final float sqLen = x*x + y*y;
        if (sqLen != 0.0f)
        {
            final float factor = 1.0f/(float)Math.sqrt(sqLen);
            x *= factor;
            y *= factor;
        }
        return this;
    }
    
    /**
     * Sets the length of this vector to the given value and returns this.
     */
    public MutableVector2f multNormLocal(float f)
    {
        final float sqLen = x*x + y*y;
        if (sqLen != 0.0f)
        {
            final float factor = f/(float)Math.sqrt(sqLen);
            x *= factor;
            y *= factor;
        }
        return this;
    }
    
    /**
     * Sets this to the vector projection of this onto the given vector and
     * returns this.
     */
    public MutableVector2f vecProjLocal(Vector2f v)
    {
        final float factor = dot(v)/v.squareLength();
        x = v.x()*factor;
        y = v.y()*factor;
        return this;
    }
    
    /**
     * Sets this to the vector projection of this onto the given vector and
     * returns this. Assumes that the given vector is normalized. Fast.
     */
    public MutableVector2f vecProjUnitLocal(Vector2f v)
    {
        final float factor = dot(v);
        x = v.x()*factor;
        y = v.y()*factor;
        return this;
    }
    
    /**
     * Reflects this vector about the given unit vector and returns this.
     */
    public MutableVector2f reflectUnitLocal(Vector2f n)
    {
        final float m = 2f*dot(n);
        
        this.x = m*n.x() - x;
        this.y = m*n.y() - y;
        return this;
    }
    
    /**
     * Refracts this vector about the given unit vector and returns this.
     * 
     * @param n The normal vector to refract about.
     * @param eta The ratio of two indices of refraction.
     */
    public MutableVector2f refractUnitLocal(Vector2f n, float eta)
    {
        final float dot = dot(n);
        final float k = 1.0f - eta*eta*(1.0f - dot*dot);
        
        if (k < 0.0f) return set();
        final float factor = (dot*eta + (float)Math.sqrt(k));
        
        x = x*eta - factor*n.x();
        y = y*eta - factor*n.y();
        return this;
    }
    // </editor-fold>
}
