package Elf32;

public class Field {
    private final String name;
    private final int offset;
    private final int size;
    private int value;

    public Field(String name, int offset, int size) {
        this.name = name;
        this.offset = offset;
        this.size = size;
    }

    public Field(String name, int offset, int size, int value) {
        this(name, offset, size);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
