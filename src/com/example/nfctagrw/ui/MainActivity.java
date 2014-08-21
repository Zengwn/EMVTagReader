package com.example.nfctagrw.ui;

import java.io.IOException;

import com.example.nfctagrw.MyApplication;
import com.example.nfctagrw.R;
import com.example.nfctagrw.data.EMVCardEntity;
import com.example.nfctagrw.util.IsoDepCardReader;
import com.example.nfctagrw.util.NfcaCardReader;
import com.example.nfctagrw.util.HexTool;

import nz.geek.rhubarb.emvtools.EMVReader;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private ProgressDialog mProgressDialog;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private EMVReader mEmvreader;
    private boolean mEMVReadSucceed = true;
    private NfcaCardReader mNfcaCardReader;
    private IsoDepCardReader mIsoDepCardReader;
    private NfcA mNfca = null;
    private IsoDep mIsoDep = null;
    private boolean isIsoDep = true;
    private byte[] mAdfInfo;
    private static final int CONNECT_OVER = 1;
    private static final int TRANS_OVER = 2;
    private static final int DIALOG_CANCEL = 3;
    private static final int START_TAGVIEWER = 4;
    private static final String ALIAS_MAINACTIVITY = ".ui.MainActivityTagDetectionAlias";
    public static final String INTENT_MAINACTIVITY = "com.example.nfctagrw.ui.MainActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_OVER:
                    Log.i(TAG, "Connected");
                    Log.i(TAG, "");
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
                        try {
                            byte[] mAdfInfo = mIsoDep
                                    .transceive(mEmvreader.SELECT_PPSE);
                            Log.i(TAG,
                                    "mAdfInfo =  "
                                            + HexTool
                                                    .bytesToHexString(mAdfInfo));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mEmvreader = new EMVReader(mIsoDepCardReader, null,
                                mAdfInfo);
                    } else {
                        mEmvreader = new EMVReader(mNfcaCardReader, null,
                                mAdfInfo);
                    }
                    mEmvreader.doTrace = true;

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                mEmvreader.read();

                                Log.i(TAG, "Issuer " + mEmvreader.issuer);
                                Log.i(TAG, "Result " + mEmvreader.pan + ",Y="
                                        + mEmvreader.expiryYear + ",M="
                                        + mEmvreader.expiryMonth);

                                ((MyApplication) getApplication())
                                        .setEMVCardEntity(mEmvreader
                                                .getEMVCardEntity());

                                mHandler.sendEmptyMessage(START_TAGVIEWER);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                mEMVReadSucceed = false;
                                EMVCardEntity entity = mEmvreader
                                        .getEMVCardEntity();
                                entity.excepMsg = e.getMessage();
                                ((MyApplication) getApplication())
                                        .setEMVCardEntity(entity);
                                mHandler.sendEmptyMessage(START_TAGVIEWER);
                            }
                        }

                    }).start();
                    break;
                case START_TAGVIEWER:
                    stopProgressDialog();
                    startViewer();
                    break;
                case DIALOG_CANCEL:
                    stopProgressDialog();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        // Log.i(TAG, "getClass() : " + getClass());
        // Log.i(TAG, "getLocalClassName : " + getLocalClassName());
        // Log.i(TAG, "getPackageName : " + getPackageName());
        // Log.i(TAG, "getComponentName : " + getComponentName());
        // Log.i(TAG, "getComponentName.getClassName : " +
        // getComponentName().getClassName());
        // Log.i(TAG, "getComponentName.getPackageName : " +
        // getComponentName().getPackageName());
        // Log.i(TAG, "getComponentName.getShortClassName : " +
        // getComponentName().getShortClassName());
        // Log.i(TAG, "getComponentName.toShortString : " +
        // getComponentName().toShortString());

        setContentView(R.layout.activity_main);

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .setComponent(getComponentName()), 0);

        IntentFilter nfcTech = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);

        mFilters = new IntentFilter[] { nfcTech, };

        // Setup a tech list for all NfcA/IsoDep tags
        mTechLists = new String[][] { new String[] { NfcA.class.getName() },
                new String[] { IsoDep.class.getName() } };

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            // mTagInfo.append("NFC is not enabled");
            return;
        }

        Intent intent = getIntent();
        if (intent.getComponent().getShortClassName()
                .equals(ALIAS_MAINACTIVITY)) {
            processIntentRaw(intent);
        }

    }

    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        Log.i(TAG, getIntent().toString());

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
                    mFilters, mTechLists);
    }

    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mNfcAdapter.disableForegroundDispatch(this);
        try {
            if (isIsoDep) {
                if (mIsoDep != null)
                    mIsoDep.close();
            } else {
                if (mNfca != null)
                    mNfca.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        processIntentRaw(intent);
    }

    private void processIntentRaw(Intent intent) {
        Log.i(TAG, "processIntentRaw");
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.i(TAG, tagFromIntent.toString());

        startProgressDialog();
        mEMVReadSucceed = true;
        for (String tech : tagFromIntent.getTechList()) {
            // mTagInfo.append(tech + "\n");
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

    public void closeApp(View view) {
        finish();
    }

    private void startViewer() {
        Log.i(TAG, "startViewer with boolean :" + mEMVReadSucceed);
        Intent intent = new Intent(this, TagInfoViewer.class).putExtra(
                INTENT_MAINACTIVITY, mEMVReadSucceed);
        startActivity(intent);
    }

    private void startProgressDialog() {
        Log.i(TAG, "Start progressDialog");
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Reading Tag...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();

        new Thread() {
            public void run() {
                int count = 0;
                while (count <= 40) {
                    mProgressDialog.setProgress(count++);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        Log.i(TAG, "ProgressDialog thread monitor error");
                    }
                }
                mHandler.sendEmptyMessage(DIALOG_CANCEL);
            }
        }.start();
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        } else {
            Log.i(TAG, "ProgressDialog is NULL");
        }
    }

}
