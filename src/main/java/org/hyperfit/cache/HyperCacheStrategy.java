package org.hyperfit.cache;

/**
 * <p>Interface that defines a cache strategy for storing resources obtained from hypermedia API</p>
 *
 */
public interface HyperCacheStrategy {

    /**
     * Gets a resource from cache
     * @param clazz resource class
     * @param key resource cache key
     * @param action refresh action for new elements
     * @return cache resource
     */
    <T> T get(Class<T> clazz, String key, Refresh<T> action);

    /**
     * Removes a resource from cache
     * @param key resource cache identifier
     */
    void remove(String key);

    /**
     * Removes all elements from cache
     */
    void removeAll();

    /**
     * Removes invalid resources. 
     * Those elements with expired TTL (time to live) values
     */
    void removeInvalid();

    /**
     * Refresh interface used for refreshing (or adding new) items in the cache.
     */
    interface Refresh<T> {

        /**
         * Obtains the fresh resource to be cached
         * @param key resource unique identifier
         * @return resource
         */
        public T execute(String key);
    }
}