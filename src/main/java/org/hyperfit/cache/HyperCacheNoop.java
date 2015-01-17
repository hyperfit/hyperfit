package org.hyperfit.cache;

import org.hyperfit.exception.HyperClientException;
import org.hyperfit.message.Messages;

/**
 * <p>No operation implementation of HyperCacheStrategy</p>
 * <p>This implementation is a dummy implementation as it doesn't really store anything in 
 * cache</p>
 * <p>Purpose of this implementation is to have an alternative way for disabling cache in the Hyper Client</p>
 *
 */
public class HyperCacheNoop implements HyperCacheStrategy {

    public HyperCacheNoop() {
    }


    /**
     * {@inheritDoc}
     */
    public <T> T get(Class<T> clazz, String key, Refresh<T> action) {
        if (clazz == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CACHE_CAST_CLASS_NULL);
        }

        if (action == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CACHE_CAST_CLASS_NULL);
        }

        try {
            return action.execute(key);
        } catch (Exception ex) {
            throw new HyperClientException(ex, Messages.MSG_ERROR_CACHE_ITEM_REFRESH, key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String key) {
        // Does nothing since it's a no operation cache. In other words, no cache is stored.
    }

    /**
     * {@inheritDoc}
     */
    public void removeAll() {
        // Does nothing since it's a no operation cache. In other words, no cache is stored.
    }

    /**
     * {@inheritDoc}
     */
    public void removeInvalid() {
        // Does nothing since it's a no operation cache. In other words, no cache is stored.
    }

}
