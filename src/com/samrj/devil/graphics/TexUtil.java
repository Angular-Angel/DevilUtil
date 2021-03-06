/*
 * Copyright (c) 2019 Sam Johnson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.samrj.devil.graphics;

import com.samrj.devil.gl.Image;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14C.*;
import static org.lwjgl.opengl.GL30C.*;

/**
 * Texture utility methods.
 * 
 * @author Samuel Johnson (SmashMaster)
 */
public class TexUtil
{
    /**
     * @param baseFormat a base internal format, as in the internalFormat
     *                   argument of the {@code glTexImage2D()} method.
     * @return the number of components in a particular texture format.
     */
    public static int getBands(int baseFormat)
    {
        switch (baseFormat)
        {
            case GL_ALPHA:
            case GL_DEPTH_COMPONENT:
            case GL_RED: return 1;
            
            case GL_DEPTH_STENCIL:
            case GL_RG: return 2;
                
            case GL_RGB: return 3;
            case GL_RGBA: return 4;
            
            default: return -1;
        }
    }
    
    /**
     * Returns the OpenGL format best corresponding with the given image, or -1
     * if none could be found.
     * 
     * @param image The image to get the format of.
     * @return The OpenGL format of the given image, or -1 if none exists.
     */
    public static int getFormat(Image image)
    {
        switch (image.type)
        {
            case BYTE: switch (image.bands)
                {
                    case 1: return GL_R8;
                    case 2: return GL_RG8;
                    case 3: return GL_RGB8;
                    case 4: return GL_RGBA8;
                }
                break;
            case CHAR: switch (image.bands)
                {
                    case 1: return GL_R16;
                    case 2: return GL_RG16;
                    case 3: return GL_RGB16;
                    case 4: return GL_RGBA16;
                }
                break;
            case SHORT: switch (image.bands)
                {
                    case 1: return GL_R16I;
                    case 2: return GL_RG16I;
                    case 3: return GL_RGB16I;
                    case 4: return GL_RGBA16I;
                }
                break;
            case INT: switch (image.bands)
                {
                    case 1: return GL_R32I;
                    case 2: return GL_RG32I;
                    case 3: return GL_RGB32I;
                    case 4: return GL_RGBA32I;
                }
                break;
            case FLOAT: switch (image.bands)
                {
                    case 1: return GL_R32F;
                    case 2: return GL_RG32F;
                    case 3: return GL_RGB32F;
                    case 4: return GL_RGBA32F;
                }
                break;
        }
        
        return -1;
    }
    
    /**
     * @param format an OpenGL texture format.
     * @return the base OpenGL internal texture format corresponding with the
     *         given format.
     */
    public static int getBaseFormat(int format)
    {
        switch (format)
        {
            case GL_DEPTH_COMPONENT:
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32:
            case GL_DEPTH_COMPONENT32F: return GL_DEPTH_COMPONENT;
            
            case GL_RED:
            case GL_R8:
            case GL_R16:
            case GL_R16F:
            case GL_R16I:
            case GL_R32F:
            case GL_R32I: return GL_RED;
            
            case GL_DEPTH_STENCIL:
            case GL_DEPTH24_STENCIL8:
            case GL_DEPTH32F_STENCIL8: return GL_DEPTH_STENCIL;
            
            case GL_RG:
            case GL_RG8:
            case GL_RG16:
            case GL_RG16F:
            case GL_RG16I:
            case GL_RG32F:
            case GL_RG32I: return GL_RG;
            
            case GL_RGB:
            case GL_R3_G3_B2:
            case GL_RGB4:
            case GL_RGB5:
            case GL_RGB8:
            case GL_RGB10:
            case GL_RGB12:
            case GL_RGB16:
            case GL_RGB16F:
            case GL_RGB16I:
            case GL_RGB32F:
            case GL_RGB32I: return GL_RGB;
            
            case GL_RGBA:
            case GL_RGBA2:
            case GL_RGBA4:
            case GL_RGBA8:
            case GL_RGBA12:
            case GL_RGBA16:
            case GL_RGBA16F:
            case GL_RGBA16I:
            case GL_RGBA32F:
            case GL_RGBA32I: return GL_RGBA;
            
            default: return -1;
        }
    }
    
    /**
     * Returns the OpenGL field name for the given format.
     * 
     * @param format An OpenGL texture format.
     * @return The OpenGL field name for the given format.
     */
    public static String formatToString(int format)
    {
        switch (format)
        {
            case GL_DEPTH_COMPONENT: return "GL_DEPTH_COMPONENT";
            case GL_DEPTH_COMPONENT16: return "GL_DEPTH_COMPONENT16";
            case GL_DEPTH_COMPONENT24: return "GL_DEPTH_COMPONENT24";
            case GL_DEPTH_COMPONENT32: return "GL_DEPTH_COMPONENT32";
            case GL_DEPTH_COMPONENT32F: return "GL_DEPTH_COMPONENT32F";
                
            case GL_RED: return "GL_RED";
            case GL_R8: return "GL_R8";
            case GL_R16: return "GL_R16";
            case GL_R16F: return "GL_R16F";
            case GL_R16I: return "GL_R16I";
            case GL_R32F: return "GL_R32F";
            case GL_R32I: return "GL_R32I";
                
            case GL_DEPTH_STENCIL: return "GL_DEPTH_STENCIL";
            case GL_DEPTH24_STENCIL8: return "GL_DEPTH24_STENCIL8";
            case GL_DEPTH32F_STENCIL8: return "GL_DEPTH32F_STENCIL8";
                
            case GL_RG: return "GL_RG";
            case GL_RG8: return "GL_RG8";
            case GL_RG16: return "GL_RG16";
            case GL_RG16F: return "GL_RG16F";
            case GL_RG16I: return "GL_RG16I";
            case GL_RG32F: return "GL_RG32F";
            case GL_RG32I: return "GL_RG32I";
                
            case GL_RGB: return "GL_RGB";
            case GL_RGB8: return "GL_RGB8";
            case GL_RGB16: return "GL_RGB16";
            case GL_RGB16F: return "GL_RGB16F";
            case GL_RGB16I: return "GL_RGB16I";
            case GL_RGB32F: return "GL_RGB32F";
            case GL_RGB32I: return "GL_RGB32I";
                
            case GL_RGBA: return "GL_RGBA";
            case GL_RGBA8: return "GL_RGBA8";
            case GL_RGBA16: return "GL_RGBA16";
            case GL_RGBA16F: return "GL_RGBA16F";
            case GL_RGBA16I: return "GL_RGBA16I";
            case GL_RGBA32F: return "GL_RGBA32F";
            case GL_RGBA32I: return "GL_RGBA32I";
            
            case GL_ALPHA: return "GL_ALPHA";
            
            case EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT: return "GL_COMPRESSED_RGB_S3TC_DXT1_EXT";
            case EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT: return "GL_COMPRESSED_RGBA_S3TC_DXT1_EXT";
            case EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT: return "GL_COMPRESSED_RGBA_S3TC_DXT3_EXT";
            case EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT: return "GL_COMPRESSED_RGBA_S3TC_DXT5_EXT";
                
            default: return "UNSUPPORTED_FORMAT";
        }
    }
    
    /**
     * @param bands the number of color channels for a format.
     * @return the 'best guess' format compatible with the given number of
     *         bands.
     */
    public static int getDefaultFormat(int bands)
    {
        switch (bands)
        {
            case 1: return GL_RED;
            case 2: return GL_RG;
            case 3: return GL_RGB;
            case 4: return GL_RGBA;
                
            default: return -1;
        }
    }
    
    /**
     * Returns the OpenGL enumerator for the given primitive type.
     * 
     * @param format an OpenGL texture format.
     * @return the primitive data type associated with the given OpenGL format.
     */
    public static int getPrimitiveType(int format)
    {
        switch (format)
        {
            default:
            case GL_R8:
            case GL_RG8:
            case GL_RGB8:
            case GL_RGBA8: return GL_UNSIGNED_BYTE;
            
            case GL_DEPTH_COMPONENT16:
            case GL_R16:
            case GL_RG16:
            case GL_RGB16:
            case GL_RGBA16: return GL_UNSIGNED_SHORT;
            
            case GL_R16I:
            case GL_RG16I:
            case GL_RGB16I:
            case GL_RGBA16I: return GL_SHORT;
            
            case GL_R16F:
            case GL_RG16F:
            case GL_RGB16F:
            case GL_RGBA16F: return GL_HALF_FLOAT;
            
            case GL_DEPTH_COMPONENT32F:
            case GL_R32F:
            case GL_RG32F:
            case GL_RGB32F:
            case GL_RGBA32F: return GL_FLOAT;
            
            case GL_R32I:
            case GL_RG32I:
            case GL_RGB32I:
            case GL_RGBA32I: return GL_INT;
            
            case GL_DEPTH24_STENCIL8: return GL_UNSIGNED_INT_24_8;
        }
    }
    
    /**
     * @param format an OpenGL texture format.
     * @return Approximately how many bits are stored per texel for the given format.
     */
    public static long getBits(int format)
    {
        switch (format)
        {
            case GL_DEPTH_COMPONENT: return 24;
            case GL_DEPTH_COMPONENT16: return 16;
            case GL_DEPTH_COMPONENT24: return 24;
            case GL_DEPTH_COMPONENT32:
            case GL_DEPTH_COMPONENT32F: return 32;
            case GL_RED:
            case GL_R8: return 8;
            case GL_R16:
            case GL_R16F:
            case GL_R16I: return 16;
            case GL_R32F:
            case GL_R32I: return 32;
            case GL_DEPTH_STENCIL: return 8;
            case GL_DEPTH24_STENCIL8: return 32;
            case GL_DEPTH32F_STENCIL8: return 40;
                
            case GL_RG:
            case GL_RG8: return 16;
            case GL_RG16:
            case GL_RG16F:
            case GL_RG16I: return 32;
            case GL_RG32F:
            case GL_RG32I: return 64;
                
            case GL_RGB: return 24;
            case GL_R3_G3_B2: return 8;
            case GL_RGB4: return 12;
            case GL_RGB5: return 15;
            case GL_RGB8: return 24;
            case GL_RGB10: return 30;
            case GL_RGB12: return 36;
            case GL_RGB16:
            case GL_RGB16F:
            case GL_RGB16I: return 48;
            case GL_RGB32F:
            case GL_RGB32I: return 96;
                
            case GL_RGBA: return 32;
            case GL_RGBA2: return 8;
            case GL_RGBA4: return 16;
            case GL_RGBA8: return 32;
            case GL_RGBA12: return 48;
            case GL_RGBA16:
            case GL_RGBA16F:
            case GL_RGBA16I: return 64;
            case GL_RGBA32F:
            case GL_RGBA32I: return 128;
                
            default: return -1;
        }
    }
    
    /**
     * @param filter an OpenGL texture minify filter.
     * @return whether or not the given filter is a mipmap filter.
     */
    public static boolean isMipmapFilter(int filter)
    {
        switch (filter)
        {
            case GL_NEAREST_MIPMAP_NEAREST:
            case GL_LINEAR_MIPMAP_NEAREST:
            case GL_NEAREST_MIPMAP_LINEAR:
            case GL_LINEAR_MIPMAP_LINEAR: return true;
                
            default: return false;
        }
    }
    
    /**
     * Returns the OpenGL depth texture format for the given number of bits, or
     * -1 if none could be found.
     * 
     * @param bits The desired number of depth bits.
     * @return An OpenGL depth format.
     */
    public static int getDepthFormat(int bits)
    {
        switch (bits)
        {
            case 16: return GL_DEPTH_COMPONENT16;
            case 24: return GL_DEPTH_COMPONENT24;
            case 32: return GL_DEPTH_COMPONENT32;
            default: return -1;
        }
    }
    
    private TexUtil() {}
}
