package Elf32;

public final class ToString {

    private static IllegalArgumentException unspecifiedValue(String varName, int value) {
        return new IllegalArgumentException(String.format(
                "Unspecified %s value: 0x%h",
                varName,
                Integer.toUnsignedLong(value)));
    }

    public static String st_bind(int bind) {
        return switch (bind) {
            case 0  -> "LOCAL";
            case 1  -> "GLOBAL";
            case 2  -> "WEAK";
            case 10 -> "LOOS";
            case 12 -> "HIOS";
            case 13 -> "LOPROC";
            case 15 -> "HIPROC";
            default -> throw unspecifiedValue("ST_BIND", bind);
        };
    }

    public static String st_type(int type) {
        return switch (type) {
            case 0 -> "NOTYPE";
            case 1 -> "OBJECT";
            case 2 -> "FUNC";
            case 3 -> "SECTION";
            case 4 -> "FILE";
            case 5 -> "COMMON";
            case 6 -> "TLS";
            case 7 -> "NUM";
            case 10 -> "LOOS";
            case 12 -> "HIOS";
            case 13 -> "LOPROC";
            case 15 -> "HIPROC";
            default -> throw unspecifiedValue("ST_TYPE", type);
        };
    }

    public static String st_visibility(int vis) {
        return switch (vis) {
            case 0 -> "DEFAULT";
            case 1 -> "INTERNAL";
            case 2 -> "HIDDEN";
            case 3 -> "PROTECTED";
            default -> throw unspecifiedValue("ST_VISIBILITY", vis);
        };
    }

    public static String st_shndx(int ndx) {
        return switch (ndx) {
            case 0x0000 -> "UNDEF";
            case 0xff00 -> "OS_SPEC";
            case 0xf001 -> "OS_SPEC";
            case 0xff20 -> "OS_SPEC";
            case 0xff3f -> "OS_PEC";
            case 0xfff1 -> "ABS";
            case 0xfff2 -> "COMMON";
            case 0xffff -> "XINDEX";
            default -> Integer.toString(ndx);
        };
    }
}
