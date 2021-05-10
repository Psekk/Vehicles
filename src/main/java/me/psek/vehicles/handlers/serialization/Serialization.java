package me.psek.vehicles.handlers.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class Serialization {
    /**
     * Serialize any object
     * @param obj the object that will be serialized
     * @return returns the serialized string
     */
    public String serialize(Object obj) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.flush();
            return bo.toString(StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Deserialize any object
     * @param str the serialized data
     * @param cls the class that it should try to deserialize to
     * @return returns the deserialized object
     */
    public <T> T deserialize(String str, Class<T> cls) {
        try {
            byte[] b = str.getBytes(StandardCharsets.ISO_8859_1);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return cls.cast(si.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
