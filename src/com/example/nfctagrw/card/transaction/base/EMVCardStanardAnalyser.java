package com.example.nfctagrw.card.transaction.base;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader;
import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.util.Log;

import com.example.nfctagrw.card.base.Card;
import com.example.nfctagrw.util.HexTool;

public class EMVCardStanardAnalyser {
    public static final String TAG = EMVCardStanardAnalyser.class
            .getSimpleName();

    private Card mHolder;
    private cardreader mCardReader;
    private EMVReader mEmvreader;
    private boolean mEMVReadDebug = false;

    public EMVCardStanardAnalyser(Card holder) {
        Log.i(TAG, "created ");
        mHolder = holder;
        mCardReader = new cardreader();
    }

    public boolean prepare() {
        boolean result = true;

        Log.i(TAG, "prepare ");
        mEmvreader = new EMVReader(mCardReader, null, null);
        mEmvreader.doTrace = mEMVReadDebug;
        try {
            mEmvreader.read();
        } catch (IOException e) {
            result = false;
        }

        return result;
    }

    public String getAid() {
        String aid = HexTool.bytesToHexString(mEmvreader.aid);
        Log.i(TAG, "AID : " + aid);
        return aid;
    }

    public String getRid() {
        String aid = HexTool.bytesToHexString(mEmvreader.aid);
        String rid = aid.substring(0, 10);
        Log.i(TAG, "RID : " + rid);
        return rid;
    }

    public String getIssuer() {
        return mEmvreader.issuer;
    }

    public String getPan() {
        return mEmvreader.pan;
    }

    public int getExpiryYear() {
        return mEmvreader.expiryYear;
    }

    public int getExpiryMonth() {
        return mEmvreader.expiryMonth;
    }

    public String getDisplayContents() {
        Log.i(TAG, "getDisplayContents");
        StringBuilder contents = new StringBuilder();
        for (EMVReader.EnumCallback c : mEmvreader.mObjectList) {
            contents.append(c.toString());
        }
        return contents.toString();
    }

    class cardreader implements CardReader {
        @Override
        public byte[] transceive(byte[] apdu) throws IOException {
            // TODO Auto-generated method stub
            return mHolder.getNfcHolder().transceive(apdu);
        }
    }

}
