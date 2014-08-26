package com.example.nfctagrw.util;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader.CardReader;

public interface Card {
    public void AttachActivity(CardAccessListener cb);
    public void prepare();
    public void connect() throws IOException;
    public void close() throws IOException;
    public void transceive(byte[] apdu, CardAccessListener cb);
    public CardReader getCardReader();
    
    public interface CardAccessListener{
        public void CardReady(boolean failed);
        public void TransceiveData(byte[] data);
    }
}
