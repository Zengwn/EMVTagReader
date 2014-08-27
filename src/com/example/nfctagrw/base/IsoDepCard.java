package com.example.nfctagrw.base;

import java.io.IOException;

import com.example.nfctagrw.util.HexTool;

import nz.geek.rhubarb.emvtools.EMVReader;
import nz.geek.rhubarb.emvtools.EMVReader.CardReader;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class IsoDepCard implements Card {
    public static final String TAG = IsoDepCard.class.getSimpleName();

    private static int count = 0;
    private IsoDep mIsoDep;
    private state mState = state.OK;
    private result mResult;
    private cardreader mCardReader;
    private EMVReader mEmvreader;
    private boolean mEMVReadDebug = false;

    public IsoDepCard(IsoDep isodep) {
        Log.i(TAG, "IsoDep Card created");
        mIsoDep = isodep;
        count = 0;
        mResult = new result();
        mCardReader = new cardreader();
    }

    @Override
    public void prepare(final CardAccessListener cb) {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!mIsoDep.isConnected()) {
                    try {
                        mIsoDep.connect();
                        defaultAction();
                        setActionError(state.OK);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.i(TAG, "Prepare error");
                        setActionError(state.ERROR);
                        e.printStackTrace();
                    }
                }
                cb.cardReady();
            }

        }).start();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        if (mIsoDep != null) {
            try {
                mIsoDep.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public result getResult() {
        // TODO Auto-generated method stub
        return mResult;
    }

    @Override
    public state getActionError() {
        // TODO Auto-generated method stub
        return mState;
    }

    private void setActionError(state s){
        Log.i(TAG, "set state: " + s);
        mState = s;
    }
    
    @Override
    public void defaultAction() throws IOException {
        mEmvreader = new EMVReader(mCardReader, null, null);
        mEmvreader.doTrace = mEMVReadDebug;
        try{
            mEmvreader.read();
            setActionError(state.OK);
            mResult.no = count++;
            mResult.object = mEmvreader.getEMVCardEntity();
        }catch(IOException e){
            setActionError(state.ERROR);
            throw e;
        }
    }

    @Override
    public void asyncTransceive(byte[] apdu, CardAccessListener cb) {
        // TODO Auto-generated method stub
        byte[] result = null;
        try {
            result = mIsoDep.transceive(apdu);
            setActionError(state.OK);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            setActionError(state.ERROR);
        }
        mResult.no = count++;
        mResult.data = result;
        Log.i(TAG, "Transceive state  " + mState);
        Log.i(TAG, "Data :" + HexTool.bytesToHexString(result));
        cb.transceiveDone();

    }

    @Override
    public byte[] transceive(byte[] apdu) {
        // TODO Auto-generated method stub
        byte[] result = null;
        try {
            result = mIsoDep.transceive(apdu);
            setActionError(state.OK);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            setActionError(state.ERROR);
        }
        mResult.no = count++;
        mResult.data = result;
        Log.i(TAG, "Transceive state  " + mState);
        Log.i(TAG, "Data :" + HexTool.bytesToHexString(result));
        return result;
    }

    class cardreader implements CardReader {
        @Override
        public byte[] transceive(byte[] apdu) throws IOException {
            // TODO Auto-generated method stub
            return mIsoDep.transceive(apdu);
        }

    }

}
