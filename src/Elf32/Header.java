package Elf32;

public class Header extends AbstractStruct {
    public Header(int offset, BinaryReader reader) {
        super(offset, reader,
                new Field[] {
                        new Field("ei_class", 0x4, 1),
                        new Field("ei_data", 0x5, 1),
                        new Field("ei_version", 0x6, 1),
                        new Field("e_type", 0x10, 2),
                        new Field("e_machine", 0x12, 2),
                        new Field("e_version", 0x14, 4),
                        new Field("e_entry", 0x18, 4),
                        new Field("e_phoff", 0x1C, 4),
                        new Field("e_shoff", 0x20, 4),
                        new Field("e_flags", 0x24, 4),
                        new Field("e_ehsize", 0x28, 2),
                        new Field("e_phentsize", 0x2A, 2),
                        new Field("e_phnum", 0x2C, 2),
                        new Field("e_shentsize", 0x2E, 2),
                        new Field("e_shnum", 0x30, 2),
                        new Field("e_shstrndx", 0x32, 2)
                    }
                );
    }

    private static String readCharTable(String s, int i) {
        int begin = i;
        while (!(s.charAt(i) == 0)) {
            i++;
        }
        return s.substring(begin, i);
    }

    public String readStringTable(int i) {
        return readCharTable(getStringTable(), i);
    }

    public String readSectionHeaderStringTable(int i) {
        return readCharTable(getSectionStringTable(), i);
    }

    public short[] getText() {
        Shdr textHeader = getSectionHeader(".text");
        short[] text = new short[textHeader.getFieldValue("sh_size") / 2];
        int offset = textHeader.getFieldValue("sh_offset");
        for (int i = 0; i < text.length; i++) {
            text[i] = reader.readShort(offset + 2 * i, 2);
        }
        return text;
    }

    public Sym[] getSymbolTable() {
        Shdr symTabHeader = getSectionHeader(".symtab");
        int n = symTabHeader.getFieldValue("sh_size") / 16;
        int offset = symTabHeader.getFieldValue("sh_offset");
        Sym[] table = new Sym[n];
        for (int i = 0; i < n; i++) {
            table[i] = new Sym(offset + 16 * i, reader);
        }
        return table;
    }

    public Shdr getSectionHeader(int i) {
        return new Shdr(getFieldValue("e_shoff") + i * getFieldValue("e_shentsize"), reader);
    }

    public Shdr getSectionHeader(String name) {
        String strTab = getSectionStringTable();
        for (Shdr sHeader : getSectionHeaders()) {
            if (readCharTable(strTab, sHeader.getFieldValue("sh_name")).equals(name)) {
                return sHeader;
            }
        }
        throw new IllegalArgumentException("No section header with name '" + name + "' found");
    }

    public Shdr[] getSectionHeaders() {
        int n = getFieldValue("e_shnum");
        Shdr[] sectionHeaders = new Shdr[n];
        for (int i = 0; i < n; i++) {
            sectionHeaders[i] = getSectionHeader(i);
        }
        return sectionHeaders;
    }

    public Shdr getSectionStringTableSectionHeader() {
        return getSectionHeader(getFieldValue("e_shstrndx"));
    }

    public String getCharTable(Shdr sHeader) {
        StringBuilder sb = new StringBuilder();
        int size = sHeader.getFieldValue("sh_size");
        int offset = sHeader.getFieldValue("sh_offset");
        for (int i = 0; i < size; i++) {
            sb.append((char) sHeader.getReader().readByte(offset + i));
        }
        return sb.toString();
    }

    public String getSectionStringTable() {
        return getCharTable(getSectionStringTableSectionHeader());
    }

    public String getStringTable() {
        return getCharTable(getSectionHeader(".strtab"));
    }

    public Header(BinaryReader reader) {
        this(0, reader);
    }
}
