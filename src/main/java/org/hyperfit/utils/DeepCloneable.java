package org.hyperfit.utils;

/**
 * Simple interface for deep cloning objects. The idea is also to
 * avoid forcing serialization just for the deep cloning.
 * Note: Using Cloneable is not recommended.
 *
 * @author Carlos Perez
 */
public interface DeepCloneable {
    public Object deepClone();
}
