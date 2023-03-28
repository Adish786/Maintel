package com.about.mantle.testing.proctor;

import java.util.Map;

/*
 * Dotdash library which contain utility functions for Proctor rule
 * https://opensource.indeedeng.io/proctor/docs/test-rules/
 */
public class MantleRuleFunctions {
    public static boolean containsKey(final Map<Object, Object> map, final Object key) {
        return map.containsKey(key);
    }

    public static Object get(final Map<Object, Object> map, final Object key) {
        return map.get(key);
    }
}
