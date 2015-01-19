package org.hyperfit.cache;


import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HyperCacheNoopTest{

    private final static String KEY_1 = "test_cache_item_1";
    private final static String KEY_2 = "test_cache_item_2";
    private final static String VALUE_1 = "test cache item_value_1";
    private final static String VALUE_2 = "test cache item_value_2";

    private HyperCacheNoop hyperCacheNoop;


    @Before
    public void setUp() {
        hyperCacheNoop = new HyperCacheNoop();
    }

    @Test
    public void get() {
        assertEquals(VALUE_1, hyperCacheNoop.get(
                String.class, KEY_1, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_1;
                    }
                }));
        assertEquals(null, hyperCacheNoop.get(
                String.class, KEY_1, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return null;
                    }
                }));
    }

    @Test
    public void getEmptyKey() {
        assertEquals(VALUE_1, hyperCacheNoop.get(
                String.class, "", new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_1;
                    }
                }));
        assertEquals(VALUE_1, hyperCacheNoop.get(
                String.class, null, new HyperCacheStrategy.Refresh<String>() {

                    public String execute(String key) {
                        return VALUE_1;
                    }
                }));
    }

    @Test(expected = NullPointerException.class)
    public void getNullClazz() {
        hyperCacheNoop.get(null, KEY_1, new HyperCacheStrategy.Refresh<Object>() {

            public Object execute(String key) {
                return VALUE_1;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void getNullRefresh() {
        hyperCacheNoop.get(String.class, KEY_1, null);
    }

}
