package org.seasar.dbflute.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/20 Saturday)
 */
public class StringKeyMapTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_put_null() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> mapAsFlexible = StringKeyMap.createAsFlexible();
        StringKeyMap<Object> mapAsFlexibleConcurrent = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        mapAsFlexible.put("aaa", null);
        try {
            mapAsFlexibleConcurrent.put("aaa", null);
            fail();
        } catch (NullPointerException e) {
            // OK
        }

        // ## Assert ##
        assertEquals(null, mapAsFlexibleConcurrent.get("aaa"));
    }

    public void test_putAll() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();
        LinkedHashMap<String, Integer> resourceMap = new LinkedHashMap<String, Integer>();
        resourceMap.put("aaa", 1);
        resourceMap.put("bbb", 2);
        resourceMap.put("ccc", 3);

        // ## Act ##
        map.putAll(resourceMap);

        // ## Assert ##
        assertEquals(1, map.get("aaa"));
        assertEquals(2, map.get("bbb"));
        assertEquals(3, map.get("ccc"));
        assertEquals(3, map.size());
    }

    public void test_equals_all_same() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map1 = StringKeyMap.createAsCaseInsensitive();
        map1.put("aaa", 1);
        map1.put("bbb", 2);
        map1.put("ccc", 3);
        StringKeyMap<Object> map2 = StringKeyMap.createAsFlexible();
        map2.put("aaa", 1);
        map2.put("bbb", 2);
        map2.put("ccc", 3);

        // ## Act ##
        boolean actual = map1.equals(map2);

        // ## Assert ##
        assertTrue(actual);
    }

    public void test_equals_searchMap_same() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map1 = StringKeyMap.createAsCaseInsensitive();
        map1.put("aaa", 1);
        map1.put("bbb", 2);
        map1.put("ccc", 3);
        StringKeyMap<Object> map2 = StringKeyMap.createAsFlexible();
        map2.put("aa_a", 1);
        map2.put("bbb", 2);
        map2.put("ccc", 3);

        // ## Act ##
        boolean actual = map1.equals(map2);

        // ## Assert ##
        assertTrue(actual);
    }

    public void test_equals_searchMap_diff() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map1 = StringKeyMap.createAsCaseInsensitive();
        map1.put("aaa", 1);
        map1.put("bbb", 2);
        map1.put("ccc", 3);
        StringKeyMap<Object> map2 = StringKeyMap.createAsCaseInsensitive();
        map2.put("aa_a", 1);
        map2.put("bbb", 2);
        map2.put("ccc", 3);

        // ## Act ##
        boolean actual = map1.equals(map2);

        // ## Assert ##
        assertFalse(actual);
    }

    // ===================================================================================
    //                                                                    Case Insensitive
    //                                                                    ================
    // -----------------------------------------------------
    //                                                Normal
    //                                                ------
    public void test_createAsCaseInsensitive() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsCaseInsensitive_plainKey_kept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertTrue(list.contains("AaA"));
        assertTrue(list.contains("Bbb"));
        assertTrue(list.contains("C_cc"));
    }

    public void test_createAsCaseInsensitive_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("aaa", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("AaA"));
        assertTrue(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("aaa"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsCaseInsensitive_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("aaa");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("AaA"));
        assertEquals(2, map.keySet().size());
    }

    // -----------------------------------------------------
    //                                            Concurrent
    //                                            ----------
    public void test_createAsCaseInsensitiveConcurrent() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveConcurrent();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsCaseInsensitiveConcurrent_plainKey_notKept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveConcurrent();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertTrue(list.contains("aaa"));
        assertTrue(list.contains("bbb"));
        assertTrue(list.contains("c_cc"));
    }

    public void test_createAsCaseInsensitiveConcurrent_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveConcurrent();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("c_cc", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("C_cc"));
        assertFalse(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertTrue(set.contains("c_cc"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsCaseInsensitiveConcurrent_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveConcurrent();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("c_cc");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("C_cc"));
        assertFalse(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("ccc"));
        assertEquals(2, map.keySet().size());
    }

    // -----------------------------------------------------
    //                                               Ordered
    //                                               -------
    public void test_createAsCaseInsensitiveOrdered() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveOrdered();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
    }

    public void test_createAsCaseInsensitiveOrdered_plainKey_kept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveOrdered();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertEquals("AaA", list.get(0));
        assertEquals("Bbb", list.get(1));
        assertEquals("C_cc", list.get(2));
    }

    public void test_createAsCaseInsensitiveOrdered_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveOrdered();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("aaa", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("AaA"));
        assertTrue(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("aaa"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsCaseInsensitiveOrdered_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveOrdered();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("aaa");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("AaA"));
        assertEquals(2, map.keySet().size());
    }

    // ===================================================================================
    //                                                                            Flexible
    //                                                                            ========
    // -----------------------------------------------------
    //                                                Normal
    //                                                ------
    public void test_createAsFlexible() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexible();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsFlexible_plainKey_kept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexible();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertTrue(list.contains("AaA"));
        assertTrue(list.contains("Bbb"));
        assertTrue(list.contains("C_cc"));
    }

    public void test_createAsFlexible_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexible();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("ccc", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("C_cc"));
        assertTrue(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("ccc"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsFlexible_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexible();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("ccc");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("C_cc"));
        assertEquals(2, map.keySet().size());
    }

    // -----------------------------------------------------
    //                                            Concurrent
    //                                            ----------
    public void test_createAsFlexibleConcurrent() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsFlexibleConcurrent_plainKey_notKept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertTrue(list.contains("aaa"));
        assertTrue(list.contains("bbb"));
        assertTrue(list.contains("ccc"));
    }

    public void test_createAsFlexibleConcurrent_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleConcurrent();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("ccc", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("C_cc"));
        assertTrue(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertTrue(set.contains("ccc"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsFlexibleConcurrent_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleConcurrent();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("ccc");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("C_cc"));
        assertFalse(map.containsKey("ccc"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("ccc"));
        assertEquals(2, map.keySet().size());
    }

    // -----------------------------------------------------
    //                                               Ordered
    //                                               -------
    public void test_createAsFlexibleOrdered() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleOrdered();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
    }

    public void test_createAsFlexibleOrdered_plainKey_kept() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleOrdered();

        // ## Act ##
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Assert ##
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        log(list);
        assertEquals("AaA", list.get(0));
        assertEquals("Bbb", list.get(1));
        assertEquals("C_cc", list.get(2));
    }

    public void test_createAsFlexibleOrdered_put() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleOrdered();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.put("aaa", 9);

        // ## Assert ##
        assertEquals(3, map.size());
        assertTrue(map.containsKey("AaA"));
        assertTrue(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("aaa"));
        assertEquals(3, map.keySet().size());
    }

    public void test_createAsFlexibleOrdered_remove() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleOrdered();
        map.put("AaA", 1);
        map.put("Bbb", 2);
        map.put("C_cc", 3);

        // ## Act ##
        map.remove("aaa");

        // ## Assert ##
        assertEquals(2, map.size());
        assertFalse(map.containsKey("aaa"));
        Set<String> set = map.keySet();
        log(set);
        assertFalse(set.contains("AaA"));
        assertEquals(2, map.keySet().size());
    }
}
