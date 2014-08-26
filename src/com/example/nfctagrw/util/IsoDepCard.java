package com.example.nfctagrw.util;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class IsoDepCard implements Card,CardReader {
    public static final String TAG = IsoDepCard.class.getSimpleName();
            
    private IsoDep mIsoDep;
    public IsoDepCard(IsoDep isodep){
        Log.i(TAG, "IsoDep Card created");
        mIsoDep = isodep;
    }
    
    @Override
    public byte[] transceive(byte[] apdu) throws IOException {
        // TODO Auto-generated method stub
        return mIsoDep.transceive(apdu);
    }

    @Override
    public void connect() throws IOException {
        // TODO Auto-generated method stub
        mIsoDep.connect();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        mIsoDep.close();
        
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
