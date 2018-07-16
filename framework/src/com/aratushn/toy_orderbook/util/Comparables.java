package com.aratushn.toy_orderbook.util;

import javax.annotation.Nullable;

public enum Comparables {
    ;
    @Nullable
    public static <T extends Comparable<? super T>> T max(@Nullable T a, @Nullable T b) {
        if (a != null && b != null) {
            if (a.compareTo(b) > 0) {
                return a;
            } else {
                return b;
            }
        } else if (a != null) {
            return a;
        } else {
            return b;
        }
    }
}
