package Elf32;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStruct {
    private final int offset;
    private final Map<String, Field> fields;
    protected final BinaryReader reader;

    public BinaryReader getReader() {
        return reader;
    }

    public Field[] getFields() {
        return fields.values().toArray(new Field[0]);
    }

    public int getFieldValue(String name) {
        return getField(name).getValue();
    }

    private Field getField(String name) {
        return fields.get(name);
    }

    protected int getOffset() {
        return offset;
    }

    protected AbstractStruct(int offset, BinaryReader reader, Field[] fields) {
        this.offset = offset;
        this.reader = reader;
        this.fields = new HashMap<>();

        for (Field field : fields) {
            this.fields.put(field.getName(), field);
            field.setValue(reader.readInt(getOffset() + field.getOffset(), field.getSize()));
        }
    }
}
