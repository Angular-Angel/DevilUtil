package com.samrj.devil.model;

import com.samrj.devil.io.IOUtil;
import com.samrj.devil.math.Vec3;
import java.io.DataInputStream;
import java.io.IOException;

public class Material implements DataBlock
{
    public final String name;
    public final Vec3 diffuseColor, specularColor;
    public final float specularHardness;
    public final float specularIOR;
    public final float emit;
    
    Material(Model model, DataInputStream in) throws IOException
    {
        name = IOUtil.readPaddedUTF(in);
        diffuseColor = new Vec3(in);
        specularColor = new Vec3(in);
        specularHardness = in.readFloat();
        specularIOR = in.readFloat();
        emit = in.readFloat();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
}
