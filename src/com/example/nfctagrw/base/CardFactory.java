package com.example.nfctagrw.base;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;

public class CardFactory {
    public static final String TAG = CardFactory.class.getSimpleName();
   
    public static Card getCurrentCard(Intent intent){
        if(!intent.hasExtra(NfcAdapter.EXTRA_TAG))
            return null;
        
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.i(TAG, tagFromIntent.toString());
        
//        
//
//        if (isIsoDep) {
//            mIsoDep = IsoDep.get(tagFromIntent);
//            mIsoDepCard = new IsoDepCard(mIsoDep);
//
//        } else {
//            mNfca = NfcA.get(tagFromIntent);
//            mNfcaCard = new NfcaCard(mNfca);
//        }
//        
//        for (String tech : tagFromIntent.getTechList()) {
//            // mTagInfo.append(tech + "\n");
//        }
        
        return new IsoDepCard(IsoDep.get(tagFromIntent));
    }
}
