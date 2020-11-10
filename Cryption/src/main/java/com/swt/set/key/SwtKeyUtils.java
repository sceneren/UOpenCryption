package com.swt.set.key;

public class SwtKeyUtils {
    static {
        System.loadLibrary("KeyLib");
    }

    public static native String getSM2PublicKey(Boolean debugModel);
}
