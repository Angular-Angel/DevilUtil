package com.samrj.devil.ui;

import com.samrj.devil.gl.DGL;
import com.samrj.devil.gl.Image;
import com.samrj.devil.gl.Texture2D;
import com.samrj.devil.io.LittleEndianInputStream;
import com.samrj.devil.math.Util;
import com.samrj.devil.math.Vec2;
import com.samrj.devil.res.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

/**
 * Bitmap font class for loading packed fonts generated by BMFont. The font data
 * file is the binary format. See BMFont's documentation for more details.
 * 
 * http://www.angelcode.com/products/bmfont/
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class AtlasFont
{
    private static final String MSG_ERROR_FORMAT = "Illegal file format specified.";
    
    private static void ensureByte(InputStream in, int b, String message) throws IOException
    {
        if (in.read() != b) throw new IOException(message);
    }
    
    private static void skip(InputStream in, int bytes) throws IOException
    {
        if (in.skip(bytes) != bytes) throw new IOException("Failed to skip bytes.");
    }
    
    private final String name;
    private final int lineHeight, baseHeight;
    private final Texture2D texture;
    private final Char[] chars;
    private final int firstCharID;
    
    public AtlasFont(String directory, String fontFile) throws IOException
    {
        if (!directory.endsWith("/")) directory += "/";
        
        InputStream inputStream = Resource.open(directory + fontFile);
        LittleEndianInputStream in = new LittleEndianInputStream(inputStream);
        
        //HEADER
        ensureByte(in, 66, MSG_ERROR_FORMAT);
        ensureByte(in, 77, MSG_ERROR_FORMAT);
        ensureByte(in, 70, MSG_ERROR_FORMAT);
        ensureByte(in, 3, MSG_ERROR_FORMAT);
        
        //INFO BLOCK
        ensureByte(in, 1, "Expected info block first.");
        skip(in, 18); //Skip rest of info block
        name = in.readNullTermStr();
        
        //COMMON BLOCK
        ensureByte(in, 2, "Expected common block second.");
        skip(in, 4); //Skip info block size
        lineHeight = in.readLittleUnsignedShort();
        baseHeight = lineHeight - in.readLittleUnsignedShort();
        skip(in, 4);
        int pages = in.readLittleUnsignedShort();
        if (pages != 1) throw new IOException("Only one page texture supported.");
        if ((in.read() & 128) != 0) throw new IOException("Channel-packed fonts not supported.");
        ensureByte(in, 0, "Alpha channel must contain glyph data.");
        skip(in, 3);
        
        //PAGES BLOCK
        ensureByte(in, 3, "Expected pages block third.");
        skip(in, 4);
        String texFile = in.readNullTermStr();
        
        Image image = DGL.loadImage(directory + texFile);
        if (!Util.isPower2(image.width) || !Util.isPower2(image.height))
            throw new IOException("Texture dimensions must be powers of two.");
        if (image.bands != 1) throw new IOException("Texture format must have one band.");
        
        texture = DGL.genTex2D();
        texture.image(image, GL11.GL_ALPHA8);
        DGL.delete(image);
        
        //CHARS BLOCK
        ensureByte(in, 4, "Expected chars block fourth.");
        int numChars = in.readLittleInt()/20;
        
        List<Char> charList = new ArrayList<>(numChars);
        int minChar = Integer.MAX_VALUE, maxChar = -1;
        for (int i=0; i<numChars; i++)
        {
            Char c = new Char(in, texture.getWidth(), texture.getHeight());
            charList.add(c);
            if (c.id < minChar) minChar = c.id;
            if (c.id > maxChar) maxChar = c.id;
        }
        
        chars = new Char[maxChar - minChar + 1];
        for (Char c : charList) chars[c.id - minChar] = c;
        firstCharID = minChar;
        
        in.close();
    }
    
    public String getName()
    {
        return name;
    }
    
    private Char getChar(char c)
    {
        if (c < firstCharID) return null;
        
        int i = c - firstCharID;
        return i < chars.length ? chars[i] : null;
    }
    
    public float getWidth(char c)
    {
        Char ch = getChar(c);
        return ch != null ? ch.xAdvance : 0.0f;
    }
    
    public float getWidth(String text)
    {
        float out = 0.0f;
        for (int i=0; i<text.length(); i++) out += getWidth(text.charAt(i));
        return out;
    }
    
    public float getHeight()
    {
        return lineHeight;
    }
    
    public void draw(String text, Vec2 pos, Vec2 align)
    {
        pos = new Vec2(pos.x, pos.y - lineHeight + baseHeight);
        align = new Vec2(align.x - 1.0f, -align.y - 1.0f).mult(0.5f);
        align.x *= getWidth(text);
        align.y *= baseHeight-lineHeight;
        pos.add(align);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glTranslatef(Math.round(pos.x), Math.round(pos.y), 0.0f);

        float x = 0f;
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        for (int i=0; i<text.length(); i++)
        {
            Char c = getChar(text.charAt(i));
            if (c == null) continue;
            
            float lf = x + c.xOffset;
            float rt = lf + c.width;
            float bt = c.yOffset;
            float tp = bt + c.height;
            
            GL11.glTexCoord2f(c.tx0, c.ty0); GL11.glVertex2f(lf, bt);
            GL11.glTexCoord2f(c.tx0, c.ty1); GL11.glVertex2f(lf, tp);
            GL11.glTexCoord2f(c.tx1, c.ty1); GL11.glVertex2f(rt, tp);
            GL11.glTexCoord2f(c.tx1, c.ty0); GL11.glVertex2f(rt, bt);

            x += c.xAdvance;
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }
    
    public void draw(String text, Vec2 p, Alignment align)
    {
        draw(text, p, align.dir());
    }
    
    public void draw(String text, Vec2 p)
    {
        draw(text, p, Alignment.NE);
    }
    
    public void delete()
    {
        DGL.delete(texture);
    }
    
    private class Char
    {
        private final int id;
        private final float width, height;
        private final float tx0, tx1, ty0, ty1;
        private final float xOffset;
        private final float yOffset;
        private final float xAdvance;
        
        private Char(LittleEndianInputStream in, float texWidth, float texHeight) throws IOException
        {
            id = in.readLittleInt();
            float x = in.readLittleUnsignedShort();
            float y = in.readLittleUnsignedShort();
            width = in.readLittleUnsignedShort();
            height = in.readLittleUnsignedShort();
            xOffset = in.readLittleUnsignedShort();
            yOffset = lineHeight - baseHeight - height - in.readLittleUnsignedShort();
            xAdvance = in.readLittleUnsignedShort();
            skip(in, 2);
            
            tx0 = x/texWidth;
            tx1 = (x + width)/texHeight;
            
            ty0 = 1.0f - (y + height)/texWidth;
            ty1 = 1.0f - y/texWidth;
        }
    }
}
