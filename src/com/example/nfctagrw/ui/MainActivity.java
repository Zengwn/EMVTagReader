package com.example.nfctagrw.ui;

import java.io.IOException;

import com.example.nfctagrw.MyApplication;
import com.example.nfctagrw.R;
import com.example.nfctagrw.data.EMVCardEntity;
import com.example.nfctagrw.util.Card;
import com.example.nfctagrw.util.CardFactory;
import com.example.nfctagrw.util.HexTool;

import nz.geek.rhubarb.emvtools.EMVReader;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private NfcAdapter mNfcAdapter;
    private Vibrator mVibrator;
    private PendingIntent mPendingIntent;
    private ProgressDialog mProgressDialog;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private EMVReader mEmvreader;
    private boolean mEMVReadSucceed = true;
    private boolean mEMVReadDebug = false;
    private Card mCard;
    private byte[] mAdfInfo;
    private static final int CONNECT_OVER = 1;
    private static final int ADF_SELECT_OVER = 2;
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                byte[] mAdfInfo = mCard.getCardReader()
                                        .transceive(mEmvreader.SELECT_PPSE);

                                Log.i(TAG,
                                        "mAdfInfo =  "
                                                + HexTool
                                                        .bytesToHexString(mAdfInfo));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mHandler.sendEmptyMessage(ADF_SELECT_OVER);
                        }
                    }).start();

                    break;
                case ADF_SELECT_OVER:
                    Log.i(TAG, "ADF selected");
                    mEmvreader = new EMVReader(mCard.getCardReader(), null,
                            mAdfInfo);
                    mEmvreader.doTrace = mEMVReadDebug;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                mEmvreader.read();
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
        setContentView(R.layout.activity_main);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
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
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not enabled", Toast.LENGTH_SHORT)
                    .show();
            finish();
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
            if (mCard != null) {
                mCard.close();
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

        mCard = CardFactory.getCurrentCard(intent);
        startProgressDialog();
        mEMVReadSucceed = true;

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mCard.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(CONNECT_OVER);
            }
        }).start();
    }

    public void closeApp(View view) {
        finish();
    }

    private void startViewer() {
        Log.i(TAG, "startViewer with boolean :" + mEMVReadSucceed);
        mVibrator.vibrate(100);
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
