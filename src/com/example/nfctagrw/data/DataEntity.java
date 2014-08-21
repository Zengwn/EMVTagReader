package com.example.nfctagrw.data;

public class DataEntity {
    public String tagName;
    public String subTag;
    public int  lengh;
    public byte[] data;
    
    public DataEntity(String tag, String subtag, int len, byte[] d){
        tagName = tag;
        subTag = subtag;
        lengh = len;
        data = d;
    }
}
