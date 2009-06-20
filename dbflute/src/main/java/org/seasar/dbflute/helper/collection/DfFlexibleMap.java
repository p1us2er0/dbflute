/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jflute
 */
public class DfFlexibleMap<KEY, VALUE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<KEY> _keyList = new ArrayList<KEY>();
    protected LinkedHashMap<KEY, VALUE> _internalMap = new LinkedHashMap<KEY, VALUE>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFlexibleMap() {
    }

    public DfFlexibleMap(Map<KEY, ? extends VALUE> map) {
        putAll(map);
    }

    public DfFlexibleMap(List<KEY> keyList, List<VALUE> valueList) {
        putAll(keyList, valueList);
    }

    // ===================================================================================
    //                                                                       List Handling
    //                                                                       =============
    public KEY getKey(int i) {
        return _keyList.get(i);
    }

    public VALUE getValue(int i) {
        final KEY key = getKey(i);
        return get(key);
    }

    public VALUE remove(int i) {
        final KEY key = getKey(i);
        return remove(key);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    public VALUE get(KEY key) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            return (VALUE) _internalMap.get(stringKey);
        } else {
            return (VALUE) _internalMap.get(key);
        }
    }

    public VALUE put(KEY key, VALUE value) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            key = stringKey;
        }
        _keyList.add(key);
        return _internalMap.put(key, value);
    }

    public final void putAll(Map<KEY, ? extends VALUE> map) {
        Set<?> entrySet = map.entrySet();
        for (Object object : entrySet) {
            @SuppressWarnings("unchecked")
            java.util.Map.Entry<KEY, ? extends VALUE> entry = (java.util.Map.Entry<KEY, ? extends VALUE>) object;
            KEY key = entry.getKey();
            VALUE value = entry.getValue();
            put(key, value);
        }
    }

    public final void putAll(List<KEY> keyList, List<VALUE> valueList) {
        if (keyList.size() != valueList.size()) {
            String msg = "The keyList and valueList should have the same size:";
            msg = msg + " keyList.size()=" + keyList.size() + " valueList.size()=" + valueList.size();
            msg = msg + " keyList=" + keyList + " valueList=" + valueList;
            throw new IllegalStateException(msg);
        }
        int index = 0;
        for (KEY key : keyList) {
            put(key, valueList.get(index));
            ++index;
        }
    }

    public VALUE remove(KEY key) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            key = stringKey;
        }
        _keyList.remove(key);
        return _internalMap.remove(key);
    }

    public boolean containsKey(KEY key) {
        return get(key) != null;
    }

    public void clear() {
        _keyList.clear();
        _internalMap.clear();
    }

    public int size() {
        return _internalMap.size();
    }

    public boolean isEmpty() {
        return _internalMap.isEmpty();
    }

    public Set<KEY> keySet() {
        return _internalMap.keySet();
    }

    public Collection<VALUE> values() {
        return _internalMap.values();
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    @SuppressWarnings("unchecked")
    protected KEY convertStringKey(KEY key) {
        if (!(key instanceof String)) {
            return null;
        }
        return (KEY) toLowerCaseKey(removeUnderscore((String) key));
    }

    protected String removeUnderscore(String key) {
        return replace((String) key, "_", "");
    }

    protected String toLowerCaseKey(String key) {
        return ((String) key).toLowerCase();
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

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _internalMap.toString();
    }
}