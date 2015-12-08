package com.samrj.devil.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;

/**
 * Utility methods for data munging.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public final class IOUtil
{
    public static byte[] hexToBytes(String s)
    {
        int len = s.length();
        byte[] data = new byte[len/2];
        for (int i = 0; i < len; i += 2)
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) +
                                 Character.digit(s.charAt(i + 1), 16));
        return data;
    }
    
    public static String readPaddedUTF(DataInputStream in) throws IOException
    {
        if (!in.markSupported()) throw new IOException("Cannot read padded UTF-8 with this stream.");
        in.mark(8);
        int utflen = in.readUnsignedShort() + 2;
        in.reset();
        String out = in.readUTF();
        int padding = (4 - (utflen % 4)) % 4;
        if (in.skip(padding) != padding) throw new IOException("Cannot skip bytes with this stream.");
        return out;
    }
    
    public static <T> T[] arrayFromStream(DataInputStream in, Class<T> type, StreamConstructor<T> constructor) throws IOException
    {
        T[] out = (T[])Array.newInstance(type, in.readInt());
        for (int i=0; i<out.length; i++) out[i] = constructor.construct(in);
        return out;
    }
    
    private IOUtil()
    {
    }
}
