package potato;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveSystem {
    private String filename;
    private Map<String, Object> data;
    private static final byte TYPE_BYTE = 1;
    private static final byte TYPE_SHORT = 2;
    private static final byte TYPE_INT = 3;
    private static final byte TYPE_LONG = 4;
    private static final byte TYPE_FLOAT = 5;
    private static final byte TYPE_DOUBLE = 6;
    private static final byte TYPE_STRING = 7;
    private static final byte TYPE_BOOLEAN = 8;

    private static final byte TYPE_BYTE_ARRAY = 9;
    private static final byte TYPE_SHORT_ARRAY = 10;
    private static final byte TYPE_INT_ARRAY = 11;
    private static final byte TYPE_LONG_ARRAY = 12;
    private static final byte TYPE_FLOAT_ARRAY = 13;
    private static final byte TYPE_DOUBLE_ARRAY = 14;
    private static final byte TYPE_STRING_ARRAY = 15;
    private static final byte TYPE_BOOLEAN_ARRAY = 16;

    /*
    TODO: Add some kind of way to save arrays and nested arrays, in general, for all types
     */

    public static final SaveSystem SETTINGS_SAVE = new SaveSystem("settings.dat");

    public SaveSystem(String filename) {
        data = new HashMap<>();
        this.filename = filename;
    }

    // New array setters
    public void setByteArray(String key, byte[] value) {
        data.put(key, value);
    }

    public void setShortArray(String key, short[] value) {
        data.put(key, value);
    }

    public void setIntArray(String key, int[] value) {
        data.put(key, value);
    }

    public void setLongArray(String key, long[] value) {
        data.put(key, value);
    }

    public void setFloatArray(String key, float[] value) {
        data.put(key, value);
    }

    public void setDoubleArray(String key, double[] value) {
        data.put(key, value);
    }

    public void setStringArray(String key, String[] value) {
        data.put(key, value);
    }

    public void setBooleanArray(String key, boolean[] value) {
        data.put(key, value);
    }


    // Setters (unchanged)
    public void setString(String key, String value) {
        data.put(key, value);
    }

    public void setInt(String key, int value) {
        data.put(key, value);
    }

    public void setDouble(String key, double value) {
        data.put(key, value);
    }

    public void setFloat(String key, float value) {
        data.put(key, value);
    }

    public void setLong(String key, long value) {
        data.put(key, value);
    }

    public void setBoolean(String key, boolean value) {
        data.put(key, value);
    }

    public void setByte(String key, byte value) {
        data.put(key, value);
    }

    public void setShort(String key, short value) {
        data.put(key, value);
    }

    public String getString(String key, String defaultValue) {
        Object value = data.get(key);
        return (value instanceof String) ? (String)value : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object value = data.get(key);
        return (value instanceof Integer) ? (Integer)value : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        Object value = data.get(key);
        return (value instanceof Double) ? (Double)value : defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        Object value = data.get(key);
        return (value instanceof Float) ? (Float)value : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        Object value = data.get(key);
        return (value instanceof Long) ? (Long)value : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = data.get(key);
        return (value instanceof Boolean) ? (Boolean)value : defaultValue;
    }

    public byte getByte(String key, byte defaultValue) {
        Object value = data.get(key);
        return (value instanceof Byte) ? (Byte)value : defaultValue;
    }

    public short getShort(String key, short defaultValue) {
        Object value = data.get(key);
        return (value instanceof Short) ? (Short)value : defaultValue;
    }

    // New array getters
    public byte[] getByteArray(String key, byte[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof byte[]) ? (byte[])value : defaultValue;
    }

    public short[] getShortArray(String key, short[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof short[]) ? (short[])value : defaultValue;
    }

    public int[] getIntArray(String key, int[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof int[]) ? (int[])value : defaultValue;
    }

    public long[] getLongArray(String key, long[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof long[]) ? (long[])value : defaultValue;
    }

    public float[] getFloatArray(String key, float[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof float[]) ? (float[])value : defaultValue;
    }

    public double[] getDoubleArray(String key, double[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof double[]) ? (double[])value : defaultValue;
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof String[]) ? (String[])value : defaultValue;
    }

    public boolean[] getBooleanArray(String key, boolean[] defaultValue) {
        Object value = data.get(key);
        return (value instanceof boolean[]) ? (boolean[])value : defaultValue;
    }

    // Modified file operations with GZIP compression
    public void save() throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(
                        new GZIPOutputStream(
                                Files.newOutputStream(Paths.get(filename)))))) {

            dos.writeInt(data.size());

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                dos.writeUTF(entry.getKey());
                Object value = entry.getValue();

                if (value instanceof byte[]) {
                    byte[] array = (byte[])value;
                    dos.writeByte(TYPE_BYTE_ARRAY);
                    dos.writeInt(array.length);
                    dos.write(array);
                } else if (value instanceof short[]) {
                    short[] array = (short[])value;
                    dos.writeByte(TYPE_SHORT_ARRAY);
                    dos.writeInt(array.length);
                    for (short v : array) dos.writeShort(v);
                } else if (value instanceof int[]) {
                    int[] array = (int[])value;
                    dos.writeByte(TYPE_INT_ARRAY);
                    dos.writeInt(array.length);
                    for (int v : array) dos.writeInt(v);
                } else if (value instanceof long[]) {
                    long[] array = (long[])value;
                    dos.writeByte(TYPE_LONG_ARRAY);
                    dos.writeInt(array.length);
                    for (long v : array) dos.writeLong(v);
                } else if (value instanceof float[]) {
                    float[] array = (float[])value;
                    dos.writeByte(TYPE_FLOAT_ARRAY);
                    dos.writeInt(array.length);
                    for (float v : array) dos.writeFloat(v);
                } else if (value instanceof double[]) {
                    double[] array = (double[])value;
                    dos.writeByte(TYPE_DOUBLE_ARRAY);
                    dos.writeInt(array.length);
                    for (double v : array) dos.writeDouble(v);
                } else if (value instanceof String[]) {
                    String[] array = (String[])value;
                    dos.writeByte(TYPE_STRING_ARRAY);
                    dos.writeInt(array.length);
                    for (String v : array) dos.writeUTF(v);
                } else if (value instanceof boolean[]) {
                    boolean[] array = (boolean[])value;
                    dos.writeByte(TYPE_BOOLEAN_ARRAY);
                    dos.writeInt(array.length);
                    for (boolean v : array) dos.writeBoolean(v);
                } else if (value instanceof String) {
                    dos.writeByte(TYPE_STRING);
                    dos.writeUTF((String)value);
                } else if (value instanceof Integer) {
                    dos.writeByte(TYPE_INT);
                    dos.writeInt((Integer)value);
                } else if (value instanceof Double) {
                    dos.writeByte(TYPE_DOUBLE);
                    dos.writeDouble((Double)value);
                } else if (value instanceof Float) {
                    dos.writeByte(TYPE_FLOAT);
                    dos.writeFloat((Float)value);
                } else if (value instanceof Long) {
                    dos.writeByte(TYPE_LONG);
                    dos.writeLong((Long)value);
                } else if (value instanceof Boolean) {
                    dos.writeByte(TYPE_BOOLEAN);
                    dos.writeBoolean((Boolean)value);
                } else if (value instanceof Byte) {
                    dos.writeByte(TYPE_BYTE);
                    dos.writeByte((Byte)value);
                } else if (value instanceof Short) {
                    dos.writeByte(TYPE_SHORT);
                    dos.writeShort((Short)value);
                }
            }
        }
    }

    public void load() throws IOException {
        data.clear();
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                        new GZIPInputStream(
                                Files.newInputStream(Paths.get(filename)))))) {

            int size = dis.readInt();

            for (int i = 0; i < size; i++) {
                String key = dis.readUTF();
                byte type = dis.readByte();

                switch (type) {
                    case TYPE_BYTE_ARRAY: {
                        int length = dis.readInt();
                        byte[] array = new byte[length];
                        dis.readFully(array);
                        data.put(key, array);
                        break;
                    }
                    case TYPE_SHORT_ARRAY: {
                        int length = dis.readInt();
                        short[] array = new short[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readShort();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_INT_ARRAY: {
                        int length = dis.readInt();
                        int[] array = new int[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readInt();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_LONG_ARRAY: {
                        int length = dis.readInt();
                        long[] array = new long[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readLong();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_FLOAT_ARRAY: {
                        int length = dis.readInt();
                        float[] array = new float[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readFloat();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_DOUBLE_ARRAY: {
                        int length = dis.readInt();
                        double[] array = new double[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readDouble();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_STRING_ARRAY: {
                        int length = dis.readInt();
                        String[] array = new String[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readUTF();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_BOOLEAN_ARRAY: {
                        int length = dis.readInt();
                        boolean[] array = new boolean[length];
                        for (int j = 0; j < length; j++) array[j] = dis.readBoolean();
                        data.put(key, array);
                        break;
                    }
                    case TYPE_STRING:
                        data.put(key, dis.readUTF());
                        break;
                    case TYPE_INT:
                        data.put(key, dis.readInt());
                        break;
                    case TYPE_DOUBLE:
                        data.put(key, dis.readDouble());
                        break;
                    case TYPE_FLOAT:
                        data.put(key, dis.readFloat());
                        break;
                    case TYPE_LONG:
                        data.put(key, dis.readLong());
                        break;
                    case TYPE_BOOLEAN:
                        data.put(key, dis.readBoolean());
                        break;
                    case TYPE_BYTE:
                        data.put(key, dis.readByte());
                        break;
                    case TYPE_SHORT:
                        data.put(key, dis.readShort());
                        break;
                }
            }
        }
    }

    // Utility methods (unchanged)
    public void clear() {
        data.clear();
    }

    public boolean hasKey(String key) {
        return data.containsKey(key);
    }

    public void removeKey(String key) {
        data.remove(key);
    }
}