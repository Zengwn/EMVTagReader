package com.example.nfctagrw;

import com.example.nfctagrw.base.Card;
import android.app.Application;

public class MyApplication extends Application {
    // private EMVCardEntity mEMVCardEntity;
    private Card mCurrentCard;

    public void onCreate() {
        super.onCreate();
    }

    public void setCurrentCard(Card c) {
        mCurrentCard = c;
    }

    public Card getCurrentCard() {
        return mCurrentCard;
    }

    // public void setEMVCardEntity(EMVCardEntity entity) {
    // mEMVCardEntity = entity;
    // }
    //
    // public EMVCardEntity getEMVCardEntity(){
    // return mEMVCardEntity;
    // }
}
