package com.example.nfctagrw.data;

import java.util.ArrayList;
import java.util.List;

public class EMVCardEntity {
    public String pan;
    public String issuer;
    public int expiryYear;
    public int expiryMonth;
    public byte[] aid,adf;
    private List<DataEntity> dataEntityList;
    public String excepMsg;
    
    public EMVCardEntity(){
        dataEntityList = new ArrayList<DataEntity>();
    }
    public void setDataEntity(String tagname, String subtag, int len, byte[] data){
        DataEntity entity = new DataEntity(tagname, subtag, len, data);
        dataEntityList.add(entity);
    }
    
    public List<DataEntity> getDataEntity(){
        return dataEntityList;
    }
}
