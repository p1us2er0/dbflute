/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jflute
 * @param <VALUE> The type of value.
 */
public class StringKeyMap<VALUE> implements Map<String, VALUE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, VALUE> _searchMap;
    protected final Map<String, VALUE> _plainMap; // invalid if concurrent
    protected boolean _removeUnderscore;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected StringKeyMap(boolean removeUnderscore, boolean order, boolean concurrent) {
        if (order && concurrent) {
            String msg = "The 'order' and 'concurrent' should not be both true at the same time!";
            throw new IllegalStateException(msg);
        }
        _removeUnderscore = removeUnderscore;
        if (concurrent) {
            _searchMap = newConcurrentHashMap();
            _plainMap = null; // invalid if concurrent
        } else {
            if (order) {
                _searchMap = newLinkedHashMap();
                _plainMap = newLinkedHashMap();
            } else {
                _searchMap = newHashMap();
                _plainMap = newHashMap();
            }
        }
    }

    /**
     * Create The map of string key as case insensitive. <br />
     * You can set null value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitive() {
        return new StringKeyMap<VALUE>(false, false, false);
    }

    /**
     * Create The map of string key as case insensitive and concurrent. <br />
     * You cannot set null value. And plain keys to be set is NOT kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive and concurrent. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitiveConcurrent() {
        return new StringKeyMap<VALUE>(false, false, true);
    }

    /**
     * Create The map of string key as case insensitive and ordered. <br />
     * You can set null value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive and ordered. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitiveOrdered() {
        return new StringKeyMap<VALUE>(false, true, false);
    }

    /**
     * Create The map of string key as flexible. <br />
     * You can set null value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexible() {
        return new StringKeyMap<VALUE>(true, false, false);
    }

    /**
     * Create The map of string key as flexible and concurrent. <br />
     * You cannot set null value. And plain keys to be set is NOT kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible and concurrent. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexibleConcurrent() {
        return new StringKeyMap<VALUE>(true, false, true);
    }

    /**
     * Create The map of string key as flexible and ordered. <br />
     * You can set null value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible and ordered. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexibleOrdered() {
        return new StringKeyMap<VALUE>(true, true, false);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    // -----------------------------------------------------
    //                                           Key Related
    //                                           -----------
    public VALUE get(Object key) {
        final String stringKey = convertStringKey(key);
        if (stringKey != null) {
            return _searchMap.get(stringKey);
        }
        return null;
    }

    public VALUE put(String key, VALUE value) {
        final String stringKey = convertStringKey(key);
        if (stringKey != null) {
            if (_plainMap != null) {
                final String plainKey;
                if (_searchMap.containsKey(stringKey)) {
                    plainKey = searchPlainKey(stringKey);
                } else {
                    plainKey = key;
                }
                _plainMap.put(plainKey, value);
            }
            return _searchMap.put(stringKey, value);
        }
        return null;
    }

    public VALUE remove(Object key) {
        final String stringKey = convertStringKey(key);
        if (stringKey != null) {
            if (_plainMap != null) {
                final Object plainKey;
                if (_searchMap.containsKey(stringKey)) {
                    plainKey = searchPlainKey(stringKey);
                } else {
                    plainKey = key;
                }
                _plainMap.remove(plainKey);
            }
            return _searchMap.remove(stringKey);
        }
        return null;
    }

    protected String searchPlainKey(String stringKey) {
        final Set<String> keySet = _plainMap.keySet();
        String plainKey = null;
        for (String currentKey : keySet) {
            if (stringKey.equals(convertStringKey(currentKey))) {
                plainKey = currentKey;
                break;
            }
        }
        if (plainKey == null) {
            String msg = "The plain map should have the key:";
            msg = msg + " stringKey=" + stringKey;
            msg = msg + " plainMap=" + _plainMap;
            throw new IllegalStateException(msg);
        }
        return plainKey;
    }

    public final void putAll(Map<? extends String, ? extends VALUE> map) {
        final Set<?> entrySet = map.entrySet();
        for (Object entryObj : entrySet) {
            @SuppressWarnings("unchecked")
            final Entry<String, VALUE> entry = (Entry<String, VALUE>) entryObj;
            put(entry.getKey(), entry.getValue());
        }
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    // -----------------------------------------------------
    //                                              Delegate
    //                                              --------
    public void clear() {
        if (_plainMap != null) {
            _plainMap.clear();
        }
        _searchMap.clear();
    }

    public int size() {
        return _searchMap.size();
    }

    public boolean isEmpty() {
        return _searchMap.isEmpty();
    }

    public Set<String> keySet() {
        if (_plainMap != null) {
            return _plainMap.keySet();
        }
        return _searchMap.keySet();
    }

    public Collection<VALUE> values() {
        return _searchMap.values();
    }

    public boolean containsValue(Object obj) {
        return _searchMap.containsValue(obj);
    }

    public Set<Entry<String, VALUE>> entrySet() {
        if (_plainMap != null) {
            return _plainMap.entrySet();
        }
        return _searchMap.entrySet();
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    protected String convertStringKey(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return toLowerCase(removeUnderscore((String) key));
    }

    protected String removeUnderscore(String value) {
        if (_removeUnderscore) {
            return replace(value, "_", "");
        }
        return value;
    }

    protected String toLowerCase(String value) {
        return value.toLowerCase();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                break;
            }
        }
        return sb.toString();
    }

    protected static <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    protected static <KEY, VALUE> HashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected static <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringKeyMap<?>) {
            return _searchMap.equals(((StringKeyMap<?>) obj)._searchMap);
        } else if (obj instanceof Map<?, ?>) {
            return _searchMap.equals(obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _searchMap.hashCode();
    }

    @Override
    public String toString() {
        if (_plainMap != null) {
            return _plainMap.toString();
        }
        return _searchMap.toString();
    }
}