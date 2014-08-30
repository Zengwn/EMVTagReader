package com.example.nfctagrw.card;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.nfctagrw.R;
import com.example.nfctagrw.card.base.Card;


public class MasterCard extends Card {
    public static final String TAG = MasterCard.class.getSimpleName();
    
    public MasterCard(Card me) {
        super(me);
        Log.i(TAG, " Created ");
    }
    
    public Bitmap getRepresentImg() {
        Log.i(TAG, "get Img");
        
        Context context = getCardHolderContext();
        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.master);
    }
}
    

