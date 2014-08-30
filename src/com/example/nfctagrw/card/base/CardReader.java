package com.example.nfctagrw.card.base;

import java.io.IOException;

public interface CardReader {
    public byte[] transceive(byte[] apdu) throws IOException;
}
