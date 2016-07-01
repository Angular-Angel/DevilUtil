package com.samrj.devil.model;

import com.samrj.devil.io.IOUtil;
import com.samrj.devil.math.Mat4;
import com.samrj.devil.math.Transform;
import com.samrj.devil.model.constraint.IKConstraint.IKDefinition;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Samuel Johnson (SmashMaster)
 * @param <DATA_TYPE> The type of datablock this ModelObject encapsulates.
 * @copyright 2016 Samuel Johnson
 * @license https://github.com/SmashMaster/DevilUtil/blob/master/LICENSE
 */
public final class ModelObject<DATA_TYPE extends DataBlock> extends DataBlock
{
    public enum EmptyType
    {
        AXES, CUBE, SPHERE;
    }
    
    public final Map<String, String> arguments;
    public final Transform transform;
    public final List<String> vertexGroups;
    public final Pose pose;
    public final List<IKDefinition> ikConstraints;
    public final DataPointer<DATA_TYPE> data;
    public final DataPointer<ModelObject<?>> parent;
    public final String parentBoneName;
    public final Mat4 parentMatrix;
    public final DataPointer<Action> action;
    public final EmptyType emptyType;
    
    ModelObject(Model model, DataInputStream in) throws IOException
    {
        super(model, in);
        
        int numArguments = in.readInt();
        Map<String, String> argMap = new HashMap<>();
        for (int i=0; i<numArguments; i++) argMap.put(IOUtil.readPaddedUTF(in), IOUtil.readPaddedUTF(in));
        arguments = Collections.unmodifiableMap(argMap);
        
        int dataType = in.readInt();
        int dataLibIndex = dataType >= 0 ? in.readInt() : -1;
        int dataIndex = dataType >= 0 && dataLibIndex < 0 ? in.readInt() : -1;
        if (dataLibIndex >= 0) IOUtil.readPaddedUTF(in); //Data library name
        data = new DataPointer(model, dataType, dataIndex);
        int parentIndex = in.readInt();
        parent = new DataPointer<>(model, Type.OBJECT, parentIndex);
        parentMatrix = parentIndex >= 0 ? new Mat4(in) : null;
        parentBoneName = (parentIndex >= 0 && in.readInt() != 0) ? IOUtil.readPaddedUTF(in) : null;
        
        transform = new Transform(in);
        vertexGroups = IOUtil.listFromStream(in, IOUtil::readPaddedUTF);
        boolean hasPose = in.readInt() != 0;
        if (hasPose)
        {
            pose = new Pose(in);
            ikConstraints = IOUtil.listFromStream(in, IKDefinition::new);
        }
        else
        {
            pose = null;
            ikConstraints = Collections.emptyList();
        }
        
        action = new DataPointer<>(model, Type.ACTION, in.readInt());
        
        int emptyTypeID = in.readInt();
        emptyType = emptyTypeID >= 0 ? EmptyType.values()[emptyTypeID] : null;
    }
    
    public <T extends DataBlock> ModelObject<T> asType(Class<T> typeClass)
    {
        return typeClass.isInstance(data.get()) ? (ModelObject<T>)this : null;
    }
    
    public <T extends DataBlock> Optional<ModelObject<T>> optionalType(Class<T> typeClass)
    {
        return Optional.ofNullable(asType(typeClass));
    }
    
    public void applyParentTransform(Transform result)
    {
        ModelObject<?> parentObj = parent.get();
        if (parentObj != null)
        {
            result.mult(parentObj.transform);
            parentObj.applyParentTransform(result);
        }
    }
    
    public void getParentedTransform(Transform result)
    {
        result.set(transform);
        applyParentTransform(result);
    }
    
    public Transform getParentedTransform()
    {
        Transform out = new Transform();
        getParentedTransform(out);
        return out;
    }
}
