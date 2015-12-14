package com.samrj.devil.graphics.model;

import com.samrj.devil.io.IOUtil;
import com.samrj.devil.io.StreamConstructor;
import com.samrj.devil.res.Resource;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * .DVM file loader. Corresponds with the Blender python exporter.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Model
{
    private static final byte[] MAGIC = IOUtil.hexToBytes("9F0A446576696C4D6F64656C");
    
    private static <T> T[] readBlock(DataInputStream in, int id, Class<T> type, StreamConstructor<T> constructor) throws IOException
    {
        if (in.readInt() != id) throw new IOException("Corrupt DVM.");
        in.skip(4);
        return IOUtil.arrayFromStream(in, type, constructor);
    }
    
    public final int versionMajor, versionMinor;
    public final Action[] actions;
    public final Armature[] armatures;
    public final Lamp[] lamps;
    public final Material[] materials;
    public final Mesh[] meshes;
    public final ModelObject[] objects;
    public final Scene[] scenes;
    
    Model(BufferedInputStream inputStream) throws IOException
    {
        try
        {
            DataInputStream in = new DataInputStream(inputStream);
            
            byte[] header = new byte[12];
            in.read(header);
            if (!Arrays.equals(header, MAGIC))
                throw new IOException("Illegal file format specified.");
            versionMajor = in.readShort();
            versionMinor = in.readShort();
            if (versionMajor != 0) throw new IOException("Unable to load DVM version " + versionMajor);
            
            actions = readBlock(in, 32, Action.class, Action::new);
            armatures = readBlock(in, 33, Armature.class, Armature::new);
            lamps = readBlock(in, 34, Lamp.class, Lamp::new);
            materials = readBlock(in, 35, Material.class, Material::new);
            meshes = readBlock(in, 36, Mesh.class, Mesh::new);
            objects = readBlock(in, 37, ModelObject.class, ModelObject::new);
            scenes = readBlock(in, 38, Scene.class, Scene::new);
            
            for (ModelObject object : objects) object.populate(this);
            for (Scene scene : scenes) scene.populate(this);
        }
        finally
        {
            inputStream.close();
        }
    }
    
    public Model(String path) throws IOException
    {
        this(new BufferedInputStream(Resource.open(path)));
    }
    
    <T extends DataBlock> T getData(DataBlock.Type type, int index)
    {
        if (index < 0) return null;
        DataBlock[] array;
        switch (type)
        {
            case ACTION: array = actions; break;
            case ARMATURE: array = armatures; break;
            case LAMP: array = lamps; break;
            case MATERIAL: array = materials; break;
            case MESH: array = meshes; break;
            case OBJECT: array = objects; break;
            case SCENE: array = scenes; break;
            default: return null;
        }
        return (T)array[index];
    }
    
    /**
     * Releases any system resources (native memory) associated with this model.
     */
    public void destroy()
    {
        for (Armature armature : armatures) armature.destroy();
        for (Mesh mesh : meshes) mesh.destroy();
        Arrays.fill(actions, null);
        Arrays.fill(armatures, null);
        Arrays.fill(lamps, null);
        Arrays.fill(materials, null);
        Arrays.fill(meshes, null);
        Arrays.fill(objects, null);
        Arrays.fill(scenes, null);
    }
}