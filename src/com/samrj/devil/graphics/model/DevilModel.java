package com.samrj.devil.graphics.model;

import com.samrj.devil.res.Resource;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * .DVM file loader. Corresponds with the Blender python exporter.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2014 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class DevilModel
{
    public static final String readPaddedUTF(DataInputStream in) throws IOException
    {
        if (!in.markSupported()) throw new IOException("Cannot read padded UTF-8 on this platform.");
        in.mark(8);
        int utflen = in.readUnsignedShort() + 2;
        in.reset();
        String out = in.readUTF();
        int padding = (4 - (utflen % 4)) % 4;
        if (in.skipBytes(padding) != padding) throw new IOException("Cannot skip bytes properly on this platform.");
        return out;
    }
    
    public final Mesh[] meshes;
    public final MeshObject[] meshObjects;
    
    public DevilModel(InputStream inputStream) throws IOException
    {
        try
        {
            DataInputStream in = new DataInputStream(inputStream);
            if (!in.readUTF().equals("DVM NO TAN 0.2"))
                throw new IOException("Illegal file format specified.");

            int numMeshes = in.readInt();
            meshes = new Mesh[numMeshes];
            for (int i=0; i<numMeshes; i++)
                meshes[i] = new Mesh(in);
            
            int numMeshObjects = in.readInt();
            meshObjects = new MeshObject[numMeshObjects];
            for (int i=0; i<numMeshes; i++)
                meshObjects[i] = new MeshObject(in, meshes);
        }
        finally
        {
            inputStream.close();
        }
    }
    
    public DevilModel(String path) throws IOException
    {
        this(Resource.open(path));
    }
    
    /**
     * Releases any system resources (native memory) associated with this model.
     */
    public void destroy()
    {
        for (Mesh mesh : meshes) mesh.destroy();
        Arrays.fill(meshes, null);
    }
}
