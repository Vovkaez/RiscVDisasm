package Elf32;

public class Sym extends AbstractStruct {

    protected Sym(int offset, BinaryReader reader) {
        super(offset, reader, new Field[] {
                new Field("st_name", 0x0, 4),
                new Field("st_value", 0x4, 4),
                new Field("st_size", 0x8, 4),
                new Field("st_info", 0xC, 1),
                new Field("st_other", 0xD, 1),
                new Field("st_shndx", 0xE, 2)
        });
    }
}
