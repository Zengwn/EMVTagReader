package com.example.nfctagrw.util;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.nfc.tech.NfcA;
import android.util.Log;

public class NfcaCard implements Card, CardReader {
    public static final String TAG = NfcaCard.class.getSimpleName();

    private NfcA mNfca;

    public NfcaCard(NfcA nfca) {
        Log.i(TAG, "Nfca Card created");
        mNfca = nfca;
    }

    @Override
    public byte[] transceive(byte[] apdu) throws IOException {
        // TODO Auto-generated method stub
        return mNfca.transceive(apdu);
    }

    @Override
    public void connect() throws IOException {
        // TODO Auto-generated method stub
        mNfca.connect();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        mNfca.close();
    }

    @Override
    public CardReader getCardReader() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void AttachActivity(CardAccessListener cb) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void prepare() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void transceive(byte[] apdu, CardAccessListener cb) {
        // TODO Auto-generated method stub
        
    }

}
