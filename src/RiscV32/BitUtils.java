package RiscV32;

public final class BitUtils {
    // Read bits from x in range [l...r]
    public static int subint(int x, int l, int r) {
        int result = 0;
        for (int i = l; i <= r; i++) {
            result |= (x & (1 << i)) >>> l;
        }
        return result;
    }

    public static int signExtend(int x, int n, int m) {
        x <<= (m - n);
        x >>= (m - n);
        return x;
    }
}
