package com.example.nfctagrw;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.nfc.tech.IsoDep;

public class IsoDepCardReader implements CardReader {

    private IsoDep mIsoDep;
    public IsoDepCardReader(IsoDep isodep){
        mIsoDep = isodep;
    }
    
    @Override
    public byte[] transceive(byte[] apdu) throws IOException {
        // TODO Auto-generated method stub
        return mIsoDep.transceive(apdu);
    }

}
