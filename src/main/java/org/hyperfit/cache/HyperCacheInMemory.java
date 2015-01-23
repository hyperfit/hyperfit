package org.hyperfit.cache;


import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import org.hyperfit.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Simple in memory cache strategy implementation</p>
 * <p>This implementation uses an internal map to store cached resources</p>
 * <p>This implementation should be avoided in intense production environments
 * where cached resources might exceed the internal memory of the JVM</p>
 *
 */
public class HyperCacheInMemory implements HyperCacheStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(HyperCacheInMemory.class);

    private final long ttlMili;
    private final boolean allowNullValues;
    private final Map<String, CacheWrapper> storage;

    public HyperCacheInMemory(long ttl) {
        this(ttl, false, new HashMap<String, CacheWrapper>());
    }

    public HyperCacheInMemory(long ttl, boolean allowNullValues) {
        this(ttl, allowNullValues, new HashMap<String, CacheWrapper>());
    }

    public HyperCacheInMemory(long ttlMili, boolean allowNullValues, Map<String, CacheWrapper> storage) {
        if (storage == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CACHE_CACHE_MAP_NULL);
        }

        this.ttlMili = ttlMili;
        this.storage = storage;
        this.allowNullValues = allowNullValues;
    }

    // -- INNER HELPER METHODS ----------------
    protected boolean isValid(CacheWrapper cacheWrapper) {
        return cacheWrapper != null && System.currentTimeMillis() < cacheWrapper.futureTimestamp;
    }

    protected <T> T put(String key, T value) {
        if (value != null || allowNullValues) {
            storage.put(key, new CacheWrapper(ttlMili + System.currentTimeMillis(), value));
            LOG.debug(Messages.MSG_DEBUG_CACHE_ITEM_SAVED, key, value);
        }
        return value;
    }

    protected <T> T refresh(String key, Refresh<T> action) {
        try {
            LOG.debug(Messages.MSG_DEBUG_CACHE_ITEM_REFRESH, key);
            return action.execute(key);
        } catch (Exception ex) {
            throw new HyperfitException(ex, Messages.MSG_ERROR_CACHE_ITEM_REFRESH, key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> T get(Class<T> clazz, String key, Refresh<T> action) {

        // Validate required elements.
        if (clazz == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CACHE_CAST_CLASS_NULL);
        }

        if (action == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CACHE_CAST_CLASS_NULL);
        }

        // Logic for empty keys.
        if (StringUtils.isEmpty(key)) {
            LOG.warn(Messages.MSG_WARN_CACHE_FOUND_EMPTY_KEY);
            return refresh(key, action);
        }

        // Logic for cached elements.
        CacheWrapper cacheWrapper = storage.get(key);
        if (isValid(cacheWrapper)) {
            LOG.debug(Messages.MSG_DEBUG_CACHE_ITEM_FOUND, key, clazz);
            return ReflectUtils.cast(clazz, cacheWrapper.cachedElement);
        }

        // Logic for new cache elements.
        return put(key, refresh(key, action));
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String key) {

        if (StringUtils.isEmpty(key)) {
            LOG.warn(Messages.MSG_WARN_CACHE_FOUND_EMPTY_KEY);
        } else {
            storage.remove(key);
            LOG.debug(Messages.MSG_DEBUG_CACHE_ITEM_REMOVED, key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeAll() {
        storage.clear();
        LOG.debug(Messages.MSG_DEBUG_CACHE_ITEMS_REMOVED);
    }

    /**
     * {@inheritDoc}
     */
    public void removeInvalid() {
        List<String> invalidKeys = new ArrayList<String>();
        for (String key : storage.keySet()) {
            if (!isValid(storage.get(key))) {
                invalidKeys.add(key);
            }
        }

        for (String invalidKey : invalidKeys) {
            remove(invalidKey);
        }

        LOG.debug(Messages.MSG_DEBUG_CACHE_ITEMS_REMOVED);
    }

    @ToString
    public static class CacheWrapper {

        private final long futureTimestamp;
        private final Object cachedElement;

        public CacheWrapper(long futureTimestamp, Object cachedElement) {
            this.futureTimestamp = futureTimestamp;
            this.cachedElement = cachedElement;
        }

        public long getFutureTimestamp() {
            return futureTimestamp;
        }

        public Object getCachedElement() {
            return cachedElement;
        }
    }
}
