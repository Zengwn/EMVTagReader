package com.example.nfctagrw;

import com.example.nfctagrw.card.base.Card;

import android.app.Application;

public class MyApplication extends Application {
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
}
