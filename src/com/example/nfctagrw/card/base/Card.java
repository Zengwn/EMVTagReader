package com.example.nfctagrw.card.base;

import java.io.IOException;

import com.example.nfctagrw.card.transaction.base.EMVCardStanardAnalyser;

import android.content.Context;
import android.graphics.Bitmap;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class Card {
    public static final String TAG = Card.class.getSimpleName();

    public enum state {
        IDLE, READY, INTERRUPTED, UNSUPPORT, ERROR, OK
    }

    public static final String RID_VISA = "a000000003";
    public static final String PIX_VISA_CreditorDebit = "1010";
    public static final String PIX_VISA_Electron = "2010";
    public static final String PIX_VISA_PLUS = "8010";

    public static final String RID_MASTERCARD = "a000000004";
    public static final String PIX_MASTERCARD_CreditorDebit = "1010";
    public static final String PIX_MASTERCARD_Maestro = "3060";
    public static final String PIX_MASTERCARD_Cirrus = "6000";

    public static final String RID_AMEXPRESS = "a000000025";
    public static final String PIX_AMEXPRESS = "01";

    public static final String RID_DISCOVER = "a000000152";
    public static final String PIX_DISCOVER = "3010";

    public static final String[] mSupportedAIDs = {
            // Visa AIDs
            RID_VISA + PIX_VISA_CreditorDebit,
            RID_VISA + PIX_VISA_Electron,
            RID_VISA + PIX_VISA_PLUS,
            // MasterCard AIDs
            RID_MASTERCARD + PIX_MASTERCARD_CreditorDebit,
            RID_MASTERCARD + PIX_MASTERCARD_Maestro,
            RID_MASTERCARD + PIX_MASTERCARD_Cirrus,
            // American Express & Discover Card AIDs
            RID_AMEXPRESS + PIX_AMEXPRESS, RID_DISCOVER + PIX_DISCOVER };

    protected IsoDep mNfcObjHolder;
    protected EMVCardStanardAnalyser mTransactionHandler;
    protected CardAccessListener mCardListener;
    protected Context mCardHolder;

    public Card(Context holder, IsoDep nfcHolder, CardAccessListener listener) {
        Log.i(TAG, "Created ");
        mNfcObjHolder = nfcHolder;
        mTransactionHandler = new EMVCardStanardAnalyser(this);
        mCardListener = listener;
        mCardHolder = holder;
    }

    public Card(Card me) {
        Log.i(TAG, "Created copy");
        Copy(me);
    }

    private void Copy(Card me) {
        Log.i(TAG, "Copy object");
        mNfcObjHolder = me.mNfcObjHolder;
        mTransactionHandler = me.mTransactionHandler;
        mCardHolder = me.mCardHolder;
        mCardListener = me.mCardListener;
    }

    public boolean init() {
        boolean result = true;
        Log.i(TAG, "init ");
        if (!mTransactionHandler.prepare())
            result = false;

        return result;
    }

    public boolean isSupportedCard(String Aid) {
        return mSupportedAIDs.toString().contains(Aid);
    }

    public String getAidName() {
        String aidName = "";

        switch (getRid()) {
            case Card.RID_VISA:
                aidName = "Visa Card";
                break;
            case Card.RID_MASTERCARD:
                aidName = "Master Card";
                break;
            case Card.RID_AMEXPRESS:
                aidName = "American Express";
                break;
            case Card.RID_DISCOVER:
                aidName = "Discover Card";
                break;
            default:
                break;
        }
        return aidName;
    }

    public String getSupportedTAG() {
        return null;
    }

    public String getAid() {
        return mTransactionHandler.getAid();
    }

    public String getRid() {
        return mTransactionHandler.getRid();
    }

    public String getDisplayContents() {
        return mTransactionHandler.getDisplayContents();
    }

    public Bitmap getRepresentImg() {
        return null;
    }

    public void close() {
        if (mNfcObjHolder.isConnected()) {
            try {
                mNfcObjHolder.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public IsoDep getNfcHolder() {
        return mNfcObjHolder;
    }

    public Context getCardHolderContext() {
        return mCardHolder;
    }

    public String getIssuer() {
        return mTransactionHandler.getIssuer();
    }

    public String getPan() {
        return mTransactionHandler.getPan();
    }

    public int getExpiryYear() {
        return mTransactionHandler.getExpiryYear();
    }

    public int getExpiryMonth() {
        return mTransactionHandler.getExpiryMonth();
    }

    public interface CardAccessListener {
        public void result(state s);

        public void transceiveDone();
    }

}
