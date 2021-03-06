package edu.uci.nomoads.prediction;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * A naive implementation of DPI: searches a given packet for all provided {@link #searchStrings}
 * in multiple passes of the packet. This could be improved with an implementation of the
 * Aho-Corasick algorithm, but the speed is not as crucial on a server. AntMonitor provides a
 * very efficient Aho-Corasick implementation for when this library is used on the mobile device.
 */
class ServerDPI implements DPIInterface {
    private String[] searchStrings;

    public void init(String[] searchStrings) {
        this.searchStrings = searchStrings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<String> search(ByteBuffer packet, int size) {
        String strPacket = byteBufferToString(packet, size);

        ArrayList<String> foundStrings = new ArrayList<>();
        for (String searchStr : searchStrings) {
            int i = strPacket.indexOf(searchStr);

            // Loop until all occurrences of specific string are found
            while (i != -1) {
                i += searchStr.length();

                // Add the found string and the ending index of where it was found
                foundStrings.add(searchStr);
                foundStrings.add(i + "");

                i = strPacket.indexOf(searchStr, i);
            }
        }

        return foundStrings;
    }

    /**
     * Converts given ByteBuffer to String based on the UTF-8 encoding (we operate on a
     * ByteBuffer to match any mobile implementations of {@link DPIInterface}).
     * @param buffer the packet
     * @param size size of the packet
     * @return String representation of the ByteBuffer
     */
    private String byteBufferToString(ByteBuffer buffer, int size) {
        String strPacket = new String(buffer.array(), 0, size, Charset.forName("UTF-8"));
        return strPacket;
    }

}
