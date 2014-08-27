package com.example.nfctagrw.base;

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
    public void prepare(CardAccessListener cb) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void defaultAction() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void asyncTransceive(byte[] apdu, CardAccessListener cb) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public byte[] transceive(byte[] apdu) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public result getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public state getActionError() {
        // TODO Auto-generated method stub
        return null;
    }


}
