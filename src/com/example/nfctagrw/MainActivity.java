package com.example.nfctagrw;

import java.io.IOException;

import nz.geek.rhubarb.emvtools.EMVReader;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private NfcAdapter mNfcAdapter;
    private TextView mTagInfo;
    private EMVReader mEmvreader;
    private NfcaCardReader mNfcaCardReader;
    private IsoDepCardReader mIsoDepCardReader;
    private NfcA mNfca;
    private IsoDep mIsoDep;
    private boolean isIsoDep = true;
    private byte[] mAdfInfo;
    private static final int CONNECT_OVER = 1;
    private static final int TRANS_OVER = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_OVER:
                    Log.i(TAG, "connect done");
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                if (isIsoDep) {
                                    byte[] mAdfInfo = mIsoDep
                                            .transceive(mEmvreader.SELECT_PPSE);
                                } else {
                                    byte[] mAdfInfo = mNfca
                                            .transceive(mEmvreader.SELECT_PPSE);
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            mHandler.sendEmptyMessage(TRANS_OVER);
                        }

                    }).start();

                    break;
                case TRANS_OVER:
                    Log.i(TAG, "trans done");
                    if (isIsoDep) {
                        Log.i(TAG,
                                "getMaxTransceiveLength :"
                                        + mIsoDep.getMaxTransceiveLength());
                        Log.i(TAG,
                                "getHiLayerResponse :"
                                        + mIsoDep.getHiLayerResponse());
                        Log.i(TAG,
                                "getHistoricalBytes :"
                                        + mIsoDep.getHistoricalBytes());
                        Log.i(TAG, "isConnected :" + mIsoDep.isConnected());
                        Log.i(TAG,
                                "isExtendedLengthApduSupported :"
                                        + mIsoDep
                                                .isExtendedLengthApduSupported());
                        Log.i(TAG, "getTag :" + mIsoDep.getTag());
                    } else {
                        Log.i(TAG,
                                "getMaxTransceiveLength :"
                                        + mNfca.getMaxTransceiveLength());
                        Log.i(TAG, "getAtqa :" + mNfca.getAtqa());
                        Log.i(TAG, "getSak :" + mNfca.getSak());
                        Log.i(TAG, "isConnected :" + mNfca.isConnected());
                        Log.i(TAG, "getTimeout :" + mNfca.getTimeout());
                        Log.i(TAG, "getTag :" + mNfca.getTag());
                    }
                    // Log.i(TAG, mAdfInfo.toString());

                    if (isIsoDep) {
                        mEmvreader = new EMVReader(mIsoDepCardReader, null /*
                                                                            * EMVReader
                                                                            * .
                                                                            * AID_PPSE
                                                                            */,
                                mAdfInfo);
                    } else {
                        mEmvreader = new EMVReader(mNfcaCardReader, null /*
                                                                          * EMVReader
                                                                          * .
                                                                          * AID_PPSE
                                                                          */,
                                mAdfInfo);
                    }
                    mEmvreader.doTrace = true;
                    try {
                        mEmvreader.read();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Issuer " + mEmvreader.issuer);
                    Log.i(TAG, "Result " + mEmvreader.pan + ",Y="
                            + mEmvreader.expiryYear + ",M="
                            + mEmvreader.expiryMonth);

                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTagInfo = (TextView) findViewById(R.id.tagInfo);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            mTagInfo.append("NFC is not enabled");
            return;
        }

    }

    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        try {
            if (isIsoDep)
                mIsoDep.close();
            else
                mNfca.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            processIntentRaw(getIntent());
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
            Log.i(TAG, "ACTION_TAG_DISCOVERED");
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Log.i(TAG, "ACTION_NDEF_DISCOVERED");
        }
    }

    public void onStop() {
        super.onStop();
        try {
            if (isIsoDep)
                mIsoDep.close();
            else
                mNfca.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    private void processIntentRaw(Intent intent) {
        Log.i(TAG, "processIntentRaw");
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        for (String tech : tagFromIntent.getTechList()) {
            mTagInfo.append(tech + "\n");
        }

        if (isIsoDep) {
            mIsoDep = IsoDep.get(tagFromIntent);
            mIsoDepCardReader = new IsoDepCardReader(mIsoDep);

        } else {
            mNfca = NfcA.get(tagFromIntent);
            mNfcaCardReader = new NfcaCardReader(mNfca);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if (isIsoDep) {
                        mIsoDep.connect();
                    } else
                        mNfca.connect();
                    mHandler.sendEmptyMessage(CONNECT_OVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void writeTag(View view) {
        mTagInfo.append("not implemented");
    }

    public void exportJson(View view) {
        mTagInfo.append("not implemented");
    }

    public void importJson(View view) {
        mTagInfo.append("not implemented");
    }
}
