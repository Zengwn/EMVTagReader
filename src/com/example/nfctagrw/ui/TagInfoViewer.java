package com.example.nfctagrw.ui;

import com.example.nfctagrw.R;
import com.example.nfctagrw.base.Card;
import com.example.nfctagrw.data.DataEntity;
import com.example.nfctagrw.data.EMVCardEntity;
import com.example.nfctagrw.util.HexTool;
import com.example.nfctagrw.MyApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class TagInfoViewer extends Activity {
    public static final String TAG = TagInfoViewer.class.getSimpleName();

    private EMVCardEntity mEMVCardEntity;
    private Card mCard;
    private ImageView mCardView;
    private TextView mCardInfo;
    private TextView mCardIssur;
    private TextView mCardPan;
    private TextView mCardExpriyDate;
    private PendingIntent mPendingIntent;
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_info_viewer);
        mCard = ((MyApplication) getApplication()).getCurrentCard();
        mEMVCardEntity = (EMVCardEntity) (mCard.getResult().object);

        showStateDialog();

        initControl();
        
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .setComponent(getComponentName()), 0);
        
        IntentFilter nfcTech = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);

        mFilters = new IntentFilter[] { nfcTech, };
        
        mTechLists = new String[][] { new String[] { NfcA.class.getName() },
                new String[] { IsoDep.class.getName() } };

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public void onResume() {
        super.onResume();
        initContent();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
                mFilters, mTechLists);
    }

    public void onPause(){
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }
    public void showStateDialog() {
        String msg;

        Card.state state = mCard.getActionError();
        Log.i(TAG, "showstatedialog: " + state);
        
        if (state.equals(Card.state.OK)) {
            msg = "New TAG Detected";
        } else {
            msg = "TAG reading interrupted";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initControl() {
        mCardView = (ImageView) findViewById(R.id.image);
        mCardInfo = (TextView) findViewById(R.id.emvInfo);
        mCardIssur = (TextView) findViewById(R.id.issuer);
        mCardPan = (TextView) findViewById(R.id.pan);
        mCardExpriyDate = (TextView) findViewById(R.id.expriy);
    }

    private void initContent() {
        try {
            mCardIssur.setText(mEMVCardEntity.issuer);
            mCardPan.setText(mEMVCardEntity.pan);
            mCardExpriyDate.setText(mEMVCardEntity.expiryMonth + " / "
                    + mEMVCardEntity.expiryYear);
            mCardInfo.setText("Process:Tag:Length:Data\n");

            for (DataEntity e : mEMVCardEntity.getDataEntity()) {
                mCardInfo.append("[" + e.tagName + "]" + "\n" + "[" + e.subTag
                        + "]" + "\n" + "[" + e.lengh + "]" + "\n"
                        + HexTool.bytesToHexString(e.data) + "\n" + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "No card information!");
        }
    }
}
