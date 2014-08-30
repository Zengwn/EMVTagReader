package com.example.nfctagrw.card.transaction;

import com.example.nfctagrw.card.base.Card;
import com.example.nfctagrw.card.transaction.base.EMVCardStanardAnalyser;

public class MasterCardTransaction extends EMVCardStanardAnalyser {
    public MasterCardTransaction(Card holder) {
        super(holder);
        // TODO Auto-generated constructor stub
    }

    public static final String TAG = MasterCardTransaction.class.getSimpleName();
    
}
