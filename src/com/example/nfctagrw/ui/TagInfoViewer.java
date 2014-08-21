package com.example.nfctagrw.ui;

import com.example.nfctagrw.R;
import com.example.nfctagrw.data.DataEntity;
import com.example.nfctagrw.data.EMVCardEntity;
import com.example.nfctagrw.util.HexTool;
import com.example.nfctagrw.MyApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TagInfoViewer extends Activity {
    public static final String TAG = TagInfoViewer.class.getSimpleName();

    private EMVCardEntity mEMVCardEntity;
    private ImageView mCardView;
    private TextView mCardInfo;
    private TextView mCardIssur;
    private TextView mCardPan;
    private TextView mCardExpriyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_info_viewer);
        mEMVCardEntity = ((MyApplication) getApplication()).getEMVCardEntity();

        showStateDialog(getIntent().getBooleanExtra(
                MainActivity.INTENT_MAINACTIVITY, true));

        initControl();
    }

    public void onResume() {
        super.onResume();
        initContent();
    }

    public void showStateDialog(boolean good) {
        String msg;

        if (good) {
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
    }
}
