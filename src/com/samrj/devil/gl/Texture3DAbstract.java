package com.samrj.devil.gl;

import com.samrj.devil.graphics.TexUtil;
import com.samrj.devil.math.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

/**
 * Abstract class for 3D textures.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Texture3DAbstract extends Texture
{
    private int width, height, depth;
    
    Texture3DAbstract(int target, int binding)
    {
        super(target, binding);
    }
    
    /**
     * @return The width of this texture, or -1 if it has no associated image.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * @return The height of this texture, or -1 if it has no associated image.
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * @return The depth of this texture, or -1 if it has no associated image.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Allocates space on the GPU for an image of the given size and format, but
     * does not upload any information.
     * 
     * @param width The width of the image.
     * @param height The height of the image.
     * @param depth The depth of the image.
     * @param format The format of the image.
     */
    public void image(int width, int height, int depth, int format)
    {
        if (width <= 0 || height <= 0 || depth <= 0)
            throw new IllegalArgumentException("Illegal image dimensions.");
        
        int baseFormat = TexUtil.getBaseFormat(format);
        if (baseFormat == -1) throw new IllegalArgumentException("Illegal image format.");
        
        Util.PrimType primType = TexUtil.getPrimType(format);
        int glPrimType = primType != null ? TexUtil.getGLPrim(primType) : GL11.GL_UNSIGNED_BYTE;
        
        this.width = width;
        this.height = height;
        this.depth = depth;
        int oldID = tempBind();
        GL12.nglTexImage3D(target, 0, format, width, height, depth, 0,
                baseFormat, glPrimType, MemoryUtil.NULL);
        tempUnbind(oldID);
    }
    
    /**
     * Overwrites the stored image layer with the given image. This texture must
     * already have allocated storage.
     * 
     * @param image The image to overwrite the 
     * @param depth The image layer to overwrite.
     * @param format The texture format to store the image as.
     */
    public void subimage(Image image, int depth, int format)
    {
        if (image.deleted()) throw new IllegalStateException("Image is deleted.");
        
        int dataFormat = TexUtil.getBaseFormat(format);
        if (image.bands != TexUtil.getBands(dataFormat))
            throw new IllegalArgumentException("Incompatible format bands.");
        
        if (depth < 0 || depth >= this.depth)
            throw new IllegalArgumentException("Illegal depth specified.");
        
        if (image.width != width || image.height != height)
            throw new IllegalArgumentException("Incompatible image dimensions.");
        
        int primType = TexUtil.getGLPrim(image.type);
        int oldID = tempBind();
        GL12.nglTexSubImage3D(target, 0, 0, 0, depth, width, height, 1,
                dataFormat, primType, image.address());
        tempUnbind(oldID);
    }
    
    /**
     * Overwrites the stored image layer with the given image. This texture must
     * already have allocated storage.
     * 
     * @param image The image to overwrite the 
     * @param depth The image layer to overwrite.
     */
    public void subimage(Image image, int depth)
    {
        int format = TexUtil.getFormat(image);
        if (format == -1) throw new IllegalArgumentException("Illegal image format.");
        subimage(image, depth, format);
    }
}