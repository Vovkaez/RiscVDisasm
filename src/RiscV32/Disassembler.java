package RiscV32;

import java.util.Map;

public final class Disassembler {
    final String UNKNOWN_COMMAND_MESSAGE = "unknown_command";
    private Map<Integer, String> labels;
    private int nxtLabel = 0;

    private record Rinstruction(
            int opcode,
            int rd,
            int funct3,
            int rs1,
            int rs2,
            int funct7) {

        public Rinstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 7, 11),
                BitUtils.subint(instruction, 12, 14),
                BitUtils.subint(instruction, 15, 19),
                BitUtils.subint(instruction, 20, 24),
                BitUtils.subint(instruction, 25, 31)
            );
        }
    }


    private record Iinstruction(
            int opcode,
            int rd,
            int funct3,
            int rs1,
            int imm) {

        public Iinstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 7, 11),
                BitUtils.subint(instruction, 12, 14),
                BitUtils.subint(instruction, 15, 19),
                BitUtils.subint(instruction, 20, 31)
            );
        }
    }

    private record Sinstruction(
            int opcode,
            int imm,
            int funct3,
            int rs1,
            int rs2) {

        public Sinstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 7, 11) | (BitUtils.subint(instruction, 25, 31) << 5),
                BitUtils.subint(instruction, 12, 14),
                BitUtils.subint(instruction, 15, 19),
                BitUtils.subint(instruction, 20, 24)
            );
        }
    }

    private record Uinstruction(
            int opcode,
            int rd,
            int imm) {

        public Uinstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 7, 11),
                BitUtils.subint(instruction, 12, 31) << 12
            );
        }
    }

    private record Binstruction(
            int opcode,
            int rs1,
            int rs2,
            int funct3,
            int imm) {

        public Binstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 15, 19),
                BitUtils.subint(instruction, 20, 24),
                BitUtils.subint(instruction, 12, 14),
            (BitUtils.subint(instruction, 8, 11) << 1) |
                    (BitUtils.subint(instruction, 25, 30) << 5) |
                    (BitUtils.subint(instruction, 7, 7) << 11) |
                    (BitUtils.subint(instruction, 31, 31) << 12)
            );
        }
    }

    private record Jinstruction(
            int opcode,
            int rd,
            int imm) {

        public Jinstruction(int instruction) {
            this(
                BitUtils.subint(instruction, 0, 6),
                BitUtils.subint(instruction, 7, 11),
            (BitUtils.subint(instruction, 21, 30) << 1) |
                    (BitUtils.subint(instruction, 20, 20) << 11) |
                    (BitUtils.subint(instruction, 12, 19) << 12) |
                    (BitUtils.subint(instruction, 31, 31) << 20)
            );
        }
    }

    private record CRinstruction(
            int op,
            int rs2,
            int rd,
            int funct4) {

        public CRinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 6),
                    BitUtils.subint(instruction, 7, 11),
                    BitUtils.subint(instruction, 12, 15)
            );
        }
    }

    private record CIinstruction(
            int op,
            int imm,
            int rd,
            int funct3) {

        public CIinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 6) | (BitUtils.subint(instruction, 12, 12) << 5),
                    BitUtils.subint(instruction, 7, 11),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CLinstruction(
            int op,
            int rd,
            int imm,
            int rs1,
            int funct3) {

        public CLinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 4),
                    BitUtils.subint(instruction, 5, 6) | (BitUtils.subint(instruction, 10, 12) << 2),
                    BitUtils.subint(instruction, 7, 9),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CSinstruction(
            int op,
            int rs2,
            int imm,
            int rs1,
            int funct3) {

        public CSinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 4),
                    BitUtils.subint(instruction, 5, 6) | (BitUtils.subint(instruction, 10, 12) << 2),
                    BitUtils.subint(instruction, 7, 9),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CBinstruction(
            int op,
            int offset,
            int rs1,
            int funct3) {

        public CBinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    (BitUtils.subint(instruction, 2, 6) | (BitUtils.subint(instruction, 10, 12) << 5)),
                    BitUtils.subint(instruction, 7, 9),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CJinstruction(
            int op,
            int target,
            int funct3) {

        public CJinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 12),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CSSinstruction(
            int op,
            int rs2,
            int imm,
            int funct3) {

        public CSSinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 6),
                    BitUtils.subint(instruction, 7, 12),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private record CIWinstruction(
            int op,
            int rd,
            int imm,
            int funct3) {

        public CIWinstruction(int instruction) {
            this(
                    BitUtils.subint(instruction, 0, 1),
                    BitUtils.subint(instruction, 2, 4),
                    BitUtils.subint(instruction, 5, 12),
                    BitUtils.subint(instruction, 13, 15)
            );
        }
    }

    private void ensureLabel(int address) {
        if (!labels.containsKey(address)) {
            labels.put(address, String.format("LOC_%05x", nxtLabel++));

        }
    }

    public String decompile(short[] text, int address, Map<Integer, String> labels) {
        this.labels = labels;
        String[] resultLines = new String[text.length];
        for (int i = 0; i < text.length; ) {
            int command = Short.toUnsignedInt(text[i]);

            if (command == 0) {
                resultLines[i] = "illegal_instruction";
                i++;
                continue;
            }

            int curAddr = address + 2 * i;

            int opcode = BitUtils.subint(command, 0, 6);
            if (BitUtils.subint(command,0, 1) == 0b11) {
                command |= Short.toUnsignedInt(text[i + 1]) << 16;

                resultLines[i] = switch (opcode) {
                    case 0b0010011 -> decompRegImm(command);
                    case 0b0110111 -> decompLUI(command);
                    case 0b0010111 -> decompAUIPC(command);
                    case 0b0110011 -> decompRegReg(command);
                    case 0b1101111 -> decompJAL(command, curAddr);
                    case 0b1100111 -> decompJALR(command);
                    case 0b1100011 -> decompBranch(command, curAddr);
                    case 0b0000011 -> decompLoad(command);
                    case 0b0100011 -> decompStore(command);
                    case 0b1110011 -> decompSystem(command);
                    default -> UNKNOWN_COMMAND_MESSAGE;
                };
                i += 2;
            } else {
                resultLines[i] = decompRVC(command, curAddr);
                i++;
            }
        }
        StringBuilder result = new StringBuilder(".text\n");
        for (int i = 0; i < text.length; ) {
            int curAddr = address + 2 * i;
            result.append(String.format("%08x ", curAddr));
            if (labels.containsKey(curAddr)) {
                result.append(String.format("%10s: ", labels.get(curAddr)));
            } else {
                result.append(String.format("%12s", ""));
            }
            result.append(resultLines[i]).append(System.lineSeparator());
            i += BitUtils.subint(text[i],0, 1) == 0b11 ? 2 : 1;
        }
        return result.toString();
    }

    private String decompRVC(int command, int address) {
        int op = BitUtils.subint(command, 0, 1);
        return switch (op) {
            case 0b00 -> {
                CLinstruction inst = new CLinstruction(command);
                yield switch (inst.funct3) {
                    case 0b010 -> decompCLW(command);
                    case 0b110 -> decompCSW(command);
                    case 0b000 -> decompCADDI4SPN(command);
                    default -> UNKNOWN_COMMAND_MESSAGE;
                };
            }
            case 0b01 -> {
                CJinstruction inst = new CJinstruction(command);
                yield switch (inst.funct3) {
                    case 0b001 -> decompCJAL(command, address);
                    case 0b101 -> decompCJ(command, address);
                    case 0b110 -> decompCBEQZ(command, address);
                    case 0b111 -> decompCBNEZ(command, address);
                    case 0b010 -> decompCLI(command);
                    case 0b011 -> decompCLUI(command);
                    case 0b000 -> decompCADDI(command);
                    case 0b100 -> BitUtils.subint(command, 10, 11) == 0b11
                                    ? decompCRegReg(command)
                                    : decompCRegImm(command);
                    default -> UNKNOWN_COMMAND_MESSAGE;
                };
            }
            case 0b10 -> {
                CIinstruction inst = new CIinstruction(command);
                yield switch (inst.funct3) {
                    case 0b000 -> decompCSLLI(command);
                    case 0b010 -> decompCLWSP(command);
                    case 0b110 -> decompCSWSP(command);
                    case 0b100 -> decompCRinst(command);
                    default -> UNKNOWN_COMMAND_MESSAGE;
                };
            }
            default -> throw error("rvc opcode");
        };
    }

    private String decompCRegReg(int command) {
        CSinstruction inst = new CSinstruction(command);
        String name = switch (BitUtils.subint(command, 5, 6)) {
            case 0b00 -> "c.sub";
            case 0b01 -> "c.xor";
            case 0b10 -> "c.or";
            case 0b11 -> "c.and";
            default -> throw error("funct");
        };
        return String.format("%s %s, %s",
                name,
                getCompressedRegisterName(inst.rs1),
                getCompressedRegisterName(inst.rs2));
    }

    private String decompCRegImm(int command) {
        CBinstruction inst = new CBinstruction(command);
        String name = switch (BitUtils.subint(command, 10, 11)) {
            case 0b00 -> "c.srli";
            case 0b01 -> "c.srai";
            case 0b10 -> "c.andi";
            default -> throw error("funct2");
        };
        int imm = BitUtils.subint(command, 2, 6) | (BitUtils.subint(command, 12, 12) << 5);
        if (name.equals("c.andi")) {
            imm = BitUtils.signExtend(imm, 6, 32);
        }
        return String.format("%s %s, %s", name, getCompressedRegisterName(inst.rs1), imm);

    }

    private String decompCSLLI(int command) {
        CIinstruction inst = new CIinstruction(command);
        return String.format("%s %s, %s", "c.slli", getRegisterName(inst.rd), inst.imm);
    }

    private String decompCADDI4SPN(int command) {
        CIWinstruction inst = new CIWinstruction(command);
        int value = (BitUtils.subint(inst.imm, 1, 1) << 2) |
                (BitUtils.subint(inst.imm, 0, 0) << 3) |
                (BitUtils.subint(inst.imm, 6, 7) << 4) |
                (BitUtils.subint(inst.imm, 2, 5) << 6);
        return String.format("%s %s, %s, %s", "c.addi4spn", getCompressedRegisterName(inst.rd), "sp", value);
    }

    private String decompCADDI(int command) {
        CIinstruction inst = new CIinstruction(command);
        if (inst.rd == 0) {
            return "c.nop";
        }
        return String.format("%s %s, %s", "c.addi", getRegisterName(inst.rd), BitUtils.signExtend(inst.imm, 6, 32));
    }

    private String decompCLUI(int command) {
        CIinstruction inst = new CIinstruction(command);
        if (inst.rd == 2) {
            int value = (BitUtils.subint(inst.imm, 4, 4) << 4) |
                    (BitUtils.subint(inst.imm, 0, 0) << 5) |
                    (BitUtils.subint(inst.imm, 3, 3) << 6) |
                    (BitUtils.subint(inst.imm, 1, 2) << 7) |
                    (BitUtils.subint(inst.imm, 5, 5) << 9);
            return String.format("%s %s, %s", "c.addi16sp",
                    getRegisterName(inst.rd),
                    BitUtils.signExtend(value, 10, 32));
        }
        return String.format("%s %s, %s", "c.lui", getRegisterName(inst.rd),
                BitUtils.signExtend(inst.imm << 12, 18, 32));
    }

    private String decompCLI(int command) {
        CIinstruction inst = new CIinstruction(command);
        return String.format("%s %s, %s", "c.li", getRegisterName(inst.rd), BitUtils.signExtend(inst.imm, 6, 32));
    }

    private String decompCBranch(String name, int command, int address) {
        CBinstruction inst = new CBinstruction(command);
        int offset = (BitUtils.subint(inst.offset, 1, 2) << 1) |
                (BitUtils.subint(inst.offset, 5, 6) << 3) |
                (BitUtils.subint(inst.offset, 0, 0) << 5) |
                (BitUtils.subint(inst.offset, 3, 4) << 6) |
                (BitUtils.subint(inst.offset, 7, 7) << 8);
        offset = BitUtils.signExtend(offset, 9, 32);
        ensureLabel(address + offset);
        return String.format("%s %s, %s", name, getCompressedRegisterName(inst.rs1), labels.get(address + offset));
    }

    private String decompCBNEZ(int command, int address) {
        return decompCBranch("c.bnez", command, address);
    }

    private String decompCBEQZ(int command, int address) {
        return decompCBranch("c.beqz", command, address);
    }

    private String decompCRinst(int command) {
        CRinstruction inst = new CRinstruction(command);
        String name = switch (inst.funct4 & 1) {
            case 0 -> switch (inst.rs2) {
                case 0 -> "c.jr";
                default -> "c.mv";
            };
            default -> switch (inst.rd) {
                case 0 -> "c.ebreak";
                default -> switch (inst.rs2) {
                    case 0 -> "c.jalr";
                    default -> "c.add";
                };
            };
        };
        String result = name;
        if (!name.equals("c.ebreak")) {
            result += " " + getRegisterName(inst.rd);
            if (name.equals("c.add") || name.equals("c.mv")) {
                result += ", " + (getRegisterName(inst.rs2));
            }
        }
        return result;
    }

    private String decompCJump(String name, int command, int address) {
        // immm: 10 9 8 7  6 5 4 3 2 1 0
        // off:  11 4 9 8 10 6 7 3 2 1 5
        CJinstruction inst = new CJinstruction(command);
        int offset =
                (BitUtils.subint(inst.target, 1, 3) << 1) |
                (BitUtils.subint(inst.target, 9, 9) << 4) |
                (BitUtils.subint(inst.target, 0, 0) << 5) |
                (BitUtils.subint(inst.target, 5, 5) << 6) |
                (BitUtils.subint(inst.target, 4, 4) << 7) |
                (BitUtils.subint(inst.target, 7, 8) << 8) |
                (BitUtils.subint(inst.target, 6, 6) << 10) |
                (BitUtils.subint(inst.target, 10, 10) << 11);
        offset = BitUtils.signExtend(offset, 12, 32);
        ensureLabel(address + offset);
        return String.format("%s %s",
                name, labels.get(address + offset));
    }

    private String decompCJ(int command, int address) {
        return decompCJump("c.j", command, address);
    }

    private String decompCJAL(int command, int address) {
        return decompCJump("c.jal", command, address);
    }

    private String decompCSW(int command) {
        CSinstruction inst = new CSinstruction(command);
        int offset =
                (BitUtils.subint(inst.imm, 1, 1) << 2) |
                (BitUtils.subint(inst.imm, 2, 4) << 3) |
                (BitUtils.subint(inst.imm, 0, 0) << 6);
        return String.format("%s %s, %s(%s)",
                "c.sw",
                getCompressedRegisterName(inst.rs2),
                offset,
                getCompressedRegisterName(inst.rs1)
        );
    }

    private String decompCLW(int command) {
        CLinstruction inst = new CLinstruction(command);
        int offset =
                (BitUtils.subint(inst.imm, 1, 1) << 2) |
                (BitUtils.subint(inst.imm, 2, 4) << 3) |
                (BitUtils.subint(inst.imm, 0, 0) << 6);
        return String.format("%s %s, %s(%s)",
                "c.lw",
                getCompressedRegisterName(inst.rd),
                offset,
                getCompressedRegisterName(inst.rs1)
        );
    }

    private String decompCSWSP(int command) {
        CSSinstruction inst = new CSSinstruction(command);
        int offset =
                (BitUtils.subint(inst.imm, 2, 5) << 2) |
                (BitUtils.subint(inst.imm, 0, 1) << 6);
        return String.format("%s %s, %s(%s)", "c.swsp", getRegisterName(inst.rs2), offset, "sp");
    }

    private String decompCLWSP(int command) {
        CIinstruction inst = new CIinstruction(command);
        int offset =
                (BitUtils.subint(inst.imm, 2, 4) << 2) |
                (BitUtils.subint(inst.imm, 5, 5) << 5) |
                (BitUtils.subint(inst.imm, 0, 1) << 6);
        return String.format("%s %s, %s(%s)", "c.lwsp", getRegisterName(inst.rd), offset, "sp");
    }

    private String decompSystem(int command) {
        Iinstruction inst = new Iinstruction(command);
        String name = switch (inst.funct3) {
                case 0b000 -> (command >>> 20) == 1 ? "ebreak" : "ecall";
                case 0b001 -> "csrrw";
                case 0b010 -> "csrrs";
                case 0b011 -> "csrrc";
                case 0b101 -> "csrrwi";
                case 0b110 -> "csrrsi";
                case 0b111 -> "csrrci";
                default -> "system instruction";
        };
        if (name.equals("ecall") || name.equals("ebreak")) {
            return name;
        }
        return String.format("%s %s, %s, %s",
                name,
                getRegisterName(inst.rd),
                getCSRname(inst.imm),
                ((inst.funct3 >>> 2) & 1) == 1 ? inst.rs1 : getRegisterName(inst.rs1));
    }

    public String decompRegImm(int command) {
        Iinstruction inst = new Iinstruction(command);
        String name = switch (inst.funct3()) {
            case 0b000 -> "addi";
            case 0b010 -> "slti";
            case 0b011 -> "sltiu";
            case 0b100 -> "xori";
            case 0b110 -> "ori";
            case 0b111 -> "andi";
            case 0b001 -> "slli";
            case 0b101 -> switch (BitUtils.subint(inst.imm, 5, 11)) {
                case 0b0000000 -> "srli";
                case 0b0100000 -> "srai";
                default -> throw error("immediate");
            };
            default -> throw error("funct3");
        };
        int imm = inst.imm;
        if (name.equals("slli") || name.equals("srli") || name.equals("srai")) {
            imm = BitUtils.subint(imm,0, 4);
        } else {
            imm = BitUtils.signExtend(imm, 12, 32);
        }
        return String.format("%s %s, %s, %s",
                name, getRegisterName(inst.rd), getRegisterName(inst.rs1), imm);
    }

    public String decompLUI(int command) {
        Uinstruction inst = new Uinstruction(command);
        return String.format("%s %s, %s",
                "lui", getRegisterName(inst.rd), inst.imm);
    }

    public String decompAUIPC(int command) {
        Uinstruction inst = new Uinstruction(command);
        return String.format("%s %s, %s",
                "auipc", getRegisterName(inst.rd), inst.imm);
    }

    public String decompRegReg(int command) {
        Rinstruction inst = new Rinstruction(command);
        String name = switch (inst.funct3) {
            case 0b000 -> switch (inst.funct7) {
                case 0b0000000 -> "add";
                case 0b0100000 -> "sub";
                case 0b0000001 -> "mul";
                default -> throw error("regreg funct7");
            };
            case 0b001 -> BitUtils.subint(command, 25, 25) == 1 ? "mulh" : "sll";
            case 0b010 -> BitUtils.subint(command, 25, 25) == 1 ? "mulhsu" : "slt";
            case 0b011 -> BitUtils.subint(command, 25, 25) == 1 ? "mulhu" : "sltu";
            case 0b100 -> BitUtils.subint(command, 25, 25) == 1 ? "div" : "xor";
            case 0b101 -> switch (inst.funct7) {
                case 0b0000000 -> "srl";
                case 0b0100000 -> "sra";
                case 0b0000001 -> "divu";
                default -> throw error("regreg funct3");
            };
            case 0b110 -> BitUtils.subint(command, 25, 25) == 1 ? "rem" : "or";
            case 0b111 -> BitUtils.subint(command, 25, 25) == 1 ? "remu" : "and";
            default -> throw error("regreg funct3");
        };
        return String.format("%s %s, %s, %s",
                name, getRegisterName(inst.rd), getRegisterName(inst.rs1), getRegisterName(inst.rs2));
    }

    public String decompJAL(int command, int address) {
        Jinstruction inst = new Jinstruction(command);
        int offset = BitUtils.signExtend(inst.imm, 21, 32);
        ensureLabel(offset + address);
        return String.format("%s %s, %s",
                "jal", getRegisterName(inst.rd), labels.get(offset + address));
    }

    public String decompJALR(int command) {
        Iinstruction inst = new Iinstruction(command);
        return String.format("%s %s, %s(%s)",
                "jalr",
                getRegisterName(inst.rd),
                BitUtils.signExtend(inst.imm, 12, 32),
                getRegisterName(inst.rs1));
    }

    public String decompBranch(int command, int address) {
        Binstruction inst = new Binstruction(command);
        String name = switch (inst.funct3) {
            case 0b000 -> "beq";
            case 0b001 -> "bne";
            case 0b100 -> "blt";
            case 0b101 -> "bge";
            case 0b110 -> "bltu";
            case 0b111 -> "bgeu";
            default -> throw error("branch funct3");
        };

        int offset = BitUtils.signExtend(inst.imm, 13, 32);
        ensureLabel(address + offset);
        return String.format("%s %s, %s, %s",
                name, getRegisterName(inst.rs1), getRegisterName(inst.rs2),
                labels.get(offset + address));
    }

    public String decompLoad(int command) {
        Iinstruction inst = new Iinstruction(command);
        String name = switch (inst.funct3) {
            case 0b000 -> "lb";
            case 0b001 -> "lh";
            case 0b010 -> "lw";
            case 0b100 -> "lbu";
            case 0b101 -> "lhu";
            default -> throw error("load funct3");
        };
        return String.format("%s %s, %s(%s)",
                name, getRegisterName(inst.rd), BitUtils.signExtend(inst.imm, 12, 32), getRegisterName(inst.rs1));
    }

    public String decompStore(int command) {
        Sinstruction inst = new Sinstruction(command);
        String name = switch (inst.funct3) {
            case 0b000 -> "sb";
            case 0b001 -> "sh";
            case 0b010 -> "sw";
            default -> throw error("store funct3");
        };
        return String.format("%s %s, %s(%s)",
                name, getRegisterName(inst.rs2), BitUtils.signExtend(inst.imm, 12, 32),
                getRegisterName(inst.rs1));
    }

    public String getRegisterName(int reg) {
        return switch (reg) {
            case 0 -> "zero";
            case 1 -> "ra";
            case 2 -> "sp";
            case 3 -> "gp";
            case 4 -> "tp";
            case 5, 6, 7 -> "t" + (reg - 5);
            case 8, 9 -> "s" + (reg - 8);
            default -> {
                if (reg < 10 || reg > 31) {
                    throw error("register");
                } else if (reg <= 17) {
                    yield "a" + (reg - 10);
                } else if (reg <= 27) {
                    yield "s" + (reg - 16);
                } else {
                    yield "t" + (reg - 25);
                }
            }
        };
    }

    public String getCompressedRegisterName(int reg) {
        return switch (reg) {
            case 0b000 -> "s0";
            case 0b001 -> "s1";
            case 0b010 -> "a0";
            case 0b011 -> "a1";
            case 0b100 -> "a2";
            case 0b101 -> "a3";
            case 0b110 -> "a4";
            case 0b111 -> "a5";
            default -> throw error("compressed register");
        };
    }

    public String getCSRname(int csr) {
        return switch (csr) {
            case 0x001 -> "fflags";
            case 0x002 -> "frm";
            case 0x003 -> "fcsr";
            case 0xC00 -> "cycle";
            case 0xC01 -> "time";
            case 0xC02 -> "instret";
            case 0xC80 -> "cycleh";
            case 0xC81 -> "timeh";
            case 0xC82 -> "instreth";
            default -> throw error("csr");
        };
    }

    private IllegalArgumentException error(String s) {
        return new IllegalArgumentException("Found bad " + s + " value");
    }
}
