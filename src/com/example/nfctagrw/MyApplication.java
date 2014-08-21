package com.example.nfctagrw;

import com.example.nfctagrw.data.EMVCardEntity;

import android.app.Application;

public class MyApplication extends Application {
    private EMVCardEntity mEMVCardEntity;

    public void onCreate() {
        super.onCreate();

    }

    public void setEMVCardEntity(EMVCardEntity entity) {
        mEMVCardEntity = entity;
    }
    
    public EMVCardEntity getEMVCardEntity(){
        return mEMVCardEntity;
    }
}
