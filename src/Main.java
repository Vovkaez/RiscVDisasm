import Elf32.Header;
import Elf32.Sym;
import Elf32.ToString;
import Elf32.BinaryReader;
import RiscV32.Disassembler;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class Main {

    public static String symbolTableToString(Sym[] symTab, Header header) {
        StringBuilder sb = new StringBuilder(".symtab" + System.lineSeparator());
        sb.append(String.format(
                "%s %-15s %7s %-8s %-8s %-8s %6s %s\n",
                "Symbol", "Value", "Size", "Type", "Bind", "Vis", "Index", "Name"));
        for (int i = 0; i < symTab.length; i++) {
            sb.append(String.format("[%4d] 0x%-15X %5d %-8s %-8s %-8s %6s %s\n",
                    i,
                    symTab[i].getFieldValue("st_value"),
                    symTab[i].getFieldValue("st_size"),
                    ToString.st_type(symTab[i].getFieldValue("st_info") & 0xf),
                    ToString.st_bind(symTab[i].getFieldValue("st_info") >> 4),
                    ToString.st_visibility(symTab[i].getFieldValue("st_other") & 0x3),
                    ToString.st_shndx(symTab[i].getFieldValue("st_shndx")),
                    header.readStringTable(symTab[i].getFieldValue("st_name"))
                    ));
        }
        return sb.toString();
    }

    public static void main(String... args) {
        if (args.length < 2) {
            System.out.println("No files provided");
            return;
        }

        final byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(args[0]));
        } catch (NoSuchFileException e) {
            System.out.println("File not found: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Caught I/O exception while reading:  " + e.getMessage());
            return;
        }

        BinaryReader binReader = new BinaryReader(data);
        Header header = new Header(binReader);

        if (header.getFieldValue("ei_class") != 1) {
            System.out.println("Only 32-bit files supported");
            return;
        }
        if (header.getFieldValue("ei_data") != 1) {
            System.out.println("Only low-endian files supported");
            return;
        }
        if (header.getFieldValue("ei_version") != 1) {
            System.out.println("ELF specification version is invalid");
            return;
        }

        Map<Integer, String> labels = new HashMap<>();

        for (Sym sym : header.getSymbolTable()) {
            if ((sym.getFieldValue("st_info") & 0xf) == 2) { // FUNC type
                labels.put(
                        sym.getFieldValue("st_value"),
                        header.readStringTable(sym.getFieldValue("st_name")
                        ));
            }
        }

        Disassembler d = new Disassembler();
        String text = d.decompile(header.getText(),
                header.getSectionHeader(".text").getFieldValue("sh_addr"),
                labels);

        String symtab = symbolTableToString(header.getSymbolTable(), header);

        try (FileWriter w = new FileWriter(args[1], StandardCharsets.UTF_8)) {
            w.write( text + System.lineSeparator() + symtab);
        } catch(FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Caught I/O exception while writing: " + e.getMessage());
        }
    }
}
