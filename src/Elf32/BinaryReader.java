package Elf32;

import java.util.List;
import java.util.function.Function;

public class BinaryReader {
    private final byte[] data;

    public BinaryReader(final byte[] data) {
        this.data = data;
    }

    public BinaryReader(final Byte[] data) {
        this.data = new byte[data.length];
        for (int i = 0; i < size(); i++) {
            this.data[i] = data[i];
        }
    }

    public BinaryReader(List<Byte> data) {
        this(data.toArray(data.toArray(new Byte[0])));
    }

    public int size() {
        return data.length;
    }

    public byte readByte(int i) {
        return data[i];
    }

    private IllegalArgumentException tooWideError(int w, String type) {
        return new IllegalArgumentException("Attempting to read " + w + " bytes into " + type);
    }

    private <T> T readByteToT(int i, Function<Byte, T> f) {
        return f.apply(readByte(i));
    }

    // Read bytes [start; start + size - 1] to short in low endian notation
    public short readShort(int start, int size) {
        if (size > 2) {
            throw tooWideError(size, "short");
        }
        short result = 0;
        for (int i = start; i < start + size; i++) {
            result |= readByteToT(i, x -> (short) Byte.toUnsignedInt(x)) << (8 * (i - start));
        }
        return result;
    }

    // Read bytes [start; start + size - 1] to int in low endian notation
    public int readInt(int start, int size) {
        if (size > 4) {
            throw tooWideError(size, "int");
        }
        int result = 0;
        for (int i = start; i < start + size; i++) {
            result |= readByteToT(i, Byte::toUnsignedInt) << (8 * (i - start));
        }
        return result;
    }

    // Read bytes [start; start + size - 1] to long in low endian notation
    public long readLong(int start, int size) {
        if (size > 8) {
            throw tooWideError(size, "long");
        }
        long result = 0;
        for (int i = start; i < start + size; i++) {
            result |= readByteToT(i, Byte::toUnsignedLong) << (8 * (i - start));
        }
        return result;
    }
}
