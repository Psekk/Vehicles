package me.psek.vehicles.utility;

import java.nio.ByteBuffer;
import java.util.UUID;

@SuppressWarnings("unused")
public class Utils {
    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] UUIDtoBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long firstLong = byteBuffer.getLong();
        long secondLong = byteBuffer.getLong();
        return new UUID(firstLong, secondLong);
    }
}
