package com.samrj.devil.graphics.model;

import com.samrj.devil.io.IOUtil;
import java.io.DataInputStream;
import java.io.IOException;

public class Action implements DataBlock
{
    public final String name;
    public final FCurve[] fcurves;
    public final float minX, maxX;
    
    Action(DataInputStream in) throws IOException
    {
        name = IOUtil.readPaddedUTF(in);
        fcurves = IOUtil.arrayFromStream(in, FCurve.class, FCurve::new);
        
        float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
        for (int i=0; i<fcurves.length; i++)
        {
            if (fcurves[i].minX < min) min = fcurves[i].minX;
            if (fcurves[i].maxX > max) max = fcurves[i].maxX;
        }
        minX = min; maxX = max;
    }
    
    public void apply(Armature armature, float time)
    {
        for (FCurve fcurve : fcurves) fcurve.apply(armature, time);
    }

    @Override
    public Type getType()
    {
        return Type.ACTION;
    }
}
