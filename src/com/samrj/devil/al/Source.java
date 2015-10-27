package com.samrj.devil.al;

import com.samrj.devil.math.Vec3;
import org.lwjgl.openal.AL10;

/**
 * An OpenAL sound source.
 * 
 * @author Samuel Johnson (SmashMaster)
 * @copyright 2015 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public class Source extends DALObj
{
    final int id;
    
    Source()
    {
        id = AL10.alGenSources();
    }
    
    public void setSound(Sound sound)
    {
        AL10.alSourcei(id, AL10.AL_BUFFER, sound.id);
    }
    
    public void setPitch(float f)
    {
        AL10.alSourcef(id, AL10.AL_PITCH, f);
    }
    
    public void setGain(float f)
    {
        AL10.alSourcef(id, AL10.AL_GAIN, f);
    }
    
    public void setPos(Vec3 v)
    {
        AL10.alSource3f(id, AL10.AL_POSITION, v.x, v.y, v.z);
    }
    
    public void setVel(Vec3 v)
    {
        AL10.alSource3f(id, AL10.AL_VELOCITY, v.x, v.y, v.z);
    }
    
    public void play()
    {
        AL10.alSourcePlay(id);
    }
    
    public void pause()
    {
        AL10.alSourcePause(id);
    }
    
    public void stop()
    {
        AL10.alSourceStop(id);
    }
    
    @Override
    void delete()
    {
        AL10.alDeleteSources(id);
    }
}