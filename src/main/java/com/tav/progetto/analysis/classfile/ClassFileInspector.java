package com.tav.progetto.analysis.classfile;

import com.tav.progetto.analysis.core.TargetType;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassFileInspector {
    private static final int MAGIC = 0xCAFEBABE;
    private static final int TABLESWITCH = 0xAA;
    private static final int LOOKUPSWITCH = 0xAB;
    private static final int WIDE = 0xC4;

    private ClassFileInspector() {}

    public static String readInternalClassName(Path classFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(classFile)))) {
            int magic = in.readInt();
            if (magic != MAGIC) throw new IOException("Not a class file");

            in.readUnsignedShort(); // minor_version
            in.readUnsignedShort(); // major_version

            int constantPoolCount = in.readUnsignedShort();
            String[] utf8 = new String[constantPoolCount];
            int[] classNameIndex = new int[constantPoolCount];

            for (int i = 1; i < constantPoolCount; i++) {
                int tag = in.readUnsignedByte();
                switch (tag) {
                    case 1: // CONSTANT_Utf8
                        utf8[i] = in.readUTF();
                        break;
                    case 3: // CONSTANT_Integer
                    case 4: // CONSTANT_Float
                        in.readInt();
                        break;
                    case 5: // CONSTANT_Long (takes two entries)
                    case 6: // CONSTANT_Double (takes two entries)
                        in.readLong();
                        i++;
                        break;
                    case 7: // CONSTANT_Class
                        classNameIndex[i] = in.readUnsignedShort();
                        break;
                    case 8: // CONSTANT_String
                        in.readUnsignedShort();
                        break;
                    case 9: // CONSTANT_Fieldref
                    case 10: // CONSTANT_Methodref
                    case 11: // CONSTANT_InterfaceMethodref
                    case 12: // CONSTANT_NameAndType
                    case 18: // CONSTANT_InvokeDynamic
                    case 17: // CONSTANT_Dynamic
                        in.readUnsignedShort();
                        in.readUnsignedShort();
                        break;
                    case 15: // CONSTANT_MethodHandle
                        in.readUnsignedByte();
                        in.readUnsignedShort();
                        break;
                    case 16: // CONSTANT_MethodType
                        in.readUnsignedShort();
                        break;
                    case 19: // CONSTANT_Module
                    case 20: // CONSTANT_Package
                        in.readUnsignedShort();
                        break;
                    default:
                        throw new IOException("Unknown constant pool tag: " + tag);
                }
            }

            in.readUnsignedShort(); // access_flags
            int thisClassIndex = in.readUnsignedShort();
            in.readUnsignedShort(); // super_class

            if (thisClassIndex <= 0 || thisClassIndex >= constantPoolCount) {
                throw new IOException("Invalid this_class index");
            }
            int nameIndex = classNameIndex[thisClassIndex];
            if (nameIndex <= 0 || nameIndex >= constantPoolCount) {
                throw new IOException("Invalid class name index");
            }
            String internalName = utf8[nameIndex];
            if (internalName == null || internalName.isEmpty()) {
                throw new IOException("Missing class name");
            }
            return internalName;
        }
    }

    public static SwitchUsageSummary inspectSwitchUsage(Path classFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(classFile)))) {
            return inspectSwitchUsage(in);
        }
    }

    public static SwitchUsageSummary inspectSwitchUsage(String originPath, TargetType targetType) throws IOException {
        if (targetType == TargetType.JAR) {
            int separator = originPath.indexOf("!/");
            if (separator < 0) throw new IOException("Invalid jar entry path");
            String jarPath = originPath.substring(0, separator);
            String entryName = originPath.substring(separator + 2);
            try (JarFile jarFile = new JarFile(new File(jarPath))) {
                JarEntry entry = jarFile.getJarEntry(entryName);
                if (entry == null) throw new IOException("Jar entry not found: " + entryName);
                try (InputStream stream = jarFile.getInputStream(entry);
                     DataInputStream in = new DataInputStream(new BufferedInputStream(stream))) {
                    return inspectSwitchUsage(in);
                }
            }
        }
        return inspectSwitchUsage(Path.of(originPath));
    }

    private static SwitchUsageSummary inspectSwitchUsage(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic != MAGIC) throw new IOException("Not a class file");

        in.readUnsignedShort(); // minor_version
        in.readUnsignedShort(); // major_version

        int constantPoolCount = in.readUnsignedShort();
        String[] utf8 = new String[constantPoolCount];
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1:
                    utf8[i] = in.readUTF();
                    break;
                case 3:
                case 4:
                    in.readInt();
                    break;
                case 5:
                case 6:
                    in.readLong();
                    i++;
                    break;
                case 7:
                case 8:
                case 16:
                case 19:
                case 20:
                    in.readUnsignedShort();
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                case 17:
                case 18:
                    in.readUnsignedShort();
                    in.readUnsignedShort();
                    break;
                case 15:
                    in.readUnsignedByte();
                    in.readUnsignedShort();
                    break;
                default:
                    throw new IOException("Unknown constant pool tag: " + tag);
            }
        }

        in.readUnsignedShort(); // access_flags
        in.readUnsignedShort(); // this_class
        in.readUnsignedShort(); // super_class

        skipInterfaces(in);
        skipMembers(in);
        return inspectMethods(in, utf8);
    }

    private static void skipInterfaces(DataInputStream in) throws IOException {
        int interfacesCount = in.readUnsignedShort();
        for (int i = 0; i < interfacesCount; i++) {
            in.readUnsignedShort();
        }
    }

    private static void skipMembers(DataInputStream in) throws IOException {
        int membersCount = in.readUnsignedShort();
        for (int i = 0; i < membersCount; i++) {
            in.readUnsignedShort(); // access_flags
            in.readUnsignedShort(); // name_index
            in.readUnsignedShort(); // descriptor_index
            int attributesCount = in.readUnsignedShort();
            skipAttributes(in, attributesCount);
        }
    }

    private static SwitchUsageSummary inspectMethods(DataInputStream in, String[] utf8) throws IOException {
        int methodsCount = in.readUnsignedShort();
        SwitchUsageSummary summary = new SwitchUsageSummary();
        for (int i = 0; i < methodsCount; i++) {
            in.readUnsignedShort(); // access_flags
            in.readUnsignedShort(); // name_index
            in.readUnsignedShort(); // descriptor_index
            int attributesCount = in.readUnsignedShort();

            int methodSwitchInstructions = 0;
            int methodSwitchCases = 0;
            for (int j = 0; j < attributesCount; j++) {
                int attributeNameIndex = in.readUnsignedShort();
                String attributeName = utf8[attributeNameIndex];
                int attributeLength = in.readInt();
                if ("Code".equals(attributeName)) {
                    in.readUnsignedShort(); // max_stack
                    in.readUnsignedShort(); // max_locals
                    int codeLength = in.readInt();
                    byte[] code = new byte[codeLength];
                    in.readFully(code);

                    SwitchUsageSummary codeSummary = inspectCode(code);
                    methodSwitchInstructions += codeSummary.switchInstructionsCount;
                    methodSwitchCases += codeSummary.totalSwitchCasesCount;

                    int exceptionTableLength = in.readUnsignedShort();
                    skipFully(in, exceptionTableLength * 8L);

                    int codeAttributesCount = in.readUnsignedShort();
                    skipAttributes(in, codeAttributesCount);
                } else {
                    skipFully(in, attributeLength);
                }
            }

            summary.switchInstructionsCount += methodSwitchInstructions;
            summary.totalSwitchCasesCount += methodSwitchCases;
            if (methodSwitchInstructions > 0) {
                summary.methodsWithSwitchCount++;
                summary.maxSwitchCasesInMethod = Math.max(summary.maxSwitchCasesInMethod, methodSwitchCases);
            }
        }
        summary.available = true;
        return summary;
    }

    private static SwitchUsageSummary inspectCode(byte[] code) throws IOException {
        SwitchUsageSummary summary = new SwitchUsageSummary();
        int offset = 0;
        while (offset < code.length) {
            int opcode = code[offset] & 0xFF;
            if (opcode == TABLESWITCH) {
                int aligned = alignToFourBytes(offset + 1);
                ensureAvailable(code, aligned + 12);
                int low = readInt(code, aligned + 4);
                int high = readInt(code, aligned + 8);
                int caseCount = Math.max(0, high - low + 1);
                ensureAvailable(code, aligned + 12 + (caseCount * 4));
                summary.switchInstructionsCount++;
                summary.totalSwitchCasesCount += caseCount;
                offset = aligned + 12 + (caseCount * 4);
                continue;
            }
            if (opcode == LOOKUPSWITCH) {
                int aligned = alignToFourBytes(offset + 1);
                ensureAvailable(code, aligned + 8);
                int pairs = Math.max(0, readInt(code, aligned + 4));
                ensureAvailable(code, aligned + 8 + (pairs * 8));
                summary.switchInstructionsCount++;
                summary.totalSwitchCasesCount += pairs;
                offset = aligned + 8 + (pairs * 8);
                continue;
            }
            offset += opcodeLength(code, offset, opcode);
        }
        return summary;
    }

    private static int opcodeLength(byte[] code, int offset, int opcode) throws IOException {
        switch (opcode) {
            case 0x10:
            case 0x12:
            case 0x15:
            case 0x16:
            case 0x17:
            case 0x18:
            case 0x19:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3A:
            case 0xA9:
            case 0xBC:
                return 2;
            case 0x11:
            case 0x13:
            case 0x14:
            case 0x84:
            case 0x99:
            case 0x9A:
            case 0x9B:
            case 0x9C:
            case 0x9D:
            case 0x9E:
            case 0x9F:
            case 0xA0:
            case 0xA1:
            case 0xA2:
            case 0xA3:
            case 0xA4:
            case 0xA5:
            case 0xA6:
            case 0xA7:
            case 0xA8:
            case 0xB2:
            case 0xB3:
            case 0xB4:
            case 0xB5:
            case 0xB6:
            case 0xB7:
            case 0xB8:
            case 0xBB:
            case 0xBD:
            case 0xC0:
            case 0xC1:
            case 0xC6:
            case 0xC7:
                return 3;
            case 0xC5:
                return 4;
            case 0xB9:
            case 0xBA:
            case 0xC8:
            case 0xC9:
                return 5;
            case WIDE:
                ensureAvailable(code, offset + 2);
                int widenedOpcode = code[offset + 1] & 0xFF;
                return widenedOpcode == 0x84 ? 6 : 4;
            default:
                return 1;
        }
    }

    private static void skipAttributes(DataInputStream in, int attributesCount) throws IOException {
        for (int i = 0; i < attributesCount; i++) {
            in.readUnsignedShort();
            int attributeLength = in.readInt();
            skipFully(in, attributeLength);
        }
    }

    private static void skipFully(DataInputStream in, long bytesToSkip) throws IOException {
        long remaining = bytesToSkip;
        while (remaining > 0) {
            long skipped = in.skip(remaining);
            if (skipped <= 0) throw new IOException("Unexpected end of class file");
            remaining -= skipped;
        }
    }

    private static int alignToFourBytes(int offset) {
        int remainder = offset % 4;
        return remainder == 0 ? offset : offset + (4 - remainder);
    }

    private static int readInt(byte[] code, int offset) {
        return ((code[offset] & 0xFF) << 24)
                | ((code[offset + 1] & 0xFF) << 16)
                | ((code[offset + 2] & 0xFF) << 8)
                | (code[offset + 3] & 0xFF);
    }

    private static void ensureAvailable(byte[] code, int requiredExclusiveIndex) throws IOException {
        if (requiredExclusiveIndex > code.length) {
            throw new IOException("Malformed bytecode");
        }
    }

    public static final class SwitchUsageSummary {
        private boolean available;
        private int switchInstructionsCount;
        private int methodsWithSwitchCount;
        private int totalSwitchCasesCount;
        private int maxSwitchCasesInMethod;

        public boolean isAvailable() {
            return available;
        }

        public int getSwitchInstructionsCount() {
            return switchInstructionsCount;
        }

        public int getMethodsWithSwitchCount() {
            return methodsWithSwitchCount;
        }

        public int getTotalSwitchCasesCount() {
            return totalSwitchCasesCount;
        }

        public int getMaxSwitchCasesInMethod() {
            return maxSwitchCasesInMethod;
        }
    }
}
