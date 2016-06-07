package com.samrj.devil.model;

import com.samrj.devil.math.Vec3;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Lamp extends DataBlock
{
    public enum Type
    {
        POINT, SUN;
    }
    
    public final Vec3 color;
    public final Type type;
    public final float radius;
    
    Lamp(Model model, DataInputStream in) throws IOException
    {
        super(model, in);
        color = new Vec3(in);
        type = Type.values()[in.readInt()];
        radius = type == Type.POINT ? in.readFloat() : -1.0f;
    }
}
