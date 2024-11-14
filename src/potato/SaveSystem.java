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

    /*
    TODO: Add some kind of way to save arrays and nested arrays, in general, for all types
     */

    public static final SaveSystem SETTINGS_SAVE = new SaveSystem("settings.dat");

    public SaveSystem(String filename) {
        data = new HashMap<>();
        this.filename = filename;
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

    // Getters (unchanged)
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

    // Modified file operations with GZIP compression
    public void save() throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(
                        new GZIPOutputStream(
                                Files.newOutputStream(Paths.get(filename)))))) {

            // Write number of entries
            dos.writeInt(data.size());

            // Write each entry
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                dos.writeUTF(entry.getKey());
                Object value = entry.getValue();

                if (value instanceof String) {
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

            // Read number of entries
            int size = dis.readInt();

            // Read each entry
            for (int i = 0; i < size; i++) {
                String key = dis.readUTF();
                byte type = dis.readByte();

                switch (type) {
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