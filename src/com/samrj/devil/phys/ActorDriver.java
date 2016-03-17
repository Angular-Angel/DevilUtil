package com.samrj.devil.phys;

import com.samrj.devil.geo3d.Ellipsoid;
import com.samrj.devil.geo3d.Geo3DUtil;
import com.samrj.devil.geo3d.GeoMesh;
import com.samrj.devil.geo3d.Geometry;
import com.samrj.devil.geo3d.SweepResult;
import com.samrj.devil.math.Util;
import com.samrj.devil.math.Vec3;

/**
 * Basic class handling collision and movement of a character in a 3D space.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public final class ActorDriver
{
    public final Vec3 pos, vel = new Vec3();
    
    /**
     * The current desired movement direction for this driver.
     */
    public final Vec3 moveDir = new Vec3();
    
    //The geometry that this driver will use for collision detection. Defaults
    //to null, with collision disabled.
    public Geometry geom;
    
    //The downward acceleration that this driver will experience at all times.
    public float gravity = 9.80665f;
    
    //The distance this driver can step over otherwise unwalkable surfaces,
    //like stairs.
    public float stepHeight = 0.25f;
    
    //The maximum y-component of the normal vector for walkable ground.
    //Determines the steepness of hills this driver can walk up. Use
    //setMaxGroundIncline() to set this as an angle.
    public float groundNormalMinY = (float)Math.cos(Util.toRadians(46.0f));
    
    //An epsilon value for determining how close this driver needs to be to the
    //ground to be considered touching the ground. Is a fraction of the step
    //height.
    public float groundThreshold = 1.0f/64.0f;
    
    //An exponential rate of decay determining how quickly this driver will
    //move downward towards the ground when not perfectly touching it.
    public float groundFloatDecay = 32.0f;
    
    //The maximum speed at which this driver can move.
    public float maxSpeed = 3.0f;
    
    //The rate of acceleration for this driver while on the ground.
    public float acceleration = 16.0f;
    
    //The rate of acceleration for this driver while falling. Set to zero for
    //'realistic' lack of air control.
    public float airAcceleration = 4.0f;
    
    //The vertical speed this driver will have upon jumping.
    public float jumpSpeed = 4.0f;
    
    //Settable callback functions for jumping, falling, and landing.
    public Runnable jumpCallback, fallCallback, landCallback;
    
    private final Ellipsoid shape = new Ellipsoid();
    private final Vec3 displacement = new Vec3();
    private Vec3 ground = new Vec3(0.0f, 1.0f, 0.0f);
    private int groundMaterial;
    private boolean applyGravity;
    
    /**
     * Creates a new default physics actor.
     */
    public ActorDriver()
    {
        pos = shape.pos;
        shape.radii.set(0.5f, 0.875f, 0.5f);
    }
    
    /**
     * Sets the maximum angle of surfaces this driver can walk up.
     * 
     * @param angle Any positive angle.
     */
    public void setMaxGroundIncline(float angle)
    {
        if (angle <= 0.0f) throw new IllegalArgumentException();
        groundNormalMinY = (float)Math.cos(angle);
    }
    
    /**
     * Makes the actor jump. May only jump when standing on something.
     * 
     * @param jumpSpeed The vertical speed at which to jump.
     */
    public void jump(float jumpSpeed)
    {
        if (ground == null) return;
        vel.y = jumpSpeed;
        ground = null;
        if (jumpCallback != null) jumpCallback.run();
    }
    
    /**
     * @return Whether or not the player is currently on walkable ground.
     */
    public boolean onGround()
    {
        return ground != null;
    }
    
    /**
     * Sets the ground of this player to a virtual, flat surface.
     */
    public void setFlatGround()
    {
        ground = new Vec3(0.0f, 1.0f, 0.0f);
    }
    
    /**
     * @return The material index of the ground last walked on.
     */
    public int getGroundMaterial()
    {
        return groundMaterial;
    }
    
    public Vec3 getFeetPos()
    {
        Vec3 out = new Vec3(pos);
        out.y -= shape.radii.y;
        return out;
    }
    
    public Vec3 getRadii()
    {
        return new Vec3(shape.radii);
    }
    
    public boolean isValidGround(Vec3 normal)
    {
        return normal.y >= groundNormalMinY;
    }
    
    private void applyAcc(Vec3 desiredVel, float acc)
    {
        if (acc == 0.0f) return;
        
        Vec3 dv = Vec3.sub(desiredVel, vel);
        float dvLen = dv.length();
        
        if (dvLen > acc) vel.madd(dv, acc/dvLen);
        else vel.set(desiredVel);
    }
    
    /**
     * Steps the player's simulation forward by the given time-step.
     * 
     * @param dt The time to step forward by.
     */
    public void step(float dt)
    {
        boolean startOnGround = onGround();
        Vec3 avgVel = new Vec3(vel);
        
        boolean wantToMove = !moveDir.isZero(0.0f);
        Vec3 adjMoveDir = new Vec3(moveDir);

        if (startOnGround) //Walking
        {
            if (wantToMove)
            {
                adjMoveDir.y = -ground.dot(adjMoveDir)*adjMoveDir.length()/ground.y;
                float moveSpeed = adjMoveDir.length();
                if (moveSpeed > 1.0f) adjMoveDir.div(moveSpeed);
                adjMoveDir.mult(maxSpeed);
            }

            //Lock to ground
            Geo3DUtil.restrain(vel, Vec3.negate(ground));
            applyAcc(adjMoveDir, acceleration*dt);
        }
        else //Falling
        {
            if (wantToMove)
            {
                float moveSpeed = adjMoveDir.length();
                if (moveSpeed > 1.0f) adjMoveDir.div(moveSpeed);
                adjMoveDir.mult(maxSpeed);
                adjMoveDir.y = vel.y;
                applyAcc(adjMoveDir, airAcceleration*dt);
            }
        }

        if (applyGravity) vel.y -= gravity*dt;
        
        //Integrate
        avgVel.add(vel).mult(0.5f);
        Vec3.mult(avgVel, dt, displacement);
        pos.add(displacement);
        
        applyGravity = true;
        
        //Find the ground
        if (geom != null && startOnGround)
        {
            ground = null;
            float oldY = pos.y;
            pos.y += stepHeight;
            Vec3 step = new Vec3(0.0f, -2.0f*stepHeight, 0.0f);
            SweepResult sweep = geom.sweepUnsorted(shape, step)
                    .filter(e -> isValidGround(e.normal))
                    .reduce((a, b) -> a.time < b.time ? a : b)
                    .orElse(null);

            pos.y = oldY;
            if (sweep != null)
            {
                float groundDist = (sweep.time*2.0f - 1.0f)*stepHeight;
                pos.y -= groundDist*(1.0f - (float)Math.pow(0.5f, dt*groundFloatDecay));
                ground = sweep.normal;
                Object object = sweep.object;
                if (object instanceof GeoMesh.Face) groundMaterial = ((GeoMesh.Face)object).material;
                applyGravity = (sweep.time - 0.5f)*2.0f > groundThreshold;
            }
        }

        //Clip against the level
        if (geom != null) geom.intersectUnsorted(shape).forEach(isect ->
        {
            pos.add(Vec3.sub(isect.point, isect.surface));
            Geo3DUtil.restrain(vel, isect.normal);

            if (!isValidGround(isect.normal)) return;
            if (!onGround() || isect.normal.y > ground.y)
            {
                ground = isect.normal;
                Object object = isect.object;
                if (object instanceof GeoMesh.Face) groundMaterial = ((GeoMesh.Face)object).material;
            }
        });
        
        boolean endOnGround = onGround();

        //Check for landing
        if (landCallback != null && !startOnGround && endOnGround)
            landCallback.run();

        //Check for falling
        if (fallCallback != null && startOnGround && !endOnGround)
            fallCallback.run();
    }
}
