package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles Layer 7 Application Message Framing over streaming TCP.
 * Prevents TCP segment fragmentation and concatenation issues by enforcing
 * a 4-byte big-endian Length-Prefix framing header:
 * [ 4-Byte Payload Length | UTF-8 Encrypted Payload Bytes ]
 */
public class MessageProtocol {

    public static final int MAX_FRAME_SIZE = 1024 * 1024; // 1 MB sanity limit

    /**
     * Sends a framed message over the provided output stream.
     */
    public static void sendFramedMessage(OutputStream out, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(payload.length);
        dos.write(payload);
        dos.flush();
    }

    /**
     * Reads a complete framed message from the input stream, blocking until all bytes arrive.
     * @return Decoded string payload, or null if connection was cleanly closed.
     */
    public static String receiveFramedMessage(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int length;
        try {
            length = dis.readInt();
        } catch (java.io.EOFException e) {
            return null; // Stream ended gracefully
        }

        if (length < 0 || length > MAX_FRAME_SIZE) {
            throw new IOException("Protocol frame length out of bounds: " + length);
        }

        byte[] buffer = new byte[length];
        dis.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
