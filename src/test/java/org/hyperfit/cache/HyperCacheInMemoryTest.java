package org.hyperfit.cache;

import org.hyperfit.cache.HyperCacheInMemory.CacheWrapper;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HyperCacheInMemoryTest {

    private final static long TEST_TTL = 300000;
    private final static String KEY_1 = "test_cache_item_1";
    private final static String KEY_2 = "test_cache_item_2";
    private final static String VALUE_1 = "test cache item_value_1";
    private final static String VALUE_2 = "test cache item_value_2";

    private HyperCacheInMemory hyperCacheInMemory;
    private Map<String, CacheWrapper> cacheMap;


    @Before
    public void setUp() {
        cacheMap = new HashMap<String, CacheWrapper>();
        hyperCacheInMemory = new HyperCacheInMemory(TEST_TTL, false, cacheMap);
    }

    private CacheWrapper getInvalid(Object value) {
        return new CacheWrapper(-System.currentTimeMillis(), value);
    }

    private CacheWrapper getValid(Object value) {
        return new CacheWrapper(100000000l + System.currentTimeMillis(), value);
    }

    private void putValid(String key, Object value) {
        cacheMap.put(key, getValid(value));
    }

    private void putInvalid(String key, Object value) {
        cacheMap.put(key, getInvalid(value));
    }

    private boolean isValidInCache(String key) {
        CacheWrapper cacheWrapper = cacheMap.get(key);
        return cacheWrapper != null && System.currentTimeMillis() < cacheWrapper.getFutureTimestamp();
    }

    @Test
    public void getValidTTL() {
        putValid(KEY_1, VALUE_1);
        assertTrue(isValidInCache(KEY_1));
        assertEquals(VALUE_1, hyperCacheInMemory.get(
                String.class, KEY_1, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return null;
                    }
                }));
        assertTrue(isValidInCache(KEY_1));
    }

    @Test
    public void getInvalidTTL() {
        putInvalid(KEY_1, VALUE_1);
        assertFalse(isValidInCache(KEY_1));
        assertEquals(VALUE_2, hyperCacheInMemory.get(
                String.class, KEY_1, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_2;
                    }
                }));
        assertTrue(isValidInCache(KEY_1));
    }

    @Test
    public void getEmptyKey() {
        assertFalse(isValidInCache(KEY_1));

        assertEquals(VALUE_1, hyperCacheInMemory.get(
                String.class, "", new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_1;
                    }
                }));
        assertFalse(isValidInCache(KEY_1));

        assertEquals(VALUE_1, hyperCacheInMemory.get(
                String.class, null, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_1;
                    }
                }));
        assertFalse(isValidInCache(KEY_1));
    }

    @Test(expected = NullPointerException.class)
    public void getNullClazz() {
        hyperCacheInMemory.get(null, KEY_1, new HyperCacheStrategy.Refresh<Object>() {

            public Object execute(String key) {
                return VALUE_1;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void getNullRefresh() {
        hyperCacheInMemory.get(String.class, KEY_1, null);
    }

    @Test(expected = NullPointerException.class)
    public void getNullCacheMap() {
        HyperCacheInMemory hyperCacheInMemory = new HyperCacheInMemory(0, true, null);
    }

    public void getNotNullValues() {
        assertEquals(VALUE_1, hyperCacheInMemory.get(
                String.class, KEY_1, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return null;
                    }
                }));
        assertFalse(isValidInCache(KEY_1));
    }

    @Test
    public void remove() {
        putValid(KEY_1, VALUE_1);
        assertTrue(isValidInCache(KEY_1));
        hyperCacheInMemory.remove(KEY_1);
        assertFalse(isValidInCache(KEY_1));
    }

    @Test
    public void removeEmptyKey() {
        putValid(KEY_1, VALUE_1);
        assertTrue(isValidInCache(KEY_1));
        hyperCacheInMemory.remove("");
        assertTrue(isValidInCache(KEY_1));
        hyperCacheInMemory.remove(null);
        assertTrue(isValidInCache(KEY_1));
    }

    @Test
    public void removeAll() {
        putValid(KEY_1, VALUE_1);
        putValid(KEY_2, VALUE_2);
        assertTrue(isValidInCache(KEY_1));
        assertTrue(isValidInCache(KEY_2));

        hyperCacheInMemory.removeAll();
        assertFalse(isValidInCache(KEY_1));
        assertFalse(isValidInCache(KEY_1));
    }

    @Test
    public void removeInvalid() {
        putValid(KEY_1, VALUE_1);
        putInvalid(KEY_2, VALUE_2);
        assertTrue(isValidInCache(KEY_1));
        assertFalse(isValidInCache(KEY_2));

        hyperCacheInMemory.removeInvalid();
        assertTrue(isValidInCache(KEY_1));
        assertFalse(isValidInCache(KEY_2));
    }

}
