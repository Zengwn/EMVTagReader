package com.example.nfctagrw.base;

import java.io.IOException;

public interface Card {
    public enum state {
        IDLE, READY, INTERRUPTED, ERROR, OK
    }

    public void prepare(CardAccessListener cb);

    public void defaultAction() throws IOException;

    public void close();

    public void asyncTransceive(byte[] apdu, CardAccessListener cb);

    public byte[] transceive(byte[] apdu);

    public result getResult();

    public state getActionError();

    public interface CardAccessListener {
        public void cardReady();

        public void transceiveDone( );
    }

    public class result {
        public int no;
        public byte[] data;
        public Object object;
    }
}
