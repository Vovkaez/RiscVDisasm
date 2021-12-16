package Elf32;

public class Shdr extends AbstractStruct {
    protected Shdr(int offset, BinaryReader reader) {
        super(offset, reader, new Field[] {
                new Field("sh_name", 0x0, 4),
                new Field("sh_type", 0x4, 4),
                new Field("sh_flags", 0x8, 4),
                new Field("sh_addr", 0xC, 4),
                new Field("sh_offset", 0x10, 4),
                new Field("sh_size", 0x14, 4),
                new Field("sh_link", 0x18, 4),
                new Field("sh_info", 0x1C, 4),
                new Field("sh_addralign", 0x20, 4),
                new Field("sh_entsize", 0x24, 4)
        });
    }
}
