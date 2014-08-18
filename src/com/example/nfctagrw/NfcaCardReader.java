package com.example.nfctagrw;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.nfc.tech.NfcA;

public class NfcaCardReader implements CardReader {

    private NfcA mNfca;
    public NfcaCardReader(NfcA nfca){
        mNfca = nfca;
    }
    
    @Override
    public byte[] transceive(byte[] apdu) throws IOException {
        // TODO Auto-generated method stub
        return mNfca.transceive(apdu);
    }

}
