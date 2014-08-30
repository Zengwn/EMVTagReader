package com.example.nfctagrw.ui;

import com.example.nfctagrw.MyApplication;
import com.example.nfctagrw.R;
import com.example.nfctagrw.card.base.Card;
import com.example.nfctagrw.card.base.Card.CardAccessListener;
import com.example.nfctagrw.card.base.Card.state;
import com.example.nfctagrw.card.base.CardFactory;

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

public class MainActivity extends Activity implements CardAccessListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private NfcAdapter mNfcAdapter;
    private Vibrator mVibrator;
    private PendingIntent mPendingIntent;
    private ProgressDialog mProgressDialog;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private CardFactory mCardFactory;

    private static final int CARD_READY = 1;
    private static final int CARD_INIT_ERROR = 2;
    private static final int CARD_NOT_SUPPORTED = 3;
    private static final int CARD_ERROR = 4;

    private static final String ALIAS_MAINACTIVITY = ".ui.MainActivityTagDetectionAlias";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CARD_INIT_ERROR:
                case CARD_ERROR:
                    Log.i(TAG, "Card error");
                    stopProgressDialog();
                    mVibrator.vibrate(500);
                    Toast.makeText(MainActivity.this,
                            "Card cannot be initialized ", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case CARD_NOT_SUPPORTED:
                    Log.i(TAG, "Card not supported");
                    stopProgressDialog();
                    mVibrator.vibrate(500);
                    Toast.makeText(MainActivity.this, "Card not supported ",
                            Toast.LENGTH_SHORT).show();
                    break;
                case CARD_READY:
                    Log.i(TAG, "Card Connected");
                    ((MyApplication) getApplication())
                            .setCurrentCard(CardFactory.getCurrentCard());
                    stopProgressDialog();
                    startViewer();
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
        mCardFactory = new CardFactory(this, this);
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
    }

    public void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        processIntentRaw(intent);
    }

    private void processIntentRaw(Intent intent) {
        Log.i(TAG, "processIntentRaw");

        startProgressDialog();
        if (!mCardFactory.prepare(intent)) {
            mHandler.sendEmptyMessage(CARD_INIT_ERROR);
        }
    }

    public void closeApp(View view) {
        finish();
    }

    private void startViewer() {
        Log.i(TAG, "start TAGViewer");
        mVibrator.vibrate(100);
        Intent intent = new Intent(this, TagInfoViewer.class);
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
    }

    private void stopProgressDialog() {
        try {
            mProgressDialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transceiveDone() {
        // TODO Auto-generated method stub

    }

    @Override
    public void result(state s) {
        // TODO Auto-generated method stub
        switch (s) {
            case UNSUPPORT:
                mHandler.sendEmptyMessage(CARD_NOT_SUPPORTED);
                break;

            case READY:
                mHandler.sendEmptyMessage(CARD_READY);
                break;

            case ERROR:
            case INTERRUPTED:
                mHandler.sendEmptyMessage(CARD_ERROR);
                break;
            default:
                break;
        }
    }

}
