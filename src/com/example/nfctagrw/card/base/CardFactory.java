package com.example.nfctagrw.card.base;

import java.io.IOException;

import com.example.nfctagrw.card.AmericanExpressCard;
import com.example.nfctagrw.card.DiscoverCard;
import com.example.nfctagrw.card.MasterCard;
import com.example.nfctagrw.card.VisaCard;
import com.example.nfctagrw.card.base.Card.CardAccessListener;
import com.example.nfctagrw.card.base.Card.state;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class CardFactory {
    public static final String TAG = CardFactory.class.getSimpleName();

    private CardAccessListener mCallback;
    private IsoDep mNfcHandler = null;
    private Card mCardBase;
    private Context mCardHolder;
    private static Card mCurrentCard;

    public CardFactory(Context holder, CardAccessListener cb) {
        mCallback = cb;
        mCardHolder = holder;
    }

    public boolean prepare(Intent intent) {

        Log.i(TAG, "prepare ");
        if (!intent.hasExtra(NfcAdapter.EXTRA_TAG))
            return false;

        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mNfcHandler = IsoDep.get(tagFromIntent);

        if (mNfcHandler == null)
            return false;

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!mNfcHandler.isConnected()) {
                    try {
                        mNfcHandler.connect();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        mCallback.result(state.ERROR);
                        e.printStackTrace();
                        return;
                    }
                }

                mCardBase = new Card(mCardHolder, mNfcHandler, mCallback);

                if (!mCardBase.init()) {
                    mCallback.result(state.ERROR);
                    return;
                }

                // Aid with highest priority
//                if (!mCardBase.isSupportedCard(mCardBase.getAid())) {
//                    mCallback.result(state.UNSUPPORT);
//                    return;
//                }
                mCurrentCard = getCard(mCardBase, mCardBase.getRid());
                
                if(mCurrentCard == null){
                    mCallback.result(state.ERROR);
                    return;
                }else{
                    mCallback.result(state.READY);
                }
                    
            }
        }).start();

        return true;
    }

    private Card getCard(Card base, String rid) {
        Card card = null;
        if (rid == null)
            return null;

        if (rid.isEmpty() || rid.equals(""))
            return null;

        switch (rid) {
            case Card.RID_VISA:
                card = new VisaCard(base);
                break;
            case Card.RID_MASTERCARD:
                card = new MasterCard(base);
                break;
            case Card.RID_AMEXPRESS:
                card = new AmericanExpressCard(base);
                break;
            case Card.RID_DISCOVER:
                card = new DiscoverCard(base);
                break;
            default:
                Log.i(TAG, "No match AID, return Base card");
                card = base;
                break;
        }
        return card;
    }

    public static Card getCurrentCard() {
        return mCurrentCard;
    }
}
