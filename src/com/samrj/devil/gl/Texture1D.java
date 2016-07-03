package com.samrj.devil.gl;

import com.samrj.devil.graphics.TexUtil;
import org.lwjgl.opengl.GL11;

/**
 * OpenGL 1D texture class.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2016 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public final class Texture1D extends Texture<Texture1D>
{
    private int width;
    
    Texture1D()
    {
        super(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_BINDING_1D);
        
        int oldID = tempBind();
        parami(GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        parami(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        parami(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        tempUnbind(oldID);
    }
    
    @Override
    Texture1D getThis()
    {
        return this;
    }
    
    /**
     * @return The width of this texture, or -1 if it has no associated image.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Allocates space on the GPU for the image, and then uploads it, linking it
     * to this texture. After calling, the image may be safely deleted from
     * memory. Any previous image data associated with this texture is released.
     * 
     * Assumes the image has a width and/or height of 1 pixel.
     * 
     * @param image The image to upload to the GPU.
     * @param format The texture format to store the image as.
     * @return This texture.
     */
    public Texture1D image(Image image, int format)
    {
        if (image.deleted()) throw new IllegalStateException("Image is deleted.");
        
        int dataFormat = TexUtil.getBaseFormat(format);
        if (image.bands != TexUtil.getBands(dataFormat))
            throw new IllegalArgumentException("Incompatible format bands.");
        
        width = image.width*image.height;
        int primType = TexUtil.getPrimitiveType(format);
        
        int oldID = tempBind();
        GL11.nglTexImage1D(target, 0, format, width, 0, dataFormat, primType, image.address());
        tempUnbind(oldID);
        
        setVRAMUsage(TexUtil.getBits(format)*width);
        
        return this;
    }
    
    /**
     * Allocates space on the GPU for the image, and then uploads it, linking it
     * to this texture. After calling, the image may be safely deleted from
     * memory. Any previous image data associated with this texture is released.
     * 
     * @param image The image to upload to the GPU.
     * @return This texture.
     */
    public Texture1D image(Image image)
    {
        int format = TexUtil.getFormat(image);
        if (format == -1) throw new IllegalArgumentException("Illegal image format.");
        return image(image, format);
    }
}
